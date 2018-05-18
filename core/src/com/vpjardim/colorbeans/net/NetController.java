/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.net;

import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.Profile;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * @author Vinícius Jardim
 * 2018/05/14
 */
public class NetController implements InputBase {

    private TargetBase target;
    private int id;

    /**
     * Current binary state of all keys: 1 down, 0 up.
     * Each bit is one key. From the least significant bit to the most the key, the order is up,
     * right, down, left, start, bt1, bt2, bt3 and bt4 keys
     */
    short keyMap = 0;

    /**
     * Previous binary state of all keys: 1 down, 0 up.
     * Each bit is one key. From the least significant bit to the most the key, the order is up,
     * right, down, left, start, bt1, bt2, bt3 and bt4 keys
     */
    short keyMapOld = 0;

    /** 1 to the keys that had an event after last update */
    private short event = 0;

    /** Change the state of the given key to the isDown value */
    public void keyEvent(int key, boolean isDown) {

        // If the key is already in the given isDown state, do nothing
        if(InputBase.getKeyMapKey(keyMap, key) == isDown) return;

        keyMapOld = InputBase.setKeyMapKey(keyMapOld, key, !isDown);
        keyMap = InputBase.setKeyMapKey(keyMap, key, isDown);
        event = InputBase.setKeyMapKey(event, key, true);
    }

    @Override
    public void setTarget(TargetBase target) { this.target = target; }

    @Override
    public void setProfile(Profile profile) { }

    @Override
    public void setId(int id) { this.id = id; }

    @Override
    public Profile getProfile() { return null; }

    /** Returns the local id of this controller. It might be different from the remote id */
    @Override
    public int getId() { return id; }

    @Override
    public void update() { }

    @Override
    public boolean getKey(int key) { return InputBase.getKeyMapKey(keyMap, key); }

    @Override
    public boolean getKeyOld(int key) {
        return InputBase.getKeyMapKey(keyMapOld, key);
    }

    @Override
    public short getKeyMap() {
        return keyMap;
    }

    @Override
    public short getKeyMapOld() {
        return keyMapOld;
    }

    @Override
    public short getEvent() { return event; }

    public boolean keyDown(int keycode) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "keyDown -> keycode = " + keycode);

        if(target == null) return false;

        target.keyDown(keycode);

        // Return true when the key down event is handled. False if it's not so other keyboard input
        // (with another key profile) can handle it

        if(keycode == InputBase.UP_KEY) {
            keyEvent(InputBase.UP_KEY, InputBase.DOWN);
            return true;
        }
        else if(keycode == InputBase.RIGHT_KEY) {
            keyEvent(InputBase.RIGHT_KEY, InputBase.DOWN);
            // target.btRightDown();
            return true;
        }
        else if(keycode == InputBase.DOWN_KEY) {
            keyEvent(InputBase.DOWN_KEY, InputBase.DOWN);
            // target.btDownDown();
            return true;
        }
        else if(keycode == InputBase.LEFT_KEY) {
            keyEvent(InputBase.LEFT_KEY, InputBase.DOWN);
            // target.btLeftDown();
            return true;
        }
        else if(keycode == InputBase.START_KEY) {
            keyEvent(InputBase.START_KEY, InputBase.DOWN);
            target.btStartDown();
            return true;
        }
        else if(keycode == InputBase.BUTTON1_KEY) {
            keyEvent(InputBase.BUTTON1_KEY, InputBase.DOWN);
            target.bt1Down();
            return true;
        }
        else if(keycode == InputBase.BUTTON2_KEY) {
            keyEvent(InputBase.BUTTON2_KEY, InputBase.DOWN);
            target.bt2Down();
            return true;
        }
        else if(keycode == InputBase.BUTTON3_KEY) {
            keyEvent(InputBase.BUTTON3_KEY, InputBase.DOWN);
            target.bt3Down();
            return true;
        }
        else if(keycode == InputBase.BUTTON4_KEY) {
            keyEvent(InputBase.BUTTON4_KEY, InputBase.DOWN);
            target.bt4Down();
            return true;
        }

        return false;
    }

    public boolean keyUp(int keycode) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "keyUp -> keycode = " + keycode);

        if(target == null) return false;

        target.keyUp(keycode);

        // Return true when the key down event is handled. False if it's not so other keyboard input
        // (with another key profile) can handle it

        if(keycode == InputBase.UP_KEY) {
            keyEvent(InputBase.UP_KEY, InputBase.UP);
            // target.btUpUp();
            return true;
        }
        else if(keycode == InputBase.RIGHT_KEY) {
            keyEvent(InputBase.RIGHT_KEY, InputBase.UP);
            // target.btRightUp();
            return true;
        }
        else if(keycode == InputBase.DOWN_KEY) {
            keyEvent(InputBase.DOWN_KEY, InputBase.UP);
            // target.btDownUp();
            return true;
        }
        else if(keycode == InputBase.LEFT_KEY) {
            keyEvent(InputBase.LEFT_KEY, InputBase.UP);
            // target.btLeftUp();
            return true;
        }
        else if(keycode == InputBase.START_KEY) {
            keyEvent(InputBase.START_KEY, InputBase.UP);
            target.btStartUp();
            return true;
        }
        else if(keycode == InputBase.BUTTON1_KEY) {
            keyEvent(InputBase.BUTTON1_KEY, InputBase.UP);
            target.bt1Up();
            return true;
        }
        else if(keycode == InputBase.BUTTON2_KEY) {
            keyEvent(InputBase.BUTTON2_KEY, InputBase.UP);
            target.bt2Up();
            return true;
        }
        else if(keycode == InputBase.BUTTON3_KEY) {
            keyEvent(InputBase.BUTTON3_KEY, InputBase.UP);
            target.bt3Up();
            return true;
        }
        else if(keycode == InputBase.BUTTON4_KEY) {
            keyEvent(InputBase.BUTTON4_KEY, InputBase.UP);
            target.bt4Up();
            return true;
        }

        return false;
    }
}
