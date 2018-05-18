/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.input.GestureDetector;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Dbg;

/**
 * @author Vinícius Jardim
 * 26/09/2015
 */
public class TouchInput extends GestureDetector.GestureAdapter implements InputBase {

    // Todo fix or delete this class. It is not working anymore

    // Todo redo this class logic for better control
    // so when the finger stops moving the player blocks stops moving to
    // following a shape like this
    // finger on the center:     ||.|..|...|..|.||
    // finger more to the left:  |..|...|..|.|.|||
    // finger more to the right: |||.|.|..|...|..|
    // Todo slide up to pause and menu options

    private TargetBase target;
    private int id;

    private short keyMap = 0;
    private short keyMapOld = 0;

    private boolean horizontalEvent = false;
    private boolean verticalEvent = false;

    /** -1 left; 1 right */
    private int horizontal = 0;
    /** -1 up; 1 down */
    private int vertical = 0;

    /** -1 left; 1 right */
    private int horizontalOld = 0;
    /** -1 up; 1 down */
    private int verticalOld = 0;

    @Override
    public void setTarget(TargetBase target) { this.target = target; }

    @Override
    public void setProfile(Profile profile) {}

    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public Profile getProfile() { return null; }

    @Override
    public int getId() { return id; }

    @Override
    public void update() {

        if(!horizontalEvent) { horizontalOld = horizontal; }
        if(!verticalEvent) { verticalOld = vertical; }

        horizontalEvent = false;
        verticalEvent = false;
    }

    @Override
    public boolean getKey(int key) {
        return false;
    }

    @Override
    public boolean getKeyOld(int key) {
        return false;
    }

    @Override
    public short getKeyMap() {
        return 0;
    }

    @Override
    public short getKeyMapOld() {
        return 0;
    }


    @Override
    public short getEvent() { return 0; }

    // @Override
    // public int getAxisX() { return horizontal; }
    // @Override
    // public int getAxisY() { return vertical; }
    // @Override
    // public int getAxisXOld() { return horizontalOld; }
    // @Override
    // public int getAxisYOld() { return verticalOld; }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "tap -> x = " + x + "; y = " + y + "; count = " + count +
                "; button = " + button);

        // Could not dispatch the event to any target
        // Returning false cause the event was not handled
        if(target == null) return false;

        if(y < G.height * 0.2f) {
            target.btStartDown();
            return false;
        }

        if(x > G.width / 2f)
            target.bt1Down();
        else
            target.bt3Down();

        // Returns false because the stage need this event on the PlayScreen
        // Todo ControllerInput or other input might have the same problem
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {

        // Horizontal move if move is more horizontal then vertical
        if(vertical == 0 && Math.abs(deltaX) >= Math.abs(deltaY) * 0.8) {
            if(deltaX > 0) {
                horizontalEvent = true;
                horizontalOld = horizontal;
                horizontal = 1;
            }
            else if(deltaX < 0) {
                horizontalEvent = true;
                horizontalOld = horizontal;
                horizontal = -1;
            }
        }
        else if(horizontal == 0 && deltaY > 0) {
            verticalEvent = true;
            verticalOld = vertical;
            vertical = 1;
        }

        if(deltaY <= 0) verticalClean();

        return false;
    }

    @Override
    public boolean panStop (float x, float y, int pointer, int button) {

        horizontalEvent = true;
        horizontalOld = horizontal;
        horizontal = 0;

        verticalClean();
        return false;
    }

    public void verticalClean() {
        if(vertical == 1) {
            verticalEvent = true;
            verticalOld = vertical;
            vertical = 0;
        }
    }
}
