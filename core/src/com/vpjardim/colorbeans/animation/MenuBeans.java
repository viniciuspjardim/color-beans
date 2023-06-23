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
    private final float MAX_OPACITY = 10F;
    private final float FRAME_TIME = 1f / 12f; // 12 frames per second

    private static class Bean {
        public int x;
        public int y;
        public float opacity;
        public float opacitySign;
        public int color;
        public int tile;
        public float animationTime;
    }

    static int[] tiles = { 0, 2, 3, 4};
    static int[] animationTiles = { 0, 4, 0, 3};

    private final Pool<Bean> pool = new Pool<Bean>(240) {
        @Override
        protected Bean newObject() {
            return new Bean();
        }
    };

    private final Array<Bean> beans = new Array<>();
    private Bean animatedBean;
    private float time = 0f;
    private float newBeanAnimationTimer = 0f;
    private float size;
    private int widthMax;
    private int heightMax;
    private int beansCount;

    public MenuBeans() {
        resize();

        for (int i = 0; i < beansCount; i++) {
            Bean bean = pool.obtain();

            bean.x = MathUtils.random(widthMax);
            bean.y = MathUtils.random(heightMax);
            bean.opacity = MathUtils.random(MAX_OPACITY);
            bean.opacitySign = MathUtils.randomBoolean() ? 1 : -1;
            bean.color = MathUtils.random(Block.CLR_A, 5);
            bean.tile = tiles[MathUtils.random(3)];
            bean.animationTime = 0f;

            if (!hasCollision(bean)) {
                beans.add(bean);
            } else {
                pool.free(bean);
            }
        }
    }

    public void resize() {
        size = G.height / 10f;
        widthMax = (int) (G.width / size);
        heightMax = (int) (G.height / size);
        beansCount = widthMax * heightMax;
    }

    private boolean hasCollision(Bean newBean) {
        for (int i = 0; i < beans.size; i++) {
            Bean bean = beans.get(i);

            if (newBean.x == bean.x && newBean.y == bean.y) {
                return true;
            }
        }

        return false;
    }

    public void update() {
        time += G.delta;
        newBeanAnimationTimer += G.delta;

        // Insert new block in the screen if it's time
        if (time >= 0.5f) {
            time = MathUtils.random(0.25f);
            Bean bean = pool.obtain();

            bean.x = MathUtils.random(widthMax);
            bean.y = MathUtils.random(heightMax);
            bean.opacity = MathUtils.random(0f);
            bean.opacitySign = 1f;
            bean.color = MathUtils.random(Block.CLR_A, 5);
            bean.tile = tiles[MathUtils.random(3)];
            bean.animationTime = 0f;

            if (!hasCollision(bean)) {
                beans.add(bean);
            } else {
                pool.free(bean);
            }
        }


        // Update beans opacity
        for (int i = 0; i < beans.size; i++) {
            Bean bean = beans.get(i);

            // If opacity is maximum, revert sign to decrease it
            if (bean.opacity > MAX_OPACITY) {
                bean.opacitySign = -1;
            }

            // Find bean to animate
            if (bean.opacity >= MAX_OPACITY / 2f && animatedBean == null && newBeanAnimationTimer > 8f) {
                newBeanAnimationTimer = MathUtils.random(4f);

                animatedBean = bean;
                animatedBean.tile = 0;
                animatedBean.animationTime = FRAME_TIME * MathUtils.random(16);
                animatedBean.opacity = 1f;
            }

            bean.opacity += (G.delta * 0.1f * bean.opacitySign);

            // It's no longer visible
            if (bean.opacity < 0) {
                beans.removeIndex(i);
                pool.free(bean);
            }
        }
    }

    public void render() {

        TextureAtlas.AtlasRegion tile;

        if(animatedBean != null) {
            // The total animation has 4 frames and can repeat 6 times. So it's 24 frames max.
            if(animatedBean.animationTime > FRAME_TIME * 24f) {
                animatedBean.tile = 0;
                animatedBean.animationTime = 0f;
                animatedBean = null;
            } else {
                animatedBean.animationTime += G.delta;
                animatedBean.tile = animationTiles[(int) (animatedBean.animationTime / FRAME_TIME) % 4];
            }
        }

        for (int i = 0; i < beans.size; i++) {
            Bean bean = beans.get(i);
            tile = G.game.data.BEANS_REG.get(bean.color * 10000 + bean.tile);
            G.game.batch.setColor(1f, 1f, 1f, Math.min(bean.opacity, 0.16f));
            G.game.batch.draw(tile, bean.x * size, bean.y * size, size, size);
        }

        G.game.batch.setColor(1f, 1f, 1f, 1f);
    }
}
