package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.InputProcessor;
import com.vpjardim.colorbeans.core.Dbg;

public class KeyboardInput implements InputBase, InputProcessor {
    private TargetBase target;
    public Profile p;
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
        // A key held down with the old profile would never come up
        keyMap = 0;
        keyMapOld = 0;
        event = 0;
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
    public boolean keyDown(int keycode) {
        Dbg.dbg(Dbg.tag(this), "[" + id + "] keyDown -> keycode = " + keycode);

        // The keys' state is kept without a target too, so the menus can read it
        int key = p.keyOf(keycode);

        if (key != Profile.UNDEFINED)
            keyEvent(key, InputBase.DOWN);

        if (target == null)
            return false;

        target.keyDown(keycode);

        if (key == InputBase.START_KEY)
            target.btStartDown();
        else if (key == InputBase.BUTTON1_KEY)
            target.bt1Down();
        else if (key == InputBase.BUTTON2_KEY)
            target.bt2Down();
        else if (key == InputBase.BUTTON3_KEY)
            target.bt3Down();
        else if (key == InputBase.BUTTON4_KEY)
            target.bt4Down();

        // Return true when the key down event is handled. False if it's not so other
        // keyboard input (with another key profile) can handle it
        return key != Profile.UNDEFINED;
    }

    @Override
    public boolean keyUp(int keycode) {
        Dbg.dbg(Dbg.tag(this), "[" + id + "] keyUp   -> keycode = " + keycode);

        int key = p.keyOf(keycode);

        if (key != Profile.UNDEFINED)
            keyEvent(key, InputBase.UP);

        if (target == null)
            return false;

        target.keyUp(keycode);

        if (key == InputBase.START_KEY)
            target.btStartUp();
        else if (key == InputBase.BUTTON1_KEY)
            target.bt1Up();
        else if (key == InputBase.BUTTON2_KEY)
            target.bt2Up();
        else if (key == InputBase.BUTTON3_KEY)
            target.bt3Up();
        else if (key == InputBase.BUTTON4_KEY)
            target.bt4Up();

        // Return true when the key up event is handled. False if it's not so other
        // keyboard input (with another key profile) can handle it
        return key != Profile.UNDEFINED;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
