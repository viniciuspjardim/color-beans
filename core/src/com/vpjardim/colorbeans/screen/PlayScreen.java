/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vpjardim.colorbeans.G;
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

/**
 * @author Vinícius Jardim
 *         2016/09/02
 */
public class PlayScreen extends ScreenBase {
    public static final int ACT_MENU = 10;
    public static final int ACT_CREDITS = 11;

    public MapManager manager;
    private FrameBuffer fb;
    private Sprite bgSprite;
    private OrthographicCamera menuCam;
    private Viewport menuViewport;
    private Table table;
    private boolean menuVisible;
    private TweenManager transition;
    // # debugCode
    private EventListener debugInput;

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
                manager.togglePaused();
                menuVisible = true;
            }
        };

        // Tap event
        debugInput = (Event e) -> {
            if (G.game.dbg.on) {
                menuVisible = false;
            } else {
                menuVisible = !menuVisible;
            }
        };

        EventHandler.get().addListener("SpecialButtons.keyDown", specialKeyDown);
        EventHandler.get().addListener("DebugInput.tap", debugInput);

        transition = new TweenManager();
        Tween.registerAccessor(OrthographicCamera.class, new CamAccessor());

        Tween.set(cam, CamAccessor.POSITION).target(G.width / 1.5f, G.height / 2f).start(transition);
        Tween.to(cam, CamAccessor.POSITION, 1.75f).target(G.width / 2f, G.height / 2f).start(transition);

        Tween.set(cam, CamAccessor.ZOOM).target(1.2f).start(transition);
        Tween.to(cam, CamAccessor.ZOOM, 1.75f).target(1f).start(transition);

        menuCam = new OrthographicCamera();
        menuViewport = new ScreenViewport(menuCam);
        viewport.apply(true);

        table = new Table(G.game.skin);
        table.setFillParent(true);
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

        table.add(resumeButt).width(bttW).pad(padM);
        table.row();
        table.add(menuButt).width(bttW).pad(G.style.padVBig, padM, padM, padM);

        stage.addActor(table);
        table.setDebug(G.game.dbg.uiTable); // #debugCode

        manager.init();

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
        G.game.bgBeans.update();

        manager.winLost();

        for (MapRender r : manager.render) {
            r.m.update();
        }

        // Draw background
        G.game.batch.setProjectionMatrix(menuCam.combined);
        G.game.batch.begin();
        G.game.bgBeans.render();
        G.game.batch.end();

        G.game.batch.setProjectionMatrix(cam.combined);
        G.game.batch.begin();

        // Draw cached background
        bgSprite.draw(G.game.batch);

        // Draw beans and other stuff
        for (MapRender r : manager.render) {
            r.renderBatch();
        }

        G.game.batch.end();

        stage.act(delta);
        stage.draw();

        table.setVisible(manager.isPaused() && menuVisible);

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

            G.game.sr.setColor(Color.RED);
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

        menuViewport.update(width, height, true);
        manager.resize();
        G.game.bgBeans.resize();

        updateCache();
    }

    /**
     * Caches the game background to avoid loss of frame rate (especially in
     * Android). The background is drawn only when the screen is resized
     */
    public void updateCache() {
        // When the app is minimized the size of the window is 0x0 px and FrameBuffer
        // would throw an exception if it's constructed. Because of this the cache is
        // not updated
        if (G.width == 0 || G.height == 0)
            return;

        G.game.batch.setProjectionMatrix(cam.combined);

        // Dispose because framebuffer and sprite will be recreated
        if (bgSprite != null)
            bgSprite.getTexture().dispose();
        if (fb != null)
            fb.dispose();

        // Create a framebuffer with the new size
        GLFrameBuffer.FrameBufferBuilder fbb = new GLFrameBuffer.FrameBufferBuilder(
                G.width, G.height);

        fbb.addColorTextureAttachment(GL20.GL_RGBA, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE);
        fb = fbb.build();

        fb.begin();

        Gdx.gl.glClearColor(0f, 0f, 0.125f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        G.game.batch.begin();
        for (MapRender r : manager.render) {
            r.cacheBg();
        }
        G.game.batch.end();

        fb.end();

        bgSprite = new Sprite(fb.getColorBufferTexture());
        bgSprite.flip(false, true);
    }

    @Override
    public void pause() {
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

        if (bgSprite != null)
            bgSprite.getTexture().dispose();

        if (fb != null)
            fb.dispose();

        if (manager.render != null) {
            for (MapRender r : manager.render) {
                r.dispose();
            }
        }

        EventHandler.get().removeListener("DebugInput.tap", debugInput);
    }
}
