package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;
import com.vpjardim.colorbeans.input.TapInput;

/**
 * Draws a map in world units, where a bean is 1 x 1. The camera scales it to
 * the screen.
 *
 * <pre>
 *
 * N_ROW + 1 ┌────┬─────────┬─┐
 *           │    │ ceiling │ │
 *     N_ROW ├────┼─────────┼─┤
 *           │Next│         │ │
 *           │Wins│  field  │ │
 *           │Acc │         │ │
 *           │Time│         │ │
 *         0 ├────┼─────────┼─┤
 *           │    │  floor  │ │
 *        -1 └────┴─────────┴─┘
 *          x-2   x     x+N_COL x+N_COL+1
 * </pre>
 */
public class MapRender {
    /** Columns of the side panel, left of the field */
    public static final int PANEL_W = 2;
    /**
     * Columns each map takes when they are side by side. The field's right wall
     * is the first column of the next map's panel
     */
    public static final int SLOT_W = PANEL_W + Map.N_COL;
    /** Height the camera shows: the field plus half the ceiling and half the floor */
    public static final float VIEW_H = Map.N_ROW + 1f;
    /** Text height (the font's em size) */
    public static final float TEXT_SIZE = 0.44f;
    /** Gap between the text and the walls */
    private static final float PAD = 0.15f;

    public Map m;
    /** Field's left edge. Its bottom edge is always y = 0 */
    public float x;
    /** Viewport the map is drawn with: used to find which block a debug tap hits */
    public Viewport viewport;

    // Bricks are picked at random once, so each map looks different and the walls
    // don't change between frames
    /** Field background: [col][y + 1], from the floor row up to the top row */
    private final AtlasRegion[][] darkBricks = new AtlasRegion[Map.N_COL][Map.N_ROW + 1];
    /** Light bricks around the field: [col + PANEL_W][y + 1], see {@link #wall} */
    private final AtlasRegion[][] wallBricks = new AtlasRegion[SLOT_W + 1][Map.N_ROW + 2];
    private final AtlasRegion nextBrick;
    private final AtlasRegion shadowCorner;
    private final AtlasRegion shadowTop;
    private final AtlasRegion shadowLeft;

    private final Vector2 tap = new Vector2();
    private EventListener tapInput;

    public MapRender() {
        TextureAtlas atlas = G.game.atlas;

        for (AtlasRegion[] col : darkBricks) {
            for (int i = 0; i < col.length; i++) {
                col[i] = atlas.findRegion("beans/brick", MathUtils.random(10, 12));
            }
        }

        for (AtlasRegion[] col : wallBricks) {
            for (int i = 0; i < col.length; i++) {
                col[i] = atlas.findRegion("beans/brick", MathUtils.random(1, 9));
            }
        }

        nextBrick = atlas.findRegion("beans/brick", 8);
        shadowCorner = atlas.findRegion("beans/bshadow", 1);
        shadowTop = atlas.findRegion("beans/bshadow", 2);
        shadowLeft = atlas.findRegion("beans/bshadow", 3);

        if (!G.game.dbg.on)
            return;

        // Tap event listener: changes block color on tap event when debugging
        tapInput = (Event e) -> {
            // On touch screen, only change beans color when it's paused
            boolean multiTouch = Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen);
            boolean changeColor = !multiTouch || m.manager.isPaused();

            if (changeColor && m != null) {
                TapInput.Data eventData = (TapInput.Data) e.getAttribute();
                viewport.unproject(tap.set(eventData.x, eventData.y));

                int bTapX = MathUtils.floor(tap.x - x);
                int bTapY = Map.OUT_ROW + Map.N_ROW - 1 - MathUtils.floor(tap.y);

                if (bTapX >= 0 && bTapX < Map.N_COL && bTapY >= 0 && bTapY < Map.N_ROW + Map.OUT_ROW) {
                    Block b = m.b[bTapX][bTapY];
                    int selectedColor = G.game.dbg.selectedColor;

                    // Change the color if the selected color is the same of tapped block
                    if (selectedColor == b.color) {
                        selectedColor = b.color + (eventData.button == 0 ? 1 : -1);

                        if (selectedColor > Block.CLR_T) {
                            selectedColor = Block.EMPTY;
                        } else if (selectedColor < Block.EMPTY) {
                            selectedColor = Block.CLR_T;
                        }
                    }

                    if (selectedColor == Block.EMPTY) {
                        b.setEmpty();
                    } else {
                        b.setColor(selectedColor);
                    }

                    m.mapLinks();
                    G.game.dbg.selectedColor = selectedColor;
                }
            }
        };

        EventHandler.get().addListener("TapInput.tap", tapInput);
    }

    /**
     * Draws what doesn't change during the match. Called for every map before
     * {@link #render()}, so no map's background covers another map's beans
     */
    public void renderBg() {
        SpriteBatch batch = G.game.batch;

        // ===== Draw map dark bg bricks =====
        for (int col = 0; col < Map.N_COL; col++) {
            for (int y = -1; y < Map.N_ROW; y++) {
                batch.draw(darkBricks[col][y + 1], x + col, y, 1f, 1f);
            }
        }

        // ===== Draw map bricks shadow =====
        final float s = 0.5f;
        final float shadowY = Map.N_ROW - s;

        batch.draw(shadowCorner, x, shadowY, s, s);

        for (float i = s; i < Map.N_COL; i += s) {
            batch.draw(shadowTop, x + i, shadowY, s, s);
        }

        for (float i = s; i < Map.N_ROW; i += s) {
            batch.draw(shadowLeft, x, shadowY - i, s, s);
        }

        // ==== Draw side border: light bricks =====
        for (int col = -PANEL_W; col <= Map.N_COL; col++) {
            if (col >= 0 && col < Map.N_COL)
                continue;

            for (int y = 0; y < Map.N_ROW; y++) {
                batch.draw(wall(col, y), x + col, y, 1f, 1f);
            }
        }
    }

    public void render() {
        SpriteBatch batch = G.game.batch;
        IntMap<AtlasRegion> beans = G.game.data.BEANS_REG;

        // ===== Draw map blocks =====
        for (int col = 0; col < Map.N_COL; col++) {
            for (int row = Map.OUT_ROW; row < Map.OUT_ROW + Map.N_ROW; row++) {
                Block block = m.b[col][row];

                if (!block.visible)
                    continue;

                batch.draw(beans.get(block.getRegionKey()), x + col, rowY(row) + block.py, 1f, 1f);
            }
        }

        // ===== Draw player blocks =====
        // Beans still above the ceiling are skipped. The ceiling, drawn later, covers
        // the ones partly behind it
        float b1X = x + m.pb.b1x + m.pb.b1.px;
        float b1Y = rowY(m.pb.b1y) + m.pb.b1.py;

        if (b1Y <= Map.N_ROW) {
            batch.draw(beans.get(m.pb.b1.getRegionKey()), b1X, b1Y, 1f, 1f);
        }

        // b2 orbits b1 as the pair rotates
        float ang = -(m.pb.rotationAnim - 0.5f) / 4f * 2f * MathUtils.PI;
        float r = 0.7071f; // 1/sqrt(2) == 0.7071
        float b2X = b1X + (r * MathUtils.cos(ang) - r * MathUtils.sin(ang));
        float b2Y = b1Y + (r * MathUtils.sin(ang) + r * MathUtils.cos(ang));

        if (b2Y <= Map.N_ROW) {
            batch.draw(beans.get(m.pb.b2.getRegionKey()), b2X, b2Y, 1f, 1f);
        }

        // ==== Draw map ceil and flor that shakes with the beans ====
        for (int col = -PANEL_W; col <= Map.N_COL; col++) {
            batch.draw(wall(col, Map.N_ROW), x + col, Map.N_ROW, 1f, 1f);
        }

        for (int col = -PANEL_W; col <= Map.N_COL; col++) {
            float beansPy = 0f;

            if (col >= 0 && col < Map.N_COL && (m.colShakeTimer[col] != 0f || m.isInState(Map.MState.OVER)))
                beansPy = m.b[col][Map.N_ROW + Map.OUT_ROW - 1].py;

            batch.draw(wall(col, -1), x + col, -1f + beansPy, 1f, 1f);
        }

        // ==== Draw next blocks =====
        float nextX = x - 1f;

        batch.draw(nextBrick, nextX, Map.N_ROW - 2f, 1f, 1f);
        batch.draw(nextBrick, nextX, Map.N_ROW - 3f, 1f, 1f);
        batch.draw(beans.get(m.pb.nextB2 * 10000), nextX, Map.N_ROW - 2f, 1f, 1f);
        batch.draw(beans.get(m.pb.nextB1 * 10000), nextX, Map.N_ROW - 3f, 1f, 1f);

        // ==== Draw texts =====
        BitmapFont font = G.game.data.mapFont;
        BitmapFont labelFont = G.game.data.mapLabelFont;
        float top = Map.N_ROW - PAD;

        // Panel texts are right aligned to the field's left wall
        labelFont.draw(batch, "Next", x - PANEL_W, top - labelFont.getCapHeight(), PANEL_W - PAD,
                Align.right, false);

        font.draw(batch, m.name, x, top, Map.N_COL - PAD, Align.right, false);
        font.draw(batch, m.scoreStr, x + PAD, top);

        drawStat("Wins", m.winsCount, Map.N_ROW - 4f);
        drawStat("Acc", m.scoreSum, Map.N_ROW - 6f);
        drawStat("Time", (int) m.matchTimer, Map.N_ROW - 8f);
    }

    /** Draws a panel label with its value under it. y is the label's top */
    private void drawStat(String label, int value, float y) {
        BitmapFont font = G.game.data.mapFont;
        BitmapFont labelFont = G.game.data.mapLabelFont;

        labelFont.draw(G.game.batch, label, x - PANEL_W, y, PANEL_W - PAD, Align.right, false);
        font.draw(G.game.batch, Integer.toString(value), x - PANEL_W, y - font.getCapHeight() - PAD,
                PANEL_W - PAD, Align.right, false);
    }

    /** Light brick of the walls, ceiling (y = N_ROW) or floor (y = -1) */
    private AtlasRegion wall(int col, int y) {
        return wallBricks[col + PANEL_W][y + 1];
    }

    /**
     * World y of a {@link Map#b} row. Rows count down from the top hidden row,
     * while y counts up from the field's bottom, so the floor row is y = 0
     */
    private static float rowY(int row) {
        return Map.OUT_ROW + Map.N_ROW - 1 - row;
    }

    public void dispose() {
        EventHandler.get().removeListener("TapInput.tap", tapInput);
    }
}
