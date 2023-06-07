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
        mapCfgs = new Array<>();
        aiCfgs = new Array<>();
        aiMapNames = new Array<>();

        int start = G.game.data.campaignCurrentStage;
        int end = 9;

        // #debugCode
        if (G.game.dbg.on) {
            end = G.game.dbg.campEnd;
        }

        if (start <= 1 && end >= 1) {
            mapCfgs.add(G.game.data.map1);
            aiCfgs.add(G.game.data.ai1);
            aiMapNames.add("1. BunnyAlan");
        }

        if (start <= 2 && end >= 2) {
            mapCfgs.add(G.game.data.map2);
            aiCfgs.add(G.game.data.ai2);
            aiMapNames.add("2. ChickenBil");
        }

        if (start <= 3 && end >= 3) {
            mapCfgs.add(G.game.data.map3);
            aiCfgs.add(G.game.data.ai3);
            aiMapNames.add("3. LizardLoyd");
        }

        if (start <= 4 && end >= 4) {
            mapCfgs.add(G.game.data.map4);
            aiCfgs.add(G.game.data.ai4);
            aiMapNames.add("4. BlackCat");
        }

        if (start <= 5 && end >= 5) {
            mapCfgs.add(G.game.data.map5);
            aiCfgs.add(G.game.data.ai5);
            aiMapNames.add("5. EagleEye");
        }

        if (start <= 6 && end >= 6) {
            mapCfgs.add(G.game.data.map6);
            aiCfgs.add(G.game.data.ai6);
            aiMapNames.add("6. CheetahSpirit");
        }

        if (start <= 7 && end >= 7) {
            mapCfgs.add(G.game.data.map7);
            aiCfgs.add(G.game.data.ai7);
            aiMapNames.add("7. DemonDog");
        }

        if (start <= 8 && end >= 8) {
            mapCfgs.add(G.game.data.map8);
            aiCfgs.add(G.game.data.ai8);
            aiMapNames.add("8. DeepnessGod");
        }

        if (start <= 9 && end >= 9) {
            mapCfgs.add(G.game.data.map9);
            aiCfgs.add(G.game.data.ai9);
            aiMapNames.add("9. Creator");
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
        if (!G.game.dbg.aiDisableMap1) {
            // TODO: Ai3 seams laggy in android. Debug (probably rendering too slow, not
            // Ai3)
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
                G.game.data.campaignCurrentStage = 1;
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
