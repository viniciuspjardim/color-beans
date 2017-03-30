/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.core.Cfg;

/**
 * @author Vinícius Jardim
 * 09/04/2016
 */
public class Ai1 extends AiCommon {

    // Todo idea to improve Ai heuristics
    // The Ai can have 2 score functions the fire function and the build function. The fire is
    // similar to what we have i.e score proportional to de deleted beans. But the build function
    // the score is grater as the structure is grater so it can create great combos.
    // The final score will be a sum of both functions each one with weights. This weights can
    // change according to some factors as opponents map chains probability, opponents map loose
    // probability, this map fullness factor and others

    private AiMap aiMap;

    @Override
    public void init(Map map, Cfg.Ai cfg) {
        super.init(map, cfg);
        aiMap = new AiMap();
    }

    @Override
    protected void entryPoint1() {}

    @Override
    protected void entryPoint2() {

        int color1 = m.pb.b2.intColor; // upper block
        int color2 = m.pb.b1.intColor; // lower block
        float bestMoveScore = AiMap.MOVE_ILLEGAL;

        IntArray movesArr = this.moves.getArray(color1, color2);

        // Loop through moves array to find the best move
        for(int i = 0; i < movesArr.size; i++) {

            moves.setMove(movesArr.get(i));

            aiMap.init(AiMap.getByteBlocks(aiMap.b, m.b), m.deleteSize, m.OUT_ROW);

            float result = aiMap.process(color1, color2, moves.position, moves.rotation);
            float score;

            // If less the zero is a illegal or lost move
            if(result < 0f) score = result;
            // Zero or grater is a legal move, then calc the score
            else score = scoreCalc();

            if(score > bestMoveScore) {
                bestMoveScore = score;
                bestMovePosition = moves.position;
                bestMoveRotation = moves.rotation;
            }
        }
        bestMoveDefined = true;
    }

    @Override
    protected void entryPoint3() {}

    private float scoreCalc() {

        int center = aiMap.b.length / 2;

        float score = 0;

        // Score for color groups
        for(IntMap.Entry<Integer> entry : aiMap.lc.entries())
        {
            score += (entry.value * entry.value) -1;
        }

        // Score for deleted groups
        score += (aiMap.blocksDeleted * aiMap.blocksDeleted) + (aiMap.trashBlocks * 5);

        // Bad position because the blocks are reaching the top and may cause
        // obstruction in the following plays.
        // i: distance from the center
        for(int i = 0; i + center < aiMap.b.length; i++) {
            // j: distance from the top
            for(int j = 0; j < 3; j++) {

                if(aiMap.b[i + center][j + aiMap.outRow] != Block.EMPTY)
                    score -= 50 * Math.pow(0.75, i + j);
                if(aiMap.b[-i + center][j + aiMap.outRow] != Block.EMPTY)
                    score -= 50 * Math.pow(0.75, i + j);
            }
        }

        // Random small number to avoid even scores
        score += MathUtils.random(0.1f);

        // Random multiplier for the Ai have a worst performance in easy levels
        score *= 1f + MathUtils.random(cfg.randomness) - cfg.randomness / 2;

        // Big random number to force AI acts nonsense
        if(trashMove) score += MathUtils.random(50);

        return score;
    }
}
