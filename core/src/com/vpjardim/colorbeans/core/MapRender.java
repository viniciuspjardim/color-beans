/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public class MapRender {

    public static final IntMap<String> COLORS = new IntMap<>();

    static {
        COLORS.put(1, "beans/red");
        COLORS.put(2, "beans/blue");
        COLORS.put(3, "beans/green");
        COLORS.put(4, "beans/yellow");
        COLORS.put(5, "beans/purple");
        COLORS.put(6, "beans/dblue");
        COLORS.put(7, "beans/orange");
        COLORS.put(8, "beans/magenta");
        COLORS.put(9, "beans/transparent");
    }

    public Map m;
    public Color bgColor = new Color(0x1a3340ff);

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

    public GlyphLayout textLayout = new GlyphLayout();

    public void renderShapes() {

        G.game.sr.setColor(bgColor);
        G.game.sr.rect(px, py, size * m.N_COL, -size * m.N_ROW);

        nextPx = px - size * 1.1f;
        nextPy = py - size;

        G.game.sr.rect(nextPx, nextPy + size, size, - size * 2);
    }

    public void renderBatch() {

        TextureAtlas.AtlasRegion tile;
        BitmapFont font = G.game.assets.get("dimbo.ttf", BitmapFont.class);

        float padding = size * 0.1f;

        font.draw(G.game.batch, m.scoreStr, px + padding, py - padding);

        textLayout.setText(font, m.name);
        float w = textLayout.width;
        font.draw(G.game.batch, m.name, px - w + (size * m.b.length) - padding, py - padding);

        // ===== Draw map blocks =====
        for(int i = 0; i < m.b.length; i++) {

            for(int j = m.OUT_ROW; j < m.b[i].length; j++) {

                Block block = m.b[i][j];

                if(!m.b[i][j].visible) continue;

                tile = G.game.atlas.findRegion(COLORS.get(block.color), block.tile);

                G.game.batch.draw(
                        tile,
                        px + (size * i),
                        py + (j +1 - m.OUT_ROW - block.py) * - size,
                        size,
                        size
                );
            }
        }

        // ===== Draw player blocks =====

        tile = G.game.atlas.findRegion(COLORS.get(m.pb.b1.color), m.pb.b1.tile);

        float b1X = px + (m.pb.b1x + m.pb.b1.px) * size;
        float b1Y = py + (m.pb.b1y +1 - m.OUT_ROW - m.pb.b1.py) * - size;
        float ang = -(m.pb.rotationAnim - 0.5f) / 4f * 2f * MathUtils.PI;

        G.game.batch.draw(tile, b1X, b1Y, size, size);

        tile = G.game.atlas.findRegion(COLORS.get(m.pb.b2.color), m.pb.b2.tile);

        float size2 = size * 0.7071f; // 1/sqrt(2) == 0.7071

        G.game.batch.draw(
                tile,
                b1X + (size2 * MathUtils.cos(ang) - size2 * MathUtils.sin(ang)),
                b1Y + (size2 * MathUtils.sin(ang) + size2 * MathUtils.cos(ang)),
                size,
                size
        );
        // Todo review ang and size2 it's a weird code

        // ==== Draw next blocks =====

        nextPx = px - size * 1.1f;
        nextPy = py - size;

        tile = G.game.atlas.findRegion(COLORS.get(m.pb.nextB2), 0);
        G.game.batch.draw(tile, nextPx, nextPy, size, size);

        tile = G.game.atlas.findRegion(COLORS.get(m.pb.nextB1), 0);
        G.game.batch.draw(tile, nextPx, nextPy - size, size, size);

        // ==== Draw wins =====
        font.draw(G.game.batch, Integer.toString(m.winsCount), nextPx, nextPy - size * 1.25f);

        // ==== Draw draw score sum =====
        font.draw(G.game.batch, Integer.toString(m.scoreSum), nextPx, nextPy - size * 2.25f);

        // ==== Draw draw match timer =====
        font.draw(G.game.batch, Integer.toString((int)m.matchTimer), nextPx, nextPy - size * 3.25f);
    }
}
