/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;

import static com.badlogic.gdx.Gdx.input;

/**
 * @author Vinícius Jardim
 *         2015/12/05
 */
public class InputManager {
    private final InputMultiplexer multiplex;
    private final Array<InputBase> inputs;
    private final Array<TargetBase> targets;
    private final ControllerConnection ctrlConn;
    private final SpecialButtons specialButtons;
    private final DebugInput debugInput;

    private int maxId = 1;

    public InputManager() {
        inputs = new Array<>();
        targets = new Array<>();
        ctrlConn = new ControllerConnection();
        specialButtons = new SpecialButtons();
        // #debugCode
        debugInput = new DebugInput();

        // Manage controller connections and disconnections
        Controllers.addListener(ctrlConn);

        multiplex = new InputMultiplexer();
        input.setInputProcessor(multiplex);
    }

    public Array<InputBase> getInputs() {
        return inputs;
    }

    public Array<TargetBase> getTargets() {
        return targets;
    }

    public void loadInputs() {
        multiplex.addProcessor(specialButtons);
        multiplex.addProcessor(new GestureDetector(debugInput));

        boolean multiTouch = Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen);
        boolean hardwareKeyboard = Gdx.input.isPeripheralAvailable(Input.Peripheral.HardwareKeyboard);

        // Initially attempts to assign controllers to the targets
        for (int i = 0; i < Controllers.getControllers().size; i++) {
            ControllerInput input = new ControllerInput();

            // If there is profile add, otherwise create a new one
            if (i < G.game.data.ctrlProfs.size)
                input.setProfile(G.game.data.ctrlProfs.get(i));
            else
                input.setProfile(new Profile());

            input.gdxController = Controllers.getControllers().get(i);
            input.gdxController.addListener(input);
            input.setId(maxId++);

            inputs.add(input);
        }

        if (multiTouch) {
            TouchInput input = new TouchInput();
            multiplex.addProcessor(new GestureDetector(input));
            input.setId(maxId++);

            inputs.add(input);
        }

        if (hardwareKeyboard) {
            // One keyboard can control one or more maps, depending on the number of
            // profiles the keyboard has
            for (int i = 0; i < G.game.data.kbProfs.size; i++) {
                KeyboardInput input = new KeyboardInput();
                input.setProfile(G.game.data.kbProfs.get(i));
                multiplex.addProcessor(input);
                input.setId(maxId++);

                inputs.add(input);
            }
        }
    }

    public void moveInput(int index, int value) {
        if (value != 1 && value != -1)
            return;
        if (index < 0 || index >= inputs.size)
            return;

        int neighborIndex = index + value;
        if (neighborIndex < 0 || neighborIndex >= inputs.size)
            return;

        inputs.swap(index, neighborIndex);
    }

    public void addProcessor(InputProcessor input) {
        multiplex.addProcessor(input);
    }

    public void removeProcessor(InputProcessor input) {
        multiplex.removeProcessor(input);
    }
}
