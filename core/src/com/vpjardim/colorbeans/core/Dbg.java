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
    public boolean lagWarn;
    /** LOG_NONE = []; LOG_ERROR [error]; LOG_INFO [error, log]; LOG_DEBUG [error, log, debug] */
    public int logLevel;
    public boolean uiTable;
    public int map0shape;
    public int map1shape;
    public int campLevels;

    public Dbg() { off(); }

    public void off() {

        delta     = DELTA_REAL;
        fps       = false;
        lagWarn   = false;
        logLevel  = Application.LOG_NONE;
        uiTable   = false;
        map0shape = 0;
        map1shape = 0;
        campLevels = Integer.MAX_VALUE;

        Gdx.app.setLogLevel(logLevel);
    }

    public void on() { Gdx.app.setLogLevel(logLevel); }

    public static void print(String str) {
        System.out.println(str);
    }
}
