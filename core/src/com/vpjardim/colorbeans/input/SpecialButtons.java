/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.events.EventHandler;

/**
 * @author Vinícius Jardim
 * 2018/01/22
 */
public class SpecialButtons extends InputAdapter {

    @Override
    public boolean keyDown (int keycode) {

        Array<TargetBase> targets = G.game.input.getTargets();
        if(targets != null && targets.size > 0) {
            targets.get(0).keyDown(keycode);
        }
        EventHandler.getHandler().addEvent("SpecialButtons.keyDown", () -> keycode);
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {

        Array<TargetBase> targets = G.game.input.getTargets();
        if(targets != null && targets.size > 0) {
            targets.get(0).keyUp(keycode);
        }
        EventHandler.getHandler().addEvent("SpecialButtons.keyUp", () -> keycode);
        return false;
    }
}
