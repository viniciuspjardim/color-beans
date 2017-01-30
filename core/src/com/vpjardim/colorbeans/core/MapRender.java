/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
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

    /** Size in pixels of each block (equals the diameter) */
    public float size;

    public GlyphLayout textLayout = new GlyphLayout();

    public void renderShapes() {

        G.game.sr.setColor(new Color(0x1a3340ff));
        G.game.sr.rect(px, py, size * m.N_COL, -size * m.N_ROW);
    }

    public void renderBatch() {

        TextureAtlas.AtlasRegion tile;
        BitmapFont font = G.game.assets.get("dimbo.ttf", BitmapFont.class);

        float padding = size * 0.1f;

        font.draw(G.game.batch, m.scoreStr, px + padding, py - padding);

        textLayout.setText(font, m.name);
        float w = textLayout.width;
        font.draw(G.game.batch, m.name, px - w + (size * m.b.length) - padding, py - padding);

        // Draw map blocks
        for(int i = 0; i < m.b.length; i++) {

            for(int j = m.OUT_ROW; j < m.b[i].length; j++) {

                if(!m.b[i][j].visible) continue;

                tile = G.game.atlas.findRegion(m.b[i][j].strColor, m.b[i][j].tile + 1);

                G.game.batch.draw(
                        tile,
                        px + (size * i),
                        py + (j +1 - m.OUT_ROW - m.b[i][j].py) * - size,
                        size,
                        size
                );
            }
        }

        // Draw play blocks

        tile = G.game.atlas.findRegion(m.pb.b1.strColor, m.pb.b1.tile +1);

        float b1X = px + (m.pb.b1x + m.pb.b1.px) * size;
        float b1Y = py + (m.pb.b1y +1 - m.OUT_ROW - m.pb.b1.py) * - size;
        float ang = -(m.pb.rotationAnim - 0.5f) / 4f * 2f * MathUtils.PI;

        G.game.batch.draw(tile, b1X, b1Y, size, size);

        tile = G.game.atlas.findRegion(m.pb.b2.strColor, m.pb.b2.tile +1);

        float size2 = size * 0.7071f; // 1/sqrt(2) == 0.7071

        G.game.batch.draw(
                tile,
                b1X + (size2 * MathUtils.cos(ang) - size2 * MathUtils.sin(ang)),
                b1Y + (size2 * MathUtils.sin(ang) + size2 * MathUtils.cos(ang)),
                size,
                size
        );
        // Todo review ang and size2 it's a weird code
    }
}
