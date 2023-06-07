/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.input;

import com.vpjardim.colorbeans.core.Dbg;

/**
 * Stores input implementation key codes. Then these keys can be mapped to
 * internal key codes
 * For example:
 * Keyboard key A (key code 29 in desktop). If A is assigned in game as the UP
 * key, it will be represented internally as with the key code 0.
 *
 * @author Vinícius Jardim
 *         2017/04/30
 */
public class Profile {

    public static final int UNDEFINED = -1;

    /** Up key. Internal code = 0 */
    public int up;

    /** Right key. Internal code = 1 */
    public int right;

    /** Down key. Internal code = 2 */
    public int down;

    /** Left key. Internal code = 3 */
    public int left;

    /** Start, play, pause button. Internal code = 4 */
    public int start;

    /** Button 1 (Up): clock wise rotation movement. Internal code = 5 */
    public int button1;

    /** Button 2 (Right): clock wise rotation movement. Internal code = 6 */
    public int button2;

    /** Button 3 (Down): counter clock wise rotation movement. Internal code = 7 */
    public int button3;

    /** Button 4 (Left): counter clock wise rotation movement. Internal code = 8 */
    public int button4;

    public Profile(int... keys) {

        if (keys != null && keys.length == 9) {
            up = keys[0];
            right = keys[1];
            down = keys[2];
            left = keys[3];
            start = keys[4];
            button1 = keys[5];
            button2 = keys[6];
            button3 = keys[7];
            button4 = keys[8];
        }
    }

    public Profile() {
        up = UNDEFINED;
        right = UNDEFINED;
        down = UNDEFINED;
        left = UNDEFINED;
        start = UNDEFINED;
        button1 = UNDEFINED;
        button2 = UNDEFINED;
        button3 = UNDEFINED;
        button4 = UNDEFINED;
    }

    public boolean hasKey(int key) {
        return key == up || key == right || key == down || key == left || key == start ||
                key == button1 || key == button2 || key == button3 || key == button4;
    }

    // #debugCode
    public void log() {
        Dbg.inf("Profile", "up -> " + this.up);
        Dbg.inf("Profile", "right -> " + this.right);
        Dbg.inf("Profile", "down -> " + this.down);
        Dbg.inf("Profile", "left -> " + this.left);
        Dbg.inf("Profile", "start -> " + this.start);
        Dbg.inf("Profile", "button1 -> " + this.button1);
        Dbg.inf("Profile", "button2 -> " + this.button2);
        Dbg.inf("Profile", "button3 -> " + this.button3);
        Dbg.inf("Profile", "button4 -> " + this.button4);
    }
}
