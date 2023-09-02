package com.vpjardim.colorbeans.animation;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.vpjardim.colorbeans.G;

public class BgBeans {
    private final static float FRAME_TIME = 1f / 12f; // 12 frames per second
    private final static int MAX_ROWS = 10;
    private final static int MAX_COLS = 30;

    private final static int NONE = 0;
    private final static int FADE_IN = 1;
    private final static int FADE_OUT = 2;
    private final static int SHAKE = 3;

    private final static int[] tiles = {0, 2, 3, 4};
    private final static int[] animationTiles = {0, 4, 0, 3};

    private static class Bean {
        public float px = 0f;
        public float py = 0f;
        public int color = 0;
        public float opacity = 0f;
        public int tile = 0;

        public int animationType = NONE;
        public float shakeTime = 0f;

        public boolean isLinked() {
            return tile == 1 || tile >= 10;
        }

        public void update() {
            if (animationType == NONE) return;

            if (animationType == FADE_IN) {
                if (opacity > 1) {
                    animationType = NONE;
                    opacity = 1f;
                    shakeTime = 0f;

                    return;
                }

                opacity += G.delta * 0.4f;
            } else if (animationType == FADE_OUT) {
                if (opacity < 0f) {
                    animationType = NONE;
                    opacity = 0f;
                    shakeTime = 0f;

                    return;
                }

                opacity -= G.delta * 0.4f;
            } else if (animationType == SHAKE) {
                if (shakeTime < 0f) {
                    animationType = NONE;
                    opacity = 1f;
                    shakeTime = 0f;

                    return;
                }

                tile = animationTiles[(int) (shakeTime / FRAME_TIME) % 4];
                shakeTime -= G.delta;
            }
        }
    }

    private final Bean[][] beans = new Bean[MAX_ROWS][MAX_COLS];
    private float size;
    private int croppedCols = 24;
    private float animationTime = 0f;

    public BgBeans() {
        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                Bean bean = new Bean();
                beans[i][j] = bean;

                if (MathUtils.random(1f) > 0.75f) {
                    bean.opacity = 0f;
                    continue;
                }

                bean.color = MathUtils.random(1, 5);
                bean.opacity = 1f;
                bean.tile = tiles[MathUtils.random(3)];
            }
        }

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                beanLinks(i, j);
            }
        }

        resize();
    }

    public void resize() {
        size = G.height / (float) MAX_ROWS;
        croppedCols = Math.min((int) Math.ceil(G.width / size), MAX_COLS);

        if (croppedCols < 6) {
            croppedCols = 6;
        }

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                beans[i][j].px = j * size;
                beans[i][j].py = i * size;
            }
        }
    }

    private void animate() {
        Bean bean = randomBean();
        animationTime += G.delta;

        if (bean.animationType == NONE && animationTime > 2f) {
            if (bean.opacity == 0f) {
                bean.animationType = FADE_IN;
                bean.color = MathUtils.random(1, 5);
                bean.tile = tiles[MathUtils.random(3)];
            } else if (bean.opacity == 1f && MathUtils.random(1f) > 0.75f) {
                bean.animationType = FADE_OUT;
                if (bean.isLinked()) {
                    bean.tile = 0;
                }
            } else if (bean.opacity == 1f && !bean.isLinked()) {
                bean.animationType = SHAKE;
                bean.shakeTime = MathUtils.random(3, 6) * FRAME_TIME * animationTiles.length;
            }

            animationTime = MathUtils.random(1.2f);
        }

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < croppedCols; j++) {
                beans[i][j].update();
            }
        }

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < croppedCols; j++) {
                beanLinks(i, j);
            }
        }
    }

    private Bean randomBean() {
        int i = MathUtils.random(MAX_ROWS - 1);
        int j = MathUtils.random(croppedCols - 1);

        return beans[i][j];
    }

    private void beanLinks(int i, int j) {
        Bean bean = beans[i][j];

        int color = bean.color;
        int tile = 0;

        if (bean.opacity < 1f) {
            return;
        }

        // Check the 4 adjacent blocks if they are linked to center one
        if (i + 1 < beans.length && beans[i + 1][j].color == color && beans[i + 1][j].opacity == 1f)
            tile += 1000;
        if (j + 1 < beans[i].length && beans[i][j + 1].color == color && beans[i][j + 1].opacity == 1f)
            tile += 100;
        if (i - 1 >= 0 && beans[i - 1][j].color == color && beans[i - 1][j].opacity == 1f)
            tile += 10;
        if (j - 1 >= 0 && beans[i][j - 1].color == color && beans[i][j - 1].opacity == 1f)
            tile += 1;

        if (tile > 0) {
            bean.tile = tile;
        } else if (bean.isLinked()) {
            bean.tile = tile;
        }
    }

    public void render() {
        animate();

        TextureAtlas.AtlasRegion tile;

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < croppedCols; j++) {
                Bean bean = beans[i][j];

                if (bean.color == 0) continue;

                tile = G.game.data.BEANS_REG.get(bean.color * 10000 + bean.tile);
                G.game.batch.setColor(1f, 1f, 1f, bean.opacity * 0.14f);
                G.game.batch.draw(tile, bean.px, bean.py, size, size);
            }
        }

        G.game.batch.setColor(1f, 1f, 1f, 1f);
    }
}
