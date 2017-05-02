/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;

/**
 * @author Vinícius Jardim
 * 06/11/2016
 */
public class Training extends MapManager {

    @Override
    public void init() {

        gameCfg = G.game.data.trainingGame;
        maps = new Array<>();
        opp = new Array<>();
        render = new Array<>();

        G.game.input.targetsClear();

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
        }

        G.game.input.linkAll();
    }

    @Override
    public void mapWin(int mapIndex) {}

    @Override
    public void mapLost(int mapIndex) {}
}
