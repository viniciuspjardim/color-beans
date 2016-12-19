/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.defaults;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.vpjardim.colorbeans.core.Cfg;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public class Db {

    public static final Color[] screenBgColor;
    public static final Color fontShadowColor = new Color(0x00000018);

    public static final Cfg.Game campGame     = new Cfg.Game();
    public static final Cfg.Game loopGame     = new Cfg.Game();
    public static final Cfg.Game trainingGame = new Cfg.Game();

    public static final Cfg.Map map7 = new Cfg.Map();
    public static final Cfg.Map map6 = new Cfg.Map();
    public static final Cfg.Map map5 = new Cfg.Map();
    public static final Cfg.Map map4 = new Cfg.Map();
    public static final Cfg.Map map3 = new Cfg.Map();
    public static final Cfg.Map map2 = new Cfg.Map();
    public static final Cfg.Map map1 = new Cfg.Map();


    public static final Cfg.Ai bestAi     = new Cfg.Ai();
    public static final Cfg.Ai mediumAi   = new Cfg.Ai();
    public static final Cfg.Ai worstAi    = new Cfg.Ai();

    public static final Cfg.Ai ai1 = new Cfg.Ai();
    public static final Cfg.Ai ai2 = new Cfg.Ai();
    public static final Cfg.Ai ai3 = new Cfg.Ai();
    public static final Cfg.Ai ai4 = new Cfg.Ai();
    public static final Cfg.Ai ai5 = new Cfg.Ai();
    public static final Cfg.Ai ai6 = new Cfg.Ai();
    public static final Cfg.Ai ai7 = new Cfg.Ai();

    static {

        screenBgColor = new Color[]{
                new Color(0x20000000), // Dark red
                new Color(0x000A0000), // Dark green
                new Color(0x00002000), // Dark blue
                new Color(0x10001000), // Dark purple
        };

        campGame.net             = Cfg.Game.NET_LOCAL;
        campGame.difficulty      = Cfg.Game.DIFFICULTY_NORMAL;
        campGame.nPlayers        = 2;
        campGame.nContinues      = 3;
        campGame.pauseAct        = Cfg.Game.PAUSE_ALL;
        campGame.finishOnWin     = true;
        campGame.lostAct         = Cfg.Game.LOST_AUTO_RESTART;

        loopGame.net             = Cfg.Game.NET_LOCAL;
        loopGame.difficulty      = Cfg.Game.DIFFICULTY_NORMAL;
        loopGame.nPlayers        = 2;
        loopGame.nContinues      = 3;
        loopGame.pauseAct        = Cfg.Game.PAUSE_ALL;
        loopGame.finishOnWin     = false;
        loopGame.lostAct         = Cfg.Game.LOST_AUTO_RESTART;

        trainingGame.net         = Cfg.Game.NET_LOCAL;
        trainingGame.difficulty  = Cfg.Game.DIFFICULTY_NORMAL;
        trainingGame.nPlayers    = 1;
        trainingGame.nContinues  = -1;
        trainingGame.pauseAct    = Cfg.Game.PAUSE_ALL;
        trainingGame.finishOnWin = false;
        trainingGame.lostAct     = Cfg.Game.LOST_RESTART_PAUSED;

        // 13 stages?
        map7.moveTime = 0.12f;
        map6.moveTime = 0.16f;
        map5.moveTime = 0.22f;
        map4.moveTime = 0.3f;
        map3.moveTime = 0.4f;
        map2.moveTime = 0.5f;
        map1.moveTime = 0.7f;

        bestAi.downKeyMin = 1f;
        bestAi.downKeyMax = 1f;
        bestAi.doubtMin = 0f;
        bestAi.doubtMax = 0f;
        bestAi.doubtFreqMin = 0f;
        bestAi.doubtFreqMax = 0f;
        bestAi.randomness = 0f;
        bestAi.trashMoves = 0f;

        mediumAi.downKeyMin = 0.2f;
        mediumAi.downKeyMax = 0.5f;
        mediumAi.doubtMin = 0.1f;
        mediumAi.doubtMax = 0.4f;
        mediumAi.doubtFreqMin = 0.06f;
        mediumAi.doubtFreqMax = 0.16f;
        mediumAi.randomness = 0.3f;
        mediumAi.trashMoves = 0.2f;

        worstAi.downKeyMin = 0.0f;
        worstAi.downKeyMax = 0.1f;
        worstAi.doubtMin = 0.5f;
        worstAi.doubtMax = 0.7f;
        worstAi.doubtFreqMin = 0.04f;
        worstAi.doubtFreqMax = 0.12f;
        worstAi.randomness = 0.6f;
        worstAi.trashMoves = 0.4f;

        ai1.downKeyMin = 0.0f;
        ai1.downKeyMax = 0.1f;
        ai1.doubtMin = 0.5f;
        ai1.doubtMax = 0.7f;
        ai1.doubtFreqMin = 0.12f;
        ai1.doubtFreqMax = 0.18f;
        ai1.randomness = 0.75f;
        ai1.trashMoves = 0.5f;

        ai2.downKeyMin = 0.0f;
        ai2.downKeyMax = 0.2f;
        ai2.doubtMin = 0.4f;
        ai2.doubtMax = 0.6f;
        ai2.doubtFreqMin = 0.1f;
        ai2.doubtFreqMax = 0.16f;
        ai2.randomness = 0.55f;
        ai2.trashMoves = 0.3f;

        ai3.downKeyMin = 0.2f;
        ai3.downKeyMax = 0.4f;
        ai3.doubtMin = 0.3f;
        ai3.doubtMax = 0.6f;
        ai3.doubtFreqMin = 0.08f;
        ai3.doubtFreqMax = 0.14f;
        ai3.randomness = 0.35f;
        ai3.trashMoves = 0.2f;

        ai4.downKeyMin = 0.2f;
        ai4.downKeyMax = 0.5f;
        ai4.doubtMin = 0.1f;
        ai4.doubtMax = 0.4f;
        ai4.doubtFreqMin = 0.06f;
        ai4.doubtFreqMax = 0.14f;
        ai4.randomness = 0.22f;
        ai4.trashMoves = 0.1f;

        ai5.downKeyMin = 0.4f;
        ai5.downKeyMax = 0.8f;
        ai5.doubtMin = 0.1f;
        ai5.doubtMax = 0.3f;
        ai5.doubtFreqMin = 0.1f;
        ai5.doubtFreqMax = 0.14f;
        ai5.randomness = 0.15f;
        ai5.trashMoves = 0.05f;

        ai6.downKeyMin = 0.6f;
        ai6.downKeyMax = 0.9f;
        ai6.doubtMin = 0f;
        ai6.doubtMax = 0.3f;
        ai6.doubtFreqMin = 0.1f;
        ai6.doubtFreqMax = 0.18f;
        ai6.randomness = 0.08f;
        ai6.trashMoves = 0.02f;

        ai7.downKeyMin = 1f;
        ai7.downKeyMax = 1f;
        ai7.doubtMin = 0f;
        ai7.doubtMax = 0f;
        ai7.doubtFreqMin = 0f;
        ai7.doubtFreqMax = 0f;
        ai7.randomness = 0f;
        ai7.trashMoves = 0f;
    }

    public static Color bgColor() {
        return screenBgColor[MathUtils.random(0, screenBgColor.length -1)];
    }
}
