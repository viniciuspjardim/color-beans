/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

/**
 * Any screen or object that need to be controlled by an input method should implement this
 * interface telling what actions needs to be done on each input event
 *
 * @author Vinícius Jardim
 * 01/11/2015
 */
public interface TargetBase {

    void setInput(InputBase input);

    void keyDown(int key);
    void btStartDown();
    void bt1Down();
    void bt2Down();
    void bt3Down();
    void bt4Down();

    void keyUp(int key);
    void btStartUp();
    void bt1Up();
    void bt2Up();
    void bt3Up();
    void bt4Up();
}
