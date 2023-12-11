package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.events.EventHandler;

public class ControllerConnection implements ControllerListener {
    @Override
    public void connected(Controller controller) {
        Dbg.inf(Dbg.tag(this), "Controller " + controller.getName() + " connected; n = " +
                Controllers.getControllers().size);

        Array<InputBase> inputs = G.game.input.getInputs();

        ControllerInput input = new ControllerInput();

        input.setProfile(G.game.data.ctrlProfs.get(0).copy());
        input.gdxController = controller;
        input.gdxController.addListener(input);
        input.setId(G.game.input.getMaxId() + 1);

        inputs.add(input);
        G.game.input.moveInputToTop(input);

        EventHandler.get().emit("ControllerConnection.event", null);
    }

    @Override
    public void disconnected(Controller controller) {
        Dbg.inf(Dbg.tag(this), "Controller " + controller.getName() + " disconnected; n = " +
                Controllers.getControllers().size);

        Array<InputBase> inputs = G.game.input.getInputs();

        for (InputBase input : inputs) {
            if (input instanceof ControllerInput) {
                ControllerInput c = (ControllerInput) input;

                if (c.gdxController == controller) {
                    TargetBase target = input.getTarget();

                    if (target != null) {
                        target.setInput(null);
                    }

                    input.setTarget(null);
                    inputs.removeValue(input, true);

                    EventHandler.get().emit("ControllerConnection.event", null);

                    return;
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
