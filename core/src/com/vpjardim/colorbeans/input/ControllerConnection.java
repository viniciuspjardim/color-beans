/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.vpjardim.colorbeans.core.Dbg;

/**
 * @author Vinícius Jardim
 *         2016/02/13
 */
public class ControllerConnection implements ControllerListener {
    @Override
    public void connected(Controller controller) {
        // #debugCode
        Dbg.inf(Dbg.tag(this), "Controller " + controller.getName() + " connected; n = " +
                Controllers.getControllers().size);
    }

    @Override
    public void disconnected(Controller controller) {
        // #debugCode
        Dbg.inf(Dbg.tag(this), "Controller " + controller.getName() + " disconnected; n = " +
                Controllers.getControllers().size);
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
}
