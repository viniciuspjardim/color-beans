/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.vpjardim.colorbeans.core.Dbg;

/**
 * @author Vinícius Jardim
 * 03/04/2015
 */
public class ControllerInput implements InputBase, ControllerListener {

    private TargetBase target;
    public Profile p;
    public Controller gdxController;

    private boolean horizontalEvent = false;
    private boolean verticalEvent = false;

    /** Axis 3 (horizontal): -1 left; 1 right */
    private int horizontal = 0;
    /** Axis 2 (vertical)  : -1 up; 1 down */
    private int vertical = 0;

    /** Axis 3 (horizontal): -1 left; 1 right */
    private int horizontalOld = 0;
    /** Axis 2 (vertical)  : -1 up; 1 down */
    private int verticalOld = 0;

    @Override
    public void setTarget(TargetBase target) { this.target = target; }

    @Override
    public void setProfile(Profile profile) {
        p = profile;
    }

    @Override
    public Profile getProfile() { return p; }

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

    /**
     * 9: start
     * 0: top
     * 1: right
     * 2: bottom
     * 3: left
     */
    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "btDown -> buttonIndex = " + buttonIndex);

        if(target == null) return true;

        target.keyDown(buttonIndex);

        if(buttonIndex == p.start) {
            target.btStartDown();
        }
        // Up button
        else if(buttonIndex == p.button1) {
            target.bt1Down();
        }
        // Right button
        else if(buttonIndex == p.button2) {
            target.bt2Down();
        }
        // Down button
        else if(buttonIndex == p.button3) {
            target.bt3Down();
        }
        // Left
        else if(buttonIndex == p.button4) {
            target.bt4Down();
        }
        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "btUp -> buttonIndex = " + buttonIndex);

        if(target == null) return true;

        target.keyUp(buttonIndex);

        if(buttonIndex == p.start) {
            target.btStartUp();
        }
        // Up button
        else if(buttonIndex == p.button1) {
            target.bt1Up();
        }
        // Right button
        else if(buttonIndex == p.button2) {
            target.bt2Up();
        }
        // Down button
        else if(buttonIndex == p.button3) {
            target.bt3Up();
        }
        // Left
        else if(buttonIndex == p.button4) {
            target.bt4Up();
        }
        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "axMove -> axisIndex = " + axisIndex + "; value = " + value);

        int val = Math.round(value);

        if(target != null && val != 0) target.keyDown(Profile.UNDEFINED);
        if(target != null && val == 0) target.keyUp(Profile.UNDEFINED);

        // Axis 0 (horizontal): -1 left; 1 right
        if(axisIndex == 0) {
            horizontalEvent = true;
            horizontalOld = horizontal;
            horizontal = val;
        }

        // Axis 1 (vertical)  : -1 up; 1 down
        if(axisIndex == 1) {
            verticalEvent = true;
            verticalOld = vertical;
            vertical = val;
        }

        return true;
    }

    @Override
    public void connected(Controller controller) {}

    @Override
    public void disconnected(Controller controller) {}

    @Override
    public boolean povMoved(Controller controller, int povIndex, PovDirection value) {
        return true;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderIndex, boolean value) {
        return true;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderIndex, boolean value) {
        return true;
    }

    @Override
    public boolean accelerometerMoved(
            Controller controller, int accelerometerIndex, Vector3 value) {
        return true;
    }
}
