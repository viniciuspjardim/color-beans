/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
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

        Array<InputBase> inputs = G.game.input.getInputs();

        ControllerInput input = new ControllerInput();

        input.setProfile(G.game.data.ctrlProfs.get(0).copy());

        input.gdxController = controller;
        input.gdxController.addListener(input);

        // TODO: add to the controller the first target without input.
        // TODO: move the controller up, before other inputs.
        // TODO: fix the input id, getting the last one don't work because it can be reordered.

        if (inputs.size > 0) {
            input.setId(inputs.get(inputs.size - 1).getId() + 1);
        } else {
            input.setId(1);
        }

        inputs.add(input);
    }

    @Override
    public void disconnected(Controller controller) {
        // #debugCode
        Dbg.inf(Dbg.tag(this), "Controller " + controller.getName() + " disconnected; n = " +
                Controllers.getControllers().size);

        Array<InputBase> inputs = G.game.input.getInputs();

        for (InputBase i : inputs) {
            if (i instanceof ControllerInput) {
                ControllerInput c = (ControllerInput) i;

                if (c.gdxController == controller) {
                    TargetBase target = i.getTarget();

                    target.setInput(null);
                    inputs.removeValue(i, true);
                }
            }
        }
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
