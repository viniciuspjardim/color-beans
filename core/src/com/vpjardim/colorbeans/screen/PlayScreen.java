package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.core.Audio;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.core.MapManager;
import com.vpjardim.colorbeans.core.MapRender;
import com.vpjardim.colorbeans.core.ScoreTable;
import com.vpjardim.colorbeans.core.Store;
import com.vpjardim.colorbeans.defaults.Db;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;
import com.vpjardim.colorbeans.input.TouchInput;

public class PlayScreen extends ScreenBase {
    public static final int ACT_MENU = 10;
    public static final int ACT_CREDITS = 11;
    /** Drops the saved match for a new one, see {@link #choosing} */
    public static final int ACT_NEW_GAME = 12;
    /** Seconds the camera takes to slide in at the start */
    private static final float INTRO_TIME = 1.75f;
    /** Seconds the camera takes to slide to another map */
    private static final float SLIDE_TIME = 0.3f;
    /**
     * Seconds into the results before their buttons show, so a key still being
     * mashed from the match doesn't skip them
     */
    private static final float RESULTS_BUTTONS_DELAY = 1.2f;
    /** Least room under the results' buttons, down to the floor, in world units */
    private static final float RESULTS_FLOOR_GAP = 0.8f;
    /** Widest the results' buttons can be, in world units, inside their panel */
    private static final float RESULTS_MAX_W = Map.N_COL - 1.4f;

    public final MapManager manager;
    /** Camera for the maps, in world units where a bean is 1 x 1. See {@link MapRender} */
    private OrthographicCamera worldCam;
    private ExtendViewport worldViewport;
    /**
     * Viewport of the buttons and the pause menu. It zooms out to {@link G#playScale},
     * so on phones they skip the bigger UI and the maps keep their room
     */
    private ScreenViewport uiViewport;
    /** Stage units in a pixel */
    private float uiUnit;
    /** Camera position after the intro. See {@link #layout(int, int)} */
    private float camX, camY;
    /** Camera x before the intro offset, and where the slide to another map started */
    private float shownX, slideFromX;
    private float slideTime = SLIDE_TIME;
    /** The player's map: the one with the touch input, or else the first */
    private MapRender home;
    /** Map the camera shows in portrait, with the others as minis in its panel */
    private MapRender focus;
    private final Array<MapRender> minis = new Array<>();
    private final Vector2 tmp = new Vector2();
    /** Buttons at the bottom: pause on the left, the maps navigation on the right */
    private Table barT;
    private Table navT;
    private Table containerT;
    /**
     * The buttons asking to continue the saved match or start a new one. On its results
     * they take the place of their buttons, else they're on a panel like the pause menu
     */
    private Table choiceT;
    private TextButton resumeButt, continueButt, newGameButt;
    private ImageButton pauseButt;
    /** Buttons under the match results */
    private Table resultsT;
    private TextButton nextMatchButt;
    private boolean resultsShown;
    /** Set once the camera slid to the player's map for the results, until the next match */
    private boolean resultsSlid;
    private boolean menuVisible;
    /** While the saved match waits for the player to continue it or start a new one */
    private boolean choosing;
    private EventListener tapInput;
    private TouchInput touchInput;

    public PlayScreen(MapManager man) {
        manageInput = false;
        manager = man;
    }

    @Override
    public void show() {
        super.show();

        // The stage goes first so it takes the touches on its buttons, which then don't
        // move or rotate the beans
        G.game.input.removeProcessor(stage);
        G.game.input.addProcessor(0, stage);

        uiUnit = G.scale / G.playScale;
        uiViewport = new ScreenViewport();
        uiViewport.setUnitsPerPixel(uiUnit);
        stage.setViewport(uiViewport);

        // Special key event
        specialKeyDown = (Event e) -> {
            int key = (Integer) e.getAttribute();

            if (G.isBackKey(key)) {
                Dbg.dbg("BackKey", "SpecialButtons.keyDown: " + key);

                // On the results and on the saved match's choice, back leaves to the menu
                if (resultsShown || choosing) {
                    action = ACT_MENU;
                    return;
                }

                menuVisible = true;
                manager.togglePaused();
            }
        };

        // Tap event
        tapInput = (Event e) -> {
            // The choice stays until the player makes it
            if (choosing)
                return;

            if (G.game.dbg.on) {
                menuVisible = false;
            } else if (manager.isPaused()) {
                menuVisible = !menuVisible;
            }
        };

        EventHandler.get().addListener("SpecialButtons.keyDown", specialKeyDown);
        EventHandler.get().addListener("TapInput.tap", tapInput);

        containerT = new Table(G.game.skin);
        containerT.setFillParent(true);
        menuVisible = true;

        G.game.audio.configMusic(Audio.MUSIC1, true, true);
        G.game.audio.playMusic();

        TextButton menuButt;

        resumeButt = new TextButton("Resume",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        resumeButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.setPaused(false);
                menuVisible = true;
            }
        });

        menuButt = new TextButton("Menu",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));
        menuButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_MENU;
            }
        });

        containerT.add(menuPanel(resumeButt, menuButt));

        continueButt = new TextButton("Continue",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        continueButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                choosing = false;
                manager.setPaused(false);
                menuVisible = true;
            }
        });

        newGameButt = new TextButton("New game",
                G.game.skin.get("bttBlue", TextButton.TextButtonStyle.class));
        newGameButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Store.remove(manager.saveKey());
                action = ACT_NEW_GAME;
            }
        });

        // The results' buttons, on the player's map. See placeResults
        nextMatchButt = new TextButton("",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        nextMatchButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.nextMatch();
            }
        });

        TextButton resultsMenuButt = new TextButton("Menu",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));
        resultsMenuButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_MENU;
            }
        });

        // The map draws the panel behind them, with the results. See MapRender
        resultsT = buttons(nextMatchButt, resultsMenuButt, G.style.padSmall);
        resultsT.setVisible(false);

        // Start is left to pause and resume, through the maps
        nav.row(continueButt).row(newGameButt).row(resumeButt).row(menuButt)
                .row(nextMatchButt).row(resultsMenuButt);
        nav.startConfirms = false;
        nav.back = () -> {
            if (choosing)
                action = ACT_MENU;
            else
                manager.setPaused(false);
        };

        // The bar's padding and the navigation's visibility are set in layout
        float bttS = G.style.iconButtSize;

        navT = new Table(G.game.skin);
        navT.add(iconButton("bttLeft", () -> navigate(-1))).size(bttS);
        navT.add(iconButton("bttHome", () -> slideTo(home))).size(bttS).padLeft(G.style.padSmall);
        navT.add(iconButton("bttRight", () -> navigate(1))).size(bttS).padLeft(G.style.padSmall);

        barT = new Table(G.game.skin);
        barT.setFillParent(true);
        barT.bottom();
        // Like the back key: pauses with the menu, or resumes
        pauseButt = iconButton("bttPause", () -> {
            menuVisible = true;
            manager.togglePaused();
        });
        barT.add(pauseButt).size(bttS).expandX().left();
        barT.add(navT);

        // The bar first, so the menus are drawn over it
        stage.addActor(barT);
        stage.addActor(resultsT);
        stage.addActor(containerT);
        barT.setDebug(G.game.dbg.uiTable);
        resultsT.setDebug(G.game.dbg.uiTable);
        containerT.setDebug(G.game.dbg.uiTable);

        manager.init();

        // A saved match shows paused, asking to continue it or start a new one
        choosing = manager.restored;

        if (manager.phase == MapManager.PHASE_RESULTS) {
            choiceT = buttons(continueButt, newGameButt, G.style.padSmall);
        } else {
            choiceT = new Table(G.game.skin);
            choiceT.setFillParent(true);
            choiceT.add(menuPanel(continueButt, newGameButt));
        }

        stage.addActor(choiceT);
        choiceT.setDebug(G.game.dbg.uiTable);
        choiceT.setVisible(choosing);
        containerT.setVisible(false);

        if (choosing) {
            manager.setPaused(true);
            nav.focus(continueButt);
        }

        // The maps are laid out on resize, which comes right after show
        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(MapRender.SLOT_W, MapRender.VIEW_H, worldCam);

        for (MapRender r : manager.render) {
            r.viewport = worldViewport;

            if (touchInput == null && r.m.input instanceof TouchInput) {
                touchInput = (TouchInput) r.m.input;
                home = r;
            }
        }

        if (home == null)
            home = manager.render.first();

        focus = home;
    }

    /** A button over the other, with the gap added between them */
    private Table buttons(TextButton top, TextButton bottom, float gap) {
        float bttW = G.style.buttWidth;
        float padM = G.style.padMedium;

        Table table = new Table(G.game.skin);
        table.add(top).width(bttW).pad(padM);
        table.row();
        table.add(bottom).width(bttW).pad(gap, padM, padM, padM);
        table.pack();
        return table;
    }

    /** The pause menu's dark panel, with a button over the other */
    private Table menuPanel(TextButton top, TextButton bottom) {
        Table panel = buttons(top, bottom, G.style.padVBig);
        panel.setBackground("bgBlack");
        panel.pad(54f * uiUnit, 48f * uiUnit, 54f * uiUnit, 48f * uiUnit);
        panel.pack();
        return panel;
    }

    /**
     * Puts the results' buttons, or the choice in their place, on the player's field,
     * centered under its stats, and tells the maps how much room they take. Only there,
     * even with a second player in the co-op. They shrink if they don't fit above the
     * floor
     */
    private void placeResults(Table buttons) {
        MapRender r = home;

        // World units in a pixel, over stage units in a pixel
        MapRender.uiToWorld = worldViewport.getWorldWidth() / worldViewport.getScreenWidth() / uiUnit;

        float top = r.resultsMenuTop();
        float w = buttons.getWidth() * MapRender.uiToWorld;
        float h = buttons.getHeight() * MapRender.uiToWorld;
        float scale = Math.min(1f, Math.min((top - RESULTS_FLOOR_GAP) / h, RESULTS_MAX_W / w));

        buttons.setTransform(scale < 1f);
        buttons.setScale(scale);

        for (MapRender other : manager.render) {
            other.resultsMenuH = other == r ? h * scale : 0f;
        }

        worldViewport.project(tmp.set(r.x + Map.N_COL / 2f, top));
        tmp.y = Gdx.graphics.getHeight() - tmp.y;
        stage.screenToStageCoordinates(tmp);
        buttons.setPosition(tmp.x - buttons.getWidth() * scale / 2f, tmp.y - buttons.getHeight() * scale);
    }

    private ImageButton iconButton(String style, Runnable action) {
        ImageButton butt = new ImageButton(G.game.skin, style);
        butt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return butt;
    }

    /** Slides the camera to the map step places from {@link #focus}. Past the ends it does nothing */
    private void navigate(int step) {
        int i = manager.render.indexOf(focus, true) + step;

        if (i >= 0 && i < manager.render.size)
            slideTo(manager.render.get(i));
    }

    private void slideTo(MapRender r) {
        if (r == focus)
            return;

        focus = r;
        slideFromX = shownX;
        slideTime = 0f;
        layout(G.width, G.height);
    }

    /**
     * Places the maps side by side and fits them in the camera. In portrait the
     * camera only shows {@link #focus}, filling the width and sticking to the top to
     * leave the space below for the thumbs and the buttons. The other maps stay off
     * screen and show as minis in its panel, and the buttons can move the camera to
     * them
     */
    private void layout(int width, int height) {
        boolean portrait = height > width;
        // In portrait each map has its own right wall, so the half the camera shows
        // isn't the next map's panel
        float slotW = portrait ? MapRender.SLOT_W + 1f : MapRender.SLOT_W;
        minis.clear();

        for (int i = 0; i < manager.render.size; i++) {
            MapRender r = manager.render.get(i);
            r.x = MapRender.PANEL_W + slotW * i;
            r.mini = false;

            if (portrait && r != focus)
                minis.add(r);
        }

        focus.stackMinis(minis);
        navT.setVisible(minis.size > 0);
        float worldW;

        if (portrait) {
            worldW = MapRender.SLOT_W + MapRender.RIGHT_WALL_W;
            camX = focus.x - MapRender.PANEL_W + worldW / 2f;
        } else {
            worldW = MapRender.SLOT_W * manager.render.size + MapRender.RIGHT_WALL_W;
            camX = worldW / 2f;
        }

        // The viewport fits the maps in the screen minus its unsafe edges (notch, home
        // indicator) and shows more of the background on other aspect ratios
        Graphics g = Gdx.graphics;
        int left = g.getSafeInsetLeft();
        int bottom = g.getSafeInsetBottom();
        int right = g.getSafeInsetRight();
        float pad = G.style.padMedium;

        barT.pad(0f, left * uiUnit + pad, bottom * uiUnit + pad, right * uiUnit + pad);

        // The maps leave room for the buttons under them. In landscape only the room
        // they don't fill, so they keep their size
        int viewW = width - left - right;
        int viewH = height - bottom - g.getSafeInsetTop();
        int barH = MathUtils.ceil((G.style.iconButtSize + 2f * pad) / uiUnit);

        if (!portrait)
            barH = MathUtils.clamp(MathUtils.floor(viewH - viewW * MapRender.VIEW_H / worldW), 0, barH);

        worldViewport.setMinWorldWidth(worldW);
        worldViewport.update(viewW, viewH - barH, false);

        // That sizes the maps to the area between the notch and the buttons. The field
        // stays in it, but the ceiling and the floor can show past it, under the notch
        // and the buttons, so the viewport takes the screen's whole height
        float unit = worldViewport.getScreenWidth() / worldViewport.getWorldWidth();
        float screenH = height / unit;
        float insetTop = g.getSafeInsetTop() / unit;
        worldViewport.setWorldHeight(screenH);
        worldViewport.setScreenBounds(worldViewport.getScreenX() + left, 0, worldViewport.getScreenWidth(), height);

        if (portrait) {
            // At the top, with the whole ceiling when it fits. The half with the incoming
            // trash stays out of the notch
            camY = Math.max(MapRender.VIEW_TOP + insetTop, Map.N_ROW + 1f) - screenH / 2f;
        } else {
            // Centered in the area
            float areaMid = ((bottom + barH) / unit + screenH - insetTop) / 2f;
            camY = Map.N_ROW / 2f + screenH / 2f - areaMid;
        }
    }

    @Override
    public void render(float delta) {
        // Resuming the saved match, like with start or the pause button, continues it
        if (choosing && !manager.isPaused())
            choosing = false;

        // The menu's navigation works while it shows, and starts on resume each time.
        // The same for the results' buttons, starting on the next match
        boolean menuShown = manager.isPaused() && (menuVisible || choosing);
        boolean results = !menuShown && manager.phase == MapManager.PHASE_RESULTS &&
                manager.phaseTime >= RESULTS_BUTTONS_DELAY;

        if (results && !resultsShown) {
            nextMatchButt.setText(manager.nextMatchLabel());
            nav.focus(nextMatchButt);
        } else if (menuShown && nav.paused) {
            nav.focus(choosing ? continueButt : resumeButt);
        }

        resultsShown = results;
        nav.paused = !menuShown && !resultsShown;
        resultsT.setVisible(resultsShown);
        pauseButt.setVisible(manager.phase == MapManager.PHASE_PLAYING);

        super.render(delta);

        manager.winLost();

        if (manager.saveRequested)
            manager.save();

        for (MapRender r : manager.render) {
            r.m.update();
        }

        // Draw background
        G.game.batch.setProjectionMatrix(cam.combined);
        G.game.batch.begin();
        G.game.bgBeans.render();
        G.game.batch.end();

        // In portrait the camera goes back to the player's map for the results, which has
        // their buttons
        if (manager.phase != MapManager.PHASE_RESULTS) {
            resultsSlid = false;
        } else if (!resultsSlid) {
            resultsSlid = true;
            slideTo(home);
        }

        // The camera slides to the map the navigation buttons picked
        slideTime += delta;
        shownX = MathUtils.lerp(slideFromX, camX, Interpolation.pow2Out.apply(Math.min(slideTime / SLIDE_TIME, 1f)));

        // Intro: the camera slides in from the right while zooming in
        float intro = Interpolation.pow2.apply(Math.min(time / INTRO_TIME, 1f));
        worldCam.position.set(shownX + (1f - intro) * worldViewport.getWorldWidth() / 6f, camY, 0f);
        worldCam.zoom = MathUtils.lerp(1.2f, 1f, intro);

        // Draw the maps
        worldViewport.apply();

        // Before the maps, which make room for the buttons in their results
        if (manager.phase == MapManager.PHASE_RESULTS)
            placeResults(choosing ? choiceT : resultsT);
        G.game.batch.setProjectionMatrix(worldCam.combined);
        G.game.batch.begin();

        for (MapRender r : manager.render) {
            r.renderBg();
        }

        for (MapRender r : manager.render) {
            r.render();
        }

        G.game.batch.end();

        G.game.sr.setProjectionMatrix(worldCam.combined);
        G.game.sr.begin(ShapeRenderer.ShapeType.Filled);

        for (MapRender r : manager.render) {
            if (r.mini)
                r.renderMini(G.game.sr);
        }

        G.game.sr.end();

        uiViewport.apply();
        stage.act(delta);
        stage.draw();

        containerT.setVisible(manager.isPaused() && menuVisible && !choosing);
        choiceT.setVisible(choosing);

        // If it has a TouchInput draw the box and the arrow of the input, in pixels
        if (touchInput != null && touchInput.draw) {
            viewport.apply();
            G.game.sr.setProjectionMatrix(cam.combined);
            G.game.sr.setAutoShapeType(true);
            G.game.sr.begin(ShapeRenderer.ShapeType.Filled);

            float yShift = 80;
            float x = touchInput.div[touchInput.moveCurr];
            float dx = touchInput.div[touchInput.moveCurr + 1] - x;

            G.game.sr.setColor(Color.ORANGE);
            // Draw subdivision highlight
            G.game.sr.rect(
                    x, G.height - touchInput.touchY + yShift,
                    dx, 150);

            G.game.sr.set(ShapeRenderer.ShapeType.Line);

            G.game.sr.setColor(Color.LIGHT_GRAY);
            // Draw input subdivisions
            for (int i = 0; i < touchInput.div.length - 1; i++) {
                x = touchInput.div[i];
                dx = touchInput.div[i + 1] - x;
                G.game.sr.rect(
                        x, G.height - touchInput.touchY + yShift,
                        dx, 150);
            }

            G.game.sr.set(ShapeRenderer.ShapeType.Filled);
            G.game.sr.setColor(Color.WHITE);

            // Draw touch triangle
            G.game.sr.triangle(
                    touchInput.touchX + touchInput.dTouchX, G.height - touchInput.touchY + 1.3f * yShift,
                    touchInput.touchX + touchInput.dTouchX - 50, G.height - touchInput.touchY - 15,
                    touchInput.touchX + touchInput.dTouchX + 50, G.height - touchInput.touchY - 15);

            G.game.sr.end();
        }

        if (manager.gameStatus == MapManager.GAME_ZEROED)
            action = ACT_CREDITS;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        uiViewport.update(width, height, true);
        layout(width, height);
        // No slide when the screen turns
        slideTime = SLIDE_TIME;
    }

    @Override
    public void pause() {
        menuVisible = true;
        manager.setPaused(true);

        // During the choice the save is unchanged, or it was dropped for a new game
        if (!choosing)
            manager.save();
    }

    @Override
    public void hide() {
        if (!choosing)
            manager.save();

        ScoreTable.save(G.game.score);
        Db.save(G.game.data);
        G.game.assets.get("audio/music1.ogg", Music.class).stop();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (manager.render != null) {
            for (MapRender r : manager.render) {
                r.dispose();
            }
        }

        EventHandler.get().removeListener("TapInput.tap", tapInput);
    }
}
