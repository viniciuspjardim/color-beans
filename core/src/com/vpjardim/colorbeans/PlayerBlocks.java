package com.vpjardim.colorbeans;

import com.badlogic.gdx.math.MathUtils;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.input.InputBase;

public class PlayerBlocks {
    public transient Map m;
    /** Block 1: center block */
    public final Block b1;
    /** Block 2: this block rotates around the center block */
    public final Block b2;
    /** Block 1 color at the next ply */
    public int nextB1;
    /** Block 2 color at the next ply */
    public int nextB2;
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
     *
     * <pre>
     *   0
     * 3 C 1
     *   2
     * </pre>
     */
    public int rotation;

    /** Float numbers between -4 and 4 (4 is 360º) similar to rotation */
    public float rotationAnim;

    /** Amount moved by the player. Negative left; positive right */
    public int moveX;

    public PlayerBlocks(Map map) {
        m = map;
        b1 = new Block(m);
        b2 = new Block(m);
        recycle();
        init();
    }

    public void recycle() {
        b1.recycle();
        b2.recycle();
        nextB1 = MathUtils.random(Block.CLR_A, Block.CLR_N);
        nextB2 = MathUtils.random(Block.CLR_A, Block.CLR_N);
    }

    public void init() {
        b1.recycle();
        b2.recycle();

        // Above the center
        b2.setColor(nextB2);
        // Center
        b1.setColor(nextB1);

        nextB2 = MathUtils.random(Block.CLR_A, Block.CLR_N);
        nextB1 = MathUtils.random(Block.CLR_A, Block.CLR_N);

        // Center block with the white border tile
        b1.tile = 2;

        // Starts at the center column
        b1x = Map.N_COL / 2;
        // Starts before the first map row (out of the map)
        b1y = Map.OUT_ROW - 1;

        rotation = 0;
        rotationAnim = 0f;

        updateB2pos();
    }

    /** Returns true if there is collision in the current position */
    public boolean collide() {
        return collide(b1x, b1y);
    }

    /**
     * Returns true if it would have collision if the center block is in de given
     * position
     */
    public boolean collide(int mapCol, int mapRow) {
        // Center block check
        if (!m.isEmpty(mapCol, mapRow))
            return true;

        // Other block check
        int deltaX = mapCol - b1x;
        int deltaY = mapRow - b1y;

        return !m.isEmpty(b2x + deltaX, b2y + deltaY);
    }

    public boolean moveHorizontal(int value) {
        if (!collide(b1x + value, b1y)) {
            b1x += value;
            moveX = value;
            updateB2pos();
            return true;
        }
        return false;
    }

    public boolean moveDueCollision() {
        // Should not collide
        if (rotation == 0) {
            Dbg.print("processCollision: rotation == 0");
            return false;
        }
        // Move player blocks left
        if (rotation == 1) {
            m.hPlayMoveTimer = m.hPlayMoveWait;
            return moveHorizontal(-1);
        }
        // Move player blocks up
        if (rotation == 2) {
            b1y--;
            updateB2pos();
            return true;
        }
        // Move player blocks right
        if (rotation == 3) {
            m.hPlayMoveTimer = m.hPlayMoveWait;
            return moveHorizontal(1);
        }

        return false;
    }

    public void rotateClockwise(boolean detectCollision) {
        int prevRotation = rotation;

        rotation = rotation + 1;
        if (rotation > 3)
            rotation = 0;

        updateB2pos();

        if (detectCollision && collide()) {
            if (!moveDueCollision()) {
                rotateCounterclockwise(false);
            }
        }

        if (detectCollision && prevRotation != rotation) {
            m.rPlayMoveTimer = m.rPlayMoveWait;
        }
    }

    public void rotateCounterclockwise(boolean detectCollision) {
        int prevRotation = rotation;

        rotation = rotation - 1;
        if (rotation < 0)
            rotation = 3;

        updateB2pos();

        if (detectCollision && collide()) {
            if (!moveDueCollision()) {
                rotateClockwise(false);
            }
        }

        if (detectCollision && prevRotation != rotation) {
            m.rPlayMoveTimer = m.rPlayMoveWait;
        }
    }

    /** Update block 2 position according to rotation and center block position */
    public void updateB2pos() {
        if (rotation == 0) {
            b2x = b1x;
            b2y = b1y - 1;
        } else if (rotation == 1) {
            b2x = b1x + 1;
            b2y = b1y;
        } else if (rotation == 2) {
            b2x = b1x;
            b2y = b1y + 1;
        } else if (rotation == 3) {
            b2x = b1x - 1;
            b2y = b1y;
        }
    }

    /** Insert the player blocks in the map blocks array */
    public void insert() {
        if (b1y < 0 || b2y < 0) {
            m.lost = true;
            return;
        }

        m.b[b1x][b1y].setColor(b1.color);
        m.b[b2x][b2y].setColor(b2.color);

        boolean downKey = m.input != null && m.input.getKey(InputBase.DOWN_KEY);

        // Triggers b1 deform animation
        boolean b1Collide = !m.isEmpty(b1x, b1y + 1);
        if (downKey && b1Collide) {
            m.b[b1x][b1y].deformTime = m.afterGravityFallWait;
        }

        // Triggers b2 deform animation
        boolean b2Collide = !m.isEmpty(b2x, b2y + 1);
        if (downKey && b2Collide) {
            m.b[b2x][b2y].deformTime = m.afterGravityFallWait;
        }
    }

    public void playerFallCalc() {
        m.vPlayMoveTimer -= G.delta;
        boolean downKey = m.input != null && m.input.getKey(InputBase.DOWN_KEY);

        if (m.vPlayMoveTimer <= 0f) {
            // Looking if there is a collision on row bellow b1y
            if (!collide(b1x, b1y + 1)) {
                m.vPlayMoveTimer += m.vPlayMoveWait2;
                setFallStartEnd(b1y, b1y + 1);
                b1y++;
                updateB2pos();
            }
            // Wait some time before insert the player blocks.
            // The player can use this time to do his last moves
            else if (!downKey && m.vPlayMoveTimer >= -m.beforeInsertWait) {
            } else {
                insert();
                init();
                m.vPlayMoveTimer = m.vPlayMoveWait2;
                m.blockChanged = true;
            }
        }
    }

    private void setFallStartEnd(int start, int end) {
        int val = end - start;
        b1.moveY = val;
        b1.py = val;
        b2.moveY = val;
        b2.py = val;
    }
}
