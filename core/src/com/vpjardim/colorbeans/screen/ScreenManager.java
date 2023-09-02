package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Campaign;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.core.Training;

import aurelienribon.tweenengine.TweenManager;

public class ScreenManager {
    private final FPSLogger fps;
    private final TweenManager transition;

    public ScreenManager() {
        fps = new FPSLogger();
        transition = new TweenManager();
    }

    public void create() {
        G.game.setScreen(new LoadingScreen());
    }

    public void render() {
        G.delta = Gdx.graphics.getDeltaTime();

        // #debugCode
        // Warn frames longer than a max time
        if (G.game.dbg.delta == Dbg.DELTA_REAL) {
            int fps = Math.round(1f / Gdx.graphics.getRawDeltaTime());

            if (G.game.dbg.lagWarn && fps < 50) {
                Dbg.inf("LagWarn", fps + " fps");
            }
        }
        // Set frames a constant time for debug propose
        else if (G.game.dbg.delta == Dbg.DELTA_0_02X)
            G.delta = 0.0003333f; // 3000 fps
        else if (G.game.dbg.delta == Dbg.DELTA_0_1X)
            G.delta = 0.0016667f; // 600 fps
        else if (G.game.dbg.delta == Dbg.DELTA_0_25X)
            G.delta = 0.0041667f; // 240 fps
        else if (G.game.dbg.delta == Dbg.DELTA_0_5X)
            G.delta = 0.0083333f; // 120 fps
        else if (G.game.dbg.delta == Dbg.DELTA_1X)
            G.delta = 0.0166667f; // 60 fps
        else if (G.game.dbg.delta == Dbg.DELTA_2X)
            G.delta = 0.0333333f; // 30 fps
        else if (G.game.dbg.delta == Dbg.DELTA_4X)
            G.delta = 0.0666667f; // 15 fps
        else if (G.game.dbg.delta == Dbg.DELTA_8X)
            G.delta = 0.1333333f; // 7.5 fps

        transition.update(G.delta);

        ScreenBase currScreen = (ScreenBase) G.game.getScreen();
        currScreen.render(G.delta);

        // #debugCode
        if (G.game.dbg.fpsText)
            fps.log();

        // #debugCode
        if (!G.loading && G.game.dbg.fps) {
            G.game.batch.setProjectionMatrix(currScreen.cam.combined);
            G.game.batch.begin();
            BitmapFont font = G.game.assets.get("roboto_shadow.ttf", BitmapFont.class);
            font.draw(G.game.batch, Integer.toString(Gdx.graphics.getFramesPerSecond()),
                    G.width * 0.85f, G.height - G.style.fontSizeMedium);

            G.game.batch.end();
        }

        // #debugCode
        if (!G.loading && G.game.dbg.input) {
            G.game.batch.setProjectionMatrix(currScreen.cam.combined);
            G.game.batch.begin();
            BitmapFont font = G.game.assets.get("roboto_shadow.ttf", BitmapFont.class);

            float lineHeight = G.style.fontSizeMedium * 2f;
            float x = G.width * 0.85f;

            font.draw(G.game.batch, Integer.toString(Gdx.graphics.getFramesPerSecond()),
                    x, G.height - G.style.fontSizeMedium);

            font.draw(G.game.batch, "Inputs: " + G.game.input.getInputs().size,
                    x, G.height - (lineHeight * 2));

            font.draw(G.game.batch, "Targets: " + G.game.input.getTargets().size,
                    x, G.height - (lineHeight * 3));

            font.draw(G.game.batch, "Multiplexer: " + G.game.input.getMultiplexSize(),
                    x, G.height - (lineHeight * 4));

            G.game.batch.end();
        }

        // When the screen is done we change to the
        // next screen
        if (currScreen.isFinished()) {
            // dispose the resources of the current screen
            currScreen.dispose();

            // Switch to the next screen based on the current screen
            // and the screen action
            if (currScreen instanceof LoadingScreen)
                G.game.setScreen(new MenuScreen());
            else if (currScreen instanceof MenuScreen) {
                if (currScreen.action == MenuScreen.ACT_PLAY)
                    G.game.setScreen(new PlayScreen(new Campaign()));
                else if (currScreen.action == MenuScreen.ACT_TRAINING)
                    G.game.setScreen(new PlayScreen(new Training()));
                else if (currScreen.action == MenuScreen.ACT_SCORE)
                    G.game.setScreen(new ScoreScreen());
                else if (currScreen.action == MenuScreen.ACT_CONFIG)
                    G.game.setScreen(new ConfigScreen());
            } else if (currScreen instanceof ScoreScreen)
                G.game.setScreen(new MenuScreen());
            else if (currScreen instanceof ConfigScreen) {
                if (currScreen.action == ConfigScreen.ACT_MENU)
                    G.game.setScreen(new MenuScreen());
                else if (currScreen.action == ConfigScreen.ACT_CREDITS)
                    G.game.setScreen(new CreditsScreen());
            } else if (currScreen instanceof PlayScreen) {
                if (currScreen.action == PlayScreen.ACT_MENU)
                    G.game.setScreen(new MenuScreen());
                else if (currScreen.action == PlayScreen.ACT_CREDITS)
                    G.game.setScreen(new CreditsScreen());
            } else if (currScreen instanceof CreditsScreen)
                G.game.setScreen(new MenuScreen());
        }
    }
}
