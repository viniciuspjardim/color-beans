/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;

/**
 * @author Vinícius Jardim
 * 02/09/2016
 */
public class MapRender {

    public Map m;

    /**
     * Map top-left corner position in X axis: useful to position
     * multiple maps in one screen
     */
    public float px;

    /**
     * Map top-left corner position in Y axis: useful to position
     * multiple maps in one screen
     */
    public float py;

    /**
     * Size in pixels of each block (equals the diameter).
     */
    public float size;

    public void renderShapes() {

        G.game.sr.setColor(0.1f, 0.2f, 0.25f, 1f);
        G.game.sr.rect(px, py, size * m.N_COL, -size * m.N_ROW);
    }

    public void renderBatch() {

        TextureAtlas.AtlasRegion tile;
        BitmapFont font = G.game.assets.get("roboto_24.ttf", BitmapFont.class);

        font.draw(G.game.batch, m.scoreStr, px + size * 0.1f, py - size * 0.1f);

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
        for(int i = 0; i < m.pb.b.length; i++) {

            for(int j = 0; j < m.pb.b[i].length; j++) {

                if(m.pb.b[i][j].intColor == Block.EMPTY) continue;

                tile = G.game.atlas.findRegion(m.pb.b[i][j].strColor, m.pb.b[i][j].tile +1);

                G.game.batch.draw(
                        tile,
                        px + (i + m.pb.mCol -1 + m.pb.b[i][j].px) * size,
                        py + (j + m.pb.mRow - m.OUT_ROW - m.pb.b[i][j].py) * - size,
                        size,
                        size
                );
            }
        }
    }
}
