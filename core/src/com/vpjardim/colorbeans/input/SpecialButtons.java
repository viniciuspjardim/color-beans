package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.InputAdapter;
import com.vpjardim.colorbeans.events.EventHandler;

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
