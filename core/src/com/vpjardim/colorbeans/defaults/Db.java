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

    public static final Cfg.Game campGame = new Cfg.Game();
    public static final Cfg.Game loopGame = new Cfg.Game();

    public static final Cfg.Ai bestAi     = new Cfg.Ai();
    public static final Cfg.Ai mediumAi   = new Cfg.Ai();
    public static final Cfg.Ai worstAi    = new Cfg.Ai();

    static {

        screenBgColor = new Color[]{
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
        campGame.lostAct     = Cfg.Game.LOST_WAIT;

        loopGame.net         = Cfg.Game.NET_LOCAL;
        loopGame.difficulty  = Cfg.Game.DIFFICULTY_NORMAL;
        loopGame.nPlayers    = 2;
        loopGame.nContinues  = 3;
        loopGame.pauseAct    = Cfg.Game.PAUSE_ALL;
        loopGame.finishOnWin = false;
        loopGame.lostAct     = Cfg.Game.LOST_AUTO_RESTART;

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
    }

    public static Color bgColor() {
        return screenBgColor[MathUtils.random(0, screenBgColor.length -1)];
    }
}
