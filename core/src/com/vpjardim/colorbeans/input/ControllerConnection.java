/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.vpjardim.colorbeans.GameClass;

/**
 * @author Vinícius Jardim
 * 13/02/2016
 */
public class ControllerConnection implements ControllerListener {

    private GameClass game;

    public ControllerConnection() {
        this.game = GameClass.get();
    }

    @Override
    public void connected(Controller controller) {
        // #debugCode
        Gdx.app.log(this.getClass().getSimpleName(), "Controller " + controller.getName()
                + " connected; n = " + Controllers.getControllers().size);
        game.input.loadInputs();
        game.input.linkAll();
    }

    @Override
    public void disconnected(Controller controller) {
        // #debugCode
        Gdx.app.log(this.getClass().getSimpleName(), "Controller " + controller.getName()
                + " disconnected; n = " + Controllers.getControllers().size);
        game.input.loadInputs();
        game.input.linkAll();
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }
}
