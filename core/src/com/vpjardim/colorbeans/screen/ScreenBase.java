/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * @author Vinícius Jardim
 * 05/12/2015
 */
public class ScreenBase implements Screen, TargetBase {

    public static final int ACT_RUNNING = 1;
    public static final int ACT_NEXT    = 2;

    protected int action;
    protected OrthographicCamera cam;
    protected Viewport viewport;
    // Todo review usage of this field, when false it disable esc and print screen buttons
    protected boolean manageInput = true;

    protected Color bgColor;

    public float time;

    public ScreenBase() {
        action = ACT_RUNNING;
        time = 0f;
    }

    public boolean isFinished() {
        return action != ACT_RUNNING;
    }

    @Override
    public void show() {
        cam = new OrthographicCamera();
        viewport = new ScreenViewport(cam);
        viewport.apply(true);

        if(manageInput) {
            G.game.input.targetsClear();
            G.game.input.addTarget(this);
            G.game.input.linkAll();
        }

        bgColor = Color.BLACK;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        time += delta;
    }

    public void printScreen() {

        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(),
                Gdx.graphics.getBackBufferHeight(), true);

        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(),
                Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);

        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(Gdx.files.external("print.png"), pixmap);
        pixmap.dispose();
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height, true); }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() { G.game.input.targetsClear(); }

    @Override
    public void setInput(InputBase input) {}

    @Override
    public void keyDown(int key) {

        Dbg.inf(Dbg.tag(this), "key = " + key);

        if(key == G.game.data.escBt || key == Input.Keys.BACK) buttonEsc(true);

        else if(key == G.game.data.printScreenBt) buttonPrintScreen(true);
    }

    public void buttonEsc(boolean isDown) {}

    public void buttonPrintScreen(boolean isDown) { printScreen(); }

    @Override
    public void btStartDown() {}

    @Override
    public void bt1Down() {}

    @Override
    public void bt2Down() {}

    @Override
    public void bt3Down() {}

    @Override
    public void bt4Down() {}

    @Override
    public void keyUp(int key) {}

    @Override
    public void btStartUp() {}

    @Override
    public void bt1Up() {}

    @Override
    public void bt2Up() {}

    @Override
    public void bt3Up() {}

    @Override
    public void bt4Up() {}
}