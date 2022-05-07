/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.ai.ai3;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.vpjardim.colorbeans.ai.AiMap;
import com.vpjardim.colorbeans.ai.Moves;
import com.vpjardim.colorbeans.ai.ScoreFormula;

/**
 * @author Vinícius Jardim
 *         2016/05/24
 */
public class Tree3 {

    public Moves moves;
    public ScoreFormula formula;

    public Tree3Node root;
    public Tree3Node bestNode;
    public long limitTime;
    public long startTime;
    public boolean processFinished;

    public Array<Tree3Node> cacheA;
    public Array<Tree3Node> cacheB;
    public int levelPos;
    public int cachePos;

    public int color1;
    public int color2;
    public int nColor1;
    public int nColor2;

    public Tree3(int nCol) {

        moves = new Moves();
        moves.init(nCol);
        limitTime = 8;
        cacheA = new Array<>(false, 1000);
        cacheB = new Array<>(false, 1000);

        reset();
    }

    public void reset() {

        if (root != null)
            Tree3Node.pool.free(root);

        root = null;
        formula = null;

        // Attention! the Pool can hold the same object multiple times.
        // Should not put bestNode in the pool. He will be freed recursively
        // when Tree3Node.pool.free(root) is called.
        bestNode = null;
        startTime = 0;
        processFinished = true;

        cacheA.clear();
        cacheB.clear();
        levelPos = 0;
        cachePos = 0;

        color1 = -1;
        color2 = -1;
    }

    public boolean hasTime() {
        return TimeUtils.timeSinceMillis(startTime) < limitTime;
    }

    public void initProcess(byte[][] map, int deleteSize, int outRow, ScoreFormula formula,
            int color1, int color2, int nColor1, int nColor2) {

        this.formula = formula;
        this.color1 = color1;
        this.color2 = color2;
        this.nColor1 = nColor1;
        this.nColor2 = nColor2;

        processFinished = false;
        root = Tree3Node.pool.obtain();
        root.init(map, deleteSize, outRow);
        cacheA.add(root);
        bestNode = Tree3Node.ILLEGAL_NODE;

        // If the color is defined, add moves only for that color
        if (this.color1 != -1) {
            addChild(root, color1, color2);
            cacheSwap();
        }
    }

    public void process() {

        startTime = TimeUtils.millis();

        for (; levelPos < 1; levelPos++) {
            for (; cachePos < cacheA.size; cachePos++) {

                if (!hasTime())
                    return;

                if (levelPos == 0)
                    addChild(cacheA.get(cachePos), nColor1, nColor2);
                else
                    addChild(cacheA.get(cachePos));
            }
            cacheSwap();
        }
        processFinished = true;
    }

    public void addChild(Tree3Node node, int c1, int c2) {

        IntArray movesArr = moves.getArray(c1, c2);

        for (int i = 0; i < movesArr.size; i++) {

            moves.setMove(movesArr.get(i));
            Tree3Node childNode = node.addChild(c1, c2, moves.position, moves.rotation);
            childNode.setScoreFormula(formula);
            childNode.process(node);
            cacheB.add(childNode);

            // Caching the best node
            if (childNode.scoreSum > bestNode.scoreSum) {
                bestNode = childNode;
            }
        }

        // As all child of this node has been added, we don't need
        // it's AiMap object anymore
        AiMap.pool.free(node.aiMap);
        node.aiMap = null;
    }

    public void addChild(Tree3Node node) {

        IntArray movesArr = moves.getArray();

        for (int i = 0; i < movesArr.size; i++) {

            moves.setMove(movesArr.get(i));
            Tree3Node childNode = node.addChild(moves.color1, moves.color2, moves.position, moves.rotation);
            childNode.setScoreFormula(formula);
            childNode.process(node);
            cacheB.add(childNode);

            // Caching the best node
            if (childNode.scoreSum > bestNode.scoreSum) {
                bestNode = childNode;
            }
        }

        // As all child of this node has been added, we don't need
        // it's AiMap object anymore
        AiMap.pool.free(node.aiMap);
        node.aiMap = null;
    }

    public Tree3Node bestRootChild() {

        Tree3Node curr = bestNode;

        while (curr.parent.parent != null) {
            curr = curr.parent;
        }

        return curr;
    }

    public void cacheSwap() {
        Array<Tree3Node> aux = cacheA;
        cacheA = cacheB;
        cacheB = aux;
        cacheB.clear();
        cachePos = 0;
    }

    // #debugCode
    public void print() {
        root.print();
    }
}
