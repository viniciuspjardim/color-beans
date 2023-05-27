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

    private Array<InputBase> inputs;
    private Array<TargetBase> targets;
    private InputMultiplexer multiplex;

    private ControllerConnection ctrlConn;
    private SpecialButtons specialButtons;
    private DebugInput debugInput;

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

        inputsClear();

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

    public void addInput(InputBase input) {
        inputs.add(input);
    }

    public void removeInput(InputBase input) {
        inputs.removeValue(input, true);
    }

    public void addTarget(TargetBase target) {
        targets.add(target);
    }

    public void removeTarget(TargetBase target) {
        targets.removeValue(target, true);
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

    public void linkAll() {

        int max = Math.min(inputs.size, targets.size);

        for (int i = 0; i < max; i++) {
            link(inputs.get(i), targets.get(i));
        }
    }

    public static void link(InputBase input, TargetBase target) {
        input.setTarget(target);
        target.setInput(input);
    }

    public void addProcessor(InputProcessor input) {
        multiplex.addProcessor(input);
    }

    public void removeProcessor(InputProcessor input) {
        multiplex.removeProcessor(input);
    }

    public void inputsClear() {

        // Clear targets references for inputs
        for (TargetBase t : targets) {
            t.setInput(null);
        }

        // Clear inputs references for targets
        for (InputBase i : inputs) {
            i.setTarget(null);
        }

        // Clear references for all inputs
        inputs.clear();
    }

    public void targetsClear() {
        maxId = 1;

        // Clear targets references for inputs
        for (TargetBase t : targets) {
            t.setInput(null);
        }

        // Clear inputs references for targets
        for (InputBase i : inputs) {
            i.setTarget(null);
        }

        // Clear references for all targets
        targets.clear();
    }
}
