/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.input;

/**
 * @author Vinícius Jardim
 * 2015/07/13
 */
public interface InputBase {

    int UP_KEY      = 0;
    int RIGHT_KEY   = 1;
    int DOWN_KEY    = 2;
    int LEFT_KEY    = 3;
    int START_KEY   = 4;
    int BUTTON1_KEY = 5;
    int BUTTON2_KEY = 6;
    int BUTTON3_KEY = 7;
    int BUTTON4_KEY = 8;

    boolean DOWN = true;
    boolean UP   = false;

    static short setKeyMapKey(short keyMap, int key, boolean isDown) {
        if(isDown) keyMap = (short)(keyMap |  (1 << key));
        else       keyMap = (short)(keyMap & ~(1 << key));
        return keyMap;
    }

    static boolean getKeyMapKey(short keyMap, int key) {
        return ((keyMap >> key) & 1) == 1 ? true : false;
    }

    // #debugCode
    static String keyMapToString(short keyMapOld, short keyMap, short events) {

        String str = "\n                      4321SLDRU\n";

        str += "    keyMapOld = ";

        str += String.format("%15s\n", Integer.toBinaryString(
                keyMapOld & 0xFFFF)).replace(' ', '0');

        str += "       keyMap = ";

        str += String.format("%15s\n", Integer.toBinaryString(
                keyMap & 0xFFFF)).replace(' ', '0');

        str += "       events = ";

        str += String.format("%15s\n", Integer.toBinaryString(
                events & 0xFFFF)).replace(' ', '0');

        return str;
    }

    void setTarget(TargetBase target);
    void setProfile(Profile profile);
    void setId(int id);
    Profile getProfile();
    int getId();

    void update();

    /** Returns the current state of the requested key: down (true), up (false) */
    boolean getKey(int key);
    /** Returns the previous state of the requested key: down (true), up (false) */
    boolean getKeyOld(int key);
    /** Returns the current state of all keys: down (bit val 1), else 0 */
    short getKeyMap();
    /** Returns the previous state of all keys: down (bit val 1), else 0 */
    short getKeyMapOld();
    /** Returns which keys had an event in last update cycle: event (bit val 1), else 0 */
    short getEvent();
}