/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans;

import com.badlogic.gdx.math.MathUtils;

/**
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class PlayBlocks {

    public transient Map m;

    /** Block 1: center block */
    public Block b1;
    /** Block 2: this block rotates around the center block*/
    public Block b2;

    /** Width position (x) on the map of center block */
    public int b1x;
    /** Height position (y) on the map of center block */
    public int b1y;
    /** Width position (x) on the map of block 2 */
    public int b2x;
    /** Height position (y) on the map of block 2 */
    public int b2y;

    /**
     * Current rotation shape.
     * Rotations around the center block:
     * <pre>
     *   0
     * 3 C 1
     *   2
     * </pre>
     */
    public int rotation;

    /** Amount moved by the player. Negative left; positive right */
    public int moveX;

    public PlayBlocks(Map map) {
        m = map;
        b1 = new Block(m);
        b2 = new Block(m);
        recycle();
        init();
    }

    public void recycle() {
        b1.recycle();
        b2.recycle();
    }

    public void init() {
        // Above the center
        b2.setColor(MathUtils.random(Block.CLR_A, Block.CLR_N));
        // Center
        b1.setColor(MathUtils.random(Block.CLR_A, Block.CLR_N));

        // Center block with the white border tile
        b1.tile = 1;

        // Starts at the center column
        b1x = Map.N_COL / 2;
        // Starts before the first map row (out of the map)
        b1y = Map.OUT_ROW -1;

        rotation = 0;

        updateB2pos();
    }

    /** Returns true if there is collision in the current position */
    public boolean collide() {
        return collide(b1x, b1y);
    }

    /** Returns true if it would have collision if the center block is in de given position */
    public boolean collide(int mapCol, int mapRow) {

        // Center block check
        if(!m.isEmpty(mapCol, mapRow)) return true;

        // Other block check
        int deltaX = mapCol - b1x;
        int deltaY = mapRow - b1y;

        if(!m.isEmpty(b2x + deltaX, b2y + deltaY)) return true;

        return false;
    }

    public boolean moveHorizontal(int value) {

        if(!collide(b1x + value, b1y)) {
            b1x += value;
            moveX = value;
            updateB2pos();
            return true;
        }
        return false;
    }

    public void rotateClockwise(boolean detectCollision) {

        int prevRotation = rotation;

        rotation = rotation + 1;
        if(rotation > 3) rotation = 0;

        updateB2pos();

        if(detectCollision && collide()) {
            if(moveHorizontal(1)) {}
            else if(moveHorizontal(-1)) {}
            else rotateCounterclockwise(false);
        }

        if(detectCollision && prevRotation != rotation) {
            m.prop.rPlayMoveWait = m.prop.rPlayMoveTime;
        }
    }

    public void rotateCounterclockwise(boolean detectCollision) {

        int prevRotation = rotation;

        rotation = rotation - 1 ;
        if(rotation < 0) rotation = 3;

        updateB2pos();

        if(detectCollision && collide()) {
            if(moveHorizontal(1)) {}
            else if(moveHorizontal(-1)) {}
            else rotateClockwise(false);
        }

        if(detectCollision && prevRotation != rotation) {
            m.prop.rPlayMoveWait = m.prop.rPlayMoveTime;
        }
    }

    /** Update block 2 position according to rotation and center block position */
    public void updateB2pos() {

        if(rotation == 0) {
            b2x = b1x;
            b2y = b1y -1;
        }
        else if(rotation == 1) {
            b2x = b1x +1;
            b2y = b1y;
        }
        else if(rotation == 2) {
            b2x = b1x;
            b2y = b1y +1;
        }
        else if(rotation == 3) {
            b2x = b1x -1;
            b2y = b1y;
        }
    }

    /** Insert the play blocks in the map blocks array */
    public void insert() {

        if(b1y < 0 || b2y < 0) {
            m.prop.lost = true;
            return;
        }

        m.b[b1x][b1y].setColor(b1.intColor);
        m.b[b2x][b2y].setColor(b2.intColor);

        boolean downKeyPressed = m.input != null && m.input.getAxisY() == 1;

        // Triggers b1 deform animation
        boolean b1Collide = !m.isEmpty(b1x, b1y + 1);
        if(downKeyPressed && b1Collide) {
            m.b[b1x][b1y].deformTime = m.prop.afterFreeFallWait;
        }

        // Triggers b2 deform animation
        boolean b2Collide = !m.isEmpty(b2x, b2y + 1);
        if(downKeyPressed && b2Collide) {
            m.b[b2x][b2y].deformTime = m.prop.afterFreeFallWait;
        }
    }

    public void playFallCalc() {

        m.prop.vPlayMoveWait -= G.delta;
        boolean downKeyPressed = m.input != null && m.input.getAxisY() == 1;

        if(m.prop.vPlayMoveWait <= 0f) {

            // Looking if there is a collision on row bellow b1y
            if(!collide(b1x, b1y + 1)) {

                m.prop.vPlayMoveWait += m.prop.vPlayMoveTime;
                setFallStartEnd(b1y, b1y + 1);
                b1y++;
                updateB2pos();
            }
            // Wait some time before insert the play blocks.
            // The player can use this time to do his last moves
            else if(!downKeyPressed && m.prop.vPlayMoveWait >= -m.prop.beforeInsertWait) {

            }
            else {
                insert();
                recycle();
                init();
                m.prop.vPlayMoveWait = m.prop.vPlayMoveTime;
                m.blockInsert = true;
            }
        }
    }

    private void setFallStartEnd(int start, int end) {
        int val  = end - start;
        b1.moveY = val;
        b1.py    = val;
        b2.moveY = val;
        b2.py    = val;
    }

    /**
     * Needs to be called before render when the map is loaded from
     * a serialized source. This because some references and objects
     * are not serialized and it needs to be setup
     */
    public void deserialize(Map m) {
        this.m = m;
        b1.deserialize(m);
        b2.deserialize(m);
    }
}
