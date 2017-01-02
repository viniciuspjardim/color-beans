/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.core.Cfg;

/**
 * @author Vinícius Jardim
 * 30/05/2016
 */
public class Ai3 implements AiBase {

    // Todo persist the last ai move
    // When the game resume, because the ai has a small random factor, the new ai
    // calc might lead to another move witch might don't have enough time to complete.
    // Todo debug, sometimes seems it did not executed the best move.
    // Done other bug is the it perform sort of a random move before move to the right place

    // #debugCode
    public static boolean debug = false;

    private Map m;
    private Tree3 tree;
    private AiInput input;
    private Map.MState prevState;

    @Override
    public void init(Map map, Cfg.Ai cfg) {
        m = map;
        tree = new Tree3(m.N_COL);

        input = new AiInput();
        input.setTarget(m);
        m.input = input;

        prevState = null;
    }

    @Override
    public void update() {

        if(m.isInState(Map.MState.PLAY_FALL)) {

            if(!m.isInState(prevState)) {

                tree.reset();
                input.cleanMove();

                int color1 = m.pb.b2.intColor; // upper block
                int color2 = m.pb.b1.intColor; // lower block

                tree.initProcess(AiMap.getByteBlocks(null, m.b), m.prop.deleteSize, m.OUT_ROW,
                        formula1, color1, color2);
            }

            if(!tree.processFinished) {
                tree.process();
            }
            else if(!input.move) {
                Tree3Node bestNode = tree.bestRootChild();
                input.setMove(bestNode.position, bestNode.rotation, true);

                // #debugCode
                // System.out.println("===================");
                // System.out.println("BestNode: pos = " + bestNode.position + "; rot = " +
                //         bestNode.rotation + "; scoreSum = " + bestNode.scoreSum);
            }

            input.update();
        }
        prevState = m.getState();
    }

    public static ScoreFormula formula1 = new ScoreFormula() {

        @Override
        public float calc(AiMap aiMap) {

            int center = aiMap.b.length / 2;

            // The blocks are obstructed. This move lead to game over.
            if(     aiMap.b[center][aiMap.outRow]     != Block.EMPTY ||
                    aiMap.b[center][aiMap.outRow + 1] != Block.EMPTY)
            {
                return AiMap.MOVE_LOST;
            }

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
                        score -= 100 * Math.pow(0.75, i + j);
                    if(aiMap.b[-i + center][j + aiMap.outRow] != Block.EMPTY)
                        score -= 100 * Math.pow(0.75, i + j);
                }
            }

            // Random small number to avoid even scores
            score += MathUtils.random(0f, 0.1f);

            return score;
        }
    };
}