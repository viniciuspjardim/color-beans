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

        // Todo remove this buttonIndex 'ors' and use profiles like keyboard

        if(buttonIndex == 9 || buttonIndex == 197) {
            target.buttonStart(true);
        }
        // Up button
        else if(buttonIndex == 0 || buttonIndex == 188) {
            target.button1(true);
        }
        // Right button
        else if(buttonIndex == 1 || buttonIndex == 189) {
            target.button2(true);
        }
        // Down button
        else if(buttonIndex == 2 || buttonIndex == 190) {
            target.button3(true);
        }
        // Left
        else if(buttonIndex == 3 || buttonIndex == 191) {
            target.button4(true);
        }
        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "axMove -> axisIndex = " + axisIndex + "; value = " + value);

        int val = Math.round(value);

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
    public boolean buttonUp(Controller controller, int buttonIndex) {
        return true;
    }

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
    public boolean accelerometerMoved(Controller controller, int accelerometerIndex, Vector3 value) {
        return true;
    }
}
