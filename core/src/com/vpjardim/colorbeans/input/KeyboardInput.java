/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * @author Vinícius Jardim
 * 15/07/2015
 */
public class KeyboardInput implements InputBase, InputProcessor {

    private TargetBase target;
    public Profile p;

    /** -1 left; 1 right */
    private int horizontal = 0;
    /** -1 up; 1 down */
    private int vertical = 0;

    /** -1 left; 1 right */
    private int horizontalOld = 0;
    /** -1 up; 1 down */
    private int verticalOld = 0;

    @Override
    public void setTarget(TargetBase target) {
        this.target = target;
    }

    public void setProfile(Profile profile) {
        p = profile;
    }

    @Override
    public void update() {

        horizontalOld = horizontal;
        verticalOld = vertical;

        // Updating horizontal axis
        if(Gdx.input.isKeyPressed(p.right)) {
            horizontal = 1;
        }
        else if(Gdx.input.isKeyPressed(p.left)) {
            horizontal = -1;
        }
        else horizontal = 0;

        // Updating vertical axis
        if(Gdx.input.isKeyPressed(p.down)) {
            vertical = 1;
        }
        else vertical = 0;
    }

    @Override
    public int getAxisX() { return horizontal; }

    @Override
    public int getAxisY() { return vertical; }

    @Override
    public int getAxisXOld() { return horizontalOld; }

    @Override
    public int getAxisYOld() { return verticalOld; }

    @Override
    public boolean keyDown(int keycode) {

        if(target == null) return false;

        // Return true when the key down event is handled
        // false if it's not so others input processors may
        // handle it

        if(keycode == p.start) {
            target.buttonStart(true);
            return true;
        }
        else if(keycode == p.button1) {
            target.button1(true);
            return true;
        }
        else if(keycode == p.button2) {
            target.button2(true);
            return true;
        }
        else if(keycode == p.button3) {
            target.button3(true);
            return true;
        }
        else if(keycode == p.button4) {
            target.button4(true);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
