/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.vpjardim.colorbeans.Map;

/**
 * #warning too slow, do not use
 *
 * @author Vinícius Jardim
 * 29/04/2016
 */
public class Ai2 implements AiBase {

    private Map m;
    private Moves moves;
    private Tree3Node root;
    private AiInput input;
    private Map.MState prevState;

    @Override
    public void init(Map map) {
        m = map;
        moves = new Moves();
        moves.init(m.N_COL);

        input = new AiInput();
        input.setTarget(m);
        m.input = input;

        prevState = null;
    }

    @Override
    public void update() {

        if(m.state.getCurrentState().equals(Map.MState.PLAY_FALL)) {

            input.update();

            if(m.state.getCurrentState() != prevState) {

                if(root != null) Tree3Node.pool.free(root);

                root = Tree3Node.pool.obtain();
                root.init(AiMap.getByteBlocks(root.aiMap.b, m.b), m.prop.deleteSize, m.OUT_ROW);

                int color1 = m.pb.b[1][0].intColor; // upper block
                int color2 = m.pb.b[1][1].intColor; // lower block

                IntArray m1 = moves.getArray(color1, color2);

                for(int i = 0; i < m1.size; i++) {
                    moves.setMove(m1.get(i));
                    root.addChild(color1, color2, moves.position, moves.rotation);

                    IntArray m2 = moves.getArray();
                    Tree3Node n = root.children.get(i);

                    for(int j = 0; j < m2.size; j++) {
                        moves.setMove(m2.get(j));
                        n.addChild(moves.color1, moves.color2, moves.position, moves.rotation);
                    }
                }

                Tree3Node bestNode = root.getBestChild();

                input.setMove(bestNode.position, bestNode.rotation, true);

                //System.out.println("===================");
                //System.out.println("BestNode: pos = " + bestNode.position + "; rot = " +
                //        bestNode.rotation + "; score = " + bestNode.score);
            }
        }
        prevState = m.state.getCurrentState();
    }

    public static float scoreCalc(AiMap move) {

        float score = AiMap.MOVE_ILLEGAL;

        // Score for color groups
        for(IntMap.Entry<Integer> entry : move.lc.entries())
        {
            score += (entry.value * entry.value) -1;
        }

        // Score for deleted groups
        score += (move.blocksDeleted * move.blocksDeleted) + (move.trashBlocks * 5);

        // Random small number to avoid even scores
        score += MathUtils.random(0f, 0.1f);

        return score;
    }
}