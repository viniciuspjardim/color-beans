/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.animation.CamAccessor;
import com.vpjardim.colorbeans.core.MapManager;
import com.vpjardim.colorbeans.core.MapRender;
import com.vpjardim.colorbeans.core.ScoreTable;
import com.vpjardim.colorbeans.input.TouchInput2;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public class PlayScreen extends ScreenBase {

    public static final int ACT_MENU     = 10;
    public static final int ACT_CREDITS  = 11;

    public MapManager manager;

    private OrthographicCamera menuCam;
    private Viewport menuViewport;
    private Stage stage;
    private Table table;
    private Color hlColor = new Color(0x2a4350ff);
    private TweenManager transition;

    public PlayScreen(MapManager man) {
        manageInput = false;
        manager = man;
    }

    @Override
    public void show() {

        super.show();

        bgColor = new Color(0x101010ff);

        transition = new TweenManager();
        Tween.registerAccessor(OrthographicCamera.class, new CamAccessor());

        Tween.set(cam, CamAccessor.POSITION).target(G.width / 1.5f, G.height / 2f).start(transition);
        Tween.to(cam, CamAccessor.POSITION, 1.75f).target(G.width / 2f, G.height / 2f).start(transition);

        Tween.set(cam, CamAccessor.ZOOM).target(1.2f).start(transition);
        Tween.to(cam, CamAccessor.ZOOM, 1.75f).target(1f).start(transition);

        menuCam = new OrthographicCamera();
        menuViewport = new ScreenViewport(menuCam);
        viewport.apply(true);
        stage = new Stage(menuViewport, G.game.batch);
        G.game.input.addProcessor(stage);

        table = new Table(G.game.skin);
        table.setFillParent(true);

        G.game.assets.get("audio/music1.ogg", Music.class).setVolume(0.4f);
        G.game.assets.get("audio/music1.ogg", Music.class).setLooping(true);
        G.game.assets.get("audio/music1.ogg", Music.class).play();

        TextButton resumeButt, menuButt;

        resumeButt = new TextButton("Resume",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        resumeButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.pause(0, false);
            }
        });

        menuButt = new TextButton("Menu",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));
        menuButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_MENU;
            }
        });

        float bttW = G.style.buttWidth;
        float padM = G.style.padMedium;

        table.add(resumeButt).width(bttW).pad(padM);
        table.row();
        table.add(menuButt).width(bttW).pad(G.style.padVBig, padM, padM, padM);

        stage.addActor(table);
        table.setDebug(G.game.dbg.uiTable); // #debugCode

        manager.init();
    }

    @Override
    public void render(float delta) {

        super.render(delta);
        transition.update(delta);

        manager.winLost();

        TouchInput2 input = null;

        G.game.sr.setProjectionMatrix(cam.combined);
        G.game.sr.begin(ShapeRenderer.ShapeType.Filled);
        for(MapRender r : manager.render) {
            r.m.update();
            if(r.m.input instanceof TouchInput2) {

                input = (TouchInput2) r.m.input;
                if(input.draw) {
                    G.game.sr.setColor(hlColor);
                    // Draw map highlight
                    G.game.sr.rect(
                            input.moveCurr * r.size + r.px, r.py,
                            r.size, r.m.N_ROW * -r.size);
                }
            }
        }
        G.game.sr.end();

        G.game.batch.setProjectionMatrix(cam.combined);
        G.game.batch.begin();
        for(MapRender r : manager.render) {
            r.renderBatch();
        }
        G.game.batch.end();

        stage.act(delta);
        stage.draw();

        if(manager.pauseStatus == MapManager.PAUSED_ALL)
            table.setVisible(true);
        else
            table.setVisible(false);

        if(input != null && input.draw) {

            G.game.sr.setProjectionMatrix(cam.combined);
            G.game.sr.setAutoShapeType(true);
            G.game.sr.begin(ShapeRenderer.ShapeType.Filled);

            float yShift = 80;
            float x = input.div[input.moveCurr];
            float dx = input.div[input.moveCurr + 1] - x;

            G.game.sr.setColor(Color.ORANGE);
            // Draw subdivision highlight
            G.game.sr.rect(
                    x, G.height - input.touchY + yShift,
                    dx, 150);

            G.game.sr.set(ShapeRenderer.ShapeType.Line);

            G.game.sr.setColor(Color.RED);
            // Draw input subdivisions
            for(int i = 0; i < input.div.length -1; i++) {
                x = input.div[i];
                dx = input.div[i + 1] - x;
                G.game.sr.rect(
                        x, G.height - input.touchY + yShift,
                        dx, 150);
            }

            G.game.sr.set(ShapeRenderer.ShapeType.Filled);
            G.game.sr.setColor(Color.WHITE);

            // Draw touch triangle
            G.game.sr.triangle(
                    input.touchX + input.dTouchX, G.height - input.touchY + 1.3f * yShift,
                    input.touchX + input.dTouchX - 50, G.height - input.touchY - 15,
                    input.touchX + input.dTouchX + 50, G.height - input.touchY - 15);

            G.game.sr.end();
        }

        if(manager.gameStatus == MapManager.GAME_ZEROED)
            action = ACT_CREDITS;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        manager.resize();
        menuViewport.update(width, height, true);
    }

    @Override
    public void pause() {
        manager.pause(0, true);
    }

    @Override
    public void hide() {
        ScoreTable.save(G.game.score);
        G.game.assets.get("audio/music1.ogg", Music.class).stop();
    }

    @Override
    public void dispose() {
        super.dispose();
        G.game.input.removeProcessor(stage);
        // Only dispose what does not come from game.assets. Do not dispose skin.
        stage.dispose();
    }
}
