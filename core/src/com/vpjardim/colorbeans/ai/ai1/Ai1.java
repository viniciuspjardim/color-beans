package com.vpjardim.colorbeans.ai.ai1;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.ai.AiCommon;
import com.vpjardim.colorbeans.ai.AiMap;
import com.vpjardim.colorbeans.ai.ScoreFormula;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.input.InputBase;

public class Ai1 extends AiCommon {
    private Tree tree;

    @Override
    public void init(Map map, Cfg.Ai cfg) {
        super.init(map, cfg);
        tree = new Tree(Map.N_COL);
    }

    @Override
    protected void entryPoint2() {
        tree.reset();
        input.cleanMove();

        int color1 = m.pb.b2.color; // upper block
        int color2 = m.pb.b1.color; // lower block
        int nColor1 = m.pb.nextB2;
        int nColor2 = m.pb.nextB1;

        tree.initProcess(AiMap.getByteBlocks(null, m.b),
                m.deleteSize, Map.OUT_ROW, formula1, color1, color2, nColor1, nColor2);
    }

    @Override
    protected void entryPoint3() {
        if (!tree.processFinished) {
            tree.process();
        } else if (!input.move) {
            TreeNode bestNode = tree.bestRootChild();
            bestMovePosition = bestNode.position;
            bestMoveRotation = bestNode.rotation;
            bestMoveDefined = true;
        }
    }

    @Override
    public InputBase getInput() {
        return input;
    }

    public static final ScoreFormula formula1 = aiMap -> {
        int center = aiMap.b.length / 2;

        // The blocks are obstructed. This move lead to game over.
        if (aiMap.b[center][aiMap.outRow] != Block.EMPTY ||
                aiMap.b[center][aiMap.outRow + 1] != Block.EMPTY) {
            return AiMap.MOVE_LOST;
        }

        float score = 0;

        // Score for color groups
        for (IntMap.Entry<Integer> entry : aiMap.lc.entries()) {
            score += (entry.value * entry.value) - 1;
        }

        // Score for deleted groups
        score += (aiMap.blocksDeleted * aiMap.blocksDeleted) + (aiMap.trashBlocks * 5);

        // Bad position because the blocks are reaching the top and may cause
        // obstruction in the following plays.
        // i: distance from the center
        for (int i = 0; i + center < aiMap.b.length; i++) {
            // j: distance from the top
            for (int j = 0; j < 3; j++) {
                if (aiMap.b[i + center][j + aiMap.outRow] != Block.EMPTY)
                    score -= 100 * Math.pow(0.75, i + j);
                if (aiMap.b[-i + center][j + aiMap.outRow] != Block.EMPTY)
                    score -= 100 * Math.pow(0.75, i + j);
            }
        }

        // Random small number to avoid even scores
        score += MathUtils.random(0f, 0.1f);

        return score;
    };
}
