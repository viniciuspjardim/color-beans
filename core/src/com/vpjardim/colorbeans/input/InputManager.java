/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;

import static com.badlogic.gdx.Gdx.input;

/**
 * @author Vinícius Jardim
 * 05/12/2015
 */
public class InputManager {

    private Array<InputBase> inputs;
    private Array<TargetBase> targets;
    private InputMultiplexer multiplex;

    private ControllerConnection ctrlConn;
    private SpecialButtons specialButtons;

    public InputManager() {

        inputs = new Array<>();
        targets = new Array<>();
        ctrlConn = new ControllerConnection();
        specialButtons = new SpecialButtons();

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

        // Initially attempts to assign controllers to the targets
        for(int i = 0; i < Controllers.getControllers().size; i++) {

            ControllerInput input = new ControllerInput();

            // If there is profile add, otherwise create a new one
            if(i < G.game.data.ctrlProfs.size) input.setProfile(G.game.data.ctrlProfs.get(i));
            else input.setProfile(new Profile());

            input.gdxController = Controllers.getControllers().get(i);
            input.gdxController.addListener(input);

            inputs.add(input);
        }

        // On Mobile devices:
        // When there is no more controllers try to assign touch input to android device
        if(Gdx.app.getType() == Application.ApplicationType.Android) {

            TouchInput2 input = new TouchInput2();
            multiplex.addProcessor(new GestureDetector(input));

            inputs.add(input);
        }
        // On Desktop:
        // When there is no more controllers try to assign keyboard profiles to desktop device
        else if(Gdx.app.getType() == Application.ApplicationType.Desktop) {

            // One keyboard can control one or more maps, depending on the number of
            // profiles the keyboard has
            for(int i = 0; i < G.game.data.kbProfs.size; i++) {

                KeyboardInput input = new KeyboardInput();
                input.setProfile(G.game.data.kbProfs.get(i));
                multiplex.addProcessor(input);

                inputs.add(input);
            }
        }
    }

    public void addTarget(TargetBase target) {
        targets.add(target);
    }

    public void removeTarget(TargetBase target) {
        targets.removeValue(target, true);
    }

    public void linkAll() {

        int max = Math.min(inputs.size, targets.size);

        for(int i = 0; i < max; i++) {
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
        for(TargetBase t : targets) {
            t.setInput(null);
        }

        // Clear inputs references for targets
        for(InputBase i : inputs) {
            i.setTarget(null);
        }

        // Clear references for all inputs
        inputs.clear();
    }

    public void targetsClear() {

        // Clear targets references for inputs
        for(TargetBase t : targets) {
            t.setInput(null);
        }

        // Clear inputs references for targets
        for(InputBase i : inputs) {
            i.setTarget(null);
        }

        // Clear references for all targets
        targets.clear();
    }
}
