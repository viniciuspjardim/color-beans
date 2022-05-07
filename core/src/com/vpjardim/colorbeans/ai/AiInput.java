/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.ai;

import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.Profile;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * @author Vinícius Jardim
 *         2016/03/31
 */
public class AiInput implements InputBase {

    private Map map;

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

    public boolean move;
    private int hMove;
    private int rotation;
    public boolean fastFall;

    @Override
    public void setTarget(TargetBase target) {
        if (target instanceof Map) {
            map = (Map) target;
            cleanMove();
        }
    }

    @Override
    public void setProfile(Profile profile) {
    }

    @Override
    public void setId(int id) {
    }

    @Override
    public Profile getProfile() {
        return null;
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException("Ai Input does not have an id");
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

    public void move() {

        // Horizontal delta
        int deltaH = hMove - map.pb.b1x;

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

        // Rotation delta
        int deltaR = rotation - map.pb.rotation;

        if (deltaR != 0) {
            // Rotate counterclockwise
            if (deltaR == 3)
                map.bt3Down();
            // Rotate clockwise
            else
                map.bt1Down();
        }

        // If there is no horizontal move and no rotation to be done, the down key can
        // be pressed
        if (deltaH == 0 && deltaR == 0 && fastFall)
            keyEvent(InputBase.DOWN_KEY, InputBase.DOWN);
        else
            keyEvent(InputBase.DOWN_KEY, InputBase.UP);
    }

    /**
     * Sets where the player block should be moved to
     *
     * @param hMove    horizontal move: column of where the left most player block
     *                 should be on the map
     * @param rotation rotation between 0 and 3
     * @param fastFall when true, act like the down key is pressed to the blocks
     *                 fall faster
     */
    public void setMove(int hMove, int rotation, boolean fastFall) {

        // Move if map is not null
        this.move = map != null;
        this.hMove = hMove;

        // Because the right block goes to the left on this rotation
        // there is a need to fix hMove
        if (rotation == 3)
            this.hMove++;

        this.rotation = rotation;
        this.fastFall = fastFall;
    }

    public void cleanMove() {

        keyMap = 0;
        keyMapOld = 0;
        move = false;
        rotation = 0;
        fastFall = false;

        if (map == null)
            hMove = 0;
        else
            hMove = Map.N_COL / 2;
    }
}
