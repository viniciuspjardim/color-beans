/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.defaults;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.input.Profile;

/**
 * The default data and the preferences values used in the game.
 *
 * The transient fields are default values, they can't be edited in the options
 * screen or in the config file. The non transient fields are the user
 * preferences and are going to be persisted in a JSON file.
 *
 * @author Vinícius Jardim
 *         2016/09/02
 */
public class Db {
    // Initialize when the application starts
    public static Color[] screenBgColor = {
            new Color(0x20000000), // Dark red
            new Color(0x000A0000), // Dark green
            new Color(0x00002000), // Dark blue
            new Color(0x10001000), // Dark purple
    };

    // Initialized after loading screen finished loading

    public transient IntMap<TextureAtlas.AtlasRegion> BEANS_REG = new IntMap<>();
    public transient BitmapFont font1;
    public transient BitmapFont font2;
    public transient BitmapFont font3;

    // Initialized inside the constructor ->

    public final transient IntMap<String> COLORS = new IntMap<>();
    public final transient int[] ri;

    public final transient Cfg.Game campGame = new Cfg.Game();
    public final transient Cfg.Game loopGame = new Cfg.Game();
    public final transient Cfg.Game trainingGame = new Cfg.Game();

    public final transient Cfg.Map map1 = new Cfg.Map();
    public final transient Cfg.Map map2 = new Cfg.Map();
    public final transient Cfg.Map map3 = new Cfg.Map();
    public final transient Cfg.Map map4 = new Cfg.Map();
    public final transient Cfg.Map map5 = new Cfg.Map();
    public final transient Cfg.Map map6 = new Cfg.Map();
    public final transient Cfg.Map map7 = new Cfg.Map();
    public final transient Cfg.Map map8 = new Cfg.Map();
    public final transient Cfg.Map map9 = new Cfg.Map();
    public final transient Cfg.Ai ai1 = new Cfg.Ai();
    public final transient Cfg.Ai ai2 = new Cfg.Ai();
    public final transient Cfg.Ai ai3 = new Cfg.Ai();
    public final transient Cfg.Ai ai4 = new Cfg.Ai();
    public final transient Cfg.Ai ai5 = new Cfg.Ai();
    public final transient Cfg.Ai ai6 = new Cfg.Ai();
    public final transient Cfg.Ai ai7 = new Cfg.Ai();
    public final transient Cfg.Ai ai8 = new Cfg.Ai();
    public final transient Cfg.Ai ai9 = new Cfg.Ai();

    /**
     * Chain power from 1 to 24+. Ref: https://puyonexus.com/wiki/Chain_Power_Table
     */
    public final transient int[] chainPowerTable = {
            0, 8, 16, 32, 64, 128, 256, 512, 999 // Puyo Puyo
            // 0, 8, 16, 32, 64, 96, 128, 160, // Puyo Puyo Tsu
            // 192, 224, 256, 288, 320, 352,
            // 384, 416, 448, 480, 512, 544,
            // 576, 608, 640, 672
    };

    /** Color bonus from 1 to 8. Ref: https://puyonexus.com/wiki/Scoring */
    public final transient int[] colorBonusTable = {
            0, 3, 6, 12, 24 // classic
            // 0, 2, 4, 8, 16, 20, 24, 28 // fever
    };

    /** Group bonus from 4 to 11+. Ref: https://puyonexus.com/wiki/Scoring */
    public final transient int[] groupBonusTable = {
            0, 2, 3, 4, 5, 6, 7, 10 // classic
            // 0, 1, 2, 3, 4, 5, 6, 8 // fever
    };

    // Non 'final transient' fields ->

    public Array<Cfg.Player> players = new Array<>();

    public int campaignCurrentStage = 1;

    public int trainingSpeed = 1;

    /**
     * The keyboard profiles are used to enable 2 or more players play in the same
     * keyboard
     */
    public Array<Profile> kbProfs = new Array<>();

    /**
     * The controller profiles are used to enable 2 or more players in controllers
     * with different keys configuration. For example, controllers of different
     * manufactures
     */
    public Array<Profile> ctrlProfs = new Array<>();

    /** Init constants (default values) */
    public Db() {
        COLORS.put(1, "beans/red");
        COLORS.put(2, "beans/blue");
        COLORS.put(3, "beans/green");
        COLORS.put(4, "beans/yellow");
        COLORS.put(5, "beans/purple");
        COLORS.put(6, "beans/dblue");
        COLORS.put(7, "beans/orange");
        COLORS.put(8, "beans/magenta");
        COLORS.put(9, "beans/transparent");

        ri = new int[] {
                0, 1, 2, 3, 4, 10, 11, 100, 101, 110, 111, 1000, 1001, 1010, 1011, 1100, 1101, 1110,
                1111 };

        campGame.net = Cfg.Game.NET_LOCAL;
        campGame.difficulty = Cfg.Game.DIFFICULTY_NORMAL;
        campGame.nPlayers = 2;
        campGame.nContinues = 3;
        campGame.pauseAct = Cfg.Game.PAUSE_ALL;
        campGame.finishOnWin = true;
        campGame.lostAct = Cfg.Game.LOST_AUTO_RESTART;

        loopGame.net = Cfg.Game.NET_LOCAL;
        loopGame.difficulty = Cfg.Game.DIFFICULTY_NORMAL;
        loopGame.nPlayers = 2;
        loopGame.nContinues = 3;
        loopGame.pauseAct = Cfg.Game.PAUSE_ALL;
        loopGame.finishOnWin = false;
        loopGame.lostAct = Cfg.Game.LOST_AUTO_RESTART;

        trainingGame.net = Cfg.Game.NET_LOCAL;
        trainingGame.difficulty = Cfg.Game.DIFFICULTY_NORMAL;
        trainingGame.nPlayers = 1;
        trainingGame.nContinues = -1;
        trainingGame.pauseAct = Cfg.Game.PAUSE_ALL;
        trainingGame.finishOnWin = false;
        trainingGame.lostAct = Cfg.Game.LOST_RESTART_PAUSED;

        // Camping maps player fall speed
        map1.moveTime = new float[] { 0, 0.80f, 15, 0.70f, 15, 0.60f, 15, 0.50f, 15, 0.44f };
        map2.moveTime = new float[] { 0, 0.70f, 15, 0.60f, 15, 0.50f, 15, 0.44f, 15, 0.38f };
        map3.moveTime = new float[] { 0, 0.50f, 15, 0.44f, 15, 0.38f, 15, 0.32f, 15, 0.28f };
        map4.moveTime = new float[] { 0, 0.40f, 15, 0.35f, 15, 0.30f, 15, 0.26f, 15, 0.22f };
        map5.moveTime = new float[] { 0, 0.30f, 15, 0.26f, 15, 0.22f, 15, 0.18f, 15, 0.16f };
        map6.moveTime = new float[] { 0, 0.22f, 15, 0.18f, 15, 0.16f, 15, 0.14f, 15, 0.12f };
        map7.moveTime = new float[] { 0, 0.16f, 15, 0.14f, 15, 0.12f, 15, 0.11f, 15, 0.10f };
        map8.moveTime = new float[] { 0, 0.12f, 15, 0.11f, 15, 0.10f, 15, 0.09f, 15, 0.08f };
        map9.moveTime = new float[] { 0, 0.11f, 15, 0.10f, 15, 0.09f, 15, 0.08f, 15, 0.07f };

        // Ai used on campaign stages
        ai1.downKeyMin = 0.0f;
        ai1.downKeyMax = 0.0f;
        ai1.doubtMin = 0.7f;
        ai1.doubtMax = 0.9f;
        ai1.doubtFreqMin = 0.12f;
        ai1.doubtFreqMax = 0.18f;
        ai1.randomness = 0.8f;
        ai1.trashMoves = 0.8f;

        ai2.downKeyMin = 0.0f;
        ai2.downKeyMax = 0.1f;
        ai2.doubtMin = 0.5f;
        ai2.doubtMax = 0.7f;
        ai2.doubtFreqMin = 0.12f;
        ai2.doubtFreqMax = 0.18f;
        ai2.randomness = 0.75f;
        ai2.trashMoves = 0.7f;

        ai3.downKeyMin = 0.0f;
        ai3.downKeyMax = 0.2f;
        ai3.doubtMin = 0.4f;
        ai3.doubtMax = 0.6f;
        ai3.doubtFreqMin = 0.1f;
        ai3.doubtFreqMax = 0.16f;
        ai3.randomness = 0.55f;
        ai3.trashMoves = 0.6f;

        ai4.downKeyMin = 0.2f;
        ai4.downKeyMax = 0.4f;
        ai4.doubtMin = 0.3f;
        ai4.doubtMax = 0.6f;
        ai4.doubtFreqMin = 0.08f;
        ai4.doubtFreqMax = 0.14f;
        ai4.randomness = 0.35f;
        ai4.trashMoves = 0.4f;

        ai5.downKeyMin = 0.2f;
        ai5.downKeyMax = 0.5f;
        ai5.doubtMin = 0.1f;
        ai5.doubtMax = 0.4f;
        ai5.doubtFreqMin = 0.06f;
        ai5.doubtFreqMax = 0.14f;
        ai5.randomness = 0.22f;
        ai5.trashMoves = 0.2f;

        ai6.downKeyMin = 0.4f;
        ai6.downKeyMax = 0.8f;
        ai6.doubtMin = 0.1f;
        ai6.doubtMax = 0.3f;
        ai6.doubtFreqMin = 0.1f;
        ai6.doubtFreqMax = 0.14f;
        ai6.randomness = 0.15f;
        ai6.trashMoves = 0.1f;

        ai7.downKeyMin = 0.6f;
        ai7.downKeyMax = 0.9f;
        ai7.doubtMin = 0f;
        ai7.doubtMax = 0.2f;
        ai7.doubtFreqMin = 0.1f;
        ai7.doubtFreqMax = 0.18f;
        ai7.randomness = 0.08f;
        ai7.trashMoves = 0.05f;

        ai8.downKeyMin = 0.8f;
        ai8.downKeyMax = 1f;
        ai8.doubtMin = 0f;
        ai8.doubtMax = 0.1f;
        ai8.doubtFreqMin = 0f;
        ai8.doubtFreqMax = 0.1f;
        ai8.randomness = 0f;
        ai8.trashMoves = 0f;

        ai9.downKeyMin = 1f;
        ai9.downKeyMax = 1f;
        ai9.doubtMin = 0f;
        ai9.doubtMax = 0f;
        ai9.doubtFreqMin = 0f;
        ai9.doubtFreqMax = 0f;
        ai9.randomness = 0f;
        ai9.trashMoves = 0f;
    }

    /**
     * Init default values of the variables. When there is a cfg file this defaults
     * can be overwritten
     */
    public void initPreferences() {
        players.add(new Cfg.Player("Player"));

        // Keyboard key profiles

        Profile p;

        p = new Profile();
        kbProfs.add(p);
        p.up = Keys.W;
        p.right = Keys.D;
        p.down = Keys.S;
        p.left = Keys.A;
        p.start = Keys.SPACE;
        p.button1 = Keys.G;
        p.button2 = Keys.H;
        p.button3 = Keys.B;
        p.button4 = Keys.V;

        p = new Profile();
        kbProfs.add(p);
        p.up = Keys.UP;
        p.right = Keys.RIGHT;
        p.down = Keys.DOWN;
        p.left = Keys.LEFT;
        p.start = Keys.NUMPAD_0;
        p.button1 = Keys.NUMPAD_5;
        p.button2 = Keys.NUMPAD_6;
        p.button3 = Keys.NUMPAD_2;
        p.button4 = Keys.NUMPAD_1;

        // Controller key profiles

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            p = new Profile();
            ctrlProfs.add(p);
            p.up = 19;
            p.right = 22;
            p.down = 20;
            p.left = 21;
            p.start = 108;
            p.button1 = 100;
            p.button2 = 97;
            p.button3 = 96;
            p.button4 = 99;

            p = new Profile();
            ctrlProfs.add(p);
            p.up = 19;
            p.right = 22;
            p.down = 20;
            p.left = 21;
            p.start = 108;
            p.button1 = 100;
            p.button2 = 97;
            p.button3 = 96;
            p.button4 = 99;
        } else if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
            p = new Profile();
            ctrlProfs.add(p);
            p.up = 12;
            p.right = 15;
            p.down = 13;
            p.left = 14;
            p.start = 9;
            p.button1 = 3;
            p.button2 = 1;
            p.button3 = 0;
            p.button4 = 2;

            p = new Profile();
            ctrlProfs.add(p);
            p.up = 12;
            p.right = 15;
            p.down = 13;
            p.left = 14;
            p.start = 9;
            p.button1 = 3;
            p.button2 = 1;
            p.button3 = 0;
            p.button4 = 2;
        } else {
            p = new Profile();
            ctrlProfs.add(p);
            p.up = 11;
            p.right = 14;
            p.down = 12;
            p.left = 13;
            p.start = 6;
            p.button1 = 3;
            p.button2 = 1;
            p.button3 = 0;
            p.button4 = 2;

            p = new Profile();
            ctrlProfs.add(p);
            p.up = 11;
            p.right = 14;
            p.down = 12;
            p.left = 13;
            p.start = 6;
            p.button1 = 3;
            p.button2 = 1;
            p.button3 = 0;
            p.button4 = 2;
        }
    }

    /**
     * Variables that cannot be initialized in the constructor because depends on
     * loading screen
     */
    public void initAfterLoading() {

        font1 = G.game.assets.get("dimbo_white.ttf", BitmapFont.class);
        font2 = G.game.assets.get("roboto_shadow.ttf", BitmapFont.class);
        font3 = G.game.assets.get("roboto.ttf", BitmapFont.class);

        for (int i = 0; i < ri.length; i++) {
            BEANS_REG.put(1 * 10000 + ri[i], G.game.atlas.findRegion("beans/red", ri[i]));
            BEANS_REG.put(2 * 10000 + ri[i], G.game.atlas.findRegion("beans/blue", ri[i]));
            BEANS_REG.put(3 * 10000 + ri[i], G.game.atlas.findRegion("beans/green", ri[i]));
            BEANS_REG.put(4 * 10000 + ri[i], G.game.atlas.findRegion("beans/yellow", ri[i]));
            BEANS_REG.put(5 * 10000 + ri[i], G.game.atlas.findRegion("beans/purple", ri[i]));
            BEANS_REG.put(6 * 10000 + ri[i], G.game.atlas.findRegion("beans/dblue", ri[i]));
            BEANS_REG.put(7 * 10000 + ri[i], G.game.atlas.findRegion("beans/orange", ri[i]));
            BEANS_REG.put(8 * 10000 + ri[i], G.game.atlas.findRegion("beans/magenta", ri[i]));
            BEANS_REG.put(9 * 10000 + ri[i], G.game.atlas.findRegion("beans/transparent", ri[i]));
        }
    }

    public int getChainPower(int val) {
        val--;
        if (val < 0)
            return 0;
        if (val >= chainPowerTable.length)
            return chainPowerTable[chainPowerTable.length - 1];
        return chainPowerTable[val];
    }

    public int getColorBonus(int val) {
        val--;
        if (val < 0)
            return 0;
        if (val >= colorBonusTable.length)
            return colorBonusTable[colorBonusTable.length - 1];
        return colorBonusTable[val];
    }

    public int getGroupBonus(int val) {
        val -= 4;
        if (val < 0)
            return 0;
        if (val >= groupBonusTable.length)
            return groupBonusTable[groupBonusTable.length - 1];
        return groupBonusTable[val];
    }

    public static boolean save(Db data) {
        boolean isLocAvailable = Gdx.files.isLocalStorageAvailable();

        if (!isLocAvailable)
            return true;

        try {
            Json json = new Json();
            json.setUsePrototypes(false);

            String jsonTxt = json.prettyPrint(data);
            FileHandle file = Gdx.files.local("state/cfg.json");
            file.writeString(jsonTxt, false);

            return true;
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Db load() {
        Db data;
        boolean isLocAvailable = Gdx.files.isLocalStorageAvailable();

        if (!isLocAvailable) {
            data = new Db();
            data.initPreferences();

            return data;
        }

        FileHandle file = Gdx.files.local("state/cfg.json");

        if (file.exists()) {
            String jsonTxt = file.readString();
            Json json = new Json();
            data = json.fromJson(Db.class, jsonTxt);
        } else {
            data = new Db();
            data.initPreferences();
        }

        return data;
    }

    public static Color bgColor() {
        return screenBgColor[MathUtils.random(0, screenBgColor.length - 1)];
    }
}
