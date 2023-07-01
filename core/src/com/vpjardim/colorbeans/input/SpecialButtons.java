/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.InputAdapter;
import com.vpjardim.colorbeans.events.EventHandler;

/**
 * @author Vinícius Jardim
 *         2018/01/22
 */
public class SpecialButtons extends InputAdapter {
    @Override
    public boolean keyDown(int keycode) {
        EventHandler.get().emit("SpecialButtons.keyDown", () -> keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        EventHandler.get().emit("SpecialButtons.keyUp", () -> keycode);
        return false;
    }
}
