/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.vpjardim.colorbeans.core.Dbg;

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

    @Override
    public void setProfile(Profile profile) {
        p = profile;
    }

    @Override
    public Profile getProfile() { return p; }

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

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "keyDown -> keycode = " + keycode);

        if(target == null) return false;

        target.keyDown(keycode);

        // Return true when the key down event is handled
        // false if it's not so others input processors may
        // handle it

        if(keycode == p.start) {
            target.btStartDown();
            return true;
        }
        else if(keycode == p.button1) {
            target.bt1Down();
            return true;
        }
        else if(keycode == p.button2) {
            target.bt2Down();
            return true;
        }
        else if(keycode == p.button3) {
            target.bt3Down();
            return true;
        }
        else if(keycode == p.button4) {
            target.bt4Down();
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "keyUp -> keycode = " + keycode);

        if(target == null) return false;

        target.keyUp(keycode);

        // Return true when the key down event is handled
        // false if it's not so others input processors may
        // handle it

        if(keycode == p.start) {
            target.btStartUp();
            return true;
        }
        else if(keycode == p.button1) {
            target.bt1Up();
            return true;
        }
        else if(keycode == p.button2) {
            target.bt2Up();
            return true;
        }
        else if(keycode == p.button3) {
            target.bt3Up();
            return true;
        }
        else if(keycode == p.button4) {
            target.bt4Up();
            return true;
        }

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
