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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vpjardim.colorbeans.input.InputManager;
import com.vpjardim.colorbeans.screen.ScreenManager;

/**
 * Game class
 *
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class G extends Game {

    public static final int DELTA_REAL   = 1;
    public static final int DELTA_SLOW   = 2;
    public static final int DELTA_NORMAL = 3;
    public static final int DELTA_FAST   = 4;

    public static float delta;
    public static G game;

	public ScreenManager screens;
    public InputManager input;
    public AssetManager assets;
    public TextureAtlas atlas;
    public SpriteBatch batch;
    public ShapeRenderer sr;

    public int deltaCfg;
    public boolean fpsDebug;
	public boolean lagWarn;

	@Override
	public void create() {

		game = (G)Gdx.app.getApplicationListener();

        // Most things are loaded in the LoadingScreen class. See explanation there.

		// LOG_NONE  = <nothing>
        // LOG_ERROR = error
        // LOG_INFO  = error, log
        // LOG_DEBUG = error, log, debug
		Gdx.app.setLogLevel(Application.LOG_NONE);

        deltaCfg = DELTA_REAL;
        fpsDebug = false;
        lagWarn  = false;

		screens = new ScreenManager();
        screens.create();
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
        game = (G)Gdx.app.getApplicationListener();
	}

	@Override
	public void dispose() {
		super.dispose();
        if(assets != null) assets.dispose();
        if(batch != null) batch.dispose();
        if(sr != null) sr.dispose();
	}
}