/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vpjardim.colorbeans.animation.MenuBeans;
import com.vpjardim.colorbeans.core.Audio;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.core.ScoreTable;
import com.vpjardim.colorbeans.defaults.Db;
import com.vpjardim.colorbeans.defaults.Style;
import com.vpjardim.colorbeans.input.InputManager;
import com.vpjardim.colorbeans.screen.ScreenBase;
import com.vpjardim.colorbeans.screen.ScreenManager;

import aurelienribon.tweenengine.Tween;

/**
 * Game class
 * 
 * <pre>
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
 * </pre>
 *
 * @author Vinícius Jardim
 *         2015/03/21
 */
public class G extends Game {
    public static G game;
    public static float delta;
    public static float scale;
    public static int width;
    public static int height;
    public static boolean loading;
    public static Style style;

    public Db data;
    public ScreenManager screens;
    public InputManager input;
    public AssetManager assets;
    public SpriteBatch batch;
    public ShapeRenderer sr;
    public TextureAtlas atlas;
    public Skin skin;
    public ScoreTable score;
    public Audio audio;
    public MenuBeans beansAnim;

    // #debugCode
    public Dbg dbg;

    @Override
    public void create() {
        // Most things are loaded in the LoadingScreen class. See explanation there.

        game = (G) Gdx.app.getApplicationListener();
        Gdx.input.setCatchBackKey(true);
        Tween.setCombinedAttributesLimit(5);

        G.width = Gdx.graphics.getWidth();
        G.height = Gdx.graphics.getHeight();
        G.style = new Style();

        beansAnim = new MenuBeans();

        dbg = new Dbg();

        // #debugCode
        // dbg.uiTable = true;
        // dbg.mapShapes = new int[] {3, 0, 0, 0};
        // dbg.clearMaps = new boolean[] { true, false, false, false };
        // dbg.campEnd = 9;
        // dbg.delta = Dbg.DELTA_0_5X;
        // dbg.fps = true;
        // dbg.fpsStat = true;
        // dbg.logLevel = Application.LOG_DEBUG;
        // dbg.aiPlayerCamp = true;
        // dbg.aiDisableMap1 = true;
        // dbg.aiTraining = new int[] {0, 3, 3, 3};
        // dbg.on();

        screens = new ScreenManager();
        screens.create();
        ScreenBase.bgColor = Db.bgColor();
    }

    @Override
    public void render() {
        screens.render();
    }

    @Override
    public void resume() {
        super.resume();
        game = (G) Gdx.app.getApplicationListener();
    }

    @Override
    public void resize(int width, int height) {
        G.width = width;
        G.height = height;

        if (screen != null)
            screen.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
        batch.dispose();
        sr.dispose();
        atlas.dispose();
        // The skin is disposed when assets are disposed
    }
}
