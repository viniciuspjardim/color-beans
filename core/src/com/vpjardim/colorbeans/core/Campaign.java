/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.ai.ai3.Ai3;

/**
 * @author Vinícius Jardim
 * 02/09/2016
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
        mapCfgs = new Array<>();
        aiCfgs = new Array<>();
        aiMapNames = new Array<>();

        if(G.game.dbg.campStart <= 1 && G.game.dbg.campEnd >= 1) {
            mapCfgs.add(G.game.data.map1);
            aiCfgs.add(G.game.data.ai1);
            aiMapNames.add("Stage 1");
        }

        if(G.game.dbg.campStart <= 2 && G.game.dbg.campEnd >= 2) {
            mapCfgs.add(G.game.data.map2);
            aiCfgs.add(G.game.data.ai2);
            aiMapNames.add("Stage 2");
        }

        if(G.game.dbg.campStart <= 3 && G.game.dbg.campEnd >= 3) {
            mapCfgs.add(G.game.data.map3);
            aiCfgs.add(G.game.data.ai3);
            aiMapNames.add("Stage 3");
        }

        if(G.game.dbg.campStart <= 4 && G.game.dbg.campEnd >= 4) {
            mapCfgs.add(G.game.data.map4);
            aiCfgs.add(G.game.data.ai4);
            aiMapNames.add("Stage 4");
        }

        if(G.game.dbg.campStart <= 5 && G.game.dbg.campEnd >= 5) {
            mapCfgs.add(G.game.data.map5);
            aiCfgs.add(G.game.data.ai5);
            aiMapNames.add("Stage 5");
        }

        if(G.game.dbg.campStart <= 6 && G.game.dbg.campEnd >= 6) {
            mapCfgs.add(G.game.data.map6);
            aiCfgs.add(G.game.data.ai6);
            aiMapNames.add("Stage 6");
        }

        if(G.game.dbg.campStart <= 7 && G.game.dbg.campEnd >= 7) {
            mapCfgs.add(G.game.data.map7);
            aiCfgs.add(G.game.data.ai7);
            aiMapNames.add("Stage 7");
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
        if(G.game.dbg.aiPlayerCamp) {
            playerMap.ai = new Ai3();
            playerMap.ai.init(playerMap, G.game.data.ai7);
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

        // #DebugCode the content is needed, just the if is debug
        if(!G.game.dbg.aiDisableMap1) {
            // Todo Ai3 seams laggy in android. Debug (probably rendering too slow, not Ai3)
            aiMap.ai = new Ai3();
            aiMap.ai.init(aiMap, aiCfgs.get(stageIndex));
        }

        G.game.input.linkAll();

        // #debugCode
        if(maps.size > 0)
            maps.get(0).debugShape(G.game.dbg.map0shape);
        // #debugCode
        if(maps.size > 1)
            maps.get(1).debugShape(G.game.dbg.map1shape);
    }

    @Override
    public void mapWin(int mapIndex) {

        // Player (index 0) won
        if(mapIndex == 0) {

            Map p = maps.get(0);

            G.game.score.addRow(ScoreTable.GMODE_CAMPAIGN, p.name, true, p.score, p.scoreSum,
                    p.matchTimer);

            stageIndex++;
            if(stageIndex >= mapCfgs.size) {
                gameStatus = GAME_ZEROED;
            }
            // Config maps to the next stage
            else {
                Map playerMap = maps.get(0);
                playerMap.setCfg(mapCfgs.get(stageIndex));

                Map aiMap = maps.get(1);
                aiMap.setCfg(mapCfgs.get(stageIndex));
                aiMap.name = aiMapNames.get(stageIndex);

                // #DebugCode
                if(!G.game.dbg.aiDisableMap1)
                    aiMap.ai.init(aiMap, aiCfgs.get(stageIndex));
            }
        }
    }

    @Override
    public void mapLost(int mapIndex) {}
}
