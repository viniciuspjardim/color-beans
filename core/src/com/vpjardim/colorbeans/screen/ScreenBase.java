/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vpjardim.colorbeans.GameClass;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * @author Vinícius Jardim
 * 05/12/2015
 */
public class ScreenBase implements Screen, TargetBase {

    protected GameClass game;
    protected int status;
    protected boolean isFinished;
    protected OrthographicCamera cam;
    protected Viewport viewport;
    protected boolean manageInput = true;

    public float time;
    public float deltaTime;

    public ScreenBase() {
        this.game = GameClass.get();
        isFinished = false;
        time = 0f;
    }

    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void show() {
        cam = new OrthographicCamera();
        viewport = new ScreenViewport(cam);
        viewport.apply(false);

        if(manageInput) {
            game.input.targetsClear();
            game.input.addTarget(this);
            game.input.linkAll();
        }
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        time += delta;
        deltaTime = delta;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        game.input.targetsClear();
    }

    @Override
    public void setInput(InputBase input) {}

    @Override
    public void button1(boolean isDown) {}

    @Override
    public void button2(boolean isDown) {}

    @Override
    public void button3(boolean isDown) {}

    @Override
    public void button4(boolean isDown) {}

    @Override
    public void buttonStart(boolean isDown) {}
}