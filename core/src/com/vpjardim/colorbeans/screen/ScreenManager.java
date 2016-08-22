package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.vpjardim.colorbeans.GameClass;

import aurelienribon.tweenengine.TweenManager;

/**
 * @author Vinícius Jardim
 * 05/12/2015
 */
public class ScreenManager {

    private GameClass game;
    private FPSLogger fps;
    private TweenManager transition;

    public ScreenManager() {
        game = GameClass.get();
        fps = new FPSLogger();
        transition = new TweenManager();
    }

    public void create() {
        game.setScreen(new LoadingScreen());
    }

    public void render() {

        float delta = Gdx.graphics.getDeltaTime();
        float rawDelta = Gdx.graphics.getRawDeltaTime();

        // #debugCode
        // Warn frames longer than a max time; set frames a constant time
        // for debug propose: DELTA_SLOW, DELTA_NORMAL, DELTA_FAST
        if(game.deltaTime == GameClass.DELTA_REAL) {

            if(game.lagWarn && rawDelta > 0.02f)
                Gdx.app.log("Slow Frame", Math.round(1f / rawDelta) + " fps");
        }
        else if(game.deltaTime == GameClass.DELTA_SLOW)
            delta = 0.004f;
        else if(game.deltaTime == GameClass.DELTA_NORMAL)
            delta = 0.016667f;
        else if(game.deltaTime == GameClass.DELTA_FAST)
            delta = 0.032f;

        ScreenBase currScreen = (ScreenBase)game.getScreen();
        currScreen.render(delta);

        transition.update(delta);

        // When the screen is done we change to the
        // next screen. Ideally the screen transitions are handled
        // in the screen itself or in a proper state machine
        if(currScreen.isFinished()) {

            // dispose the resources of the current screen
            currScreen.dispose();

            // Switch to the next screen based on the current screen
            // and the screen status
            if(currScreen instanceof LoadingScreen)
                game.setScreen(new StudioScreen());

            else if(currScreen instanceof StudioScreen)
                game.setScreen(new MenuScreen());

            else if(currScreen instanceof MenuScreen)
                game.setScreen(new MapScreen());
        }

        // #debugCode
        if(game.fpsDebug) fps.log();
    }
}
