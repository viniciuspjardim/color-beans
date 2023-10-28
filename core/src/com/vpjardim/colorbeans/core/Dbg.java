package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.vpjardim.colorbeans.Block;

/** #debugCode */
public class Dbg {
    public boolean on = false;

    // Used to change game speed
    public static final int DELTA_REAL = 1;
    public static final int DELTA_0_02X = 2;
    public static final int DELTA_0_1X = 3;
    public static final int DELTA_0_25X = 4;
    public static final int DELTA_0_5X = 5;
    public static final int DELTA_1X = 6;
    public static final int DELTA_2X = 7;
    public static final int DELTA_4X = 8;
    public static final int DELTA_8X = 9;

    public int delta;
    public boolean input;
    public boolean fps;
    public boolean fpsText;
    public boolean lagWarn;
    public boolean fpsStat;
    /**
     * LOG_NONE = []; LOG_ERROR [error]; LOG_INFO [error, log]; LOG_DEBUG [error,
     * log, debug]
     */
    public int logLevel;
    public boolean uiTable;
    public int[] mapShapes;
    public boolean[] clearMaps;
    public int campEnd;
    public boolean aiPlayerCamp;
    public boolean aiDisableMap1;
    public int[] aiTraining;
    public int selectedColor;

    public Dbg() {
        off();
    }

    public void off() {
        on = false;

        delta = DELTA_REAL;
        input = false;
        fps = false;
        fpsText = false;
        lagWarn = false;
        fpsStat = false;
        logLevel = Application.LOG_NONE;
        mapShapes = null;
        clearMaps = null;
        uiTable = false;
        campEnd = 11;
        aiPlayerCamp = false;
        aiDisableMap1 = false;
        aiTraining = null;
        selectedColor = Block.CLR_A;

        Gdx.app.setLogLevel(logLevel);
    }

    public void on() {
        on = true;
        Gdx.app.setLogLevel(logLevel);
    }

    public static void print(String str) {
        System.out.println(str);
    }

    public static void err(String tag, String str) {
        Gdx.app.error(tag, str);
    }

    public static void inf(String tag, String str) {
        Gdx.app.log(tag, str);
    }

    public static void dbg(String tag, String str) {
        Gdx.app.debug(tag, str);
    }

    /** Tag only class name */
    public static String tag(Object o) {
        return o.getClass().getSimpleName();
    }

    /** Tag class # object name */
    public static String tagO(Object o) {
        return o.getClass().getSimpleName() + "#" + o;
    }
}
