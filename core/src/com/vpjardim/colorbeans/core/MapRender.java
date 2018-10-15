/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;
import com.vpjardim.colorbeans.input.DebugInput;

/**
 * @author Vinícius Jardim
 * 2016/09/02
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

    public GlyphLayout gl = new GlyphLayout();

    private RandomXS128 rand = new RandomXS128();
    private long seed0 = rand.getState(0);
    private long seed1 = rand.getState(1);

    // # debugCode
    private EventListener debugInput;

    public MapRender() {

        if(!G.game.dbg.on) return;

        // Tap event listener: changes block color on tap event when debugging
        debugInput = (Event e) -> {

            // On android, only change beans color when it's paused
            boolean changeColor = m != null && (
                    (Gdx.app.getType() == Application.ApplicationType.Android && m.pause) ||
                    Gdx.app.getType() == Application.ApplicationType.Desktop
            );

            if(changeColor) {

                DebugInput.Data evData = (DebugInput.Data) e.getAttribute();
                evData.y = G.height - evData.y;

                int bTapX = (int)((evData.x - px) / size);
                int bTapY = (int)((evData.y - (G.height - py)) / size);
                bTapY = Map.OUT_ROW + Map.N_ROW - bTapY - 1;

                // Dbg.dbg(Dbg.tag(this), "tap button = " + evData.button);
                // Dbg.dbg(Dbg.tag(this), "tap => x = " + bTapX + "; y = " + bTapY);

                if(bTapX >= 0 && bTapX < Map.N_COL && bTapY >= 0 && bTapY < Map.N_ROW + Map.OUT_ROW) {
                    Block b = m.b[bTapX][bTapY];
                    int color = b.color + (evData.button == 0 ? 1 : -1);
                    if(color > Block.CLR_T || color == 0) b.setEmpty();
                    else if(color < 0) b.setColor(Block.CLR_T);
                    else b.setColor(color);
                }
            }
        };

        // Todo remove listener when object disposed
        EventHandler.getHandler().addListener("DebugInput.tap", debugInput);
    }

    /**
     * Used to draw only once the elements that don't change during the match (or if the screen is
     * resized)
     */
    public void cacheBg() {

        TextureAtlas.AtlasRegion tile;
        rand.setState(seed0, seed1);

        // ===== Draw map dark bg bricks =====

        for(int i = 0; i < m.b.length; i++) {

            for(int j = Map.OUT_ROW; j < m.b[i].length + 1; j++) {

                tile = G.game.atlas.findRegion("beans/brick", rand() % 3 + 10);

                G.game.batch.draw(
                        tile,
                        px + (size * i),
                        py + (j +1 - Map.OUT_ROW) * - size,
                        size,
                        size
                );
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

        for(int i = -2; i < m.b.length +1; i++) {

            for(int j = Map.OUT_ROW; j < m.b[0].length; j++) {

                if(i >= 0 && i < m.b.length) continue;

                tile = G.game.atlas.findRegion("beans/brick", rand() % 9 + 1);

                G.game.batch.draw(
                        tile,
                        px + (size * i),
                        py + (j +1 - Map.OUT_ROW) * - size,
                        size,
                        size
                );
            }
        }

        nextPx = px - size;
        nextPy = py - size;

        // ==== Init var =====
        float pad = G.style.fontSizeVSmall;
        String txt;
        float w;

        BitmapFont font2 = G.game.data.font2;

        // ==== Draw next text =====
        txt = "Next";
        gl.setText(font2, txt);
        w = gl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - pad, py - gl.height - pad);

        // ==== Draw wins text =====
        txt = "Wins";
        gl.setText(font2, txt);
        w = gl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 3);

        // ==== Draw draw score sum =====
        txt = "Acc";
        gl.setText(font2, txt);
        w = gl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 5);

        // ==== Draw draw match timer =====
        txt = "Time";
        gl.setText(font2, txt);
        w = gl.width;
        font2.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 7);
    }

    public void renderBatch() {

        TextureAtlas.AtlasRegion tile;

        // ===== Draw map blocks =====
        for(int i = 0; i < m.b.length; i++) {

            for(int j = Map.OUT_ROW; j < m.b[i].length; j++) {

                Block block = m.b[i][j];

                if(!m.b[i][j].visible) continue;

                tile = G.game.data.BEANS_REG.get(block.getRegionKey());

                G.game.batch.draw(
                        tile,
                        px + (size * i),
                        py + (j +1 - Map.OUT_ROW - block.py) * - size,
                        size,
                        size
                );
            }
        }

        // ===== Draw player blocks =====
        tile = G.game.data.BEANS_REG.get(m.pb.b1.getRegionKey());

        float b1X = px + (m.pb.b1x + m.pb.b1.px) * size;
        float b1Y = py + (m.pb.b1y +1 - Map.OUT_ROW - m.pb.b1.py) * - size;
        float ang = -(m.pb.rotationAnim - 0.5f) / 4f * 2f * MathUtils.PI;

        G.game.batch.draw(tile, b1X, b1Y, size, size);

        tile = G.game.data.BEANS_REG.get(m.pb.b2.getRegionKey());

        // Todo review ang and size2 it's a weird code
        float size2 = size * 0.7071f; // 1/sqrt(2) == 0.7071

        G.game.batch.draw(
                tile,
                b1X + (size2 * MathUtils.cos(ang) - size2 * MathUtils.sin(ang)),
                b1Y + (size2 * MathUtils.sin(ang) + size2 * MathUtils.cos(ang)),
                size,
                size
        );

        // ==== Draw map ceil and flor that shakes with the beans ====
        rand.setState(seed0, seed1);

        for(int i = -2; i < m.b.length + 1; i++) {

            tile = G.game.atlas.findRegion("beans/brick", rand() % 9 + 1);

            G.game.batch.draw(
                    tile,
                    px + (size * i),
                    py,
                    size,
                    size
            );
        }

        for(int i = -2; i < m.b.length + 1; i++) {

            tile = G.game.atlas.findRegion("beans/brick", rand() % 9 + 1);

            float beansPy;

            if(i >=0 && i < m.b.length && (m.colShakeTimer[i] != 0f || m.isInState(Map.MState.OVER)))
                beansPy = m.b[i][Map.N_ROW + Map.OUT_ROW -1].py * size;
            else
                beansPy = 0f;

            G.game.batch.draw(
                    tile,
                    px + (size * i),
                    py + (m.b[0].length +1 - Map.OUT_ROW) * - size + beansPy,
                    size,
                    size
            );
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

        // ==== Draw player name =====
        txt = m.name;
        gl.setText(font1, txt);
        w = gl.width;
        font1.draw(G.game.batch, txt, px - w + (size * m.b.length) - pad, py - pad);

        // ==== Draw score =====
        font1.draw(G.game.batch, m.scoreStr, px + pad, py - pad);

        // ==== Draw wins =====
        txt = Integer.toString(m.winsCount);
        gl.setText(font1, txt);
        w = gl.width;
        font1.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 3 - gl.height - pad);

        // ==== Draw draw score sum =====
        txt = Integer.toString(m.scoreSum);
        gl.setText(font1, txt);
        w = gl.width;
        font1.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 5 - gl.height - pad);

        // ==== Draw draw match timer =====
        txt = Integer.toString((int)m.matchTimer);
        gl.setText(font1, txt);
        w = gl.width;
        font1.draw(G.game.batch, txt, nextPx - w + size - pad, nextPy - size * 7 - gl.height - pad);
    }

    private int rand() { return (Math.abs(rand.nextInt()) % 12) + 1; }
}
