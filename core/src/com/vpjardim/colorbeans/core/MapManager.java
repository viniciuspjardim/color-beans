package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.Map;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public abstract class MapManager {

    public Cfg.Game gameCfg;
    public Array<Map> maps;
    public Array<MapRender> render;
    public Map winnerMap;

    public abstract void init();

    public void resize() {

        // Calculating side size

        // Margin of 2% of the screen width
        float marginX = Gdx.graphics.getWidth() * (2f / 100f);
        float totalMarginX = marginX * (maps.size + 1);
        float mapsX = Gdx.graphics.getWidth() - totalMarginX;
        float sideX = mapsX / (Map.N_COL * maps.size);

        // Margin of 2% of the screen height
        float marginY = Gdx.graphics.getHeight() * (2f / 100f);
        float totalMarginY = marginY * 2;
        float mapsY = Gdx.graphics.getHeight() - totalMarginY;
        float sideY = mapsY / Map.N_ROW;

        float side = Math.min(sideX, sideY);

        mapsX = side * gameCfg.nPlayers * Map.N_COL + totalMarginX;
        mapsY = side * Map.N_ROW + totalMarginY;

        // Updating size and positions
        for(int i = 0; i < render.size; i++) {

            MapRender r = render.get(i);

            r.size = side;
            r.px = (-Gdx.graphics.getWidth() / 2f) + marginX +
                    (i * (side * Map.N_COL + marginX)) +
                    ((Gdx.graphics.getWidth() - mapsX) / 2f)
            ;
            r.py = (Gdx.graphics.getHeight() / 2f) - marginY -
                    ((Gdx.graphics.getHeight() - mapsY) / 2f)
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
            if(!(m.isState(Map.MState.OVER) || m.isState(Map.MState.DONE))) {
                mapsPlaying++;
                mapPlaying = m;
            }

            // Maps not in DONE state: playing or animating (win or lost animations)
            if(!m.isState(Map.MState.DONE)) {
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

        // If auto restart is on and animations finished: restart the game
        if(gameCfg.lostAct == Cfg.Game.LOST_AUTO_RESTART && mapsAnimating == 0) {
            for(Map m : maps) {
                m.recycle();
                m.state.changeState(Map.MState.FREE_FALL);
            }
            winnerMap = null;
            // #debugCode
            // maps.first().debugShape(2);
        }
    }

    public void pause(int mapIndex) {

        if(gameCfg.pauseAct == Cfg.Game.PAUSE_SELF) {
            Map m = maps.get(mapIndex);
            m.prop.pause = !m.prop.pause;
        }
        else if(gameCfg.pauseAct == Cfg.Game.PAUSE_ALL) {

            boolean paused = !maps.get(mapIndex).prop.pause;

            for(int i = 0; i < maps.size; i++) {
                Map m = maps.get(i);
                m.prop.pause = paused;
            }
        }
        else if(gameCfg.pauseAct == Cfg.Game.PAUSE_OFF) {}
    }
}
