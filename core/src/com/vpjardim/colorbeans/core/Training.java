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
        render = new Array<>();

        Map playerMap = new Map(this);
        playerMap.index = 0;
        maps.add(playerMap);

        G.game.input.targetsClear();

        MapRender r = new MapRender();
        r.m = playerMap;
        render.add(r);

        playerMap.setCfg(Db.mapT);
        playerMap.name = "Player";
        G.game.input.addTarget(playerMap);

        G.game.input.linkAll();
    }

    @Override
    public void mapWin(int mapIndex) {}

    @Override
    public void mapLost(int mapIndex) {}
}
