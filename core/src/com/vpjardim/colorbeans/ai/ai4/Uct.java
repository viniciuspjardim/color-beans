/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.ai.ai4;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.vpjardim.colorbeans.ai.Moves;
import com.vpjardim.colorbeans.ai.ScoreFormula;

/**
 * @author Vinícius Jardim
 *         2016/07/29
 */
public class Uct {

    // Done fix child nodes add: child nodes has always been added from right to
    // left (should be randomly) until all child has at least on child;

    // TODO: fix exploitation vs exploration balance. It's always exploiting.
    // Research RAVE.

    static final float EPSILON1 = 0.00001f;
    static final float EPSILON2 = 0.01f;
    static final float EXPLORE_CONST = 175f; // 0.7071067812f; // 1/sqrt(2)

    public Moves moves;
    public ScoreFormula formula;

    public UctNode root;
    public Array<UctNode> visited;
    public UctNode bestNode;

    public long startTime;
    public long limitTime; // Processing time limit per frame
    public int framesCount;
    public int framesAi; // Max number of frames where AI will get to make a move
    public int totalIter;

    public int color1;
    public int color2;

    public Uct(int nCol) {

        moves = new Moves();
        moves.init(nCol);
        visited = new Array<>(false, 30);

        startTime = 0;
        limitTime = 8;
        framesCount = 0;
        framesAi = 30;
        totalIter = 0;
    }

    public void initProcess(byte[][] map, int deleteSize, int outRow, ScoreFormula formula,
            int color1, int color2) {
        this.formula = formula;
        this.color1 = color1;
        this.color2 = color2;

        bestNode = UctNode.ILLEGAL_NODE;

        root = UctNode.pool.obtain();
        root.init(this, map, deleteSize, outRow);
    }

    public void process() {

        startTime = TimeUtils.millis();

        while (hasTime() && framesCount <= framesAi) {
            root.iterate();
            totalIter++;
        }
        framesCount++;
    }

    public UctNode bestRootChild() {

        for (UctNode c : root.children) {
            if (c.totalValue >= bestNode.totalValue) {
                bestNode = c;
            }
        }

        return bestNode;
    }

    public void reset() {

        if (root != null)
            UctNode.pool.free(root);

        formula = null;
        root = null;

        // Attention! the Pool can hold the same object multiple times.
        // Should not put bestNode in the pool. He will be freed recursively
        // when Tree3Node.pool.free(root) is called.
        bestNode = null;
        startTime = 0;
        framesCount = 0;
        totalIter = 0;

        color1 = -1;
        color2 = -1;
    }

    public boolean hasTime() {
        return TimeUtils.timeSinceMillis(startTime) < limitTime;
    }

    public boolean processFinished() {
        return framesCount > framesAi;
    }
}
