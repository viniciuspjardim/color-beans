/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

/**
 * @author Vinícius Jardim
 * 2017/04/30
 */

public class Profile {

    public static final int UNDEFINED = -1;

    // Directional movements
    public int up;
    public int right;
    public int down;
    public int left;

    /** Start, play, pause button */
    public int start;

    /** Button 1 (Up): clock wise rotation movement */
    public int button1;

    /** Button 2 (Right): clock wise rotation movement */
    public int button2;

    /** Button 3 (Down): counter clock wise rotation movement */
    public int button3;

    /** Button 4 (Left): counter clock wise rotation movement */
    public int button4;

    public Profile(int... keys) {

        if(keys != null && keys.length == 9) {
            up      = keys[0];
            right   = keys[1];
            down    = keys[2];
            left    = keys[3];
            start   = keys[4];
            button1 = keys[5];
            button2 = keys[6];
            button3 = keys[7];
            button4 = keys[8];
        }
    }

    public Profile() {
        up      = UNDEFINED;
        right   = UNDEFINED;
        down    = UNDEFINED;
        left    = UNDEFINED;
        start   = UNDEFINED;
        button1 = UNDEFINED;
        button2 = UNDEFINED;
        button3 = UNDEFINED;
        button4 = UNDEFINED;
    }

    public boolean hasKey(int key) {
        return key == up || key == right || key == down || key == left || key == start ||
                key == button1 || key == button2 || key == button3 || key == button4;
    }
}
