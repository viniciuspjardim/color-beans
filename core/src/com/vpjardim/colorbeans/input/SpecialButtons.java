/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 * 2018/01/22
 */
public class SpecialButtons extends InputAdapter {

    public boolean keyDown (int keycode) {

        Array<TargetBase> targets = G.game.input.getTargets();
        if(targets != null && targets.size > 0) {
            targets.get(0).keyDown(keycode);
        }
        return false;
    }

    public boolean keyUp (int keycode) {

        Array<TargetBase> targets = G.game.input.getTargets();
        if(targets != null && targets.size > 0) {
            targets.get(0).keyUp(keycode);
        }
        return false;
    }
}
