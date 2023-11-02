package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.vpjardim.colorbeans.Block;

/** #debugCode */
public class Dbg {
    public int tapsCount;
    public boolean on;
    public float delta;
    public boolean input;
    public boolean fps;
    public boolean fpsText;
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
        tapsCount = 0;
        on = false;
        delta = -1f;
        input = false;
        fps = false;
        fpsText = false;
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

    public void setLogLevel(int level) {
        logLevel = level;
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
