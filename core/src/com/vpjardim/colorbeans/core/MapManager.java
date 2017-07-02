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
    public Array<Map> opps;
    public Array<MapRender> render;
    public Map winnerMap;
    public int gameStatus = GAME_CONTIUES;
    public int pauseStatus = PAUSED_NONE;

    public abstract void init();

    public void resize() {

        // Calculating side size

        // Margin of 2% of the screen width
        float marginX = G.width * (2f / 100f);
        // Margin of 2% of the screen height
        float marginY = G.height * (2f / 100f);
        // Used margin
        float margin = Math.min(marginX, marginY);

        float totalMarginX = margin * (maps.size + 1);
        float mapsX = G.width - totalMarginX;
        float sideX = mapsX / ((Map.N_COL + 1.1f) * maps.size); // 1.1 is the next block space

        float totalMarginY = margin * 2;
        float mapsY = G.height - totalMarginY;
        float sideY = mapsY / Map.N_ROW;

        float side = Math.min(sideX, sideY);

        // 1.1 is the next block space
        float totalX = totalMarginX + (side * (Map.N_COL + 1.1f) * maps.size);
        float totalY = totalMarginY + side * Map.N_ROW;

        // Updating size and positions
        for(int i = 0; i < render.size; i++) {

            MapRender r = render.get(i);

            r.size = side;

                                                            // The next block space
            r.px = margin + (side * Map.N_COL + margin) * i + (side * 1.1f * (i + 1))
                   + (G.width - totalX) / 2f;

            r.py = -margin + (G.height + totalY) / 2f;
        }
    }

    public Map getOpponent(int excludeIndex) {

        if(maps.size <= 1) return null;

        opps.clear();
        opps.addAll(maps);
        opps.removeIndex(excludeIndex);

        for(int i = 0; i < opps.size; i++) {

            Map opp = opps.get(i);

            // Todo fix cause the map go to GRAVITY_FALL state in winLost method if is autoRestart
            if(opp.isInState(Map.MState.OVER) || opp.isInState(Map.MState.DONE))
                opps.removeIndex(i);
        }

        if(opps.size > 0) {
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
            mapPlaying.gameWin = true;
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
                    m.recycle(true); // Todo remove and test. It's done in MState
                    m.state.changeState(Map.MState.GRAVITY_FALL);

                    pause(m.index, paused);
                }
                winnerMap = null;

                // #debugCode
                if(maps.size > 0)
                    maps.get(0).debugShape(G.game.dbg.map0shape);
                // #debugCode
                if(maps.size > 1)
                    maps.get(1).debugShape(G.game.dbg.map1shape);
            }
        }
    }

    public void pause(int mapIndex, boolean paused) {

        if(gameCfg.pauseAct == Cfg.Game.PAUSE_SELF) {
            Map m = maps.get(mapIndex);
            m.pause = paused;
        }
        else if(gameCfg.pauseAct == Cfg.Game.PAUSE_ALL) {
            for(int i = 0; i < maps.size; i++) {
                Map m = maps.get(i);
                m.pause = paused;
            }
        }
        else if(gameCfg.pauseAct == Cfg.Game.PAUSE_OFF) {}

        updatePausedStatus();
    }

    public void updatePausedStatus() {

        int pausedCont = 0;

        for(int i = 0; i < maps.size; i++) {
            Map m = maps.get(i);
            if(m.pause) pausedCont++;
        }

        if(pausedCont == 0) pauseStatus = PAUSED_NONE;
        else if(pausedCont == maps.size) pauseStatus = PAUSED_ALL;
        else pauseStatus = PAUSED_MIXED;
    }
}
