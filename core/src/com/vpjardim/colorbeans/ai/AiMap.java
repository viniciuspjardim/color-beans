package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Pool;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.ai.ai3.Ai3;
import com.vpjardim.colorbeans.core.Dbg;

public class AiMap implements Pool.Poolable {
    public static int objCount = 0;
    public static final float MOVE_ILLEGAL = -999999999f;
    public static final float MOVE_LEGAL = 0f;
    public static final float MOVE_LOST = -99999999f;

    public static final Pool<AiMap> pool = new Pool<AiMap>(8450) {
        @Override
        protected AiMap newObject() {
            return new AiMap();
        }
    };

    public byte[][] b;
    public short[][] l;
    public int blocksDeleted;
    public int trashBlocks;
    public int deleteSize;
    public int outRow;
    public boolean labelDeleted;
    public transient Array<IntSet> le;
    public transient IntMap<Integer> lc;

    public static byte[][] getByteBlocks(byte[][] result, Block[][] source) {
        if (result == null)
            result = new byte[source.length][source[0].length];

        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[i].length; j++) {
                result[i][j] = (byte) source[i][j].color;
            }
        }

        return result;
    }

    public AiMap() {
        objCount++;
        le = new Array<>();
        lc = new IntMap<>();
    }

    private void init(int deleteSize, int outRow) {
        // Allocate new l only when necessary
        if (l == null || l.length != b.length || l[0].length != b[0].length)
            l = new short[b.length][b[0].length];

        blocksDeleted = 0;
        trashBlocks = 0;

        this.deleteSize = deleteSize;
        this.outRow = outRow;
        labelDeleted = false;
    }

    public void init(byte[][] blocks, int deleteSize, int outRow) {
        b = blocks;
        init(deleteSize, outRow);
    }

    public AiMap copy() {
        AiMap to = AiMap.pool.obtain();

        to.blocksDeleted = blocksDeleted;
        to.trashBlocks = trashBlocks;
        to.deleteSize = deleteSize;
        to.outRow = outRow;
        to.labelDeleted = labelDeleted;

        if (to.b == null) {
            to.b = new byte[b.length][b[0].length];
            to.l = new short[l.length][l[0].length];
        }

        for (int i = 0; i < b.length; i++) {
            System.arraycopy(b[i], 0, to.b[i], 0, b[i].length);
        }

        return to;
    }

    /**
     * @param color1   color of the upper block
     * @param color2   color of the lower (center) block
     * @param rotation rotation between 0 and 3: 0, 2 vertical; 1, 3 horizontal
     * @param position column of the block (the left one when horizontal) on the
     *                 map.
     */
    public float process(int color1, int color2, int position, int rotation) {
        // #debugCode in all the method

        // Done detect insert collisions/obstructed moves
        // When Ai tries to put the player blocks on a obstructed position
        // Done detect move that will lead to lost state
        // In case of collision on the blocks exit area this move will
        // lead to a lost state

        // Because the right block goes to the left on this rotation
        // there is a need to fix position
        if (rotation == 0) {
            if (isMoveObstructed(position, outRow - 1, position, outRow))
                return MOVE_ILLEGAL;

            b[position][outRow - 1] = (byte) color1; // upper
            b[position][outRow] = (byte) color2; // center
        } else if (rotation == 1) {
            if (isMoveObstructed(position, outRow, position + 1, outRow))
                return MOVE_ILLEGAL;

            b[position][outRow] = (byte) color2; // center stay
            b[position + 1][outRow] = (byte) color1; // upper goes down/left
        } else if (rotation == 2) {
            if (isMoveObstructed(position, outRow - 1, position, outRow))
                return MOVE_ILLEGAL;

            b[position][outRow - 1] = (byte) color2; // center goes up
            b[position][outRow] = (byte) color1; // upper goes down
        } else if (rotation == 3) {
            if (isMoveObstructed(position, outRow, position + 1, outRow))
                return MOVE_ILLEGAL;

            b[position][outRow] = (byte) color1; // upper goes down
            b[position + 1][outRow] = (byte) color2; // center goes right
        }

        if (Ai3.debug) {
            Dbg.print("================");
            Dbg.print("pos = " + position + "; rot = " + rotation);
            print();
        }

        do {
            gravityFallCalc();
            labelCalc();

        } while (labelDeleted);

        int center = b.length / 2;

        // The blocks are obstructed. This move lead to game over.
        if (b[center][outRow] != Block.EMPTY ||
                b[center][outRow + 1] != Block.EMPTY) {
            return AiMap.MOVE_LOST;
        }

        trashBlocksCalc();

        return MOVE_LEGAL;
    }

    public boolean isBlock(int col, int row) {
        return b[col][row] != Block.EMPTY;
    }

    public boolean isMoveObstructed(int b1X, int b1Y, int b2X, int b2Y) {
        int center = b.length / 2;
        int deltaX = b1X - center;
        int sign = deltaX != 0 ? deltaX / Math.abs(deltaX) : 1;

        for (int i = 0; i <= Math.abs(deltaX); i++) {
            if (isBlock(b1X - i * sign, b1Y) || isBlock(b2X - i * sign, b2Y))
                return true;
        }

        return false;
    }

    private void trashBlocksCalc() {
        if (blocksDeleted <= 0)
            return;

        trashBlocks = Math.round(((blocksDeleted * 10f) * (blocksDeleted - 3f)) / 70f);
    }

    private void gravityFallCalc() {
        // Loop through the columns 0(left) to 6(right)
        for (int col = 0; col < b.length; col++) {
            // Number of empty rows blocks in this column
            int nEmpty = 0;

            // Loop through the rows 14 + OUT_ROW (floor) to 0 (top) of the actual
            // column. Keep looking from floor to top and counting empty blocks
            // then move down non empty the number that has been counted
            for (int row = b[col].length - 1; row >= 0; row--) {
                // If it`s not empty then swap: the block goes down
                // and the empty goes up
                if (b[col][row] != Block.EMPTY) {
                    if (nEmpty > 0) {
                        byte swap = b[col][row + nEmpty];
                        b[col][row + nEmpty] = b[col][row];
                        b[col][row] = swap;
                    }
                }
                // If it`s empty increment
                else {
                    nEmpty++;
                }
            }
        }
    }

    private void labelCalc() {
        recycleLabelEquivalence();

        short label = 1;

        // cols 0 -> 6
        for (int col = 0; col < b.length; col++) {
            // row 0 -> 14 + OUT_ROW
            for (int row = 0; row < b[col].length; row++) {
                // Empty blocks and trash blocks don`t group at first:
                // put a zero label
                if (!Block.isColor(b[col][row])) {
                    l[col][row] = 0;
                    continue;
                }

                boolean sameColorLeft = false;
                boolean sameColorUpper = false;
                int left = col - 1;
                int upper = row - 1;

                // If it has the same color of the left block
                // then mark with the same label
                if (left >= 0 && b[left][row] == b[col][row]) {
                    l[col][row] = l[left][row];
                    sameColorLeft = true;
                }

                // If it has the same color of the upper block
                if (upper >= 0 && b[col][upper] == b[col][row]) {
                    sameColorUpper = true;

                    // If it has the same color of the left block add a label
                    // equivalence
                    if (sameColorLeft) {
                        addLabelEquivalence(l[col][row], l[col][upper]);
                    }

                    // copy the label of the top block
                    l[col][row] = l[col][upper];
                }

                // If it has not the same color of the left and de upper one,
                // then put a new label
                if (!sameColorLeft && !sameColorUpper) {
                    l[col][row] = label;
                    label++;
                }
            }
        }

        mergeEquivalentLabels();
        labelDelete();
    }

    private void addLabelEquivalence(int labelA, int labelB) {
        // If they are equal they not need to be marked
        // as equivalent
        if (labelA == labelB)
            return;

        // try to find a set that contains A or B label
        for (IntSet s : le) {
            // If contains A or B, add A and B
            if (s.contains(labelA) || s.contains(labelB)) {
                s.addAll(labelA, labelB);
                return;
            }
        }

        // Try to find a empty set where A and B labels
        // can be added
        for (IntSet s : le) {
            if (s.size == 0) {
                s.addAll(labelA, labelB);
                return;
            }
        }

        // Add a new set with A and B labels
        IntSet s = new IntSet();
        s.addAll(labelA, labelB);
        le.add(s);
    }

    private void mergeEquivalentLabels() {
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                if (l[i][j] == 0)
                    continue;

                for (IntSet s : le) {
                    if (s.contains(l[i][j])) {
                        l[i][j] = (short) s.first();
                    }
                }

                // Updates the number of blocks with the same
                // label (which are the same color and form a group)
                int v = lc.get(l[i][j], 0);
                lc.put(l[i][j], ++v);
            }
        }
    }

    private void labelDelete() {
        labelDeleted = false;

        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                if (l[i][j] == 0)
                    continue;

                if (lc.get(l[i][j]) >= deleteSize) {
                    b[i][j] = Block.EMPTY;
                    l[i][j] = 0;
                    blocksDeleted++;
                    labelDeleted = true;

                    // Check the 4 adjacent blocks: delete those that are trash blocks
                    if (i - 1 >= 0 && b[i - 1][j] == Block.CLR_T)
                        b[i - 1][j] = Block.EMPTY;

                    if (i + 1 < b.length && b[i + 1][j] == Block.CLR_T)
                        b[i + 1][j] = Block.EMPTY;

                    if (j - 1 >= 0 && b[i][j - 1] == Block.CLR_T)
                        b[i][j - 1] = Block.EMPTY;

                    if (j + 1 < b[i].length && b[i][j + 1] == Block.CLR_T)
                        b[i][j + 1] = Block.EMPTY;
                }
            }
        }
    }

    private void recycleLabelEquivalence() {
        for (IntSet s : le) {
            s.clear();
        }

        lc.clear();
    }

    // #debugCode
    public void print() {
        Dbg.print("Blocks print ->");

        // row 0 -> 14 + OUT_ROW
        for (int row = 7; row < b[0].length; row++) {
            // col 0 -> 6
            for (int col = 0; col < b.length; col++) {
                System.out.print(b[col][row] + " ");
            }

            Dbg.print("");

            if (row == outRow - 1)
                Dbg.print("---");
        }
    }

    @Override
    public void reset() {
    }
}
