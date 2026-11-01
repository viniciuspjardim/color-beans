package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
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
 * the screen. A map can also be drawn as a mini map in another map's panel,
 * see {@link #stackMinis(Array)}.
 *
 * <pre>
 *
 * N_ROW + 1 ┌────┬─────────┬─┐
 *           │    │ ceiling │ │
 *     N_ROW ├────┼─────────┼─┤
 *           │Next│         │ │
 *           │Wins│         │ │
 *           │Acc │  field  │ │
 *           │Time│         │ │
 *           │Mini│         │ │
 *         0 ├────┼─────────┼─┤
 *           │    │  floor  │ │
 *        -1 └────┴─────────┴─┘
 *          x-1   x     x+N_COL x+N_COL+1
 * </pre>
 */
public class MapRender {
    /** Columns of the side panel, left of the field */
    public static final int PANEL_W = 1;
    /**
     * Columns each map takes when they are side by side. The field's right wall
     * is the next map's panel
     */
    public static final int SLOT_W = PANEL_W + Map.N_COL;
    /** Visible part of the rightmost map's right wall: half, like the ceiling and floor */
    public static final float RIGHT_WALL_W = 0.5f;
    /** Height the camera shows: the field plus half the ceiling and half the floor */
    public static final float VIEW_H = Map.N_ROW + 1f;
    /** Top of the area the camera shows */
    public static final float VIEW_TOP = Map.N_ROW + 0.5f;
    /** Text height (the font's em size) */
    public static final float TEXT_SIZE = 0.44f;
    /** Smaller text for the side panel's labels */
    private static final float LABEL_SIZE = 0.3f;
    /** Gap between the text and the walls */
    private static final float PAD = 0.15f;
    /** Top of the first stat in the panel, under the next beans */
    private static final float STATS_TOP = Map.N_ROW - 3.25f;
    /** Height of each stat in the panel: a label with its value under it */
    private static final float STAT_H = 1.2f;
    /**
     * Top of the first mini map slot: a brick edge under the stats. Each slot is 2
     * bricks tall, and there's room for 4 above the floor (the most players is 4)
     */
    private static final float MINIS_TOP = Map.N_ROW - 7f;
    private static final float MINI_SLOT_H = 2f;
    /** Side of a mini map's square: 7 of them fill the panel, minus a margin */
    private static final float MINI_CELL = (PANEL_W - 0.14f) / Map.N_COL;
    /** Incoming trash beans that fit the visible half of the ceiling */
    private static final int TRASH_ICONS = Map.N_COL * 2;

    /** Mini map square colors by {@link Block#color}, sampled from the bean sprites */
    private static final Color[] SQUARE_COLORS = {
            null,
            Color.valueOf("c53431"), // Red
            Color.valueOf("0baef6"), // Blue
            Color.valueOf("24c338"), // Green
            Color.valueOf("f09e13"), // Yellow
            Color.valueOf("962abd"), // Purple
            Color.valueOf("9a9a9a"), // Trash
    };
    private static final Color MINI_BG = Color.valueOf("121220");

    public Map m;
    /** Field's left edge. Its bottom edge is always y = 0 */
    public float x;
    /** Also draws the map as a mini map in another map's panel. See {@link #stackMinis(Array)} */
    public boolean mini;
    /** Mini map's bottom left corner */
    public float miniX, miniY;
    /** Viewport the map is drawn with: used to find which block a debug tap hits */
    public Viewport viewport;

    // Bricks are picked at random once, so each map looks different and the walls
    // don't change between frames
    /** Field background: [col][y + 1], from the floor row up to the top row */
    private final AtlasRegion[][] darkBricks = new AtlasRegion[Map.N_COL][Map.N_ROW + 1];
    /** Light bricks around the field: [col + PANEL_W][y + 1], see {@link #wall} */
    private final AtlasRegion[][] wallBricks = new AtlasRegion[SLOT_W + 1][Map.N_ROW + 2];
    private final AtlasRegion nextBrick;
    /** Dark brick behind the incoming trash, which is see-through */
    private final AtlasRegion trashBrick;
    private final AtlasRegion shadowCorner;
    private final AtlasRegion shadowTop;
    private final AtlasRegion shadowLeft;

    /** Player beans' positions relative to the field. See {@link #pairPosition()} */
    private final Vector2 pair1 = new Vector2();
    private final Vector2 pair2 = new Vector2();
    private final Color squareColor = new Color();

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
        trashBrick = atlas.findRegion("beans/brick", 12);
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

            if (changeColor && m != null && !mini) {
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
     * Stacks the given maps as minis in this map's panel, under the stats, each
     * centered in its slot
     */
    public void stackMinis(Array<MapRender> minis) {
        float w = MINI_CELL * Map.N_COL;
        float h = MINI_CELL * Map.N_ROW;

        for (int i = 0; i < minis.size; i++) {
            MapRender r = minis.get(i);
            r.mini = true;
            r.miniX = x - PANEL_W + (PANEL_W - w) / 2f;
            r.miniY = MINIS_TOP - MINI_SLOT_H * i - (MINI_SLOT_H + h) / 2f;
        }
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
        pairPosition();

        if (pair1.y <= Map.N_ROW) {
            batch.draw(beans.get(m.pb.b1.getRegionKey()), x + pair1.x, pair1.y, 1f, 1f);
        }

        if (pair2.y <= Map.N_ROW) {
            batch.draw(beans.get(m.pb.b2.getRegionKey()), x + pair2.x, pair2.y, 1f, 1f);
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

        // ==== Draw incoming trash: a half size bean each on the visible ceiling ====
        BitmapFont font = G.game.data.mapFont;
        BitmapFont labelFont = G.game.data.mapLabelFont;
        int trash = m.getTrashBlocksToAdd();

        if (trash > 0) {
            // When they don't fit, the last 2 icons give room to the count of the others
            int icons = trash <= TRASH_ICONS ? trash : TRASH_ICONS - 2;
            float s = 0.5f;

            for (int i = 0; i < icons; i++) {
                batch.draw(trashBrick, x + i * s, Map.N_ROW, s, s);
                batch.draw(beans.get(Block.CLR_T * 10000), x + i * s, Map.N_ROW, s, s);
            }

            if (icons < trash) {
                batch.draw(trashBrick, x + icons * s, Map.N_ROW, 2f * s, s);
                setTextSize(TEXT_SIZE);
                font.draw(batch, "+" + (trash - icons), x + icons * s, Map.N_ROW + (s + font.getCapHeight()) / 2f,
                        2f * s, Align.center, false);
            }
        }

        // ==== Draw texts =====
        float top = Map.N_ROW - PAD;

        setTextSize(TEXT_SIZE);
        font.draw(batch, m.name, x, top, Map.N_COL - PAD, Align.right, false);
        font.draw(batch, m.scoreStr, x + PAD, top);

        setTextSize(LABEL_SIZE);
        labelFont.draw(batch, "Next", x - PANEL_W, top - labelFont.getCapHeight(), PANEL_W, Align.center,
                false);

        drawStat("Wins", m.winsCount, STATS_TOP);
        drawStat("Acc", m.scoreSum, STATS_TOP - STAT_H);
        drawStat("Time", (int) m.matchTimer, STATS_TOP - 2f * STAT_H);
    }

    /**
     * Draws the map at {@link #miniX}, {@link #miniY} as flat squares, with no
     * walls or text. Dimmed after the map loses
     */
    public void renderMini(ShapeRenderer sr) {
        boolean lost = m.isInState(Map.MState.OVER) || m.isInState(Map.MState.DONE);

        sr.setColor(MINI_BG);
        sr.rect(miniX, miniY, MINI_CELL * Map.N_COL, MINI_CELL * Map.N_ROW);

        for (int col = 0; col < Map.N_COL; col++) {
            for (int row = Map.OUT_ROW; row < Map.OUT_ROW + Map.N_ROW; row++) {
                Block block = m.b[col][row];

                if (block.visible)
                    square(sr, block.color, col, rowY(row) + block.py, lost);
            }
        }

        pairPosition();
        square(sr, m.pb.b1.color, pair1.x, pair1.y, lost);
        square(sr, m.pb.b2.color, pair2.x, pair2.y, lost);
    }

    /** Draws a mini map square at a field position, if it's whole inside the field */
    private void square(ShapeRenderer sr, int color, float col, float fieldY, boolean lost) {
        if (fieldY < 0f || fieldY > Map.N_ROW - 1f || color < Block.CLR_A || color > Block.CLR_T)
            return;

        float dim = lost ? 0.4f : 1f;
        float gap = MINI_CELL * 0.1f;
        float side = MINI_CELL - gap;

        sr.setColor(squareColor.set(SQUARE_COLORS[color]).mul(dim, dim, dim, 1f));
        sr.rect(miniX + col * MINI_CELL + gap / 2f, miniY + fieldY * MINI_CELL + gap / 2f, side, side);
    }

    /** Sets {@link #pair1} and {@link #pair2} to where the player beans are */
    private void pairPosition() {
        pair1.set(m.pb.b1x + m.pb.b1.px, rowY(m.pb.b1y) + m.pb.b1.py);

        // b2 orbits b1 as the pair rotates
        float ang = -(m.pb.rotationAnim - 0.5f) / 4f * 2f * MathUtils.PI;
        float r = 0.7071f; // 1/sqrt(2) == 0.7071
        pair2.set(pair1.x + (r * MathUtils.cos(ang) - r * MathUtils.sin(ang)),
                pair1.y + (r * MathUtils.sin(ang) + r * MathUtils.cos(ang)));
    }

    /** Draws a panel label with its value under it. y is the label's top */
    private void drawStat(String label, int value, float y) {
        BitmapFont font = G.game.data.mapFont;
        BitmapFont labelFont = G.game.data.mapLabelFont;

        // Big numbers are shortened to fit the panel: 12345 -> 12k
        String valueStr;

        if (value < 10000)
            valueStr = Integer.toString(value);
        else if (value < 1000000)
            valueStr = value / 1000 + "k";
        else
            valueStr = value / 1000000 + "M";

        setTextSize(LABEL_SIZE);
        labelFont.draw(G.game.batch, label, x - PANEL_W, y, PANEL_W, Align.center, false);
        float valueY = y - labelFont.getCapHeight() - PAD;

        setTextSize(TEXT_SIZE);
        font.draw(G.game.batch, valueStr, x - PANEL_W, valueY, PANEL_W, Align.center, false);
    }

    /** Sets the map fonts' height in world units (their em size) */
    private static void setTextSize(float size) {
        float scale = size / G.style.fontSizeBig;
        G.game.data.mapFont.getData().setScale(scale);
        G.game.data.mapLabelFont.getData().setScale(scale);
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
