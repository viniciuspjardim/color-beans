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
        render = new Array<>();

        stageIndex = 0;
        mapCfgs = new Array<>();
        aiCfgs = new Array<>();
        aiMapNames = new Array<>();

        mapCfgs.add(Db.map1);
        mapCfgs.add(Db.map2);
        mapCfgs.add(Db.map3);
        mapCfgs.add(Db.map4);
        mapCfgs.add(Db.map5);
        mapCfgs.add(Db.map6);
        mapCfgs.add(Db.map7);

        aiCfgs.add(Db.ai1);
        aiCfgs.add(Db.ai2);
        aiCfgs.add(Db.ai3);
        aiCfgs.add(Db.ai4);
        aiCfgs.add(Db.ai5);
        aiCfgs.add(Db.ai6);
        aiCfgs.add(Db.ai7);

        aiMapNames.add("Stage 1");
        aiMapNames.add("Stage 2");
        aiMapNames.add("Stage 3");
        aiMapNames.add("Stage 4");
        aiMapNames.add("Stage 5");
        aiMapNames.add("Stage 6");
        aiMapNames.add("Stage 7");

        G.game.input.targetsClear();

        MapRender r;

        Map playerMap = new Map(this);
        // #debugCode
        // playerMap.debugShape(2);
        playerMap.index = 0;
        maps.add(playerMap);
        r = new MapRender();
        r.m = playerMap;
        render.add(r);
        // Config player's map to the first stage
        playerMap.setCfg(mapCfgs.get(stageIndex));
        playerMap.name = "Player";
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
    }

    @Override
    public void mapWin(int mapIndex) {

        // Player (index 0) won
        if(mapIndex == 0) {

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
