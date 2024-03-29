package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.ai.ai1.Ai1;

public class Training extends MapManager {
    @Override
    public void init() {
        gameCfg = G.game.data.trainingGame;
        maps = new Array<>();
        opps = new Array<>();
        render = new Array<>();

        G.game.input.targetsClear();

        Cfg.Map mapT = G.game.data.createMapConfig(G.game.data.trainingSpeed);

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

            if (G.game.dbg.aiTraining != null && G.game.dbg.aiTraining[i]) {
                m.ai = new Ai1();
                m.ai.init(m, G.game.data.createAiConfig(11));
                G.game.input.removeTarget(m);
            }

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
}
