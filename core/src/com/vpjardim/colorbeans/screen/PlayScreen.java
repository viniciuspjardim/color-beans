package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.animation.CamAccessor;
import com.vpjardim.colorbeans.core.Audio;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.core.MapManager;
import com.vpjardim.colorbeans.core.MapRender;
import com.vpjardim.colorbeans.core.ScoreTable;
import com.vpjardim.colorbeans.defaults.Db;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;
import com.vpjardim.colorbeans.input.TouchInput;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

public class PlayScreen extends ScreenBase {
    public static final int ACT_MENU = 10;
    public static final int ACT_CREDITS = 11;

    public final MapManager manager;
    /** Camera for the maps, in world units where a bean is 1 x 1. See {@link MapRender} */
    private OrthographicCamera worldCam;
    private ExtendViewport worldViewport;
    private Table containerT;
    private boolean menuVisible;
    private TweenManager transition;
    private EventListener tapInput;
    private TouchInput touchInput;

    public PlayScreen(MapManager man) {
        manageInput = false;
        manager = man;
    }

    @Override
    public void show() {
        super.show();

        // Special key event
        specialKeyDown = (Event e) -> {
            int key = (Integer) e.getAttribute();

            if (G.isBackKey(key)) {
                Dbg.dbg("BackKey", "SpecialButtons.keyDown: " + key);
                menuVisible = true;
                manager.togglePaused();
            }
        };

        // Tap event
        tapInput = (Event e) -> {
            if (G.game.dbg.on) {
                menuVisible = false;
            } else if (manager.isPaused()) {
                menuVisible = !menuVisible;
            }
        };

        EventHandler.get().addListener("SpecialButtons.keyDown", specialKeyDown);
        EventHandler.get().addListener("TapInput.tap", tapInput);

        containerT = new Table(G.game.skin);
        containerT.setFillParent(true);

        Table contentT = new Table(G.game.skin);
        contentT.background("bgBlack");
        contentT.pad(54f, 48f, 54f, 48f);
        contentT.setBackground("bgBlack");

        containerT.add(contentT);
        menuVisible = true;

        G.game.audio.configMusic(Audio.MUSIC1, true, true);
        G.game.audio.playMusic();

        TextButton resumeButt, menuButt;

        resumeButt = new TextButton("Resume",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        resumeButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.setPaused(false);
                menuVisible = true;
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

        contentT.add(resumeButt).width(bttW).pad(padM);
        contentT.row();
        contentT.add(menuButt).width(bttW).pad(G.style.padVBig, padM, padM, padM);

        stage.addActor(containerT);
        containerT.setDebug(G.game.dbg.uiTable);

        manager.init();

        // Maps side by side. The viewport always fits them all and shows more of the
        // background on screens with a different aspect ratio
        int maps = manager.render.size;
        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(MapRender.SLOT_W * maps + 1f, MapRender.VIEW_H, worldCam);
        // Sets the visible world size, used by the intro below
        worldViewport.update(G.width, G.height, false);

        for (int i = 0; i < maps; i++) {
            MapRender r = manager.render.get(i);
            r.x = MapRender.PANEL_W + MapRender.SLOT_W * i;
            r.viewport = worldViewport;
        }

        // Intro: the camera slides in from the right while zooming in, ending centered
        // on the maps
        float centerX = worldViewport.getMinWorldWidth() / 2f;
        float centerY = Map.N_ROW / 2f;

        transition = new TweenManager();
        Tween.registerAccessor(OrthographicCamera.class, new CamAccessor());

        Tween.set(worldCam, CamAccessor.POSITION)
                .target(centerX + worldViewport.getWorldWidth() / 6f, centerY).start(transition);
        Tween.to(worldCam, CamAccessor.POSITION, 1.75f).target(centerX, centerY).start(transition);

        Tween.set(worldCam, CamAccessor.ZOOM).target(1.2f).start(transition);
        Tween.to(worldCam, CamAccessor.ZOOM, 1.75f).target(1f).start(transition);

        for (MapRender r : manager.render) {
            if (r.m.input instanceof TouchInput) {
                touchInput = (TouchInput) r.m.input;
                break;
            }
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        transition.update(delta);
        manager.winLost();

        for (MapRender r : manager.render) {
            r.m.update();
        }

        // Draw background
        G.game.batch.setProjectionMatrix(cam.combined);
        G.game.batch.begin();
        G.game.bgBeans.render();
        G.game.batch.end();

        // Draw the maps
        worldViewport.apply();
        G.game.batch.setProjectionMatrix(worldCam.combined);
        G.game.batch.begin();

        for (MapRender r : manager.render) {
            r.renderBg();
        }

        for (MapRender r : manager.render) {
            r.render();
        }

        G.game.batch.end();

        // Back to pixels for the menu and the touch input
        viewport.apply();
        stage.act(delta);
        stage.draw();

        containerT.setVisible(manager.isPaused() && menuVisible);

        // If it has a TouchInput draw the box and the arrow of the input
        if (touchInput != null && touchInput.draw) {
            G.game.sr.setProjectionMatrix(cam.combined);
            G.game.sr.setAutoShapeType(true);
            G.game.sr.begin(ShapeRenderer.ShapeType.Filled);

            float yShift = 80;
            float x = touchInput.div[touchInput.moveCurr];
            float dx = touchInput.div[touchInput.moveCurr + 1] - x;

            G.game.sr.setColor(Color.ORANGE);
            // Draw subdivision highlight
            G.game.sr.rect(
                    x, G.height - touchInput.touchY + yShift,
                    dx, 150);

            G.game.sr.set(ShapeRenderer.ShapeType.Line);

            G.game.sr.setColor(Color.LIGHT_GRAY);
            // Draw input subdivisions
            for (int i = 0; i < touchInput.div.length - 1; i++) {
                x = touchInput.div[i];
                dx = touchInput.div[i + 1] - x;
                G.game.sr.rect(
                        x, G.height - touchInput.touchY + yShift,
                        dx, 150);
            }

            G.game.sr.set(ShapeRenderer.ShapeType.Filled);
            G.game.sr.setColor(Color.WHITE);

            // Draw touch triangle
            G.game.sr.triangle(
                    touchInput.touchX + touchInput.dTouchX, G.height - touchInput.touchY + 1.3f * yShift,
                    touchInput.touchX + touchInput.dTouchX - 50, G.height - touchInput.touchY - 15,
                    touchInput.touchX + touchInput.dTouchX + 50, G.height - touchInput.touchY - 15);

            G.game.sr.end();
        }

        if (manager.gameStatus == MapManager.GAME_ZEROED)
            action = ACT_CREDITS;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // Don't center the camera: its position is set by the intro tween
        worldViewport.update(width, height, false);
    }

    @Override
    public void pause() {
        menuVisible = true;
        manager.setPaused(true);
    }

    @Override
    public void hide() {
        ScoreTable.save(G.game.score);
        Db.save(G.game.data);
        G.game.assets.get("audio/music1.ogg", Music.class).stop();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (manager.render != null) {
            for (MapRender r : manager.render) {
                r.dispose();
            }
        }

        EventHandler.get().removeListener("TapInput.tap", tapInput);
    }
}
