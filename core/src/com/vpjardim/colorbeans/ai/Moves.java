/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.utils.IntArray;
import com.vpjardim.colorbeans.Block;

/**
 * @author Vinícius Jardim
 *         2016/05/02
 */
public class Moves {

    /** All moves */
    private IntArray movesAll;
    /** Moves for 2 different colors */
    private IntArray moves2C;
    /** Moves for 2 blocks with the same color */
    private IntArray moves1C;

    public int color1;
    public int color2;
    public int position;
    public int rotation;

    public void init(int nCol) {

        movesAll = new IntArray(325);
        moves2C = new IntArray(26);
        moves1C = new IntArray(13);

        // === All colors ===
        for (int color1 = 1; color1 < 6; color1++) {
            for (int color2 = color1; color2 < 6; color2++) {

                // No rotation (vertical)
                for (int i = 0; i < nCol; i++)
                    movesAll.add(getMove(color1, color2, i, 0));
                // Rotation = 1 (horizontal)
                for (int i = 0; i < nCol - 1; i++)
                    movesAll.add(getMove(color1, color2, i, 1));

                // If the colors are equal, the following moves wore
                // already covered. Do not need the 2 and the 3 rotation
                if (color1 != color2) {
                    // Rotation = 2 (vertical)
                    for (int i = 0; i < nCol; i++)
                        movesAll.add(getMove(color1, color2, i, 2));
                    // Rotation = 3 (horizontal)
                    for (int i = 0; i < nCol - 1; i++)
                        movesAll.add(getMove(color1, color2, i, 3));
                }
            }
        }

        // === Fixed different colors ===

        // No rotation (vertical)
        for (int i = 0; i < nCol; i++)
            moves2C.add(getMove(99, 99, i, 0));
        // Rotation = 1 (horizontal)
        for (int i = 0; i < nCol - 1; i++)
            moves2C.add(getMove(99, 99, i, 1));
        // Rotation = 2 (vertical)
        for (int i = 0; i < nCol; i++)
            moves2C.add(getMove(99, 99, i, 2));
        // Rotation = 3 (horizontal)
        for (int i = 0; i < nCol - 1; i++)
            moves2C.add(getMove(99, 99, i, 3));

        // === Fixed equal colors ===

        // No rotation (vertical)
        for (int i = 0; i < nCol; i++)
            moves1C.add(getMove(99, 99, i, 0));
        // Rotation = 1 (horizontal)
        for (int i = 0; i < nCol - 1; i++)
            moves1C.add(getMove(99, 99, i, 1));
    }

    public static int getMove(int color1, int color2, int position, int rotation) {
        return (color1 * 100000) + (color2 * 1000) + (position * 10) + rotation;
    }

    public int getMove() {
        return Moves.getMove(color1, color2, position, rotation);
    }

    /**
     * @param move number in the format AABBPPR (AA = color1, BB color2, PP =
     *             position, R =
     *             rotation)
     */
    public void setMove(int move) {
        color1 = move / 100000;
        color2 = (move / 1000) % 100;
        position = (move / 10) % 100;
        rotation = move % 10;
    }

    public IntArray getArray() {
        return movesAll;
    }

    public IntArray getArray(int color1, int color2) {

        if (color1 < Block.CLR_A)
            return movesAll;
        if (color1 == color2)
            return moves1C;

        return moves2C;
    }

    public String toString() {
        return color1 + ";" + color2 + ";" + position + ";" + rotation;
    }
}
