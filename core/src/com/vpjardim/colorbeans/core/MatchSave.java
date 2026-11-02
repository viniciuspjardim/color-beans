package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.Map;

/**
 * A game mode's match, kept in the {@link Store} to continue it later. It keeps the
 * configuration it started with, so a change in the options only applies to new
 * matches. See {@link MapManager#save()} and {@link Map#save}
 */
public class MatchSave {
    /**
     * Whether the match had ended. Then it continues on its results, with the fields
     * empty like they are by then
     */
    public boolean ended;
    /** The campaign's stage it started on, see {@link Campaign} for the others */
    public int startStage;
    public int stageIndex;
    public boolean stageCleared;
    public Array<Cfg.Map> mapCfgs;
    public Array<Cfg.Ai> aiCfgs;
    public Array<String> aiMapNames;
    public Array<MapSave> maps = new Array<>();

    public static class MapSave {
        public String name;
        public Cfg.Map cfg;
        /** Name of the {@link Map.MState} */
        public String state;
        /** Color of each block, column after column */
        public int[] colors;
        /** The player blocks' colors: falling and next */
        public int b1, b2, nextB1, nextB2;
        public boolean blockChanged;
        public boolean trashBlocksTurn;
        public boolean gameWin;
        public boolean lost;
        public int blocksDeleted;
        public int scoredBlocks;
        public int chainPowerCount;
        public int trashBlocksToAdd;
        public int winsCount;
        public int lostCount;
        public int scoreSum;
        public int score;
        public int maxChain;
        public int trashSent;
        public float matchTimer;
        public float speedTimer;
        public int speedIndex;
    }

    /** Whether it fits the maps of this version of the game */
    public boolean isValid() {
        if (maps == null || maps.size == 0)
            return false;

        for (MapSave m : maps) {
            if (m == null || m.cfg == null || m.cfg.moveTime == null)
                return false;

            if (!isState(m.state) || m.colors == null
                    || m.colors.length != Map.N_COL * (Map.OUT_ROW + Map.N_ROW))
                return false;
        }

        return true;
    }

    private static boolean isState(String name) {
        for (Map.MState s : Map.MState.values()) {
            if (s.name().equals(name))
                return true;
        }

        return false;
    }
}
