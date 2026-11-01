package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.core.Audio;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.core.MapManager;
import com.vpjardim.colorbeans.core.MapRender;
import com.vpjardim.colorbeans.core.ScoreTable;
import com.vpjardim.colorbeans.defaults.Db;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;
import com.vpjardim.colorbeans.input.TouchInput;

public class PlayScreen extends ScreenBase {
    public static final int ACT_MENU = 10;
    public static final int ACT_CREDITS = 11;
    /** Seconds the camera takes to slide in at the start */
    private static final float INTRO_TIME = 1.75f;
    /** Seconds the camera takes to slide to another map */
    private static final float SLIDE_TIME = 0.3f;

    public final MapManager manager;
    /** Camera for the maps, in world units where a bean is 1 x 1. See {@link MapRender} */
    private OrthographicCamera worldCam;
    private ExtendViewport worldViewport;
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
    /** Buttons at the bottom: pause on the left, the maps navigation on the right */
    private Table barT;
    private Table navT;
    private Table containerT;
    private boolean menuVisible;
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

        // Special key event
        specialKeyDown = (Event e) -> {
            int key = (Integer) e.getAttribute();

            if (G.isBackKey(key)) {
                Dbg.dbg("BackKey", "SpecialButtons.keyDown: " + key);
                menuVisible = true;
                manager.togglePaused();
            }
        };

        // Tap event
        tapInput = (Event e) -> {
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

        Table contentT = new Table(G.game.skin);
        contentT.background("bgBlack");
        contentT.pad(54f, 48f, 54f, 48f);
        contentT.setBackground("bgBlack");

        containerT.add(contentT);
        menuVisible = true;

        G.game.audio.configMusic(Audio.MUSIC1, true, true);
        G.game.audio.playMusic();

        TextButton resumeButt, menuButt;

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

        float bttW = G.style.buttWidth;
        float padM = G.style.padMedium;

        contentT.add(resumeButt).width(bttW).pad(padM);
        contentT.row();
        contentT.add(menuButt).width(bttW).pad(G.style.padVBig, padM, padM, padM);

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
        barT.add(iconButton("bttPause", () -> {
            menuVisible = true;
            manager.togglePaused();
        })).size(bttS).expandX().left();
        barT.add(navT);

        // The bar first, so the menu is drawn over it
        stage.addActor(barT);
        stage.addActor(containerT);
        barT.setDebug(G.game.dbg.uiTable);
        containerT.setDebug(G.game.dbg.uiTable);

        manager.init();

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

        barT.pad(0f, left + pad, bottom + pad, right + pad);

        // The maps leave room for the buttons under them. In landscape only the room
        // they don't fill, so they keep their size
        int viewW = width - left - right;
        int viewH = height - bottom - g.getSafeInsetTop();
        int barH = MathUtils.ceil(G.style.iconButtSize + 2f * pad);

        if (!portrait)
            barH = MathUtils.clamp(MathUtils.floor(viewH - viewW * MapRender.VIEW_H / worldW), 0, barH);

        worldViewport.setMinWorldWidth(worldW);
        worldViewport.update(viewW, viewH - barH, false);
        worldViewport.setScreenPosition(worldViewport.getScreenX() + left, worldViewport.getScreenY() + bottom + barH);

        camY = portrait ? MapRender.VIEW_TOP - worldViewport.getWorldHeight() / 2f : Map.N_ROW / 2f;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        manager.winLost();

        for (MapRender r : manager.render) {
            r.m.update();
        }

        // Draw background
        G.game.batch.setProjectionMatrix(cam.combined);
        G.game.batch.begin();
        G.game.bgBeans.render();
        G.game.batch.end();

        // The camera slides to the map the navigation buttons picked
        slideTime += delta;
        shownX = MathUtils.lerp(slideFromX, camX, Interpolation.pow2Out.apply(Math.min(slideTime / SLIDE_TIME, 1f)));

        // Intro: the camera slides in from the right while zooming in
        float intro = Interpolation.pow2.apply(Math.min(time / INTRO_TIME, 1f));
        worldCam.position.set(shownX + (1f - intro) * worldViewport.getWorldWidth() / 6f, camY, 0f);
        worldCam.zoom = MathUtils.lerp(1.2f, 1f, intro);

        // Draw the maps
        worldViewport.apply();
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

        // Back to pixels for the menu and the touch input
        viewport.apply();
        stage.act(delta);
        stage.draw();

        containerT.setVisible(manager.isPaused() && menuVisible);

        // If it has a TouchInput draw the box and the arrow of the input
        if (touchInput != null && touchInput.draw) {
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
        layout(width, height);
        // No slide when the screen turns
        slideTime = SLIDE_TIME;
    }

    @Override
    public void pause() {
        menuVisible = true;
        manager.setPaused(true);
    }

    @Override
    public void hide() {
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
