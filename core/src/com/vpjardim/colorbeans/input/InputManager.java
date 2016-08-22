package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.Array;

/**
 * @author Vinícius Jardim
 * 05/12/2015
 */
public class InputManager {

    private Array<InputBase> inputs;
    private Array<TargetBase> targets;
    ControllerConnection ctrlConn;

    private InputMultiplexer multiplex;

    public InputManager() {

        inputs = new Array<InputBase>();
        targets = new Array<TargetBase>();
        ctrlConn = new ControllerConnection();
        // Manage controller connections and disconnections
        Controllers.addListener(ctrlConn);

        multiplex = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplex);
    }

    public void loadInputs() {

        inputsClear();

        // Initially attempts to assign controllers to the targets
        for(int i = 0; i < Controllers.getControllers().size; i++) {

            ControllerInput input = new ControllerInput();
            input.gdxController = Controllers.getControllers().get(i);
            input.gdxController.addListener(input);

            inputs.add(input);
        }

        // On Mobile devices:
        // When there is no more controllers try to assign touch input to android device
        if(Gdx.app.getType() == Application.ApplicationType.Android) {

            TouchInput input = new TouchInput();
            multiplex.addProcessor(new GestureDetector(input));

            inputs.add(input);
        }
        // On Desktop:
        // When there is no more controllers try to assign keyboard profiles to desktop device
        else if(Gdx.app.getType() == Application.ApplicationType.Desktop) {

            // One keyboard can control one or more maps, depending on the number of
            // profiles the class KeyboardInput has
            for(int i = 0; i < KeyboardInput.profiles.length; i++) {

                KeyboardInput input = new KeyboardInput();
                input.setProfile(KeyboardInput.profiles[i]);
                multiplex.addProcessor(input);

                inputs.add(input);
            }
        }
    }

    public void addTarget(TargetBase target) {
        targets.add(target);
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

        // Clear references for all targets
        targets.clear();

        // Clear inputs references for targets
        for(InputBase i : inputs) {
            i.setTarget(null);
        }
    }
}
