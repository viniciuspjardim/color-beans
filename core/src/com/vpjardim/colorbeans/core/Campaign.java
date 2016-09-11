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

    @Override
    public void init() {

        gameCfg = Db.loopGame;

        maps    = new Array<>();
        render  = new Array<>();
        mapCfgs = new Array<>();
        aiCfgs  = new Array<>();

        Map m;
        MapRender r;

        G.game.input.targetsClear();

        for(int i = 0; i < gameCfg.nPlayers; i++) {

            m = new Map(this);
            r = new MapRender();
            render.add(r);

            r.m = m;
            r.px = 0;
            r.py = 300;
            r.size = 40;

            G.game.input.addTarget(m);
            maps.add(m);
            m.name = "Map" + i;
            m.index = i;
        }

        G.game.input.linkAll();

        // maps.get(0).ai = new Ai1();
        // maps.get(0).ai.init(maps.get(0), Db.mediumAi);
        maps.get(1).ai = new Ai1();
        maps.get(1).ai.init(maps.get(1), Db.bestAi);
    }
}
