/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public class Cfg {

    public static class Game {

        public static final int NET_LOCAL         = 1;
        public static final int NET_SERVER        = 2;
        public static final int NET_CLIENT        = 3;

        public static final int DIFFICULTY_VEASY  = 1;
        public static final int DIFFICULTY_EASY   = 2;
        public static final int DIFFICULTY_NORMAL = 3;
        public static final int DIFFICULTY_HARD   = 4;
        public static final int DIFFICULTY_VHARD  = 5;

        public static final int PAUSE_OFF         = 1;
        public static final int PAUSE_SELF        = 2;
        public static final int PAUSE_ALL         = 3;

        public static final int LOST_WAIT         = 1;
        public static final int LOST_RESTART      = 2;
        public static final int LOST_AUTO_RESTART = 3;

        public int net;
        public int difficulty;
        public int nPlayers;
        public int nContinues;
        public int pauseAct;
        public boolean finishOnWin;
        public int lostAct;
    }

    public static class Map {

        public int width;
        public int height;
        public boolean paused;
        public float speed;
    }

    public static class Ai {

        public int level;
        public float downKey;
        public float doubtness;
        public float randomness;
        public float nonBest;
    }
}
