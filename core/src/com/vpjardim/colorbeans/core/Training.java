/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.ai.Ai1;
import com.vpjardim.colorbeans.ai.ai3.Ai3;

/**
 * @author Vinícius Jardim
 *         2016/11/06
 */
public class Training extends MapManager {

    @Override
    public void init() {
        gameCfg = G.game.data.trainingGame;
        maps = new Array<>();
        opps = new Array<>();
        render = new Array<>();

        G.game.input.targetsClear();

        Cfg.Map mapT = new Cfg.Map();

        // Training mode player fall speed default options
        if (G.game.data.trainingSpeed == 1) {
            mapT.moveTime = G.game.data.map1.moveTime;
        } else if (G.game.data.trainingSpeed == 2) {
            mapT.moveTime = G.game.data.map2.moveTime;
        } else if (G.game.data.trainingSpeed == 3) {
            mapT.moveTime = G.game.data.map3.moveTime;
        } else if (G.game.data.trainingSpeed == 4) {
            mapT.moveTime = G.game.data.map4.moveTime;
        } else if (G.game.data.trainingSpeed == 5) {
            mapT.moveTime = G.game.data.map5.moveTime;
        } else if (G.game.data.trainingSpeed == 6) {
            mapT.moveTime = G.game.data.map6.moveTime;
        } else if (G.game.data.trainingSpeed == 7) {
            mapT.moveTime = G.game.data.map7.moveTime;
        } else if (G.game.data.trainingSpeed == 8) {
            mapT.moveTime = G.game.data.map8.moveTime;
        } else if (G.game.data.trainingSpeed == 9) {
            mapT.moveTime = G.game.data.map9.moveTime;
        } else if (G.game.data.trainingSpeed == 10) {
            mapT.moveTime = G.game.data.map10.moveTime;
        } else if (G.game.data.trainingSpeed == 11) {
            mapT.moveTime = G.game.data.map11.moveTime;
        } else if (G.game.data.trainingSpeed == 12) {
            mapT.moveTime = G.game.data.map12.moveTime;
        } else {
            G.game.data.trainingSpeed = 1;
            mapT.moveTime = G.game.data.map1.moveTime;
        }

        // #debugCode
        if (G.game.dbg.aiTraining != null) {
            gameCfg = G.game.data.loopGame;
        }

        for (int i = 0; i < G.game.data.players.size; i++) {
            Map m = new Map(this);
            m.index = i;
            m.setCfg(mapT);
            m.name = G.game.data.players.get(i).name;
            maps.add(m);
            G.game.input.addTarget(m);

            MapRender r = new MapRender();
            r.m = m;
            render.add(r);

            // #debugCode
            if (G.game.dbg.aiTraining != null) {

                if (G.game.dbg.aiTraining[i] == 1) {
                    m.ai = new Ai1();
                    m.ai.init(m, G.game.data.ai7);
                    G.game.input.removeTarget(m);
                } else if (G.game.dbg.aiTraining[i] == 3) {
                    m.ai = new Ai3();
                    m.ai.init(m, G.game.data.ai7);
                    G.game.input.removeTarget(m);
                }
            }

            // #debugCode
            if (G.game.dbg.mapShapes != null)
                m.debugShape(G.game.dbg.mapShapes[i]);
        }

        G.game.input.linkAll();
    }

    @Override
    public void mapWin(int mapIndex) {

        Map m = maps.get(mapIndex);
        G.game.score.addRow(ScoreTable.GMODE_TRAINING, m.name, true, m.score, m.scoreSum,
                m.matchTimer);
    }

    @Override
    public void mapLost(int mapIndex) {
    }
}
