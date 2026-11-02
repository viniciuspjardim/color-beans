package com.vpjardim.colorbeans.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;
import com.vpjardim.colorbeans.G;

/**
 * Small beans that burst out of a popping bean, fly up and fall away while they
 * fade. Positions are in field units, like {@link com.vpjardim.colorbeans.Block}
 * positions: a bean is 1 x 1 and the floor row is y = 0
 */
public class Sparks {
    /** Most sparks alive at once. A new burst replaces the oldest ones when it's full */
    private static final int MAX = 160;
    private static final int PER_BURST = 3;
    /** Rows per second squared */
    private static final float GRAVITY = -28f;
    /** Seconds of life the fade out takes, at the end */
    private static final float FADE_TIME = 0.3f;

    private final float[] x = new float[MAX];
    private final float[] y = new float[MAX];
    private final float[] vx = new float[MAX];
    private final float[] vy = new float[MAX];
    private final float[] size = new float[MAX];
    private final float[] angle = new float[MAX];
    private final float[] spin = new float[MAX];
    /** Seconds left. 0 is a free slot */
    private final float[] life = new float[MAX];
    private final int[] color = new int[MAX];
    /** Next slot to use, round the pool */
    private int next;

    /** Throws a few sparks of the bean color from the bean centered at x, y */
    public void burst(float cx, float cy, int beanColor) {
        for (int n = 0; n < PER_BURST; n++) {
            int i = next;
            next = (next + 1) % MAX;

            x[i] = cx;
            y[i] = cy;
            vx[i] = MathUtils.random(-3.5f, 3.5f);
            vy[i] = MathUtils.random(5f, 11f);
            size[i] = MathUtils.random(0.3f, 0.5f);
            angle[i] = MathUtils.random(360f);
            spin[i] = MathUtils.random(-540f, 540f);
            life[i] = MathUtils.random(0.6f, 1f);
            color[i] = beanColor;
        }
    }

    public void update() {
        float delta = G.delta;

        for (int i = 0; i < MAX; i++) {
            if (life[i] <= 0f)
                continue;

            life[i] = Math.max(life[i] - delta, 0f);
            vy[i] += GRAVITY * delta;
            x[i] += vx[i] * delta;
            y[i] += vy[i] * delta;
            angle[i] += spin[i] * delta;
        }
    }

    /** Draws the sparks with the field's left edge at fieldX */
    public void render(SpriteBatch batch, float fieldX) {
        IntMap<AtlasRegion> beans = G.game.data.BEANS_REG;

        for (int i = 0; i < MAX; i++) {
            if (life[i] <= 0f)
                continue;

            float s = size[i];
            batch.setColor(1f, 1f, 1f, Math.min(life[i] / FADE_TIME, 1f));
            batch.draw(beans.get(color[i] * 10000), fieldX + x[i] - s / 2f, y[i] - s / 2f, s / 2f, s / 2f,
                    s, s, 1f, 1f, angle[i]);
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void clear() {
        for (int i = 0; i < MAX; i++) {
            life[i] = 0f;
        }
    }
}
