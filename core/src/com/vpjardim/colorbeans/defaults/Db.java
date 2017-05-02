/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.defaults;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.input.Profile;

/**
 * The default data and the preferences values used in the game.
 *
 * The transient fields are default values, they can't be edited in the options screen or in the
 * config file. The non transient fields are the user preferences and are going to be persisted in a
 * JSON file.
 *
 * @author Vinícius Jardim
 * 02/09/2016
 */
public class Db {

    // Todo add function to save and load this properties in a file

    public final transient Color[]  screenBgColor;

    public final transient Cfg.Game campGame     = new Cfg.Game();
    public final transient Cfg.Game loopGame     = new Cfg.Game();
    public final transient Cfg.Game trainingGame = new Cfg.Game();

    public final transient Cfg.Map map1 = new Cfg.Map();
    public final transient Cfg.Map map2 = new Cfg.Map();
    public final transient Cfg.Map map3 = new Cfg.Map();
    public final transient Cfg.Map map4 = new Cfg.Map();
    public final transient Cfg.Map map5 = new Cfg.Map();
    public final transient Cfg.Map map6 = new Cfg.Map();
    public final transient Cfg.Map map7 = new Cfg.Map();

    public final transient Cfg.Map mapT1 = new Cfg.Map();
    public final transient Cfg.Map mapT2 = new Cfg.Map();

    public final transient Cfg.Ai ai1 = new Cfg.Ai();
    public final transient Cfg.Ai ai2 = new Cfg.Ai();
    public final transient Cfg.Ai ai3 = new Cfg.Ai();
    public final transient Cfg.Ai ai4 = new Cfg.Ai();
    public final transient Cfg.Ai ai5 = new Cfg.Ai();
    public final transient Cfg.Ai ai6 = new Cfg.Ai();
    public final transient Cfg.Ai ai7 = new Cfg.Ai();

    // Non final transient fields ->

    public Array<Cfg.Player> players = new Array<>();

    /** The keyboard profiles are used to enable 2 or more players play in the same keyboard */
    public Profile[] kbProfs = new Profile[] { new Profile(),  new Profile(), };

    /**
     * The controller profiles are used to enable 2 or more players in controllers with different
     * keys configuration. For example, controllers of different manufactures
     */
    public Profile[] ctrlProfs = new Profile[] { new Profile(),  new Profile(), };

    public Cfg.Map mapT  = null;

    /** Init constants (default values) */
    public Db() {

        screenBgColor = new Color[] {
                new Color(0x20000000), // Dark red
                new Color(0x000A0000), // Dark green
                new Color(0x00002000), // Dark blue
                new Color(0x10001000), // Dark purple
        };

        campGame.net         = Cfg.Game.NET_LOCAL;
        campGame.difficulty  = Cfg.Game.DIFFICULTY_NORMAL;
        campGame.nPlayers    = 2;
        campGame.nContinues  = 3;
        campGame.pauseAct    = Cfg.Game.PAUSE_ALL;
        campGame.finishOnWin = true;
        campGame.lostAct     = Cfg.Game.LOST_AUTO_RESTART;

        loopGame.net         = Cfg.Game.NET_LOCAL;
        loopGame.difficulty  = Cfg.Game.DIFFICULTY_NORMAL;
        loopGame.nPlayers    = 2;
        loopGame.nContinues  = 3;
        loopGame.pauseAct    = Cfg.Game.PAUSE_ALL;
        loopGame.finishOnWin = false;
        loopGame.lostAct     = Cfg.Game.LOST_AUTO_RESTART;

        trainingGame.net         = Cfg.Game.NET_LOCAL;
        trainingGame.difficulty  = Cfg.Game.DIFFICULTY_NORMAL;
        trainingGame.nPlayers    = 1;
        trainingGame.nContinues  = -1;
        trainingGame.pauseAct    = Cfg.Game.PAUSE_ALL;
        trainingGame.finishOnWin = false;
        trainingGame.lostAct     = Cfg.Game.LOST_RESTART_PAUSED;

        // Camping maps play fall speed
        map1.moveTime = new float[] {0, 0.70f, 15, 0.60f, 15, 0.50f, 15, 0.44f, 15, 0.38f};
        map2.moveTime = new float[] {0, 0.50f, 15, 0.44f, 15, 0.38f, 15, 0.32f, 15, 0.28f};
        map3.moveTime = new float[] {0, 0.40f, 15, 0.35f, 15, 0.30f, 15, 0.26f, 15, 0.22f};
        map4.moveTime = new float[] {0, 0.30f, 15, 0.26f, 15, 0.22f, 15, 0.18f, 15, 0.16f};
        map5.moveTime = new float[] {0, 0.22f, 15, 0.18f, 15, 0.16f, 15, 0.14f, 15, 0.12f};
        map6.moveTime = new float[] {0, 0.16f, 15, 0.14f, 15, 0.12f, 15, 0.11f, 15, 0.10f};
        map7.moveTime = new float[] {0, 0.12f, 15, 0.11f, 15, 0.10f, 15, 0.09f, 15, 0.08f};

        // Training mode play fall speed default options
        mapT1.moveTime = new float[] {0, 0.30f, 15, 0.26f, 15, 0.22f, 15, 0.18f, 15, 0.16f};
        mapT2.moveTime = new float[] {
                0, 0.50f, 15, 0.40f, 15, 0.32f, 15, 0.16f,
                15, 0.32f, 15, 0.24f, 15, 0.18f, 15, 0.12f,
                15, 0.18f, 15, 0.16f, 15, 0.14f, 15, 0.10f,
                15, 0.14f, 15, 0.12f, 15, 0.11f, 15, 0.08f,
                15, 0.11f, 15, 0.10f, 15, 0.09f, 15, 0.07f,
                15, 0.09f,
        };

        // Ai used on campaign stages

        ai1.downKeyMin   = 0.0f;
        ai1.downKeyMax   = 0.1f;
        ai1.doubtMin     = 0.5f;
        ai1.doubtMax     = 0.7f;
        ai1.doubtFreqMin = 0.12f;
        ai1.doubtFreqMax = 0.18f;
        ai1.randomness   = 0.75f;
        ai1.trashMoves   = 0.5f;

        ai2.downKeyMin   = 0.0f;
        ai2.downKeyMax   = 0.2f;
        ai2.doubtMin     = 0.4f;
        ai2.doubtMax     = 0.6f;
        ai2.doubtFreqMin = 0.1f;
        ai2.doubtFreqMax = 0.16f;
        ai2.randomness   = 0.55f;
        ai2.trashMoves   = 0.3f;

        ai3.downKeyMin   = 0.2f;
        ai3.downKeyMax   = 0.4f;
        ai3.doubtMin     = 0.3f;
        ai3.doubtMax     = 0.6f;
        ai3.doubtFreqMin = 0.08f;
        ai3.doubtFreqMax = 0.14f;
        ai3.randomness   = 0.35f;
        ai3.trashMoves   = 0.2f;

        ai4.downKeyMin   = 0.2f;
        ai4.downKeyMax   = 0.5f;
        ai4.doubtMin     = 0.1f;
        ai4.doubtMax     = 0.4f;
        ai4.doubtFreqMin = 0.06f;
        ai4.doubtFreqMax = 0.14f;
        ai4.randomness   = 0.22f;
        ai4.trashMoves   = 0.1f;

        ai5.downKeyMin   = 0.4f;
        ai5.downKeyMax   = 0.8f;
        ai5.doubtMin     = 0.1f;
        ai5.doubtMax     = 0.3f;
        ai5.doubtFreqMin = 0.1f;
        ai5.doubtFreqMax = 0.14f;
        ai5.randomness   = 0.15f;
        ai5.trashMoves   = 0.05f;

        ai6.downKeyMin   = 0.6f;
        ai6.downKeyMax   = 0.9f;
        ai6.doubtMin     = 0f;
        ai6.doubtMax     = 0.2f;
        ai6.doubtFreqMin = 0.1f;
        ai6.doubtFreqMax = 0.18f;
        ai6.randomness   = 0.08f;
        ai6.trashMoves   = 0.02f;

        ai7.downKeyMin   = 1f;
        ai7.downKeyMax   = 1f;
        ai7.doubtMin     = 0f;
        ai7.doubtMax     = 0f;
        ai7.doubtFreqMin = 0f;
        ai7.doubtFreqMax = 0f;
        ai7.randomness   = 0f;
        ai7.trashMoves   = 0f;
    }

    public void initPreferences() {

        players.add(new Cfg.Player("Player"));

        // Keyboard key profiles

        kbProfs[0].start   = Keys.SPACE;
        kbProfs[0].up      = Keys.W;
        kbProfs[0].right   = Keys.D;
        kbProfs[0].down    = Keys.S;
        kbProfs[0].left    = Keys.A;
        kbProfs[0].button1 = Profile.UNDEFINED;
        kbProfs[0].button2 = Keys.G;
        kbProfs[0].button3 = Keys.V;
        kbProfs[0].button4 = Profile.UNDEFINED;

        kbProfs[1].start   = Keys.NUMPAD_0;
        kbProfs[1].up      = Keys.UP;
        kbProfs[1].right   = Keys.RIGHT;
        kbProfs[1].down    = Keys.DOWN;
        kbProfs[1].left    = Keys.LEFT;
        kbProfs[1].button1 = Profile.UNDEFINED;
        kbProfs[1].button2 = Keys.NUMPAD_6;
        kbProfs[1].button3 = Keys.NUMPAD_2;
        kbProfs[1].button4 = Profile.UNDEFINED;

        // Controller key profiles

        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            ctrlProfs[0].start   = 197;
            ctrlProfs[0].button1 = 188;
            ctrlProfs[0].button2 = 189;
            ctrlProfs[0].button3 = 190;
            ctrlProfs[0].button4 = 191;

            ctrlProfs[1].start   = 197;
            ctrlProfs[1].button1 = 188;
            ctrlProfs[1].button2 = 189;
            ctrlProfs[1].button3 = 190;
            ctrlProfs[1].button4 = 191;
        }
        else {
            ctrlProfs[0].start   = 9;
            ctrlProfs[0].button1 = 0;
            ctrlProfs[0].button2 = 1;
            ctrlProfs[0].button3 = 2;
            ctrlProfs[0].button4 = 3;

            ctrlProfs[1].start   = 9;
            ctrlProfs[1].button1 = 0;
            ctrlProfs[1].button2 = 1;
            ctrlProfs[1].button3 = 2;
            ctrlProfs[1].button4 = 3;
        }

        // Current training mode play fall speed
        mapT = mapT1;
    }

    public static boolean save(Db data) {

        try {
            Json json = new Json();

            String jsonTxt = json.prettyPrint(data);
            FileHandle file = Gdx.files.local("state/cfg.json");
            file.writeString(jsonTxt, false);
            return true;
        }
        catch(GdxRuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Db load() {

        FileHandle file = Gdx.files.local("state/cfg.json");
        Db data;

        if(file.exists()) {
            String jsonTxt = file.readString();
            Json json = new Json();
            data = json.fromJson(Db.class, jsonTxt);
        }
        else {
            data = new Db();
            data.initPreferences();
        }

        return data;
    }

    public Color bgColor() {
        return screenBgColor[MathUtils.random(0, screenBgColor.length -1)];
    }
}
