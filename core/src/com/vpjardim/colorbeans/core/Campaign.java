/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.ai.Ai1;
import com.vpjardim.colorbeans.defaults.Db;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public class Campaign extends MapManager {

    public int stageIndex;
    public Array<Cfg.Map> mapCfgs;
    public Array<Cfg.Ai> aiCfgs;
    public Array<String> aiMapNames;

    @Override
    public void init() {

        gameCfg = Db.campGame;
        maps = new Array<>();
        opp = new Array<>();
        render = new Array<>();

        stageIndex = 0;
        mapCfgs = new Array<>();
        aiCfgs = new Array<>();
        aiMapNames = new Array<>();

        if(G.game.dbg.campStart <= 1 && G.game.dbg.campEnd >= 1) {
            mapCfgs.add(Db.map1);
            aiCfgs.add(Db.ai1);
            aiMapNames.add("Stage 1");
        }

        if(G.game.dbg.campStart <= 2 && G.game.dbg.campEnd >= 2) {
            mapCfgs.add(Db.map2);
            aiCfgs.add(Db.ai2);
            aiMapNames.add("Stage 2");
        }

        if(G.game.dbg.campStart <= 3 && G.game.dbg.campEnd >= 3) {
            mapCfgs.add(Db.map3);
            aiCfgs.add(Db.ai3);
            aiMapNames.add("Stage 3");
        }

        if(G.game.dbg.campStart <= 4 && G.game.dbg.campEnd >= 4) {
            mapCfgs.add(Db.map4);
            aiCfgs.add(Db.ai4);
            aiMapNames.add("Stage 4");
        }

        if(G.game.dbg.campStart <= 5 && G.game.dbg.campEnd >= 5) {
            mapCfgs.add(Db.map5);
            aiCfgs.add(Db.ai5);
            aiMapNames.add("Stage 5");
        }

        if(G.game.dbg.campStart <= 6 && G.game.dbg.campEnd >= 6) {
            mapCfgs.add(Db.map6);
            aiCfgs.add(Db.ai6);
            aiMapNames.add("Stage 6");
        }

        if(G.game.dbg.campStart <= 7 && G.game.dbg.campEnd >= 7) {
            mapCfgs.add(Db.map7);
            aiCfgs.add(Db.ai7);
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
        playerMap.name = G.game.players.first();
        G.game.input.addTarget(playerMap);

        Map aiMap = new Map(this);
        aiMap.index = 1;
        maps.add(aiMap);
        r = new MapRender();
        r.m = aiMap;
        render.add(r);
        // Config AI's map to the first stage
        aiMap.setCfg(mapCfgs.get(stageIndex));
        aiMap.name = aiMapNames.get(stageIndex);
        aiMap.ai = new Ai1();
        aiMap.ai.init(aiMap, aiCfgs.get(stageIndex));

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
            p.scoreSum += p.score;

            G.game.score.addRow(p.name, p.score, p.scoreSum, p.matchTimer);

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
                aiMap.ai.init(aiMap, aiCfgs.get(stageIndex));
            }
        }
    }

    @Override
    public void mapLost(int mapIndex) {}
}
