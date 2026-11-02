package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
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
    /**
     * Height that fits between the notch and the buttons: the field plus half the
     * ceiling and half the floor. The other halves show where the screen has room
     */
    public static final float VIEW_H = Map.N_ROW + 1f;
    /** Top of the ceiling's half that shows the incoming trash */
    public static final float VIEW_TOP = Map.N_ROW + 0.5f;
    /** Text height (the font's em size) */
    public static final float TEXT_SIZE = 0.44f;
    /** Smaller text for the side panel's labels */
    private static final float LABEL_SIZE = 0.3f;
    /** Gap between the text and the walls */
    private static final float PAD = 0.15f;
    /**
     * Bottom of the panel brick of the first stat, under the next beans. Each stat
     * takes the brick under the previous one
     */
    private static final float STATS_Y = Map.N_ROW - 4f;
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

    // ===== Match results, see renderResults =====
    /** Middle of the title */
    private static final float TITLE_Y = Map.N_ROW - 3.3f;
    /**
     * Height of the title's capital letters, and the same in the title images,
     * which are drawn in to_pack/results/*.svg
     */
    private static final float TITLE_CAP_H = 1.7f;
    private static final float TITLE_CAP_PX = 170f;
    /** Tallest a title can be, with its outline and shadow */
    private static final float TITLE_MAX_H = 2.7f;
    private static final float TITLE_POP_TIME = 0.45f;
    /** Top of the first stat line, and the step to the next */
    private static final float STATS_TOP = Map.N_ROW - 5.3f;
    private static final float STATS_LINE_H = 0.9f;
    private static final float RESULT_LABEL_SIZE = 0.4f;
    private static final float RESULT_VALUE_SIZE = 0.55f;
    /** Seconds before the first stat shows, and between the next ones */
    private static final float STATS_DELAY = 0.35f;
    private static final float STATS_STEP = 0.15f;
    /** Seconds the score takes to count up */
    private static final float COUNT_TIME = 0.8f;
    /** Gap between the results' panel edges and what's in it, and the field's walls */
    private static final float PANEL_PAD = 0.45f;
    private static final float PANEL_MARGIN = 0.3f;
    private static final float PANEL_FADE_TIME = 0.2f;

    /** World units in a UI (stage) unit. The play screen keeps it current */
    public static float uiToWorld = 0.02f;
    /**
     * Height of the results' buttons on this map, in world units, which the panel
     * makes room for. 0 on the maps without them. Set by the play screen
     */
    public float resultsMenuH;
    /** Darker wall bricks: purple, steel blue and dark green */
    private static final int[] DARK_WALLS = { 6, 8, 9 };

    public Map m;
    /** Field's left edge. Its bottom edge is always y = 0 */
    public float x;
    /** Also draws the map as a mini map in another map's panel. See {@link #stackMinis(Array)} */
    public boolean mini;
    /** Mini map's bottom left corner */
    public float miniX, miniY;
    /** Bottom left corner of the mini map's slot, 1 brick wide and 2 tall */
    private float slotX, slotY;
    /** Viewport the map is drawn with: used to find which block a debug tap hits */
    public Viewport viewport;

    // Bricks are picked at random once, so each map looks different and the walls
    // don't change between frames
    /** Field background: [col][y + 1], from the floor row up to the top row */
    private final AtlasRegion[][] darkBricks = new AtlasRegion[Map.N_COL][Map.N_ROW + 1];
    /** Light bricks around the field: [col + PANEL_W][y + 1], see {@link #wall} */
    private final AtlasRegion[][] wallBricks = new AtlasRegion[SLOT_W + 1][Map.N_ROW + 2];
    private final AtlasRegion nextBrick;
    /** Brick behind the map when it's a mini in another map's panel */
    private final AtlasRegion miniBrick;
    private final AtlasRegion shadowCorner;
    private final AtlasRegion shadowTop;
    private final AtlasRegion shadowLeft;
    private final AtlasRegion winTitle;
    private final AtlasRegion loseTitle;
    private final AtlasRegion gameOverTitle;
    private final AtlasRegion drawTitle;

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

        // The ceiling and the panel bricks with text get only the darker colors, so
        // the text and the incoming trash stand out
        for (int col = -PANEL_W; col <= Map.N_COL; col++) {
            wallBricks[col + PANEL_W][Map.N_ROW + 1] = darkWall(atlas);
        }

        float[] textYs = { Map.N_ROW - 1f, STATS_Y, STATS_Y - 1f, STATS_Y - 2f };

        for (float y : textYs) {
            wallBricks[0][(int) y + 1] = darkWall(atlas);
        }

        nextBrick = atlas.findRegion("beans/brick", 8);
        miniBrick = atlas.findRegion("beans/brick", 1);
        shadowCorner = atlas.findRegion("beans/bshadow", 1);
        shadowTop = atlas.findRegion("beans/bshadow", 2);
        shadowLeft = atlas.findRegion("beans/bshadow", 3);
        winTitle = atlas.findRegion("results/win");
        loseTitle = atlas.findRegion("results/lose");
        gameOverTitle = atlas.findRegion("results/game_over");
        drawTitle = atlas.findRegion("results/draw");

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
            r.slotX = x - PANEL_W;
            r.slotY = MINIS_TOP - MINI_SLOT_H * (i + 1);
            r.miniX = r.slotX + (PANEL_W - w) / 2f;
            r.miniY = r.slotY + (MINI_SLOT_H - h) / 2f;
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

        // ===== Draw the mini map's slot, over the other map's panel =====
        // Here and not in renderBg, which draws that panel after it when the other
        // map comes later
        if (mini) {
            batch.draw(miniBrick, slotX, slotY, PANEL_W, 1f);
            batch.draw(miniBrick, slotX, slotY + 1f, PANEL_W, 1f);
        }

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
        // the ones partly behind it. None after the match ends
        pairPosition();
        boolean pairShown = !m.isInState(Map.MState.DONE);

        if (pairShown && pair1.y <= Map.N_ROW) {
            batch.draw(beans.get(m.pb.b1.getRegionKey()), x + pair1.x, pair1.y, 1f, 1f);
        }

        if (pairShown && pair2.y <= Map.N_ROW) {
            batch.draw(beans.get(m.pb.b2.getRegionKey()), x + pair2.x, pair2.y, 1f, 1f);
        }

        m.anim.sparks.render(batch, x);

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
                batch.draw(beans.get(Block.CLR_T * 10000), x + i * s, Map.N_ROW, s, s);
            }

            if (icons < trash) {
                setTextSize(font, TEXT_SIZE);
                font.draw(batch, "+" + (trash - icons), x + icons * s, Map.N_ROW + (s + font.getCapHeight()) / 2f,
                        2f * s, Align.center, false);
            }
        }

        // ==== Draw texts =====
        float top = Map.N_ROW - PAD;

        setTextSize(font, TEXT_SIZE);
        font.draw(batch, m.name, x, top, Map.N_COL - PAD, Align.right, false);
        font.draw(batch, m.scoreStr, x + PAD, top);

        // Text is drawn down from the top of its capitals. "Next" sits at the bottom of
        // its brick, over the beans it labels. The stats are centered in theirs
        setTextSize(labelFont, LABEL_SIZE);
        labelFont.draw(batch, "Next", x - PANEL_W, Map.N_ROW - 1f + PAD + labelFont.getCapHeight(), PANEL_W,
                Align.center, false);

        drawStat("Wins", m.winsCount, STATS_Y);
        drawStat("Acc", m.scoreSum, STATS_Y - 1f);
        drawStat("Time", (int) m.matchTimer, STATS_Y - 2f);

        if (m.manager.phase == MapManager.PHASE_RESULTS && resultsShown())
            renderResults(m.manager.phaseTime);
    }

    /**
     * Draws the match result over the field, emptied by then: a title that pops in,
     * then the stats one by one, with the score counting up
     */
    private void renderResults(float time) {
        SpriteBatch batch = G.game.batch;
        MapManager manager = m.manager;

        AtlasRegion title;

        if (m.gameWin)
            title = winTitle;
        else if (manager.maps.size == 1)
            title = gameOverTitle;
        else if (manager.winners.size == 0)
            title = drawTitle;
        else
            title = loseTitle;

        // Same letter size for all, unless it's too tall, like the 2 lines of game over
        float titleScale = Math.min(TITLE_CAP_H / TITLE_CAP_PX, TITLE_MAX_H / title.getRegionHeight());
        float titleW = title.getRegionWidth() * titleScale;
        float titleH = title.getRegionHeight() * titleScale;

        // ===== The pause menu's panel behind it all, fading in =====
        float bottom = resultsMenuH > 0f
                ? resultsMenuTop() - resultsMenuH - PANEL_PAD
                : statsBottom() - PANEL_PAD;

        // The title's outline and shadow pad it already
        float top = TITLE_Y + titleH / 2f + PANEL_PAD / 2f;
        float panelX = x + PANEL_MARGIN;
        float panelW = Map.N_COL - 2f * PANEL_MARGIN;
        // Its corners are sized like the pause menu's, which is in UI units
        float s = uiToWorld;

        batch.setColor(1f, 1f, 1f, Math.min(time / PANEL_FADE_TIME, 1f));
        G.game.skin.get("bgBlack", NinePatch.class).draw(batch, panelX, bottom, 0f, 0f, panelW / s,
                (top - bottom) / s, s, s, 0f);
        batch.setColor(Color.WHITE);

        // The title pops in, then the winner's keeps pulsing
        float pop = Interpolation.swingOut.apply(Math.min(time / TITLE_POP_TIME, 1f));
        float pulse = m.gameWin ? 1f + 0.05f * MathUtils.sin(time * 5f) : 1f;
        float midX = x + Map.N_COL / 2f;

        batch.draw(title, midX - titleW / 2f, TITLE_Y - titleH / 2f, titleW / 2f, titleH / 2f, titleW, titleH,
                pop * pulse, pop * pulse, 0f);

        // Score counts up from 0
        float count = Interpolation.pow2Out.apply(MathUtils.clamp((time - STATS_DELAY) / COUNT_TIME, 0f, 1f));
        int line = 0;

        drawResult("Score", Integer.toString((int) (m.score * count)), line++, time);
        drawResult("Max chain", Integer.toString(m.maxChain), line++, time);

        if (manager.maps.size > 1)
            drawResult("Trash sent", Integer.toString(m.trashSent), line++, time);

        int seconds = (int) m.matchTimer;
        drawResult("Time", seconds / 60 + ":" + (seconds % 60 < 10 ? "0" : "") + seconds % 60, line, time);
    }

    /** Only the players get results, unless the AI plays alone */
    private boolean resultsShown() {
        return m.ai == null || !m.manager.playersPlayed();
    }

    private int statLines() {
        return m.manager.maps.size > 1 ? 4 : 3;
    }

    /** Bottom of the last stat line */
    private float statsBottom() {
        return STATS_TOP - (statLines() - 1) * STATS_LINE_H - STATS_LINE_H / 2f;
    }

    /** Top of the results' buttons (see PlayScreen), under the stats */
    public float resultsMenuTop() {
        return statsBottom() - 0.2f;
    }

    /** Draws a line of the results, fading in after the ones above it */
    private void drawResult(String label, String value, int line, float time) {
        float alpha = MathUtils.clamp((time - STATS_DELAY - line * STATS_STEP) / STATS_STEP, 0f, 1f);

        if (alpha == 0f)
            return;

        BitmapFont font = G.game.data.mapFont;
        BitmapFont labelFont = G.game.data.mapLabelFont;
        float top = STATS_TOP - line * STATS_LINE_H;
        float pad = 0.6f;

        setTextSize(labelFont, RESULT_LABEL_SIZE);
        setTextSize(font, RESULT_VALUE_SIZE);
        labelFont.setColor(1f, 1f, 1f, alpha);
        font.setColor(1f, 1f, 1f, alpha);

        // Both centered on the line's middle
        float mid = top - font.getCapHeight() / 2f;
        labelFont.draw(G.game.batch, label, x + pad, mid + labelFont.getCapHeight() / 2f);
        font.draw(G.game.batch, value, x + pad, top, Map.N_COL - 2f * pad, Align.right, false);

        labelFont.setColor(Color.WHITE);
        font.setColor(Color.WHITE);
    }

    /**
     * Draws the map at {@link #miniX}, {@link #miniY} as flat squares, with no
     * walls or text. Dimmed after the map loses
     */
    public void renderMini(ShapeRenderer sr) {
        boolean lost = Map.lost(m);

        sr.setColor(MINI_BG);
        sr.rect(miniX, miniY, MINI_CELL * Map.N_COL, MINI_CELL * Map.N_ROW);

        for (int col = 0; col < Map.N_COL; col++) {
            for (int row = Map.OUT_ROW; row < Map.OUT_ROW + Map.N_ROW; row++) {
                Block block = m.b[col][row];

                if (block.visible)
                    square(sr, block.color, col, rowY(row) + block.py, lost);
            }
        }

        if (m.isInState(Map.MState.DONE))
            return;

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

    /**
     * Draws a panel label with its value under it, centered together in the brick
     * with bottom at y
     */
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

        setTextSize(labelFont, LABEL_SIZE);
        setTextSize(font, TEXT_SIZE);
        float labelH = labelFont.getCapHeight();
        float top = y + (1f + labelH + PAD + font.getCapHeight()) / 2f;

        labelFont.draw(G.game.batch, label, x - PANEL_W, top, PANEL_W, Align.center, false);
        font.draw(G.game.batch, valueStr, x - PANEL_W, top - labelH - PAD, PANEL_W, Align.center, false);
    }

    /** Sets a map font's height in world units (its em size) */
    private static void setTextSize(BitmapFont font, float size) {
        font.getData().setScale(size / G.style.fontSizeBig);
    }

    /** Random dark purple, steel blue or dark green wall brick */
    private static AtlasRegion darkWall(TextureAtlas atlas) {
        return atlas.findRegion("beans/brick", DARK_WALLS[MathUtils.random(DARK_WALLS.length - 1)]);
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
