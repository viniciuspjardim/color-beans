/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;

/**
 * @author Vinícius Jardim
 *         2016/09/02
 */
public abstract class MapManager {

    public static final int PAUSED_NONE = 1;
    public static final int PAUSED_ALL = 2;
    public static final int PAUSED_MIXED = 3;

    public static final int GAME_CONTIUES = 1;
    public static final int GAME_OVER = 2;
    public static final int GAME_ZEROED = 3;

    public Cfg.Game gameCfg;
    public Array<Map> maps;
    public Array<Map> opps;
    public Array<MapRender> render;
    public Map winnerMap;
    public int gameStatus = GAME_CONTIUES;
    public int pauseStatus = PAUSED_NONE;

    public abstract void init();

    public void resize() {

        // Calculating side size

        float sideX = G.width / (((Map.N_COL + 2f) * maps.size) + 1);
        float sideY = G.height / (Map.N_ROW + 1);

        float side = Math.min(sideX, sideY);

        float totalX = (side * (Map.N_COL + 2f) * maps.size) + side;
        float totalY = (side * Map.N_ROW) + side;

        // Updating size and positions
        for (int i = 0; i < render.size; i++) {

            MapRender r = render.get(i);

            r.size = side;
            r.px = (side * Map.N_COL) * i + (side * 2f * (i + 1))
                    + (G.width - totalX) / 2f;
            r.py = (G.height + totalY) / 2f - side / 2f;
        }
    }

    public Map getOpponent(int excludeIndex) {

        if (maps.size <= 1)
            return null;

        opps.clear();
        opps.addAll(maps);
        opps.removeIndex(excludeIndex);

        for (int i = 0; i < opps.size; i++) {

            Map opp = opps.get(i);

            // TODO: fix cause the map go to GRAVITY_FALL state in winLost method if is
            // autoRestart
            if (opp.isInState(Map.MState.OVER) || opp.isInState(Map.MState.DONE))
                opps.removeIndex(i);
        }

        if (opps.size > 0) {
            return opps.get(MathUtils.random(0, opps.size - 1));
        }
        return null;
    }

    public abstract void mapWin(int mapIndex);

    public abstract void mapLost(int mapIndex);

    public void winLost() {

        int mapsAnimating = 0;
        int mapsPlaying = 0;
        Map mapPlaying = null;

        for (Map m : maps) {

            // Maps not in (OVER or DONE) state: maps playing
            if (!(m.isInState(Map.MState.OVER) || m.isInState(Map.MState.DONE))) {
                mapsPlaying++;
                mapPlaying = m;
            }

            // Maps not in DONE state: playing or animating (win or lost animations)
            if (!m.isInState(Map.MState.DONE)) {
                mapsAnimating++;
            }
        }

        // All maps except one are in OVER or DONE state, so one map win
        if (mapsPlaying == 1 && maps.size > 1) {
            mapPlaying.gameWin = true;
            winnerMap = mapPlaying;
        }

        // Wait until animation is over to call mapWin / mapLost events if there is a
        // winner
        if (winnerMap != null && mapsAnimating == 0) {
            mapWin(winnerMap.index);

            for (Map m : maps) {
                if (winnerMap.index == m.index)
                    continue;
                mapLost(m.index);
            }
        }

        // Maps animations ended
        if (mapsAnimating == 0) {

            boolean autoRestart = gameCfg.lostAct == Cfg.Game.LOST_AUTO_RESTART ||
                    gameCfg.lostAct == Cfg.Game.LOST_RESTART_PAUSED;

            boolean paused = gameCfg.lostAct == Cfg.Game.LOST_RESTART_PAUSED;

            // If auto restart is on and animations finished: restart the game
            if (autoRestart) {
                for (Map m : maps) {
                    m.recycle(true); // TODO: remove and test. It's done in MState
                    m.state.changeState(Map.MState.GRAVITY_FALL);

                    pause(m.index, paused);
                }
                winnerMap = null;

                // #debugCode
                if (G.game.dbg.mapShape != null) {
                    for (int i = 0; i < maps.size; i++) {
                        Map m = maps.get(i);
                        m.debugShape(G.game.dbg.mapShape[i]);
                    }
                }
            }
        }
    }

    public void pause(int mapIndex, boolean paused) {

        if (gameCfg.pauseAct == Cfg.Game.PAUSE_SELF) {
            Map m = maps.get(mapIndex);
            m.pause = paused;
        } else if (gameCfg.pauseAct == Cfg.Game.PAUSE_ALL) {
            for (int i = 0; i < maps.size; i++) {
                Map m = maps.get(i);
                m.pause = paused;
            }
        } else if (gameCfg.pauseAct == Cfg.Game.PAUSE_OFF) {
        }

        updatePausedStatus();
    }

    public void updatePausedStatus() {

        int pausedCont = 0;

        for (int i = 0; i < maps.size; i++) {
            Map m = maps.get(i);
            if (m.pause)
                pausedCont++;
        }

        if (pausedCont == 0)
            pauseStatus = PAUSED_NONE;
        else if (pausedCont == maps.size)
            pauseStatus = PAUSED_ALL;
        else
            pauseStatus = PAUSED_MIXED;
    }
}
