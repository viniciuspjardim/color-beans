/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.defaults;

import com.vpjardim.colorbeans.core.Cfg;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public class Defaults {

    public static Cfg.Game campGame = new Cfg.Game();
    public static Cfg.Game loopGame = new Cfg.Game();

    static {
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
    }
}
