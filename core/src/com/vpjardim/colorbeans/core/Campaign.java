package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.ai.ai3.Ai3;

public class Campaign extends MapManager {
    public int stageIndex;
    public Array<Cfg.Map> mapCfgs;
    public Array<Cfg.Ai> aiCfgs;
    public Array<String> aiMapNames;
    public final Array<Map> winnerMaps = new Array<>();

    @Override
    public void init() {
        gameCfg = G.game.data.campGame;
        maps = new Array<>();
        opps = new Array<>();
        render = new Array<>();

        stageIndex = 0;
        mapCfgs = new Array<>(12);
        aiCfgs = new Array<>(12);
        aiMapNames = new Array<>(12);

        int start = G.game.data.campaignCurrentStage;
        int end = 11;

        if (G.game.dbg.on) {
            end = G.game.dbg.campEnd;
        }

        // Create all stages
        for (int i = start; i <= end; i++) {
            mapCfgs.add(G.game.data.createMapConfig(i));
            aiCfgs.add(G.game.data.createAiConfig(i));
            aiMapNames.add(G.game.data.stageNames[i]);
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
            playerMap.ai = new Ai3();
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
            aiMap.ai = new Ai3();
            aiMap.ai.init(aiMap, aiCfgs.get(stageIndex));
        }

        if (G.game.data.coopCampaign) {
            Map player2Map = new Map(this);
            player2Map.index = 2;
            maps.add(player2Map);
            r = new MapRender();
            r.m = player2Map;
            render.add(r);

            player2Map.setCfg(mapCfgs.get(stageIndex));
            player2Map.name = G.game.data.players.get(1).name;
            G.game.input.addTarget(player2Map);

            if (G.game.dbg.mapShapes != null)
                player2Map.debugShape(G.game.dbg.mapShapes[2]);
        }

        if (G.game.dbg.mapShapes != null) {
            playerMap.debugShape(G.game.dbg.mapShapes[0]);
            aiMap.debugShape(G.game.dbg.mapShapes[1]);
        }

        G.game.input.linkAll();
    }

    @Override
    public void mapWin(int mapIndex) {
        if (mapIndex != 1) {
            playersWin();
        }
    }

    public void playersWin() {
        G.game.data.campaignCurrentStage++;
        stageIndex++;

        // After the last map the game is zeroed
        if (stageIndex >= mapCfgs.size) {
            gameStatus = GAME_ZEROED;
            stageIndex = 0;
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

        // Config maps to the next stage
        if (gameStatus != GAME_ZEROED) {
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
    }

    public void winLost() {
        int mapsAnimating = 0;

        Map p1 = maps.get(0);
        Map aiMap = maps.get(1);
        Map p2 = maps.size == 3 ? maps.get(2) : null;

        for (Map m : maps) {
            // Maps not in DONE state: playing or animating (win or lost animations)
            if (!m.isInState(Map.MState.DONE) && !m.gameWin) {
                mapsAnimating++;
            }
        }

        // Ai Lost
        if (Map.lost(aiMap)) {
            if (!Map.lost(p1)) {
                p1.gameWin = true;
                winnerMaps.add(p1);
            }

            if (p2 != null && !Map.lost(p2)) {
                p2.gameWin = true;
                winnerMaps.add(p2);
            }
            // Players lost
        } else if (Map.lost(p1) && (p2 == null || Map.lost(p2))) {
            aiMap.gameWin = true;
            winnerMaps.add(aiMap);
        }

        // Dbg.dbg("WinLost ===>", "winners size: " + winnerMaps.size + "; mapsAnimating: " + mapsAnimating);

        // Wait until animation is over to call mapWin
        if (winnerMaps.size > 0 && mapsAnimating == 0) {
            mapWin(winnerMaps.get(0).index);
        }

        // Maps animations ended
        if (mapsAnimating == 0) {
            winnerMaps.clear();

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

                if (G.game.dbg.mapShapes != null) {
                    for (int i = 0; i < maps.size; i++) {
                        Map m = maps.get(i);
                        m.debugShape(G.game.dbg.mapShapes[i]);
                    }
                }
            }
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
