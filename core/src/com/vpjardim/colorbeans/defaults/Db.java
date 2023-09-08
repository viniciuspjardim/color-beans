package com.vpjardim.colorbeans.defaults;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.input.Profile;

/**
 * The default data and the preferences values used in the game.
 * <p>
 * The transient fields are default values, they can't be edited in the options
 * screen or in the config file. The non transient fields are the user
 * preferences and are going to be persisted in a JSON file.
 */
public class Db {
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

    /**
     * Chain power from 1 to 24+ <a href="https://puyonexus.com/wiki/Chain_Power_Table">Info</a>
     */
    public final transient int[] chainPowerTable = {
            0, 8, 16, 32, 64, 128, 256, 512, 999,
    };

    /** Color bonus from 1 to 8 <a href="https://puyonexus.com/wiki/Scoring">Info</a> */
    public final transient int[] colorBonusTable = {
            0, 3, 6, 12, 24,
    };

    /** Group bonus from 4 to 11+ <a href="https://puyonexus.com/wiki/Scoring">Info</a> */
    public final transient int[] groupBonusTable = {
            0, 2, 3, 4, 5, 6, 7, 10,
    };

    /** Player block map fall times, lower is faster */
    public final transient int[] fallTimes = {
            90, 80, 70, 62, 54, 48, 42, 36, 32, 28, 24, 20, 16, 12, 10, 8, 7, 6, 5, 4, 3
    };

    /** Stage Names */
    public final transient String[] stageNames = {
            "1. BunnyAlan", "2. ChickenBil", "3. LizardLoyd", "4. MadCow", "5. BlackCat",
            "6. EagleEye", "7. DemonDog", "8. RedStorm", "9. BlackLight", "10. DeepnessGod",
            "11. HellKeeper", "12. TheCreator",
    };

    /** AI move speed (down key), higher is harder */
    public final transient int[] downKeyMinAi = {
            0, 0, 0, 0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 100, 100,
    };

    /** AI move speed (down key), higher is harder */
    public final transient int[] downKeyMaxAi = {
            0, 0, 0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 100, 100, 100,
    };

    /** AI doubt, lower it fells more confident */
    public final transient int[] doubtMinAi = {
            90, 80, 60, 50, 40, 30, 20, 15, 10, 5, 3, 2, 1, 0, 0, 0,
    };

    /** AI doubt, lower it fells more confident */
    public final transient int[] doubtMaxAi = {
            100, 90, 80, 60, 50, 40, 30, 25, 20, 15, 10, 5, 2, 1, 0, 0,
    };

    /** AI doubt frequency, lower it fells more confident */
    public final transient int[] doubtFreqMinAi = {
            18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0,
    };

    /** AI doubt frequency, lower it fells more confident */
    public final transient int[] doubtFreqMaxAi = {
            22, 20, 18, 16, 14, 12, 10, 9, 8, 7, 6, 5, 4, 3, 0, 0,
    };

    /** Lower is harder */
    public final transient int[] randomnessAi = {
            90, 80, 70, 60, 52, 44, 36, 28, 22, 16, 10, 6, 4, 2, 0, 0,
    };

    /** Lower is harder */
    public final transient int[] trashMovesAi = {
            90, 80, 70, 60, 52, 44, 36, 28, 22, 16, 10, 6, 4, 2, 0, 0,
    };

    public final transient String[] difficultyNames = {
            "Very Easy", "Easy", "Normal", "Hard", "Very Hard",
    };

    // Non 'final transient' fields ->

    public final Array<Cfg.Player> players = new Array<>();

    public boolean coopCampaign = false;
    public int difficulty = 2; // 0 to 4
    public int campaignCurrentStage = 0; // 0 to 11
    public int trainingSpeed = 0; // 0 to 11
    public float musicVolume = 0.25f; // 0 to 1
    public float effectsVolume = 0.5f; // 0 to 1

    /**
     * The keyboard profiles are used to enable 2 or more players play in the same
     * keyboard
     */
    public final Array<Profile> kbProfs = new Array<>();

    /**
     * The controller profiles are used to enable 2 or more players in controllers
     * with different keys configuration. For example, controllers of different
     * manufactures
     */
    public final Array<Profile> ctrlProfs = new Array<>();

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

        ri = new int[]{
                0, 1, 2, 3, 4, 10, 11, 100, 101, 110, 111, 1000, 1001, 1010, 1011, 1100, 1101, 1110,
                1111,
        };

        campGame.nPlayers = 2;
        campGame.finishOnWin = true;
        campGame.lostAct = Cfg.Game.LOST_AUTO_RESTART;

        loopGame.nPlayers = 2;
        loopGame.finishOnWin = false;
        loopGame.lostAct = Cfg.Game.LOST_AUTO_RESTART;

        trainingGame.nPlayers = 1;
        trainingGame.finishOnWin = false;
        trainingGame.lostAct = Cfg.Game.LOST_RESTART_PAUSED;
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
            BEANS_REG.put(10000 + ri[i], G.game.atlas.findRegion("beans/red", ri[i]));
            BEANS_REG.put(2 * 10000 + ri[i], G.game.atlas.findRegion("beans/blue", ri[i]));
            BEANS_REG.put(3 * 10000 + ri[i], G.game.atlas.findRegion("beans/green", ri[i]));
            BEANS_REG.put(4 * 10000 + ri[i], G.game.atlas.findRegion("beans/yellow", ri[i]));
            BEANS_REG.put(5 * 10000 + ri[i], G.game.atlas.findRegion("beans/purple", ri[i]));
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

    public Cfg.Map createMapConfig(int stage) {
        int i = stage + difficulty;
        Cfg.Map mapCfg = new Cfg.Map();

        mapCfg.moveTime = new float[]{
                0, fallTimes[i] / 100f,
                15, fallTimes[i + 1] / 100f,
                15, fallTimes[i + 2] / 100f,
                15, fallTimes[i + 3] / 100f,
                15, fallTimes[i + 4] / 100f,
        };

        // #debugCode
        Dbg.dbg("createMapConfig", "stage: " + stage + "; moveTime: " +
                mapCfg.moveTime[1] + " to " + mapCfg.moveTime[9] + "; difficulty: " +
                difficulty);

        return mapCfg;
    }

    public Cfg.Ai createAiConfig(int stage) {
        int i = stage + difficulty;
        Cfg.Ai aiCfg = new Cfg.Ai();

        aiCfg.downKeyMin = downKeyMinAi[i] / 100f;
        aiCfg.downKeyMax = downKeyMaxAi[i] / 100f;
        aiCfg.doubtMin = doubtMinAi[i] / 100f;
        aiCfg.doubtMax = doubtMaxAi[i] / 100f;
        aiCfg.doubtFreqMin = doubtFreqMinAi[i] / 100f;
        aiCfg.doubtFreqMax = doubtFreqMaxAi[i] / 100f;
        aiCfg.randomness = randomnessAi[i] / 100f;
        aiCfg.trashMoves = trashMovesAi[i] / 100f;

        // #debugCode
        Dbg.dbg("createAiConfig ", "stage: " + stage + "; downKeyMin: " +
                aiCfg.downKeyMin + "; trashMoves: " + aiCfg.trashMoves + "; difficulty: " +
                difficulty);

        return aiCfg;
    }

    public static void save(Db data) {
        boolean isLocAvailable = Gdx.files.isLocalStorageAvailable();

        if (!isLocAvailable)
            return;

        try {
            Json json = new Json();
            json.setUsePrototypes(false);

            String jsonTxt = json.prettyPrint(data);
            FileHandle file = Gdx.files.local("state/cfg.json");
            file.writeString(jsonTxt, false);
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
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
}
