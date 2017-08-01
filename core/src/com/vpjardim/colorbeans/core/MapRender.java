/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public class MapRender {

    public Map m;

    /** Map top-left corner position in X axis: useful to position multiple maps in one screen */
    public float px;

    /** Map top-left corner position in Y axis: useful to position multiple maps in one screen */
    public float py;

    /** Next blocks position in X axis */
    public float nextPx;

    /** Next blocks position in Y axis */
    public float nextPy;

    /** Size in pixels of each block (equals the diameter) */
    public float size;

    public final int[] rand = new int[105];
    public int randIndex = 0;

    public GlyphLayout tl = new GlyphLayout();

    public MapRender() {
        for(int i = 0; i < rand.length; i++) {
            rand[i] = MathUtils.random(1, 12);
        }
    }

    public void cacheBg() {

        TextureAtlas.AtlasRegion tile;

        // ===== Draw map dark bg bricks =====
        randIndex = 0;

        for(int i = 0; i < m.b.length; i++) {

            for(int j = m.OUT_ROW; j < m.b[i].length + 1; j++) {

                tile = G.game.atlas.findRegion("beans/brick", rand[randIndex] % 3 + 10);

                G.game.batch.draw(
                        tile,
                        px + (size * i),
                        py + (j +1 - m.OUT_ROW) * - size,
                        size,
                        size
                );

                if(randIndex < rand.length -1) randIndex++;
                else randIndex = 0;
            }
        }

        // ===== Draw map bricks shadow =====
        tile = G.game.atlas.findRegion("beans/bshadow", 1);
        G.game.batch.draw(tile, px, py - size / 2f, size / 2f, size / 2f);

        tile = G.game.atlas.findRegion("beans/bshadow", 2);
        for(float i = 0.5f; i < m.b.length; i = i + 0.5f) {
            G.game.batch.draw(tile, px + (size * i), py - size / 2f, size / 2f, size / 2f);
        }

        tile = G.game.atlas.findRegion("beans/bshadow", 3);
        for(float i = 0.5f; i < m.b[0].length - Map.OUT_ROW; i = i + 0.5f) {
            G.game.batch.draw(tile, px, py - size / 2f - (size * i), size / 2f, size / 2f);
        }

        // ==== Draw side border: light bricks =====
        randIndex = 0;

        for(int i = -2; i < m.b.length +1; i++) {

            for(int j = m.OUT_ROW; j < m.b[0].length; j++) {

                if(i >= 0 && i < m.b.length) continue;

                tile = G.game.atlas.findRegion("beans/brick", rand[randIndex] % 9 + 1);

                G.game.batch.draw(
                        tile,
                        px + (size * i),
                        py + (j +1 - m.OUT_ROW) * - size,
                        size,
                        size
                );

                if(randIndex < rand.length -1) randIndex++;
                else randIndex = 0;
            }
        }
    }

    public void renderBatch() {

        TextureAtlas.AtlasRegion tile;

        // ===== Draw map blocks =====
        for(int i = 0; i < m.b.length; i++) {

            for(int j = m.OUT_ROW; j < m.b[i].length; j++) {

                Block block = m.b[i][j];

                if(!m.b[i][j].visible) continue;

                tile = G.game.data.BEANS_REG.get(block.getRegionKey());

                G.game.batch.draw(
                        tile,
                        px + (size * i),
                        py + (j +1 - m.OUT_ROW - block.py) * - size,
                        size,
                        size
                );
            }
        }

        randIndex = 0;

        // ===== Draw player blocks =====
        tile = G.game.data.BEANS_REG.get(m.pb.b1.getRegionKey());

        float b1X = px + (m.pb.b1x + m.pb.b1.px) * size;
        float b1Y = py + (m.pb.b1y +1 - m.OUT_ROW - m.pb.b1.py) * - size;
        float ang = -(m.pb.rotationAnim - 0.5f) / 4f * 2f * MathUtils.PI;

        G.game.batch.draw(tile, b1X, b1Y, size, size);

        tile = G.game.data.BEANS_REG.get(m.pb.b2.getRegionKey());

        float size2 = size * 0.7071f; // 1/sqrt(2) == 0.7071

        G.game.batch.draw(
                tile,
                b1X + (size2 * MathUtils.cos(ang) - size2 * MathUtils.sin(ang)),
                b1Y + (size2 * MathUtils.sin(ang) + size2 * MathUtils.cos(ang)),
                size,
                size
        );
        // Todo review ang and size2 it's a weird code

        // ==== Draw map ceil and flor that shakes with the beans ====
        for(int i = -2; i < m.b.length + 1; i++) {

            tile = G.game.atlas.findRegion("beans/brick", rand[randIndex] % 9 + 1);

            G.game.batch.draw(
                    tile,
                    px + (size * i),
                    py,
                    size,
                    size
            );

            if(randIndex < rand.length -1) randIndex++;
            else randIndex = 0;
        }

        for(int i = -2; i < m.b.length + 1; i++) {

            tile = G.game.atlas.findRegion("beans/brick", rand[randIndex] % 9 + 1);

            float beansPy;

            if(i >=0 && i < m.b.length && (m.colShakeTimer[i] != 0f || m.isInState(Map.MState.OVER)))
                beansPy = m.b[i][Map.N_ROW + Map.OUT_ROW -1].py * size;
            else
                beansPy = 0f;

            G.game.batch.draw(
                    tile,
                    px + (size * i),
                    py + (m.b[0].length +1 - m.OUT_ROW) * - size + beansPy,
                    size,
                    size
            );

            if(randIndex < rand.length -1) randIndex++;
            else randIndex = 0;
        }

        // ==== Draw next blocks =====
        nextPx = px - size;
        nextPy = py - size;

        tile = G.game.atlas.findRegion("beans/brick", 8);

        G.game.batch.draw(tile, nextPx, nextPy - size, size, size);
        G.game.batch.draw(tile, nextPx, nextPy - size * 2, size, size);

        tile = G.game.data.BEANS_REG.get(m.pb.nextB2 * 10000);
        G.game.batch.draw(tile, nextPx, nextPy - size, size, size);

        tile = G.game.data.BEANS_REG.get(m.pb.nextB1 * 10000);
        G.game.batch.draw(tile, nextPx, nextPy - size * 2, size, size);

        // ==== Init var =====
        float pad = G.style.fontSizeVSmall;
        String txt;
        float w;

        BitmapFont font1 = G.game.data.font1;
        BitmapFont font2 = G.game.data.font2;

        // ==== Draw score =====
        font1.draw(G.game.batch, m.scoreStr, px + pad, py - pad);

        // ==== Draw player name =====
        txt = m.name;
        tl.setText(font1, txt);
        w = tl.width;
        font1.draw(G.game.batch, txt, px - w + (size * m.b.length) - pad, py - pad);

        // ==== Draw next text =====
        txt = "Next";
        tl.setText(font2, txt);
        w = tl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - pad, py - tl.height - pad);

        // ==== Draw wins =====
        txt = "Wins";
        tl.setText(font2, txt);
        w = tl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 3);

        txt = Integer.toString(m.winsCount);
        tl.setText(font1, txt);
        w = tl.width;
        font1.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 3 - tl.height - pad);

        // ==== Draw draw score sum =====
        txt = "Acc";
        tl.setText(font2, txt);
        w = tl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 5);

        txt = Integer.toString(m.scoreSum);
        tl.setText(font1, txt);
        w = tl.width;
        font1.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 5 - tl.height - pad);

        // ==== Draw draw match timer =====
        txt = "Time";
        tl.setText(font2, txt);
        w = tl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 7);

        txt = Integer.toString((int)m.matchTimer);
        tl.setText(font1, txt);
        w = tl.width;
        font1.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 7 - tl.height - pad);
    }
}
