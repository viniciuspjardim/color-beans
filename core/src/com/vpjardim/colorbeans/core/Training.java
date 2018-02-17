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
 * 2016/11/06
 */
public class Training extends MapManager {

    @Override
    public void init() {

        gameCfg = G.game.data.trainingGame;
        maps = new Array<>();
        opps = new Array<>();
        render = new Array<>();

        G.game.input.targetsClear();

        // #debugCode
        if(G.game.dbg.aiTraining != null) { gameCfg = G.game.data.loopGame; }

        for(int i = 0; i < G.game.data.players.size; i++) {
            Map m = new Map(this);
            m.index = i;
            m.setCfg(G.game.data.mapT);
            m.name = G.game.data.players.get(i).name;
            maps.add(m);
            G.game.input.addTarget(m);

            MapRender r = new MapRender();
            r.m = m;
            render.add(r);

            // #debugCode
            if(G.game.dbg.aiTraining != null) {

                if(G.game.dbg.aiTraining[i] == 1) {
                    m.ai = new Ai1();
                    m.ai.init(m, G.game.data.ai7);
                    G.game.input.removeTarget(m);
                }
                else if(G.game.dbg.aiTraining[i] == 3) {
                    m.ai = new Ai3();
                    m.ai.init(m, G.game.data.ai7);
                    G.game.input.removeTarget(m);
                }
            }

            // #debugCode
            if(G.game.dbg.mapShape != null)
                m.debugShape(G.game.dbg.mapShape[i]);
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
    public void mapLost(int mapIndex) {}
}
