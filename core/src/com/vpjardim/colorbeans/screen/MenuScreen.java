package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.defaults.Style;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;

public class MenuScreen extends ScreenBase {
    public static final int ACT_PLAY = 10;
    public static final int ACT_TRAINING = 11;
    public static final int ACT_SCORE = 12;
    public static final int ACT_CONFIG = 13;

    /** Part of the window's height the menu may take at most. It can take the whole width */
    private static final float MAX_FILL = 0.9f;

    private Stack menu;
    private ScreenViewport menuViewport;

    @Override
    public void show() {
        super.show();

        // Browser pages can't close themselves and iOS apps must not quit, so there's no exit
        Application.ApplicationType appType = Gdx.app.getType();
        final boolean canExit = appType != Application.ApplicationType.WebGL
                && appType != Application.ApplicationType.iOS;

        specialKeyDown = (Event e) -> {
            int key = (Integer) e.getAttribute();

            if (canExit && G.isBackKey(key)) {
                Gdx.app.exit();
            }
        };

        EventHandler.get().addListener("SpecialButtons.keyDown", specialKeyDown);

        Table outerT = new Table(G.game.skin);
        Table panelT = new Table(G.game.skin);
        Table ribbonT = new Table(G.game.skin);

        outerT.setFillParent(true);
        panelT.setBackground("bgYellow");
        ribbonT.setBackground("game/ribbon_red");

        TextButton playButt, trainingButt, scoreButt, optionsButt, exitButt;

        playButt = new TextButton("Play!",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        playButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_PLAY;
            }
        });

        trainingButt = new TextButton("Training",
                G.game.skin.get("bttBlue", TextButton.TextButtonStyle.class));
        trainingButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_TRAINING;
            }
        });

        scoreButt = new TextButton("Score Board",
                G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));
        scoreButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_SCORE;
            }
        });

        optionsButt = new TextButton("Options...",
                G.game.skin.get("bttGray", TextButton.TextButtonStyle.class));
        optionsButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_CONFIG;
            }
        });

        exitButt = new TextButton("Exit",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));
        exitButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        float bttW = G.style.buttWidth;
        float padS = G.style.padSmall;

        panelT.defaults().width(bttW).pad(padS);

        // Room for the ribbon, which is drawn over the top of the panel
        panelT.add().width(G.style.ribbonWidth).height(G.style.ribbonHeight * 0.85f);
        panelT.row();
        panelT.add(playButt);
        panelT.row();
        panelT.add(trainingButt);
        panelT.row();
        panelT.add(scoreButt);
        panelT.row();
        panelT.add(optionsButt);
        panelT.row();

        nav.row(playButt).row(trainingButt).row(scoreButt).row(optionsButt);

        if (canExit) {
            panelT.add(exitButt);
            nav.row(exitButt);
        }

        // The ribbon's padding centers the title on its front face
        ribbonT.add(new Image(G.game.skin, "game/title"));

        // The ribbon is wider than the panel and hangs over both sides of it
        Table panelLayer = new Table();
        panelLayer.add(panelT).padLeft(G.style.ribbonOverhang).padRight(G.style.ribbonOverhang);

        Table ribbonLayer = new Table();
        ribbonLayer.top();
        ribbonLayer.add(ribbonT).growX().height(G.style.ribbonHeight).padTop(G.style.padVBig);

        menu = new Stack(panelLayer, ribbonLayer);
        outerT.add(menu);

        // The background beans stay on the pixel camera, so the menu gets its own viewport
        // that resize() can zoom
        menuViewport = new ScreenViewport();
        stage.setViewport(menuViewport);
        stage.addActor(outerT);
        outerT.setDebug(G.game.dbg.uiTable, true);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        G.game.batch.setProjectionMatrix(cam.combined);
        G.game.batch.begin();
        G.game.bgBeans.render();
        G.game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (width <= 0 || height <= 0)
            return;

        // Fonts and images were sized for the window at startup. Zoom so the menu keeps its
        // size relative to the window, and zoom out more when it would still not fit. On
        // phones it doesn't, and the ribbon's ends reach the window's sides
        float zoom = Style.uiScaleFor(width, height) / G.scale;
        float fit = Math.min(width / menu.getPrefWidth(), MAX_FILL * height / menu.getPrefHeight());

        menuViewport.setUnitsPerPixel(1f / Math.min(zoom, fit));
        menuViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
