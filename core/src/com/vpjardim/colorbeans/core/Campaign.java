/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.ai.ai3.Ai3;

/**
 * @author Vinícius Jardim
 *         2016/09/02
 */
public class Campaign extends MapManager {
    // #debugCode all over the class

    public int stageIndex;
    public Array<Cfg.Map> mapCfgs;
    public Array<Cfg.Ai> aiCfgs;
    public Array<String> aiMapNames;

    @Override
    public void init() {
        gameCfg = G.game.data.campGame;
        maps = new Array<>();
        opps = new Array<>();
        render = new Array<>();

        stageIndex = 0;
        mapCfgs = new Array<>(12);
        aiCfgs = new Array<>(12);
        aiMapNames = new Array(12);

        int start = G.game.data.campaignCurrentStage;
        int end = 11;

        // #debugCode
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

        // #debugCode
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

        // #debugCode the content is needed, just the if is debug
        if (!G.game.dbg.aiDisableMap1) {
            aiMap.ai = new Ai3();
            aiMap.ai.init(aiMap, aiCfgs.get(stageIndex));
        }

        // #debugCode
        if (G.game.dbg.mapShapes != null) {
            playerMap.debugShape(G.game.dbg.mapShapes[0]);
            aiMap.debugShape(G.game.dbg.mapShapes[1]);
        }

        G.game.input.linkAll();
    }

    @Override
    public void mapWin(int mapIndex) {
        // Player (index 0) won
        if (mapIndex == 0) {
            Map p = maps.get(0);
            stageIndex++;

            G.game.score.addRow(ScoreTable.GMODE_CAMPAIGN, p.name, true, p.score, p.scoreSum,
                    p.matchTimer);

            G.game.data.campaignCurrentStage++;

            if (stageIndex >= mapCfgs.size) {
                gameStatus = GAME_ZEROED;
                G.game.data.campaignCurrentStage = 0;
            }
            // Config maps to the next stage
            else {
                Map playerMap = maps.get(0);
                playerMap.setCfg(mapCfgs.get(stageIndex));

                Map aiMap = maps.get(1);
                aiMap.setCfg(mapCfgs.get(stageIndex));
                aiMap.name = aiMapNames.get(stageIndex);

                // #DebugCode
                if (!G.game.dbg.aiDisableMap1)
                    aiMap.ai.init(aiMap, aiCfgs.get(stageIndex));
            }
        }
    }

    @Override
    public void mapLost(int mapIndex) {
    }
}
