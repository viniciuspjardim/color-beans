/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.animation;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 *         2017/08/08
 */

public class MenuBeans {

    private static class Bean {
        public float x;
        public float y;
        public float moveTime;
        public int color;
    }

    private Pool<Bean> pool = new Pool<Bean>(12) {
        @Override
        protected Bean newObject() {
            return new Bean();
        }
    };

    private Array<Bean> beans = new Array<>();
    private float time = 0f;
    private float timeMax = 2f;
    private float rand = MathUtils.random(timeMax);
    private float size;
    private int widthMax;
    private float yStart;
    private float yEnd;

    public void resize() {
        float w = G.width / 7f;
        float h = G.height / 10f;
        size = Math.min(w, h);
        widthMax = (int) (G.width / size);
        yStart = G.height + size;
        yEnd = -size;
    }

    public void update() {

        time += G.delta;

        // Insert new block in the screen if it's time
        if (time >= rand) {

            // Update rand for next block anim
            rand = MathUtils.random(timeMax);

            Bean bean = pool.obtain();
            bean.x = ((float) MathUtils.random(widthMax)) * size;
            bean.y = yStart;
            bean.color = MathUtils.random(Block.CLR_A, Block.CLR_N);
            beans.add(bean);
            time = 0f;
        }

        // Update blocks position
        for (int i = 0; i < beans.size; i++) {
            Bean bean = beans.get(i);
            bean.moveTime += G.delta;

            // position = 1/2 * a * t^2
            bean.y = yStart - (0.5f * 1 * bean.moveTime * bean.moveTime * size);

            // It has fell out of the screen
            if (bean.y < yEnd) {
                bean.x = 0f;
                bean.x = 0f;
                bean.moveTime = 0;
                bean.color = 0;

                beans.removeIndex(i);
                pool.free(bean);
            }
        }
    }

    public void render() {

        TextureAtlas.AtlasRegion tile;

        G.game.batch.setColor(1f, 1f, 1f, 0.16f);
        for (int i = 0; i < beans.size; i++) {
            Bean bean = beans.get(i);
            tile = G.game.data.BEANS_REG.get(bean.color * 10000);
            G.game.batch.draw(tile, bean.x, bean.y, size, size);
        }
        G.game.batch.setColor(1f, 1f, 1f, 1f);
    }
}
