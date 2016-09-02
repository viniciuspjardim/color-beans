/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.ai.Ai3;
import com.vpjardim.colorbeans.net.GameServer;

/**
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class MapScreen extends ScreenBase {

    public Array<Map> maps;
    public int nMaps;

    // #debugCode Todo it's not running on android, maybe updating Kryonet jar would fix
    GameServer server;

    public boolean autoRestart = true;

    public ShapeRenderer sr;

    public float side;
    public float marginX;
    public float marginY;
    public float mapsX;
    public float mapsY;

    public int controllerCount;
    public int keyboardCount;
    public int touchCount;

    public MapScreen() {
        super();
        manageInput = false;
    }

    // Todo should not return maps in game over state
    public Map getOpponent(Map mCaller) {

        if(maps.size <= 1 || mCaller == null) return null;

        int rand = MathUtils.random(0, maps.size -1);
        Map opp = maps.get(rand);

        // If the random map picked is the on that
        // has called this method, chose the next on
        if(opp == mCaller) {

            // If it`s the last, get the first one
            if(rand == maps.size -1) opp = maps.get(0);
            else {
                opp = maps.get(rand +1);
            }
        }
        return opp;
    }

    public void calcSideSize() {

        // Margin of 2% of the screen width
        marginX = Gdx.graphics.getWidth() * (2f / 100f);
        float totalMarginX = marginX * (nMaps + 1);
        mapsX = Gdx.graphics.getWidth() - totalMarginX;
        float sideX = mapsX / (Map.N_COL * nMaps);

        // Margin of 2% of the screen height
        marginY = Gdx.graphics.getHeight() * (2f / 100f);
        float totalMarginY = marginY * 2;
        mapsY = Gdx.graphics.getHeight() - totalMarginY;
        float sideY = mapsY / Map.N_ROW;

        side = Math.min(sideX, sideY);

        mapsX = side * nMaps * Map.N_COL + totalMarginX;
        mapsY = side * Map.N_ROW + totalMarginY;
    }

    public void alignMap(Map m, int position) {

        m.prop.side = side;
        m.prop.px = (-Gdx.graphics.getWidth() / 2f) +  marginX +
                    (position * (side * Map.N_COL + marginX)) +
                    ((Gdx.graphics.getWidth() - mapsX) / 2f)
        ;
        m.prop.py = (Gdx.graphics.getHeight() / 2f) - marginY -
                    ((Gdx.graphics.getHeight() - mapsY) / 2f)
        ;
    }

    public void saveState() {

        Gson gson = new GsonBuilder().create();

        String jsonTxt = gson.toJson(maps.toArray());
        FileHandle file = Gdx.files.local("state/save.json");
        file.writeString(jsonTxt, false);
    }

    public void loadState() {

        FileHandle file = Gdx.files.local("state/save.json");

        if(file.exists()) {
            recycle();
            String jsonTxt = file.readString();
            Gson gson = new Gson();
            maps = new Array<Map>();
            maps.addAll(gson.fromJson(jsonTxt, Map[].class));
            nMaps = maps.size;

            game.input.targetsClear();

            calcSideSize();

            for(int i = 0; i < maps.size; i++) {
                Map m = maps.get(i);
                m.deserialize(this);
                alignMap(m, i);
                game.input.addTarget(m);
            }
            game.input.linkAll();

            // Todo ai.init() should be called after game.input.linkAll()
            // #debugCode
            //maps.get(0).ai = new Ai3();
            //maps.get(0).ai.init(maps.get(0));
            if(maps.size > 1) {
                maps.get(1).ai = new Ai3();
                maps.get(1).ai.init(maps.get(1));
            }
        }
    }

    public void recycle() {

        deltaTime = 0f;
        maps = null;
        nMaps = 0;

        side = 0f;
        marginX = 0f;
        marginY = 0f;
        mapsX = 0f;
        mapsY = 0f;

        controllerCount = 0;
        keyboardCount = 0;
        touchCount = 0;
    }

    @Override
    public void show() {

        super.show();

        nMaps = 2;

        if(Gdx.app.getType() == ApplicationType.Android)
            nMaps = 1;

        maps = new Array();
        Map m;

        game.input.targetsClear();

        calcSideSize();

        for(int i = 0; i < nMaps; i++) {
            m = new Map(this);
            alignMap(m, i);
            game.input.addTarget(m);
            maps.add(m);
            m.name = "Map" + i;
        }

        game.input.linkAll();

        // Todo ai.init() should be called after game.input.linkAll()
        // #debugCode
        //maps.get(0).ai = new Ai3();
        //maps.get(0).ai.init(maps.get(0));
        if(maps.size > 1) {
            maps.get(1).ai = new Ai3();
            maps.get(1).ai.init(maps.get(1));
        }
        //maps.get(1).debugShape(5);

        sr = new ShapeRenderer();
        sr.setColor(Color.WHITE);

        // #debugCode
        server = new GameServer();
        server.mapScreen = this;
        server.init();
    }

    @Override
    public void render(float delta) {

        super.render(delta);

        update();

        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        for(Map m : maps) {
            m.renderShapes();
        }

        sr.end();

        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();

        for(Map m : maps) {
            m.renderBatch();
        }

        game.batch.end();
    }

    public void update() {

        boolean multiPlayer = maps.size > 1;

        if(multiPlayer) {

            int mapsNotDone = 0;
            int mapsNotOverDone = 0;
            Map mapNotDone = null;

            for(Map m : maps) {

                if(!m.state.getCurrentState().equals(Map.MState.OVER) &&
                        !m.state.getCurrentState().equals(Map.MState.DONE)) {
                    mapsNotOverDone++;
                    mapNotDone = m;
                }

                if(!m.state.getCurrentState().equals(Map.MState.DONE)) {
                    mapsNotDone++;
                }
            }

            // All maps but one are in OVER or DONE state
            if(mapsNotOverDone == 1) {
                mapNotDone.prop.gameWin = true;
            }

            // If all maps are in DONE state and autoRestart is on
            // restart the game
            if(autoRestart && mapsNotDone == 0) {
                for(Map m : maps) {
                    m.recycle();
                    m.state.changeState(Map.MState.FREE_FALL);
                }
            }
        }
        else if(autoRestart) {

            Map m = maps.first();

            if(m.state.getCurrentState().equals(Map.MState.DONE)) {
                m.recycle();
                m.state.changeState(Map.MState.FREE_FALL);
            }
        }
    }

    @Override
    public void resize(int width, int height) {

        super.resize(width, height);

        // #debugCode
        Gdx.app.log(this.getClass().getSimpleName(), " w = " + width + "; h = " + height);

        calcSideSize();

        for(int i = 0; i < maps.size; i++) {
            alignMap(maps.get(i), i);
        }
    }

    @Override
    public void pause() {

        for(Map m : maps) {
            m.prop.pause = true;
        }
        saveState();
    }

    @Override
    public void resume() {
        loadState();
    }

    @Override
    public void dispose() {
        super.dispose();
        sr.dispose();
    }
}
