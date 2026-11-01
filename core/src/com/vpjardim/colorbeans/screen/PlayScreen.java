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

    public final MapManager manager;
    /** Camera for the maps, in world units where a bean is 1 x 1. See {@link MapRender} */
    private OrthographicCamera worldCam;
    private ExtendViewport worldViewport;
    /** Camera position after the intro. See {@link #layout(int, int)} */
    private float camX, camY;
    /** Map that fills the screen in portrait, with the others as minis in its panel */
    private MapRender focus;
    private final Array<MapRender> minis = new Array<>();
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

        stage.addActor(containerT);
        containerT.setDebug(G.game.dbg.uiTable);

        manager.init();

        // The maps are laid out on resize, which comes right after show
        worldCam = new OrthographicCamera();
        worldViewport = new ExtendViewport(MapRender.SLOT_W, MapRender.VIEW_H, worldCam);

        for (MapRender r : manager.render) {
            r.viewport = worldViewport;

            if (touchInput == null && r.m.input instanceof TouchInput) {
                touchInput = (TouchInput) r.m.input;
                focus = r;
            }
        }

        if (focus == null)
            focus = manager.render.first();
    }

    /**
     * Places the maps for the screen size. Wider than tall, they're side by side.
     * Otherwise {@link #focus} fills the width with the other maps as minis in its
     * panel, and sticks to the top to leave the space below for the thumbs
     */
    private void layout(int width, int height) {
        boolean portrait = height > width;
        float worldW;

        if (portrait) {
            minis.clear();

            for (MapRender r : manager.render) {
                if (r != focus)
                    minis.add(r);
            }

            focus.mini = false;
            focus.x = MapRender.PANEL_W;
            focus.y = 0f;
            focus.stackMinis(minis);
            worldW = MapRender.SLOT_W + MapRender.RIGHT_WALL_W;
        } else {
            for (int i = 0; i < manager.render.size; i++) {
                MapRender r = manager.render.get(i);
                r.mini = false;
                r.x = MapRender.PANEL_W + MapRender.SLOT_W * i;
                r.y = 0f;
            }

            worldW = MapRender.SLOT_W * manager.render.size + MapRender.RIGHT_WALL_W;
        }

        // The viewport fits the maps in the screen minus its unsafe edges (notch, home
        // indicator) and shows more of the background on other aspect ratios
        Graphics g = Gdx.graphics;
        int left = g.getSafeInsetLeft();
        int bottom = g.getSafeInsetBottom();

        worldViewport.setMinWorldWidth(worldW);
        worldViewport.update(width - left - g.getSafeInsetRight(), height - bottom - g.getSafeInsetTop(), false);
        worldViewport.setScreenPosition(worldViewport.getScreenX() + left, worldViewport.getScreenY() + bottom);

        camX = worldW / 2f;
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

        // Intro: the camera slides in from the right while zooming in
        float intro = Interpolation.pow2.apply(Math.min(time / INTRO_TIME, 1f));
        worldCam.position.set(camX + (1f - intro) * worldViewport.getWorldWidth() / 6f, camY, 0f);
        worldCam.zoom = MathUtils.lerp(1.2f, 1f, intro);

        // Draw the maps
        worldViewport.apply();
        G.game.batch.setProjectionMatrix(worldCam.combined);
        G.game.batch.begin();

        for (MapRender r : manager.render) {
            if (!r.mini)
                r.renderBg();
        }

        for (MapRender r : manager.render) {
            if (!r.mini)
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
