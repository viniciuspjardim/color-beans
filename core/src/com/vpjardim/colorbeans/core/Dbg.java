/*
 * Copyright 2017 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

/**
 * @author Vinícius Jardim
 * 06/01/2017
 *
 * #debugCode
 */
public class Dbg {

    // Used to change game speed
    public static final int DELTA_REAL   = 1;
    public static final int DELTA_SLOW   = 2;
    public static final int DELTA_NORMAL = 3;
    public static final int DELTA_FAST   = 4;

    public int delta;
    public boolean fps;
    public boolean fpsText;
    public boolean lagWarn;
    /** LOG_NONE = []; LOG_ERROR [error]; LOG_INFO [error, log]; LOG_DEBUG [error, log, debug] */
    public int logLevel;
    public boolean uiTable;
    public int map0shape;
    public int map1shape;
    public int campStart;
    public int campEnd;
    public boolean aiPlayerCamp;

    public Dbg() { off(); }

    public void off() {

        delta        = DELTA_REAL;
        fps          = false;
        fpsText      = false;
        lagWarn      = false;
        logLevel     = Application.LOG_NONE;
        uiTable      = false;
        map0shape    = 0;
        map1shape    = 0;
        campStart    = 0;
        campEnd      = Integer.MAX_VALUE;
        aiPlayerCamp = false;

        Gdx.app.setLogLevel(logLevel);
    }

    public void on() { Gdx.app.setLogLevel(logLevel); }

    public static void print(String str) { System.out.println(str); }

    public static void err(String tag, String str) { Gdx.app.error(tag, str); }

    public static void inf(String tag, String str) { Gdx.app.log(tag, str); }

    public static void dbg(String tag, String str) { Gdx.app.debug(tag, str); }

    public static String tag(Object o) { return tag(o, true); }

    public static String tag(Object o, boolean oName) {
        if(oName) return o.getClass().getSimpleName() + "#" + o;
        return o.getClass().getSimpleName();
    }
}
