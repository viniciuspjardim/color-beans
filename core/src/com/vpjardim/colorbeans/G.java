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

    // TODO: android very laggy: 25fps with 2 maps, 17 with 4 maps;

    // TODO: small memory leak even in debugPG variant;

    // TODO: one map player blocks falls first then the others when if the game is
    // restarted. It happen to the loser map when the winner wins pressing down key;

    // TODO: black screen after minimizing and restoring on full screen mode;

    // TODO: restart music on map win, music fade in/out, add win/lost sound
    // effects, pause music on game paused, music2 volume seems lower then music2;

    // TODO: fix art: create text shade and text dialog balloon 9patch;

    // TODO: add win/lost camera transition and statistics layer;

    // TODO: fix game over animation: top blocks might render as if they were linked
    // to offscreen blocks;

    // TODO: "Next" text is rendered behind the next block bg;

    // TODO: do not render blocks in the out rows;

    // TODO: Android back button crashes app in LoadingScreen;

    // TODO: create a semi transparent black bg and to use in front of the beans in
    // MenuScreen.

    // TODO: negative score sometimes after first match in campaign (maybe fixed)

    // TODO: touch and drag in Android crashes app when using controller (fixed);

    // TODO: capture android back button event: go to menu when in PlayScreen
    // (done).

    // Game resolution
    public static final int RES_SMALL = 1;
    public static final int RES_MEDIUM = 2;

    public static G game;
    public static float delta;
    public static float scale;
    public static int res;
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
        // dbg.mapShape = new int[] {3, 0, 0, 0};
        // dbg.campStart = 9;
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

    /**
     * Convert Y down (0 = top left corner) to Y up (0 = bottom left corner) and
     * vice-versa. All the game logic should use Y down and this method should be
     * called when giving Y to a libGDX draw method.
     * 
     * @param y coordinate to be flipped
     * @return flipped coordinate
     */

    public static float flipY(float y, float height, float scale) {
        return G.height - 1 - y - (height * (scale + 1f) / 2f);
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
        // Skin is disposed when assets is disposed
    }
}
