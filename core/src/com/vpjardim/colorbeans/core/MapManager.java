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
    public Array<Cfg.Map> mapCfgs;
    public Array<Cfg.Ai> aiCfgs;

    public abstract void init();

    public abstract void mapWin(int mapIndex);

    public abstract void mapLost(int mapIndex);

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

    public void update() {

        if(maps.size > 1) {

            int mapsNotDone = 0;
            int mapsNotOverDone = 0;
            Map mapNotDone = null;

            for(Map m : maps) {

                if(!m.state.getCurrentState().equals(Map.MState.OVER) &&
                        !m.state.getCurrentState().equals(Map.MState.DONE)) {
                    mapsNotOverDone++;
                    mapNotDone = m;
                }

                if(!m.state.getCurrentState().equals(Map.MState.DONE)) {
                    mapsNotDone++;
                }
            }

            // All maps except one are in OVER or DONE state
            if(mapsNotOverDone == 1) {
                mapNotDone.prop.gameWin = true;
            }

            // If all maps are in DONE state and autoRestart is on
            // restart the game
            if(gameCfg.lostAct == Cfg.Game.LOST_AUTO_RESTART && mapsNotDone == 0) {
                for(Map m : maps) {
                    m.recycle();
                    m.state.changeState(Map.MState.FREE_FALL);
                }
            }
        }
        else if(gameCfg.lostAct == Cfg.Game.LOST_AUTO_RESTART) {

            Map m = maps.first();

            if(m.state.getCurrentState().equals(Map.MState.DONE)) {
                m.recycle();
                m.state.changeState(Map.MState.FREE_FALL);
            }
        }
    }
}
