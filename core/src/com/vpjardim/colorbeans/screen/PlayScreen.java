/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

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
import com.vpjardim.colorbeans.core.MapManager;
import com.vpjardim.colorbeans.core.MapRender;
import com.vpjardim.colorbeans.core.ScoreTable;

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

    public PlayScreen(MapManager man) {
        manageInput = false;
        manager = man;
    }

    @Override
    public void show() {

        super.show();

        menuCam = new OrthographicCamera();
        menuViewport = new ScreenViewport(menuCam);
        viewport.apply(false);
        stage = new Stage(menuViewport, G.game.batch);
        G.game.input.addProcessor(stage);

        Table table = new Table(G.game.skin);
        table.setBounds(0, 0, G.width, G.height);

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

        manager.winLost();

        G.game.sr.setProjectionMatrix(cam.combined);
        G.game.sr.begin(ShapeRenderer.ShapeType.Filled);
        for(MapRender r : manager.render) {
            r.m.update();
            r.renderShapes();
        }
        G.game.sr.end();

        G.game.batch.setProjectionMatrix(cam.combined);
        G.game.batch.begin();
        for(MapRender r : manager.render) {
            r.renderBatch();
        }
        G.game.batch.end();

        if(manager.pauseStatus == MapManager.PAUSED_ALL) {
            stage.act(delta);
            stage.draw();
        }

        if(manager.gameStatus == MapManager.GAME_ZEROED)
            action = ACT_CREDITS;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        manager.resize();
        menuViewport.update(width, height, false);
    }

    @Override
    public void pause() {
        manager.pause(0, true);
    }

    @Override
    public void hide() { ScoreTable.save(G.game.score); }

    @Override
    public void dispose() {
        super.dispose();
        G.game.input.removeProcessor(stage);
        // Only dispose what does not come from game.assets. Do not dispose skin.
        stage.dispose();
    }
}
