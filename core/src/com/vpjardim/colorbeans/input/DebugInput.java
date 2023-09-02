package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.input.GestureDetector;
import com.vpjardim.colorbeans.events.EventHandler;

public class DebugInput extends GestureDetector.GestureAdapter {
    public static class Data {
        public float x;
        public float y;
        public int button;

        public Data(float x, float y, int button) {
            this.x = x;
            this.y = y;
            this.button = button;
        }
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        EventHandler.get().emit("DebugInput.tap", () -> new Data(x, y, button));
        return false;
    }
}
