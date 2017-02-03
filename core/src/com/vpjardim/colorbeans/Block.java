/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class Block {

    public static final String RED_STR     = "game/red";
    public static final String BLUE_STR    = "game/blue";
    public static final String GREEN_STR   = "game/green";
    public static final String YELLOW_STR  = "game/yellow";
    public static final String PURPLE_STR  = "game/purple";
    public static final String DBLUE_STR   = "game/dblue";
    public static final String ORANGE_STR  = "game/orange";
    public static final String MAGENTA_STR = "game/magenta";
    public static final String MAROON_STR  = "";
    public static final String WHITE_STR   = "";
    public static final String BLACK_STR   = "";
    public static final String TRASH_STR   = "game/transparent";

    /** Empty space: no block */
    public static final int EMPTY = 0;
    /** First color number (1 default)*/
    public static final int CLR_A = 1;
    /** Last color number (5 default)*/
    public static final int CLR_N = 5;
    /** Color of the trash block */
    public static final int CLR_T = 9;

    public transient Map m;

    public transient Color color;

    public int intColor;
    public String strColor;

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
    public boolean visible;

    public int label;
    public float toDelete;

    public int moveX; // negative left; positive right
    public int moveY; // positive down; negative up
    public float moveTime;
    public float deformTime;

    public float px;
    public float py;


    public Block(Map map) {
        m = map;
        recycle();
    }

    public static Color intToColor(int iColor) {

        Color c = null;

        if(iColor == Block.CLR_A)          c = Color.RED;     // 1
        else if(iColor == Block.CLR_A +1)  c = Color.BLUE;    // 2
        else if(iColor == Block.CLR_A +2)  c = Color.GREEN;   // 3
        else if(iColor == Block.CLR_A +3)  c = Color.YELLOW;  // 4
        else if(iColor == Block.CLR_A +4)  c = Color.PURPLE;  // 5 (last on by default)
        else if(iColor == Block.CLR_A +5)  c = Color.CYAN;    // 6 should be dark blue instead
        else if(iColor == Block.CLR_A +6)  c = Color.ORANGE;  // 7
        else if(iColor == Block.CLR_A +7)  c = Color.MAGENTA; // 8
        else if(iColor == Block.CLR_A +8)  c = Color.MAROON;  // 9
        else if(iColor == Block.CLR_A +9)  c = Color.WHITE;   // 10
        else if(iColor == Block.CLR_A +10) c = Color.BLACK;   // 11

        if(iColor == Block.CLR_T)          c = Color.GRAY;    // 9

        return c;
    }

    public static String intToColorStr(int iColor) {

        String c = null;

        if(iColor == Block.CLR_A)          c = RED_STR;
        else if(iColor == Block.CLR_A +1)  c = BLUE_STR;
        else if(iColor == Block.CLR_A +2)  c = GREEN_STR;
        else if(iColor == Block.CLR_A +3)  c = YELLOW_STR;
        else if(iColor == Block.CLR_A +4)  c = PURPLE_STR;
        else if(iColor == Block.CLR_A +5)  c = DBLUE_STR;
        else if(iColor == Block.CLR_A +6)  c = ORANGE_STR;
        else if(iColor == Block.CLR_A +7)  c = MAGENTA_STR;
        else if(iColor == Block.CLR_A +8)  c = MAROON_STR;
        else if(iColor == Block.CLR_A +9)  c = WHITE_STR;
        else if(iColor == Block.CLR_A +10) c = BLACK_STR;

        if(iColor == Block.CLR_T)          c = TRASH_STR;

        return c;
    }

    public void recycle() {

        intColor   = EMPTY;
        strColor   = null;
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
        return intColor >= CLR_A && intColor <= CLR_N;
    }

    public boolean isEmpty() {
        return intColor == EMPTY;
    }

    public boolean isTrash() {
        return intColor == CLR_T;
    }

    public void setColor(int intColor) {

        assert(intColor != EMPTY);

        recycle();
        this.intColor = intColor;
        strColor = Block.intToColorStr(intColor);
        color = Block.intToColor(intColor);
        visible = true;
    }

    public void recycleFall() {
        moveY = 0;
        moveTime = 0f;
        py = 0f;
    }

    public void setFreeFallTrajectory(int freeFallStart, int freeFallEnd) {

        moveY = freeFallEnd - freeFallStart;
        py = freeFallEnd - freeFallStart;
    }

    /**
     * Needs to be called before render when the map is loaded from a serialized source. This
     * because some references and objects are not serialized and it needs to be setup
     */
    public void deserialize(Map m) {
        this.m = m;
        color = Block.intToColor(intColor);
    }
}
