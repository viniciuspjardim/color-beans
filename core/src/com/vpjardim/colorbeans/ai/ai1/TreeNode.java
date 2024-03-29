package com.vpjardim.colorbeans.ai.ai1;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.vpjardim.colorbeans.ai.AiMap;
import com.vpjardim.colorbeans.ai.ScoreFormula;

public class TreeNode implements Pool.Poolable {
    public static final Pool<TreeNode> pool = new Pool<TreeNode>(676) {
        @Override
        protected TreeNode newObject() {
            return new TreeNode();
        }
    };

    public static final TreeNode ILLEGAL_NODE = new TreeNode();

    static {
        ILLEGAL_NODE.scoreSum = AiMap.MOVE_ILLEGAL;
    }

    public TreeNode parent;
    public Array<TreeNode> children;
    public AiMap aiMap;
    public ScoreFormula formula;

    public float score;
    public float scoreSum;
    /** Best child index */
    public int bCIndex;
    /** Best child score sum */
    public float bCScoreSum;

    public int color1;
    public int color2;
    public int position;
    public int rotation;

    public TreeNode() {
        children = new Array<>(false, 325);
        init();
    }

    private void init() {
        parent = null;
        score = AiMap.MOVE_ILLEGAL;
        scoreSum = 0;
        bCIndex = -1;
        bCScoreSum = 0;
        color1 = -1;
        color2 = -1;
        position = -1;
        rotation = -1;
    }

    public void init(TreeNode parent, int color1, int color2, int position, int rotation) {
        this.parent = parent;
        this.aiMap = parent.aiMap.copy();
        this.color1 = color1;
        this.color2 = color2;
        this.position = position;
        this.rotation = rotation;
    }

    public void init(byte[][] map, int deleteSize, int outRow) {
        aiMap = AiMap.pool.obtain();
        aiMap.init(map, deleteSize, outRow);
    }

    public void setScoreFormula(ScoreFormula formula) {
        this.formula = formula;
    }

    public void process(TreeNode parent) {
        float result = aiMap.process(color1, color2, position, rotation);

        // If less the zero is a illegal or lost move
        if (result < 0f)
            scoreSum = result;
        else {
            score = formula.calc(aiMap);
            scoreSum = score + parent.scoreSum;
        }

        // Updating the best child cache for the parent node if this child node
        // has the higher score by now
        if (scoreSum > parent.bCScoreSum) {
            parent.bCScoreSum = scoreSum;
            parent.bCIndex = parent.children.size - 1;
        }
    }

    public TreeNode addChild(int color1, int color2, int position, int rotation) {
        TreeNode child = TreeNode.pool.obtain();
        child.init(this, color1, color2, position, rotation);
        children.add(child);

        return child;
    }

    @Override
    public void reset() {
        for (int i = 0; i < children.size; i++) {
            TreeNode.pool.free(children.get(i));
        }

        children.clear();

        if (aiMap != null) {
            AiMap.pool.free(aiMap);
            aiMap = null;
        }

        init();
    }
}
