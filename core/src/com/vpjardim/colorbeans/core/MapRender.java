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

    // Todo add shadow in the map left edge
    // Todo Add win/lost camera transition and statistics layer

    public static final IntMap<String> COLORS = new IntMap<>();
    // Todo make the rand field non static
    public static final int[] rand = new int[53];
    public static int randIndex = 0;

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

        for(int i = 0; i < rand.length; i++) {
            rand[i] = MathUtils.random(1, 9);
        }
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

    public GlyphLayout tl = new GlyphLayout();

    public void renderBatch() {

        TextureAtlas.AtlasRegion tile;

        // ===== Draw map bricks =====
        tile = G.game.atlas.findRegion("beans/bricks", 6);

        G.game.batch.setColor(new Color(0x888888ff));

        for(int i = 0; i < m.b.length; i++) {

            for(int j = m.OUT_ROW; j < m.b[i].length; j++) {

                G.game.batch.draw(
                        tile,
                        px + (size * i),
                        py + (j +1 - m.OUT_ROW) * - size,
                        size,
                        size
                );
            }
        }

        G.game.batch.setColor(new Color(0xffffffff));

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

        // ==== Draw border =====
        //G.game.batch.setColor(new Color(0xd0d0d0ff));
        randIndex = 0;

        for(int i = -2; i < m.b.length +1; i++) {

            if(i >= 0 && i < m.b.length) continue;

            for(int j = m.OUT_ROW; j < m.b[0].length; j++) {

                tile = G.game.atlas.findRegion("beans/bricks", rand[randIndex]);

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
        G.game.batch.setColor(new Color(0xffffffff));

        // ==== Draw next blocks =====
        nextPx = px - size;
        nextPy = py - size;

        tile = G.game.atlas.findRegion("beans/bricks", 2);

        G.game.batch.setColor(new Color(0x606060ff));
        G.game.batch.draw(tile, nextPx, nextPy - size, size, size);
        G.game.batch.draw(tile, nextPx, nextPy - size * 2, size, size);
        G.game.batch.setColor(new Color(0xffffffff));

        tile = G.game.atlas.findRegion(COLORS.get(m.pb.nextB2), 0);
        G.game.batch.draw(tile, nextPx, nextPy - size, size, size);

        tile = G.game.atlas.findRegion(COLORS.get(m.pb.nextB1), 0);
        G.game.batch.draw(tile, nextPx, nextPy - size * 2, size, size);

        BitmapFont font1 = G.game.assets.get("dimbo_white.ttf", BitmapFont.class);
        BitmapFont font2 = G.game.assets.get("roboto_shadow.ttf", BitmapFont.class);

        float padd = G.style.fontSizeVSmall;

        // ==== Draw score =====
        font1.draw(G.game.batch, m.scoreStr, px + padd, py - padd);

        // ==== Init var =====
        String txt;
        float w;

        // ==== Draw player name =====
        txt = m.name;
        tl.setText(font1, txt);
        w = tl.width;
        font1.draw(G.game.batch, txt, px - w + (size * m.b.length) - padd, py - padd);

        // ==== Draw next text =====
        txt = "Next";
        tl.setText(font2, txt);
        w = tl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - padd, py - tl.height - padd);

        // ==== Draw wins =====
        txt = "Wins";
        tl.setText(font2, txt);
        w = tl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - padd, nextPy - size * 3);

        txt = Integer.toString(m.winsCount);
        tl.setText(font1, txt);
        w = tl.width;
        font1.draw(G.game.batch, txt, nextPx - w + size - padd, nextPy - size * 3 - tl.height - padd);

        // ==== Draw draw score sum =====
        txt = "Acc";
        tl.setText(font2, txt);
        w = tl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - padd, nextPy - size * 5);

        txt = Integer.toString(m.scoreSum);
        tl.setText(font1, txt);
        w = tl.width;
        font1.draw(G.game.batch, txt, nextPx - w + size - padd, nextPy - size * 5 - tl.height - padd);

        // ==== Draw draw match timer =====
        txt = "Time";
        tl.setText(font2, txt);
        w = tl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - padd, nextPy - size * 7);

        txt = Integer.toString((int)m.matchTimer);
        tl.setText(font1, txt);
        w = tl.width;
        font1.draw(G.game.batch, txt, nextPx - w + size - padd, nextPy - size * 7 - tl.height - padd);
    }
}
