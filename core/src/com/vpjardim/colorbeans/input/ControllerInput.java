package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.vpjardim.colorbeans.core.Dbg;

public class ControllerInput implements InputBase, ControllerListener {
    private TargetBase target;
    public Profile p;
    public Controller gdxController;
    private int id;

    /**
     * Current binary state of all keys: 1 down, 0 up.
     * Each bit is one key. From the least significant bit to the most the key, the
     * order is up, right, down, left, start, bt1, bt2, bt3 and bt4 keys
     */
    private short keyMap = 0;

    /**
     * Previous binary state of all keys: 1 down, 0 up.
     * Each bit is one key. From the least significant bit to the most the key, the
     * order is up, right, down, left, start, bt1, bt2, bt3 and bt4 keys
     */
    private short keyMapOld = 0;

    /** 1 to the keys that had an event after last update */
    private short event = 0;

    /** Change the state of the given key to the isDown value */
    public void keyEvent(int key, boolean isDown) {
        // If the key is already in the given isDown state, do nothing
        if (InputBase.getKeyMapKey(keyMap, key) == isDown)
            return;

        keyMapOld = InputBase.setKeyMapKey(keyMapOld, key, !isDown);
        keyMap = InputBase.setKeyMapKey(keyMap, key, isDown);
        event = InputBase.setKeyMapKey(event, key, true);
    }

    @Override
    public void setTarget(TargetBase target) {
        this.target = target;
    }

    @Override
    public void setProfile(Profile profile) {
        p = profile;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Profile getProfile() {
        return p;
    }

    @Override
    public TargetBase getTarget() {
        return target;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void update() {
        // These flowing 2 lines changes keyMapOld only the bits that had no events in
        // the last update. This is done because keyMapOld needs to stay one update
        // before keyMap.
        keyMapOld = keyMap;
        keyMapOld = (short) (keyMapOld ^ event);
        // Clear event bits
        event = 0;
    }

    @Override
    public boolean getKey(int key) {
        return InputBase.getKeyMapKey(keyMap, key);
    }

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
    public short getEvent() {
        return event;
    }

    @Override
    public boolean buttonDown(Controller controller, int keycode) {
        // #debugCode
        Dbg.dbg(Dbg.tag(this), "[" + id + "] buttonDown -> keycode = " + keycode);

        if (target == null)
            return true;

        target.keyDown(keycode);

        // Always return true because there is only one key profile per controller

        if (keycode == p.up) {
            keyEvent(InputBase.UP_KEY, InputBase.DOWN);
            return true;
        } else if (keycode == p.right) {
            keyEvent(InputBase.RIGHT_KEY, InputBase.DOWN);
            return true;
        } else if (keycode == p.down) {
            keyEvent(InputBase.DOWN_KEY, InputBase.DOWN);
            return true;
        } else if (keycode == p.left) {
            keyEvent(InputBase.LEFT_KEY, InputBase.DOWN);
            return true;
        } else if (keycode == p.start) {
            keyEvent(InputBase.START_KEY, InputBase.DOWN);
            target.btStartDown();
        } else if (keycode == p.button1) {
            keyEvent(InputBase.BUTTON1_KEY, InputBase.DOWN);
            target.bt1Down();
        } else if (keycode == p.button2) {
            keyEvent(InputBase.BUTTON2_KEY, InputBase.DOWN);
            target.bt2Down();
        } else if (keycode == p.button3) {
            keyEvent(InputBase.BUTTON3_KEY, InputBase.DOWN);
            target.bt3Down();
        } else if (keycode == p.button4) {
            keyEvent(InputBase.BUTTON4_KEY, InputBase.DOWN);
            target.bt4Down();
        }

        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int keycode) {
        // #debugCode
        Dbg.dbg(Dbg.tag(this), "[" + id + "] buttonUp   -> keycode = " + keycode);

        if (target == null)
            return true;

        target.keyUp(keycode);

        // Always return true because there is only one key profile per controller

        if (keycode == p.up) {
            keyEvent(InputBase.UP_KEY, InputBase.UP);
            return true;
        } else if (keycode == p.right) {
            keyEvent(InputBase.RIGHT_KEY, InputBase.UP);
            return true;
        } else if (keycode == p.down) {
            keyEvent(InputBase.DOWN_KEY, InputBase.UP);
            return true;
        } else if (keycode == p.left) {
            keyEvent(InputBase.LEFT_KEY, InputBase.UP);
            return true;
        } else if (keycode == p.start) {
            keyEvent(InputBase.START_KEY, InputBase.UP);
            target.btStartUp();
            return true;
        } else if (keycode == p.button1) {
            keyEvent(InputBase.BUTTON1_KEY, InputBase.UP);
            target.bt1Up();
            return true;
        } else if (keycode == p.button2) {
            keyEvent(InputBase.BUTTON2_KEY, InputBase.UP);
            target.bt2Up();
            return true;
        } else if (keycode == p.button3) {
            keyEvent(InputBase.BUTTON3_KEY, InputBase.UP);
            target.bt3Up();
            return true;
        } else if (keycode == p.button4) {
            keyEvent(InputBase.BUTTON4_KEY, InputBase.UP);
            target.bt4Up();
            return true;
        }

        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        // #debugCode
        Dbg.dbg(Dbg.tag(this), "[" + id + "] axisMoved -> axisIndex = " + axisIndex + "; value = " + value);

        int val = Math.round(value);

        if (target != null && val != 0)
            target.keyDown(Profile.UNDEFINED);
        if (target != null && val == 0)
            target.keyUp(Profile.UNDEFINED);

        // Axis 0 (horizontal): -1 left; 1 right
        if (axisIndex == 0) {
            // Move left
            if (val == -1) {
                keyEvent(InputBase.LEFT_KEY, InputBase.DOWN);
                // target.btLeftDown();
            }
            // Move right
            else if (val == 1) {
                keyEvent(InputBase.RIGHT_KEY, InputBase.DOWN);
                // target.btRightDown();
            }
            // Right or left key up
            else if (val == 0) {
                keyEvent(InputBase.RIGHT_KEY, InputBase.UP);
                // target.btDownUp();
                keyEvent(InputBase.LEFT_KEY, InputBase.UP);
                // target.btUpUp();
            }
        }

        // Axis 1 (vertical) : -1 up; 1 down
        if (axisIndex == 1) {
            // Move up
            if (val == -1) {
                keyEvent(InputBase.UP_KEY, InputBase.DOWN);
                // target.btUpDown();
            }
            // Move down
            else if (val == 1) {
                keyEvent(InputBase.DOWN_KEY, InputBase.DOWN);
                // target.btDownDown();
            }
            // Up or down key up
            else if (val == 0) {
                keyEvent(InputBase.UP_KEY, InputBase.UP);
                // target.btUpUp();
                keyEvent(InputBase.DOWN_KEY, InputBase.UP);
                // target.btDownUp();
            }
        }

        return true;
    }

    @Override
    public void connected(Controller controller) {
    }

    @Override
    public void disconnected(Controller controller) {
    }
}
