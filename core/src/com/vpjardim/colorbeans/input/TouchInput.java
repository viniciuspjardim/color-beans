/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;

/**
 * @author Vinícius Jardim
 * 26/09/2015
 */
public class TouchInput extends GestureDetector.GestureAdapter implements InputBase
{
    // Todo redo this class logic for better control
    // so when the finger stops moving the play blocks stops moving to
    // following a shape like this
    // finger on the center:     ||.|..|...|..|.||
    // finger more to the left:  |..|...|..|.||
    // finger more to the right: ||.|..|...|..|

    private TargetBase target;

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
    public void setTarget(TargetBase target) {
        this.target = target;
    }

    @Override
    public void update() {

        if(!horizontalEvent) { horizontalOld = horizontal; }
        if(!verticalEvent) { verticalOld = vertical; }

        horizontalEvent = false;
        verticalEvent = false;
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
    public boolean tap(float x, float y, int count, int button) {

        // #debugCode
        Gdx.app.debug(this.getClass().getSimpleName(), "tap -> x = " + x +
                "; y = " + y + "; count = " + count + "; button = " + button);

        // Could not dispatch the event to any target
        // Returning false cause the event was not handled
        if(target == null) return false;

        if(y < Gdx.graphics.getHeight() * 0.2f) {
            target.buttonStart(true);
            return true;
        }

        if(x > Gdx.graphics.getWidth() / 2f)
            target.button1(true);
        else
            target.button3(true);

        return true;
    }

    @Override
    public boolean pan (float x, float y, float deltaX, float deltaY) {

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

        return true;
    }

    @Override
    public boolean panStop (float x, float y, int pointer, int button) {

        horizontalEvent = true;
        horizontalOld = horizontal;
        horizontal = 0;

        verticalClean();
        return true;
    }

    public void verticalClean() {

        if(vertical == 1) {
            verticalEvent = true;
            verticalOld = vertical;
            vertical = 0;
        }
    }
}
