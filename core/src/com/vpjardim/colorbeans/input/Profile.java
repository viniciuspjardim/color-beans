package com.vpjardim.colorbeans.input;

/**
 * @author Vinícius Jardim
 * 2017/04/30
 */

public class Profile {

    public static final int UNDEFINED = -1;

    /** Start, play, pause button */
    public int start;

    // Directional movements
    public int up;
    public int right;
    public int down;
    public int left;

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
            start   = keys[0];
            up      = keys[1];
            right   = keys[2];
            down    = keys[3];
            left    = keys[4];
            button1 = keys[5];
            button2 = keys[6];
            button3 = keys[7];
            button4 = keys[8];
        }
    }

    public Profile() {
        start   = UNDEFINED;
        up      = UNDEFINED;
        right   = UNDEFINED;
        down    = UNDEFINED;
        left    = UNDEFINED;
        button1 = UNDEFINED;
        button2 = UNDEFINED;
        button3 = UNDEFINED;
        button4 = UNDEFINED;
    }
}
