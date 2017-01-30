/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.ai;

import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * @author Vinícius Jardim
 * 31/03/2016
 */
public class AiInput implements InputBase {

    private Map map;

    private boolean horizontalEvent = false;
    private boolean verticalEvent = false;

    /** Axis 3 (horizontal): -1 left; 1 right */
    private int horizontal = 0;
    /** Axis 2 (vertical)  : -1 up; 1 down */
    private int vertical = 0;

    /** Axis 3 (horizontal): -1 left; 1 right */
    private int horizontalOld = 0;
    /** Axis 2 (vertical)  : -1 up; 1 down */
    private int verticalOld = 0;

    public boolean move;
    private int hMove;
    private int rotation;
    public boolean fastFall;

    @Override
    public void setTarget(TargetBase target) {

        if(target instanceof Map) {
            map = (Map) target;
            cleanMove();
        }
    }

    @Override
    public void update() {
        if(!horizontalEvent) { horizontalOld = horizontal; }
        if(!verticalEvent) { verticalOld = vertical; }

        horizontalEvent = false;
        verticalEvent = false;

        if(move) move();
    }

    @Override
    public int getAxisX() { return horizontal; }

    @Override
    public int getAxisY() { return vertical; }

    @Override
    public int getAxisXOld() { return horizontalOld; }

    @Override
    public int getAxisYOld() { return verticalOld; }

    public void move() {

        if(map.isInState(Map.MState.PLAY_FALL)) {

            int deltaH = hMove - map.pb.b1x;
            int deltaR = rotation - map.pb.rotation;

            if(deltaH == 0) horizontal = 0;
            else horizontal = deltaH / Math.abs(deltaH);

            if(deltaR != 0) {
                // Rotate counterclockwise
                if(deltaR == 3) map.button3(true);
                // Rotate clockwise
                else map.button1(true);
            }

            if(deltaH == 0 && deltaR == 0 && fastFall) vertical = 1;
            else vertical = 0;
        }
        else {
            vertical = 0;
        }
    }

    /**
     * @param hMove column of the block (the left one when horizontal) on the map.
     * @param rotation rotation between 0 and 3
     * @param fastFall when true, act like the down key is pressed to the blocks fall faster
     */
    public void setMove(int hMove, int rotation, boolean fastFall) {

        // Move if map is not null
        this.move = map != null;
        this.hMove = hMove;

        // Because the right block goes to the left on this rotation
        // there is a need to fix hMove
        if(rotation == 3) this.hMove++;

        this.rotation = rotation;
        this.fastFall = fastFall;
    }

    public void cleanMove() {

        move = false;
        rotation = 0;
        fastFall = false;
        horizontal = 0;
        vertical = 0;
        horizontalOld = 0;
        verticalOld = 0;

        if(map == null) hMove = 0;
        else hMove = map.N_COL / 2;
    }
}
