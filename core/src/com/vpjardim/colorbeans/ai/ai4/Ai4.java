/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.ai.ai4;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.ai.AiBase;
import com.vpjardim.colorbeans.ai.AiInput;
import com.vpjardim.colorbeans.ai.AiMap;
import com.vpjardim.colorbeans.ai.ScoreFormula;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.input.InputBase;

/**
 * Todo not usable yet
 *
 * @author Vinícius Jardim
 * 28/07/2016
 */
public class Ai4 implements AiBase {

    // Todo persist the last ai move
    // When the game resume, because the ai has a small random factor, the new ai
    // calc might lead to another move witch might don't have enough time to complete.
    // Todo fix: some times this AI "suicide", it don't execute any move until loses the match

    private Map m;
    private Uct uctTree;
    private AiInput input;
    private Map.MState prevState;

    @Override
    public void init(Map map, Cfg.Ai cfg) {
        m = map;
        uctTree = new Uct(m.N_COL);

        input = new AiInput();
        input.setTarget(m);
        m.input = input;

        prevState = null;
    }

    @Override
    public InputBase getInput() { return input; }

    @Override
    public void update() {

        if(m.isInState(Map.MState.PLAYER_FALL)) {

            if(!m.isInState(prevState)) {

                uctTree.reset();
                input.cleanMove();

                int color1 = m.pb.b2.color; // upper block
                int color2 = m.pb.b1.color; // lower block

                uctTree.initProcess(AiMap.getByteBlocks(null, m.b), m.deleteSize, m.OUT_ROW,
                        formula1, color1, color2);
            }

            if(!uctTree.processFinished()) {
                uctTree.process();
            }
            else if(!input.move) {
                UctNode bestNode = uctTree.bestRootChild();
                input.setMove(bestNode.position, bestNode.rotation, true);

                // #debugCode
                //Dbg.print("Uct iterations: " + uctTree.totalIter);
                //Dbg.print("UctNodes obj: " + UctNode.objCount);
                //Dbg.print("AiMap obj: " + AiMap.objCount);
                //Dbg.print("======================");
            }

            input.update();
        }
        prevState = m.getState();
    }

    public static ScoreFormula formula1 = new ScoreFormula() {

        @Override
        public float calc(AiMap aiMap) {

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