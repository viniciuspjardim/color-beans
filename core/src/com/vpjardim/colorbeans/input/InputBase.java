/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

/**
 * @author Vinícius Jardim
 * 13/07/2015
 */
public interface InputBase {

    void setTarget(TargetBase target);
    void setProfile(Profile profile);
    Profile getProfile();

    void update();

    /** Returns axis X (horizontal) position: -1 left; 0 center; 1 right */
    int getAxisX();
    /** Returns Axis Y (vertical) position: -1 up; 0 center; 1 down */
    int getAxisY();
    /** Returns axis X (horizontal) position: -1 left; 0 center; 1 right */
    int getAxisXOld();
    /** Returns Axis Y (vertical) position: -1 up; 0 center; 1 down */
    int getAxisYOld();
}