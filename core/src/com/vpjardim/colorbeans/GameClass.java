/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.vpjardim.colorbeans.input.InputManager;
import com.vpjardim.colorbeans.screen.ScreenManager;

/**
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class GameClass extends Game {

    public static final int DELTA_REAL   = 1;
    public static final int DELTA_SLOW   = 2;
    public static final int DELTA_NORMAL = 3;
    public static final int DELTA_FAST   = 4;

    public ScreenManager screens;
    public InputManager input;
    public AssetManager assets;
    public TextureAtlas atlas;
    public SpriteBatch batch;

    public int deltaTime;
    public boolean fpsDebug;
	public boolean lagWarn;

	@Override
	public void create() {

        // Most things are loaded in the LoadingScreen class. See explanation there.

		// LOG_NONE  = <nothing>
		// LOG_ERROR = error
		// LOG_INFO  = error, log
        // LOG_DEBUG = error, log, debug
		Gdx.app.setLogLevel(Application.LOG_NONE);

        deltaTime = DELTA_REAL;
        fpsDebug  = false;
        lagWarn   = false;

		screens = new ScreenManager();
        screens.create();
	}

	/** Return the only instance of GameClass */
    public static GameClass get() {
        return (GameClass)Gdx.app.getApplicationListener();
    }

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void render() { screens.render(); }

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		super.dispose();
        if(assets != null) assets.dispose();
        if(batch != null) batch.dispose();
	}
}