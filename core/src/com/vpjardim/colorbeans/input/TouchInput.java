/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.input.GestureDetector;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.core.Dbg;

/**
 * @author Vinícius Jardim
 *         2017/02/10
 */
public class TouchInput extends GestureDetector.GestureAdapter implements InputBase {

    private TargetBase target;
    private Map map;
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

    public int width = 400;
    public float[] div;
    public boolean hPanning = false;
    public boolean vPanning = false;
    public boolean move = false;
    public boolean draw = false;

    public int moveCurr;
    public int moveStart;

    public float touchX = 0f;
    public float touchY = 0f;
    public float dTouchX = 0f;
    public float dTouchY = 0f;

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
        div = new float[Map.N_COL + 1];

        if (target instanceof Map)
            map = (Map) target;
        else
            map = null;
    }

    @Override
    public void setProfile(Profile profile) {
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Profile getProfile() {
        return null;
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

        if (map != null && map.isInState(Map.MState.PLAYER_FALL) && move)
            move();
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

    private void move() {

        // Horizontal delta
        int deltaH = moveCurr - map.pb.b1x;

        // Needs to move right
        if (deltaH > 0) {
            keyEvent(InputBase.RIGHT_KEY, InputBase.DOWN);
            keyEvent(InputBase.LEFT_KEY, InputBase.UP);
        }
        // Needs to move left
        else if (deltaH < 0) {
            keyEvent(InputBase.LEFT_KEY, InputBase.DOWN);
            keyEvent(InputBase.RIGHT_KEY, InputBase.UP);
        }
        // No move
        else {
            keyEvent(InputBase.RIGHT_KEY, InputBase.UP);
            keyEvent(InputBase.LEFT_KEY, InputBase.UP);
        }
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        // TODO: tap represents a touch down and touch up event. Currently we are only
        // triggering the key down event

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "tap -> x = " + x + "; y = " + y + "; count = " + count +
                "; button = " + button);

        if (target == null)
            return false;

        if (x > G.width / 2f) {
            keyEvent(InputBase.BUTTON1_KEY, InputBase.DOWN);
            target.bt3Down();
        } else {
            keyEvent(InputBase.BUTTON3_KEY, InputBase.DOWN);
            target.bt1Down();
        }

        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "pan ->   x = " + x + ";  y = " + y + "; dTouchX = " +
                deltaX + "; dTouchY = " + deltaY);

        // If not panning, resetting values
        if (!hPanning && !vPanning) {
            move = false;
            hPanning = false;
            vPanning = false;
            touchX = x;
            touchY = y;
            dTouchX = 0f;
            dTouchY = 0f;
            if (map != null)
                moveStart = map.pb.b1x;
            else
                moveStart = Map.N_COL / 2;
        }

        draw = true;
        dTouchX += deltaX;
        dTouchY += deltaY;
        updateDiv();
        findCurrent();

        boolean vertical = InputBase.getKeyMapKey(keyMap, InputBase.UP_KEY) ||
                InputBase.getKeyMapKey(keyMap, InputBase.DOWN_KEY);

        boolean horizontal = InputBase.getKeyMapKey(keyMap, InputBase.RIGHT_KEY) ||
                InputBase.getKeyMapKey(keyMap, InputBase.LEFT_KEY);

        if (!vertical && Math.abs(deltaX) >= Math.abs(deltaY) * 0.8) {
            hPanning = true;
            move = true;

            if (map == null) {
                if (deltaX < 0)
                    keyEvent(InputBase.LEFT_KEY, InputBase.DOWN);
                else if (deltaX > 0)
                    keyEvent(InputBase.RIGHT_KEY, InputBase.DOWN);
            }
        } else if (!horizontal && !hPanning && deltaY > 0) {
            vPanning = true;
            keyEvent(InputBase.DOWN_KEY, InputBase.DOWN);
        }

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "pan2 -> tx = " + touchX + "; ty = " + touchY +
                "; dTouchX = " + this.dTouchX + "; dTouchY = " + this.dTouchY);

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "panStop -> x = " + x + "; y = " + y);

        keyEvent(InputBase.UP_KEY, InputBase.UP);
        // target.btUpUp();

        keyEvent(InputBase.RIGHT_KEY, InputBase.UP);
        // target.btRightUp();

        keyEvent(InputBase.DOWN_KEY, InputBase.UP);
        // target.btDownUp();

        keyEvent(InputBase.LEFT_KEY, InputBase.UP);
        // target.btLeftUp();

        hPanning = false;
        vPanning = false;
        move = false;
        draw = false;
        moveCurr = 0;
        moveStart = 0;

        touchX = 0f;
        touchY = 0f;
        dTouchX = 0f;
        dTouchY = 0f;

        return false;
    }

    private void updateDiv() {

        float colWidth = width / Map.N_COL;
        float x = touchX - (moveStart * colWidth) - colWidth / 2f;
        div[0] = x;
        div[div.length - 1] = x + width;

        for (int i = 1; i < div.length - 1; i++) {
            div[i] = x + i * colWidth;
        }
    }

    private void findCurrent() {

        float touchCurr = dTouchX + touchX;

        for (int i = 1; i < div.length - 1; i++) {
            if (div[i] > touchCurr) {
                moveCurr = i - 1;
                return;
            }
        }
        moveCurr = Map.N_COL - 1;
    }
}
