/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
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
 * 2016/09/02
 */
public class PlayScreen extends ScreenBase {

    public static final int ACT_MENU     = 10;
    public static final int ACT_CREDITS  = 11;

    public MapManager manager;

    private FrameBuffer fb;
    private Sprite bgSprite;
    private OrthographicCamera menuCam;
    private Viewport menuViewport;
    private Stage stage;
    private Table table;
    private Color hlColor = new Color(0x2a4350ff);
    private TweenManager transition;

    private TouchInput2 touchInput2;

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

        for(MapRender r : manager.render) {
            if(r.m.input instanceof TouchInput2) {
                touchInput2 = (TouchInput2) r.m.input;
                break;
            }
        }
    }

    @Override
    public void render(float delta) {

        super.render(delta);
        transition.update(delta);

        manager.winLost();

        for(MapRender r : manager.render) {
            r.m.update();
        }

        G.game.batch.setProjectionMatrix(cam.combined);
        G.game.batch.begin();

        // Draw cached background
        bgSprite.draw(G.game.batch);
        // Draw beans and other stuff
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

        // If it has a TouchInput2 draw the box and the arrow of the input
        if(touchInput2 != null && touchInput2.draw) {

            G.game.sr.setProjectionMatrix(cam.combined);
            G.game.sr.setAutoShapeType(true);
            G.game.sr.begin(ShapeRenderer.ShapeType.Filled);

            float yShift = 80;
            float x = touchInput2.div[touchInput2.moveCurr];
            float dx = touchInput2.div[touchInput2.moveCurr + 1] - x;

            G.game.sr.setColor(Color.ORANGE);
            // Draw subdivision highlight
            G.game.sr.rect(
                    x, G.height - touchInput2.touchY + yShift,
                    dx, 150);

            G.game.sr.set(ShapeRenderer.ShapeType.Line);

            G.game.sr.setColor(Color.RED);
            // Draw input subdivisions
            for(int i = 0; i < touchInput2.div.length -1; i++) {
                x = touchInput2.div[i];
                dx = touchInput2.div[i + 1] - x;
                G.game.sr.rect(
                        x, G.height - touchInput2.touchY + yShift,
                        dx, 150);
            }

            G.game.sr.set(ShapeRenderer.ShapeType.Filled);
            G.game.sr.setColor(Color.WHITE);

            // Draw touch triangle
            G.game.sr.triangle(
                    touchInput2.touchX + touchInput2.dTouchX, G.height - touchInput2.touchY + 1.3f * yShift,
                    touchInput2.touchX + touchInput2.dTouchX - 50, G.height - touchInput2.touchY - 15,
                    touchInput2.touchX + touchInput2.dTouchX + 50, G.height - touchInput2.touchY - 15);

            G.game.sr.end();
        }

        if(manager.gameStatus == MapManager.GAME_ZEROED)
            action = ACT_CREDITS;
    }

    @Override
    public void resize(int width, int height) {

        super.resize(width, height);

        menuViewport.update(width, height, true);
        manager.resize();

        updateCache();
    }

    /**
     * Caches the game background to avoid loss of frame rate (especially in Android). The
     * background is drawn only when the screen is resized
     */
    public void updateCache() {

        // When the app is minimized the size of the window is 0x0 px and FrameBuffer would throw an
        // exception if it's constructed. Because of this the cache is not updated
        if(G.width == 0 || G.height == 0) return;

        G.game.batch.setProjectionMatrix(cam.combined);

        // Dispose because framebuffer and sprite will be recreated
        if(bgSprite != null) bgSprite.getTexture().dispose();
        if(fb != null) fb.dispose();

        // Create a framebuffer with the new size
        GLFrameBuffer.FrameBufferBuilder fbb = new GLFrameBuffer.FrameBufferBuilder(
                G.width, G.height);

        fbb.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
        fb = fbb.build();

        fb.begin();

        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        G.game.batch.begin();
        for(MapRender r : manager.render) {
            r.cacheBg();
        }
        G.game.batch.end();

        fb.end();

        bgSprite = new Sprite(fb.getColorBufferTexture());
        bgSprite.flip(false, true);
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
        if(bgSprite != null) bgSprite.getTexture().dispose();
        if(fb != null) fb.dispose();
        G.game.input.removeProcessor(stage);
        // Only dispose what does not come from game.assets. Do not dispose skin.
        stage.dispose();
    }
}
