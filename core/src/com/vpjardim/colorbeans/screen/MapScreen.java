/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Campaign;
import com.vpjardim.colorbeans.core.MapManager;
import com.vpjardim.colorbeans.core.MapRender;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public class MapScreen extends ScreenBase {

    public MapManager manager;

    public MapScreen() {
        super();
        manageInput = false;
    }

    @Override
    public void show() {
        super.show();
        manager = new Campaign();
        manager.init();
    }

    @Override
    public void render(float delta) {

        super.render(delta);

        manager.update();

        G.game.sr.setProjectionMatrix(cam.combined);
        G.game.sr.begin(ShapeRenderer.ShapeType.Filled);
        for(MapRender r : manager.render) {
            r.m.update();
            r.renderShapes();
        }
        G.game.sr.end();

        G.game.batch.setProjectionMatrix(cam.combined);
        G.game.batch.begin();
        for(MapRender r : manager.render) {
            r.renderBatch();
        }
        G.game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        manager.resize();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
