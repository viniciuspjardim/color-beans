/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.defaults.Db;

/**
 * @author Vinícius Jardim
 * 06/11/2016
 */
public class Training extends MapManager {

    @Override
    public void init() {

        gameCfg = Db.trainingGame;
        maps = new Array<>();
        opp = new Array<>();
        render = new Array<>();

        G.game.input.targetsClear();

        for(int i = 0; i < G.game.players.size; i++) {
            Map m = new Map(this);
            m.index = i;
            m.setCfg(Db.mapT);
            m.name = G.game.players.get(i);
            maps.add(m);
            G.game.input.addTarget(m);

            MapRender r = new MapRender();
            r.m = m;
            render.add(r);
        }

        G.game.input.linkAll();
    }

    @Override
    public void mapWin(int mapIndex) {}

    @Override
    public void mapLost(int mapIndex) {}
}
