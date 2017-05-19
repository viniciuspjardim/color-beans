/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans;

/**
 * Describe a space indexed by column index and row index on the {@link Map#b} matrix. It can be
 * empty when {@link #color} equals the EMPTY constant or represent a bean of the following
 * colors: red, blue, green, yellow, purple. It's also used in the {@link PlayerBlocks} class as the
 * playable falling beans.
 * Each bean is grouped to all 4-neighborhood beans that has the same color creating chains. Each
 * chain has a unique number stored in {@link #label} field.
 *
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class Block {

    /** Empty space: no block */
    public static final int EMPTY = 0;

    /** First color number (1 default)*/
    public static final int CLR_A = 1;

    /** Last color number (5 default)*/
    public static final int CLR_N = 5;

    /** Color of the trash block */
    public static final int CLR_T = 9;

    public transient Map m;

    public int color;

    /**
     * X represents de current block, then the tile number will have de shape ABCD. Each digit
     * represents if there is a link (1) or not (0). There are 3 tiles that don't have links and
     * don't follow this rule: the white bordered (2), the stretched (3) and the squeezed (4)
     *   A
     * D X B
     *   C
     * Example: 101. There are links only in B and D (no leading zeros needed cause it's a integer)
     */
    public int tile;

    /** true to be rendered */
    public boolean visible;

    /** Unique group / chain number shared with blocks chained in 4-neighborhood */
    public int label;

    /** Time before block is deleted. 0 means it's not tagged to deletion */
    public float toDelete;

    public int moveX;

    /** The number of levels (rows) it fell down. Used to know the block's previous position */
    public int moveY;

    /**
     * The time that the block is falling. Starts at 0 and increment.
     * When there is a gravity fall or a player fall the block is moved to its final position
     * instantly. For the animation to happen the {@link #py} shift and moveTime is used to track
     * the block's position.
     */
    public float moveTime;

    /** Deform animation timer. Starts at {@link Map#afterGravityFallWait} and decrement */
    public float deformTime;

    /**
     * Shift in the x axis relative to the block's position in the Map. Negative is left, positive
     * is right. The unit is the block side length
     */
    public float px;

    /**
     * Shift in the y axis relative to the block's position in the Map. Negative is up, positive
     * is down. The unit is the block side length
     */
    public float py;


    public Block(Map map) {
        m = map;
        recycle();
    }

    public void recycle() {

        color      = EMPTY;
        tile       = 0;
        visible    = false;

        label      = 0;
        toDelete   = 0f;

        moveX      = 0;
        moveY      = 0;
        moveTime   = 0f;
        deformTime = 0f;

        px         = 0f;
        py         = 0f;
    }

    public static boolean isColor(int color) {
        return color >= CLR_A && color <= CLR_N;
    }

    public boolean isColor() {
        return color >= CLR_A && color <= CLR_N;
    }

    public boolean isEmpty() {
        return color == EMPTY;
    }

    public boolean isTrash() {
        return color == CLR_T;
    }

    public void setColor(int color) {
        recycle();
        this.color = color;
        visible = true;
    }

    public void setEmpty() {
        recycle();
        color = EMPTY;
        visible = false;
    }

    public void recycleFall() {
        moveY = 0;
        moveTime = 0f;
        py = 0f;
    }

    public void setGravityFallTrajectory(int gravityFallStart, int gravityFallEnd) {
        moveY = gravityFallEnd - gravityFallStart;
        py = gravityFallEnd - gravityFallStart;
    }

    /**
     * Needs to be called before render when the map is loaded from a serialized source. This
     * because some references and objects are not serialized and it needs to be setup
     */
    public void deserialize(Map m) {
        this.m = m;
    }
}
