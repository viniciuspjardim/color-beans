/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.vpjardim.colorbeans.G;

import aurelienribon.tweenengine.TweenManager;

/**
 * @author Vinícius Jardim
 * 05/12/2015
 */
public class ScreenManager {

    private FPSLogger fps;
    private TweenManager transition;

    public ScreenManager() {
        fps = new FPSLogger();
        transition = new TweenManager();
    }

    public void create() {
        G.game.setScreen(new LoadingScreen());
    }

    public void render() {

        G.delta = Gdx.graphics.getDeltaTime();
        float rawDelta = Gdx.graphics.getRawDeltaTime();

        // #debugCode
        // Warn frames longer than a max time; set frames a constant time
        // for debug propose: DELTA_SLOW, DELTA_NORMAL, DELTA_FAST
        if(G.game.deltaCfg == G.DELTA_REAL) {

            if(G.game.lagWarn && rawDelta > 0.02f)
                Gdx.app.log("Slow Frame", Math.round(1f / rawDelta) + " fps");
        }
        else if(G.game.deltaCfg == G.DELTA_SLOW)
            G.delta = 0.004f;
        else if(G.game.deltaCfg == G.DELTA_NORMAL)
            G.delta = 0.016667f;
        else if(G.game.deltaCfg == G.DELTA_FAST)
            G.delta = 0.032f;

        ScreenBase currScreen = (ScreenBase) G.game.getScreen();
        currScreen.render(G.delta);

        transition.update(G.delta);

        // When the screen is done we change to the
        // next screen. Ideally the screen transitions are handled
        // in the screen itself or in a proper state machine
        if(currScreen.isFinished()) {

            // dispose the resources of the current screen
            currScreen.dispose();

            // Switch to the next screen based on the current screen
            // and the screen status
            if(currScreen instanceof LoadingScreen)
                G.game.setScreen(new StudioScreen());

            else if(currScreen instanceof StudioScreen)
                G.game.setScreen(new MenuScreen());

            else if(currScreen instanceof MenuScreen)
                G.game.setScreen(new MapScreen());
        }

        // #debugCode
        if(G.game.fpsDebug) fps.log();
    }
}
