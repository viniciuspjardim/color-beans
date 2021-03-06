/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.core;

/**
 * @author Vinícius Jardim
 * 2016/09/02
 */
public class Cfg {

    public static class Game {

        public static final int NET_LOCAL           = 1;
        public static final int NET_SERVER          = 2;
        public static final int NET_CLIENT          = 3;

        public static final int DIFFICULTY_VEASY    = 1;
        public static final int DIFFICULTY_EASY     = 2;
        public static final int DIFFICULTY_NORMAL   = 3;
        public static final int DIFFICULTY_HARD     = 4;
        public static final int DIFFICULTY_VHARD    = 5;

        public static final int PAUSE_OFF           = 1;
        public static final int PAUSE_SELF          = 2;
        public static final int PAUSE_ALL           = 3;

        public static final int LOST_WAIT           = 1;
        public static final int LOST_RESTART_PAUSED = 2;
        public static final int LOST_AUTO_RESTART   = 3;

        public int net;
        public int difficulty;
        public int nPlayers;
        public int nContinues;
        public int pauseAct;
        public boolean finishOnWin;
        public int lostAct;
    }

    public static class Map {
        /** Pairs of change time and the new vertical moveTime (less is faster) */
        public float[] moveTime;
    }

    public static class Player {
        public String name;

        public Player() {}

        public Player(String name) { this.name = name; }
    }

    /**
     * AI configuration. With this params we can change the AI difficulty level. Although I wrote
     * "percent" or "percentage" in the fields doc, I mean 0 to 1 intervals. Which 1 would be 100%;
     * 0.5, 50% etc.
     */
    public static class Ai {

        /**
         * Minimum fall percent in which down key will be pressed. It happens in the end of the fall
         */
        public float downKeyMin;

        /**
         * Maximum fall percent in which down key will be pressed. It happens in the end of the fall
         */
        public float downKeyMax;

        /**
         * Minimum fall percent in which the AI will fake doubt (horizontal/rotations moves). It
         * happens in the beginning of the fall
         */
        public float doubtMin;

        /**
         * Maximum fall percent in which the AI will fake doubt (horizontal/rotations moves). It
         * happens in the beginning of the fall
         */
        public float doubtMax;

        /** Minimum fall percent in which the AI should choose another "doubt" move */
        public float doubtFreqMin;

        /** Maximum fall percent in which the AI should choose another "doubt" move */
        public float doubtFreqMax;

        /** Score random multiplier: 0.5 means 50% */
        public float randomness;

        /** Percentage of moves that should be random or almost random */
        public float trashMoves;
    }
}
