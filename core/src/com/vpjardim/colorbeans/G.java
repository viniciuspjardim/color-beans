/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.core.ScoreTable;
import com.vpjardim.colorbeans.defaults.Style;
import com.vpjardim.colorbeans.input.InputManager;
import com.vpjardim.colorbeans.screen.ScreenManager;

import java.text.NumberFormat;

/**
 * Game class
 *
 * libGDX coordinate system
 *
 * Touch
 *    0
 *   --------------
 * 0 |x  >  .  .  .|
 *   |V  .  .  .  .|
 *   |.  .  .  .  .|
 *   |.  .  .  .  .| H
 *                W [x = 5, y = 4]
 *
 * Render
 *                W
 *   -------------- [x = 5, y = 4]
 *   |.  .  .  .  .| H
 *   |.  .  .  .  .|
 *   |A  .  .  .  .|
 * 0 |x  >  .  .  .|
 *    0
 *
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class G extends Game {

    // Todo small memory leak even in debugPG variant. Update Proguard File to remove log methods

    // Game resolution
    public static final int RES_SMALL  = 1;
    public static final int RES_MEDIUM = 2;

    public static G game;
    public static float delta;
    public static float scale;
    public static int res;
    public static int width;
    public static int height;
    public static boolean loading;
    public static Style style;

    public Array<String> players; // Todo create a player class
    public ScreenManager screens;
    public InputManager input;
    public AssetManager assets;
    public SpriteBatch batch;
    public ShapeRenderer sr;
    public TextureAtlas atlas;
    public Skin skin;
    public ScoreTable score;
    public NumberFormat intFmt;

    // #debugCode
    public Dbg dbg;

    @Override
    public void create() {

        // Most things are loaded in the LoadingScreen class. See explanation there.

        game = (G)Gdx.app.getApplicationListener();

        G.width = Gdx.graphics.getWidth();
        G.height = Gdx.graphics.getHeight();
        G.style = new Style();

        dbg = new Dbg();
        // #debugCode
        // dbg.uiTable = true;
        // dbg.map0shape = 3;
        // dbg.campStart = 7;
        // dbg.campEnd = 7;
        // dbg.delta = Dbg.DELTA_SLOW;
        // dbg.fps = true;
        // dbg.logLevel = Application.LOG_INFO;
        // dbg.on();

        players = new Array<>();
        screens = new ScreenManager();
        screens.create();
    }

    @Override
    public void render() { screens.render(); }

    @Override
    public void resume() {
        super.resume();
        game = (G)Gdx.app.getApplicationListener();
    }

    @Override
    public void resize (int width, int height) {
        G.width = width;
        G.height = height;
        if(screen != null) screen.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
        batch.dispose();
        sr.dispose();
        atlas.dispose();
        // Skin is disposed when assets is disposed
    }
}