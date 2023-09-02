package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;

public abstract class MapManager {
    public static final int GAME_CONTINUE = 1;
    public static final int GAME_ZEROED = 2;

    public Cfg.Game gameCfg;
    public Array<Map> maps;
    public Array<Map> opps;
    public Array<MapRender> render;
    public int gameStatus = GAME_CONTINUE;
    private boolean paused = false;

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

    public void winLost() {
        Map winnerMap = null;
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

        // Wait until animation is over to call mapWin
        if (winnerMap != null && mapsAnimating == 0) {
            mapWin(winnerMap.index);
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

                    setPaused(paused);
                }

                // #debugCode
                if (G.game.dbg.mapShapes != null) {
                    for (int i = 0; i < maps.size; i++) {
                        Map m = maps.get(i);
                        m.debugShape(G.game.dbg.mapShapes[i]);
                    }
                }
            }
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        if (paused) {
            G.game.audio.pauseMusic();
        } else {
            G.game.audio.playMusic();
        }

        this.paused = paused;
    }

    public void togglePaused() {
        setPaused(!paused);
    }
}
