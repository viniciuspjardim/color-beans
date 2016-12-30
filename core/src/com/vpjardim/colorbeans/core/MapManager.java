package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public abstract class MapManager {

    public static final int PAUSED_NONE  = 1;
    public static final int PAUSED_ALL   = 2;
    public static final int PAUSED_MIXED = 3;

    public static final int GAME_CONTIUES = 1;
    public static final int GAME_OVER     = 2;
    public static final int GAME_ZEROED   = 3;

    public Cfg.Game gameCfg;
    public Array<Map> maps;
    public Array<MapRender> render;
    public Map winnerMap;
    public int gameStatus = GAME_CONTIUES;
    public int pauseStatus = PAUSED_NONE;

    public abstract void init();

    public void resize() {

        // Calculating side size

        // Margin of 2% of the screen width
        float marginX = G.width * (2f / 100f);
        float totalMarginX = marginX * (maps.size + 1);
        float mapsX = G.width - totalMarginX;
        float sideX = mapsX / (Map.N_COL * maps.size);

        // Margin of 2% of the screen height
        float marginY = G.height * (2f / 100f);
        float totalMarginY = marginY * 2;
        float mapsY = G.height - totalMarginY;
        float sideY = mapsY / Map.N_ROW;

        float side = Math.min(sideX, sideY);

        mapsX = side * gameCfg.nPlayers * Map.N_COL + totalMarginX;
        mapsY = side * Map.N_ROW + totalMarginY;

        // Updating size and positions
        for(int i = 0; i < render.size; i++) {

            MapRender r = render.get(i);

            r.size = side;
            r.px = (-G.width / 2f) + marginX +
                    (i * (side * Map.N_COL + marginX)) +
                    ((G.width - mapsX) / 2f)
            ;
            r.py = (G.height / 2f) - marginY -
                    ((G.height - mapsY) / 2f)
            ;
        }
    }

    public Map getOpponent(int excludeIndex) {

        // Todo should not return maps in game over state

        if(maps.size <= 1) return null;

        int rand = MathUtils.random(0, maps.size -1);
        Map opp = maps.get(rand);

        // If the random map picked is the on that
        // has called this method, chose the next on
        if(rand == excludeIndex) {

            // If it`s the last, get the first one
            if(rand == maps.size -1) opp = maps.get(0);
            else {
                opp = maps.get(rand +1);
            }
        }
        return opp;
    }

    public abstract void mapWin(int mapIndex);

    public abstract void mapLost(int mapIndex);

    public void winLost() {

        int mapsAnimating = 0;
        int mapsPlaying = 0;
        Map mapPlaying = null;

        for(Map m : maps) {

            // Maps not in (OVER or DONE) state: maps playing
            if(!(m.isInState(Map.MState.OVER) || m.isInState(Map.MState.DONE))) {
                mapsPlaying++;
                mapPlaying = m;
            }

            // Maps not in DONE state: playing or animating (win or lost animations)
            if(!m.isInState(Map.MState.DONE)) {
                mapsAnimating++;
            }
        }

        // All maps except one are in OVER or DONE state, so one map win
        if(mapsPlaying == 1 && maps.size > 1) {
            mapPlaying.prop.gameWin = true;
            winnerMap = mapPlaying;
        }

        // Wait until animation is over to call mapWin / mapLost events if there is a winner
        if(winnerMap != null && mapsAnimating == 0) {
            mapWin(winnerMap.index);

            for(Map m : maps) {
                if(winnerMap.index == m.index) continue;
                mapLost(m.index);
            }
        }

        // Maps animations ended
        if(mapsAnimating == 0) {

            boolean autoRestart = gameCfg.lostAct == Cfg.Game.LOST_AUTO_RESTART ||
                    gameCfg.lostAct == Cfg.Game.LOST_RESTART_PAUSED;

            boolean paused = gameCfg.lostAct == Cfg.Game.LOST_RESTART_PAUSED;

            // If auto restart is on and animations finished: restart the game
            if(autoRestart) {
                for(Map m : maps) {
                    m.recycle();
                    m.state.changeState(Map.MState.FREE_FALL);

                    pause(m.index, paused);
                }
                winnerMap = null;
                // #debugCode
                // maps.first().debugShape(2);
            }
        }
    }

    public void pause(int mapIndex, boolean paused) {

        if(gameCfg.pauseAct == Cfg.Game.PAUSE_SELF) {
            Map m = maps.get(mapIndex);
            m.prop.pause = paused;
        }
        else if(gameCfg.pauseAct == Cfg.Game.PAUSE_ALL) {
            for(int i = 0; i < maps.size; i++) {
                Map m = maps.get(i);
                m.prop.pause = paused;
            }
        }
        else if(gameCfg.pauseAct == Cfg.Game.PAUSE_OFF) {}

        updatePausedStatus();
    }

    public void updatePausedStatus() {

        int pausedCont = 0;

        for(int i = 0; i < maps.size; i++) {
            Map m = maps.get(i);
            if(m.prop.pause) pausedCont++;
        }

        if(pausedCont == 0) pauseStatus = PAUSED_NONE;
        else if(pausedCont == maps.size) pauseStatus = PAUSED_ALL;
        else pauseStatus = PAUSED_MIXED;
    }
}
