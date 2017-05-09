/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

/**
 * Any screen that need to be controlled by an input method should implement this interface telling
 * what actions needs to be done on each input event
 *
 * @author Vinícius Jardim
 * 01/11/2015
 */
public interface TargetBase {

    void setInput(InputBase input);

    void keyPressed(int key);

    void buttonStart(boolean isDown);
    void button1(boolean isDown);
    void button2(boolean isDown);
    void button3(boolean isDown);
    void button4(boolean isDown);
}
