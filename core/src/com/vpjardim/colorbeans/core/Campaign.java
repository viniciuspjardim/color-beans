package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.ai.ai1.Ai1;

public class Campaign extends MapManager {
    /** The stage the campaign started on, and the index of the current one from it */
    public int startStage;
    public int stageIndex;
    public Array<Cfg.Map> mapCfgs;
    public Array<Cfg.Ai> aiCfgs;
    public Array<String> aiMapNames;
    /** Set when the players win a stage, until the maps change to the next one */
    private boolean stageCleared;

    @Override
    public void init() {
        gameCfg = G.game.data.campGame;
        maps = new Array<>();
        opps = new Array<>();
        render = new Array<>();

        MatchSave save = loadSave();

        if (save != null) {
            // The stages it started with, even if the options changed
            startStage = save.startStage;
            stageIndex = save.stageIndex;
            mapCfgs = save.mapCfgs;
            aiCfgs = save.aiCfgs;
            aiMapNames = save.aiMapNames;
        } else {
            startStage = G.game.data.campaignCurrentStage;
            stageIndex = 0;
            mapCfgs = new Array<>(12);
            aiCfgs = new Array<>(12);
            aiMapNames = new Array<>(12);

            int end = 11;

            if (G.game.dbg.on) {
                end = G.game.dbg.campEnd;
            }

            // Create all stages
            for (int i = startStage; i <= end; i++) {
                mapCfgs.add(G.game.data.createMapConfig(i));
                aiCfgs.add(G.game.data.createAiConfig(i));
                aiMapNames.add(G.game.data.stageNames[i]);
            }
        }

        G.game.input.targetsClear();

        MapRender r;

        Map playerMap = new Map(this);
        playerMap.index = 0;
        maps.add(playerMap);
        r = new MapRender();
        r.m = playerMap;
        render.add(r);

        // Config player's map to the first stage
        playerMap.setCfg(mapCfgs.get(stageIndex));
        playerMap.name = G.game.data.players.first().name;
        G.game.input.addTarget(playerMap);

        if (G.game.dbg.aiPlayerCamp) {
            playerMap.ai = new Ai1();
            playerMap.ai.init(playerMap, G.game.data.createAiConfig(11));
            G.game.input.removeTarget(playerMap);
        }

        Map aiMap = new Map(this);
        aiMap.index = 1;
        maps.add(aiMap);
        r = new MapRender();
        r.m = aiMap;
        render.add(r);

        // Config AI's map to the first stage
        aiMap.setCfg(mapCfgs.get(stageIndex));
        aiMap.name = aiMapNames.get(stageIndex);

        if (!G.game.dbg.aiDisableMap1) {
            aiMap.ai = new Ai1();
            aiMap.ai.init(aiMap, aiCfgs.get(stageIndex));
        }

        boolean coop = save != null ? save.maps.size == 3 : G.game.data.coopCampaign;

        if (coop) {
            Map player2Map = new Map(this);
            player2Map.index = 2;
            maps.add(player2Map);
            r = new MapRender();
            r.m = player2Map;
            render.add(r);

            player2Map.setCfg(mapCfgs.get(stageIndex));

            // A saved match has its names
            if (save == null)
                player2Map.name = G.game.data.players.get(1).name;

            G.game.input.addTarget(player2Map);

            if (G.game.dbg.mapShapes != null)
                player2Map.debugShape(G.game.dbg.mapShapes[2]);
        }

        if (G.game.dbg.mapShapes != null) {
            playerMap.debugShape(G.game.dbg.mapShapes[0]);
            aiMap.debugShape(G.game.dbg.mapShapes[1]);
        }

        restore(save);

        // One saved on the results continues on the stage after it was cleared, but its
        // maps kept the speed and the AI's name of the stage that ended
        if (save != null && save.ended) {
            for (Map m : maps) {
                m.setCfg(mapCfgs.get(stageIndex));
            }

            aiMap.name = aiMapNames.get(stageIndex);
        }

        G.game.input.linkAll();
    }

    @Override
    public String saveKey() {
        return Store.CAMPAIGN;
    }

    @Override
    protected boolean isValid(MatchSave save) {
        return super.isValid(save) && save.mapCfgs != null && save.aiCfgs != null
                && save.aiMapNames != null && save.stageIndex >= 0
                && save.stageIndex < save.mapCfgs.size
                && save.aiCfgs.size == save.mapCfgs.size
                && save.aiMapNames.size == save.mapCfgs.size
                && (save.maps.size == 2 || save.maps.size == 3);
    }

    /** After the last stage there's nothing to continue */
    @Override
    protected boolean canSave() {
        return super.canSave() && !campaignFinished();
    }

    @Override
    protected void save(MatchSave save) {
        save.startStage = startStage;
        save.stageIndex = stageIndex;
        save.mapCfgs = mapCfgs;
        save.aiCfgs = aiCfgs;
        save.aiMapNames = aiMapNames;
    }

    @Override
    public void mapWin(int mapIndex) {
        if (mapIndex != 1) {
            playersWin();
        }
    }

    /**
     * Saves the progress and the scores. The maps change to the next stage in
     * {@link #prepareNextMatch()}, after the results
     */
    public void playersWin() {
        stageIndex++;
        G.game.data.campaignCurrentStage = startStage + stageIndex;
        stageCleared = true;

        // After the last stage the campaign starts over
        if (stageIndex >= mapCfgs.size) {
            G.game.data.campaignCurrentStage = 0;
        }

        Map p1 = maps.get(0);
        Map p2 = maps.size == 3 ? maps.get(2) : null; // index 1 is the AI.

        G.game.score.addRow(ScoreTable.GMODE_CAMPAIGN, p1.name, true, p1.score, p1.scoreSum,
                p1.matchTimer);

        if (p2 != null) {
            G.game.score.addRow(ScoreTable.GMODE_CAMPAIGN, p2.name, true, p2.score, p2.scoreSum,
                    p2.matchTimer);
        }
    }

    /** Whether the stage that ended was the last one */
    public boolean campaignFinished() {
        return stageCleared && stageIndex >= mapCfgs.size;
    }

    @Override
    protected void prepareNextMatch() {
        if (!stageCleared)
            return;

        stageCleared = false;

        // After the last stage the game is zeroed
        if (stageIndex >= mapCfgs.size) {
            gameStatus = GAME_ZEROED;
            stageIndex = 0;
            return;
        }

        // Config maps to the next stage
        Map p1 = maps.get(0);
        Map p2 = maps.size == 3 ? maps.get(2) : null;

        p1.setCfg(mapCfgs.get(stageIndex));

        if (p2 != null) {
            p2.setCfg(mapCfgs.get(stageIndex));
        }

        Map aiMap = maps.get(1);
        aiMap.setCfg(mapCfgs.get(stageIndex));
        aiMap.name = aiMapNames.get(stageIndex);

        if (!G.game.dbg.aiDisableMap1)
            aiMap.ai.init(aiMap, aiCfgs.get(stageIndex));
    }

    @Override
    public String nextMatchLabel() {
        if (campaignFinished())
            return "Continue";

        return stageCleared ? "Next stage" : "Retry";
    }

    /** The players win when the AI loses, and the AI when all the players lose */
    @Override
    protected void findWinners() {
        Map p1 = maps.get(0);
        Map aiMap = maps.get(1);
        Map p2 = maps.size == 3 ? maps.get(2) : null;

        if (Map.lost(aiMap)) {
            if (!Map.lost(p1))
                p1.gameWin = true;

            if (p2 != null && !Map.lost(p2))
                p2.gameWin = true;
        } else if (Map.lost(p1) && (p2 == null || Map.lost(p2))) {
            aiMap.gameWin = true;
        }
    }

    @Override
    public Map getOpponent(int excludeIndex) {
        if (maps.size <= 1)
            return null;

        // Index 1 is the AI. Players will always pick AI as opponent (coop mode).
        if (excludeIndex != 1) {
            return maps.get(1);
        }

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
}
