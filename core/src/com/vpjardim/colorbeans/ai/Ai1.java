package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;
import com.vpjardim.colorbeans.Map;

/**
 * @author Vinícius Jardim
 * 09/04/2016
 */
public class Ai1 implements AiBase {

    // Todo this ai has a bug, sometime it do not execute the best move.
    // Other bug is the it perform sort of a random move before move to the right place

    private Map m;
    private AiInput input;
    private Map.MState prevState;
    private AiMap move;

    @Override
    public void init(Map map) {
        m = map;
        input = new AiInput();
        input.setTarget(m);
        m.input = input;
        prevState = null;
        move = new AiMap();
    }

    @Override
    public void update() {

        if(m.state.getCurrentState().equals(Map.MState.PLAY_FALL)) {

            if(m.state.getCurrentState() != prevState) {

                int color1 = m.pb.b[1][0].intColor; // upper block
                int color2 = m.pb.b[1][1].intColor; // lower block

                float bestMoveScore = AiMap.MOVE_ILLEGAL;
                int bestMovePosition = 0;
                int bestMoveRotation = 0;

                // No rotation (vertical)
                for(int i = 0; i < m.b.length; i++) {

                    move.init(AiMap.getByteBlocks(move.b, m.b), m.prop.deleteSize, m.OUT_ROW);
                    move.process(color1, color2, i, 0);
                    float score = scoreCalc();

                    System.out.println("score = " + score);

                    if(score > bestMoveScore) {
                        bestMoveScore = score;
                        bestMovePosition = i;
                        bestMoveRotation = 0;
                    }
                }

                // Rotation = 1 (horizontal)
                for(int i = 0; i < m.b.length -1; i++) {

                    move.init(AiMap.getByteBlocks(move.b, m.b), m.prop.deleteSize, m.OUT_ROW);
                    move.process(color1, color2, i, 1);
                    float score = scoreCalc();

                    System.out.println("score = " + score);

                    if(score > bestMoveScore) {
                        bestMoveScore = score;
                        bestMovePosition = i;
                        bestMoveRotation = 1;
                    }
                }

                // If the colors are equal, the following moves wore
                // already covered. Do not need the 2 and the 3 rotation
                if(color1 != color2) {

                    // Rotation = 2 (vertical)
                    for (int i = 0; i < m.b.length; i++) {

                        move.init(AiMap.getByteBlocks(move.b, m.b), m.prop.deleteSize, m.OUT_ROW);
                        move.process(color1, color2, i, 2);
                        float score = scoreCalc();

                        System.out.println("score = " + score);

                        if(score > bestMoveScore) {
                            bestMoveScore = score;
                            bestMovePosition = i;
                            bestMoveRotation = 2;
                        }
                    }

                    // Rotation = 3 (horizontal)
                    for (int i = 0; i < m.b.length -1; i++) {

                        move.init(AiMap.getByteBlocks(move.b, m.b), m.prop.deleteSize, m.OUT_ROW);
                        move.process(color1, color2, i, 3);
                        float score = scoreCalc();

                        System.out.println("score = " + score);

                        if(score > bestMoveScore) {
                            bestMoveScore = score;
                            bestMovePosition = i;
                            bestMoveRotation = 3;
                        }
                    }
                }

                input.setMove(bestMovePosition, bestMoveRotation, true);

                System.out.println("===================");
                System.out.println("BestMove: pos = " + bestMovePosition + "; rot = " +
                        bestMoveRotation + "; score = " + bestMoveScore);
                System.out.println("\n\n");
            }

            input.update();
        }
        prevState = m.state.getCurrentState();
    }

    private float scoreCalc() {

        float score = 0;

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
