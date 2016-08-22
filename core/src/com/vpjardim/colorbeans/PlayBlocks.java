package com.vpjardim.colorbeans;

import com.badlogic.gdx.math.MathUtils;

/**
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class PlayBlocks {

    public transient Map m;

    public Block[][] b;

    /**
     * Indexes around the center block
     * <pre>
     *   0
     * 3 C 1
     *   2
     * </pre>
     */
    public transient Block[] nonCenter;

    /** Width position on the map of the center block on a 3x3 matrix */
    public int mCol;
    /** Height position on the map of the center block on a 3x3 matrix */
    public int mRow;

    public int rotation;

    public int moveX; // negative left; positive right
    public int moveY; // positive down; negative up
    public float moveTime;

    public PlayBlocks(Map map) {

        m = map;
        b = new Block[3][3];
        nonCenter = new Block[4];

        // Create empty blocks
        for(int i = 0; i < b.length; i++) {

            for(int j = 0; j < b[i].length; j++) {

                b[i][j] = new Block(m);
            }
        }

        recycle();
        init();
    }

    public void recycle() {

        for(int i = 0; i < b.length; i++) {

            for(int j = 0; j < b[i].length; j++) {

                b[i][j].recycle();
            }
        }
    }

    public void init() {
        // Above the center
        b[1][0].setColor(MathUtils.random(Block.CLR_A, Block.CLR_N));
        // Center
        b[1][1].setColor(MathUtils.random(Block.CLR_A, Block.CLR_N));

        b[1][1].tile = 1;

        // Starts at the center column
        mCol = Map.N_COL / 2;
        // Starts before the first map row (out of the map)
        mRow = Map.OUT_ROW -1;

        rotation = 0;

        bToNonCenter();
    }

    public void bToNonCenter() {
        nonCenter[0] = b[1][0];
        nonCenter[1] = b[2][1];
        nonCenter[2] = b[1][2];
        nonCenter[3] = b[0][1];
    }

    public void nonCenterToB() {
        b[1][0]= nonCenter[0];
        b[2][1]= nonCenter[1];
        b[1][2]= nonCenter[2];
        b[0][1]= nonCenter[3];
    }

    public boolean collide() {
        return collide(mCol, mRow);
    }

    public boolean collide(int mapCol, int mapRow) {

        for(int i = 0; i < b.length; i++) {

            for(int j = 0; j < b[i].length; j++) {

                if(b[i][j].isEmpty()) continue;

                if(!m.isEmpty(mapIdxCol(i, mapCol), mapIdxRow(j, mapRow))) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean moveHorizontal(int value) {

        if(!collide(mCol + value, mRow)) {
            mCol += value;
            moveX = value;
            return true;
        }
        return false;
    }

    public void rotateClockwise(boolean detectCollision) {

        bToNonCenter();

        Block last = nonCenter[3];

        nonCenter[3] = nonCenter[2];
        nonCenter[2] = nonCenter[1];
        nonCenter[1] = nonCenter[0];
        nonCenter[0] = last;

        int prevRotation = rotation;

        rotation = rotation + 1;
        if(rotation > 3) rotation = 0;

        nonCenterToB();

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

        bToNonCenter();

        Block first = nonCenter[0];

        nonCenter[0] = nonCenter[1];
        nonCenter[1] = nonCenter[2];
        nonCenter[2] = nonCenter[3];
        nonCenter[3] = first;

        int prevRotation = rotation;

        rotation = rotation - 1 ;
        if(rotation < 0) rotation = 3;

        nonCenterToB();

        if(detectCollision && collide()) {
            if(moveHorizontal(1)) {}
            else if(moveHorizontal(-1)) {}
            else rotateClockwise(false);
        }

        if(detectCollision && prevRotation != rotation) {
            m.prop.rPlayMoveWait = m.prop.rPlayMoveTime;
        }
    }

    public void insert() {

        for(int i = 0; i < b.length; i++) {

            // Going from the bottom to the top to check collisions
            // properly. If the lower collide, the upper collide to.
            for(int j = b[i].length -1; j >= 0; j--) {

                if(b[i][j].isEmpty()) continue;

                int mapRow = mapIdxRow(j);
                if(mapRow < 0) {
                    m.prop.lost = true;
                    return;
                }

                m.b[mapIdxCol(i)][mapRow].setColor(b[i][j].intColor);

                boolean downKeyPressed = m.input != null && m.input.getAxisY() == 1;
                boolean collide = !m.isEmpty(mapIdxCol(i), mapRow + 1);

                if(downKeyPressed && collide) {
                    m.b[mapIdxCol(i)][mapRow].deformTime = m.prop.afterFreeFallWait;
                }
            }
        }
    }

    public int mapIdxCol(int iCol) {
        return mapIdxCol(iCol, mCol);
    }

    public int mapIdxRow(int iRow) {
        return mapIdxRow(iRow, mRow);
    }

    public int mapIdxCol(int iCol, int mapCol) {
        return +iCol + mapCol -1;
    }

    public int mapIdxRow(int iRow, int mapRow) {
        return +iRow + mapRow -1;
    }

    public void playFallCalc() {

        m.prop.vPlayMoveWait -= m.screen.deltaTime;
        boolean downKeyPressed = m.input != null && m.input.getAxisY() == 1;

        if(m.prop.vPlayMoveWait <= 0f) {

            // Looking if there is a collision on row bellow mRow
            if(!collide(mCol, mRow + 1)) {

                m.prop.vPlayMoveWait += m.prop.vPlayMoveTime;
                setFallStartEnd(mRow, mRow + 1);
                mRow++;
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

        for(int i = 0; i < b.length; i++) {

            for(int j = 0; j < b[i].length; j++) {

                if(b[i][j].isEmpty()) continue;

                b[i][j].moveY = end - start;
                b[i][j].py = end - start;
            }
        }
    }

    /**
     * Needs to be called before render when the map is loaded from
     * a serialized source. This because some references and objects
     * are not serialized and it needs to be setup
     */
    public void deserialize(Map m) {
        this.m = m;

        for(int i = 0; i < b.length; i++) {

            for(int j = 0; j < b[i].length; j++) {

                b[i][j].deserialize(m);
            }
        }

        nonCenter = new Block[4];
        bToNonCenter();
    }
}
