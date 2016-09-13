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
public class Ai1 implements AiBase {

    private Map m;
    private AiInput input;
    private Map.MState prevState;
    private AiMap aiMap;
    private Moves moves;

    private int bestMovePosition;
    private int bestMoveRotation;

    private Cfg.Ai cfg;
    private float downKeyRand;
    private float doubtRand;
    private float doubtFreqRand;
    private boolean trashMove;
    private float lastMoveSwitch;
    private boolean isFinalMoveSet;

    @Override
    public void init(Map map, Cfg.Ai cfg) {

        m = map;
        input = new AiInput();
        input.setTarget(m);
        m.input = input;
        prevState = null;
        aiMap = new AiMap();
        moves = new Moves();
        moves.init(m.b.length);

        bestMovePosition = 0;
        bestMoveRotation = 0;

        this.cfg = cfg;
        downKeyRand = 1f;
        doubtRand = 0f;
        doubtFreqRand = 0f;
        trashMove = false;
        lastMoveSwitch = 0;
        isFinalMoveSet = false;
    }

    @Override
    public void update() {

        if(m.isState(Map.MState.PLAY_FALL)) {

            if(m.getState() != prevState) {

                downKeyRand = MathUtils.random(cfg.downKeyMin, cfg.downKeyMax);
                doubtRand = MathUtils.random(cfg.doubtMin, cfg.doubtMax);
                doubtFreqRand = MathUtils.random(cfg.doubtFreqMin, cfg.doubtFreqMax);
                trashMove = cfg.trashMoves > MathUtils.random(1f);
                lastMoveSwitch = 0;
                isFinalMoveSet = false;

                // #debugCode
                // if(trashMove) System.out.println("trash move");
                // else System.out.println("normal move");

                int color1 = m.pb.b[1][0].intColor; // upper block
                int color2 = m.pb.b[1][1].intColor; // lower block

                float bestMoveScore = AiMap.MOVE_ILLEGAL;
                bestMovePosition = 0;
                bestMoveRotation = 0;

                IntArray movesArr = this.moves.getArray(color1, color2);

                // Loop through moves array to find the best move
                for(int i = 0; i < movesArr.size; i++) {

                    moves.setMove(movesArr.get(i));

                    aiMap.init(AiMap.getByteBlocks(aiMap.b, m.b), m.prop.deleteSize, m.OUT_ROW);

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

                // If it will not fake doubt, set the best move in the AI input
                if(cfg.doubtMax == 0f) {
                    input.setMove(bestMovePosition, bestMoveRotation, false);
                    isFinalMoveSet = true;
                }
            }

            // Todo fallAmount should be relative to the top block in the row, not to the floor
            // When it's relative to the floor, blocks in the map can obstruct the path

            // Amount fallen until this frame: from 0 (top) to 1 (floor)
            float fallAmount = (float)Math.max(m.pb.mRow - m.OUT_ROW, 0) / (float)m.N_ROW;

            float slowFallMax = 1 - downKeyRand;
            boolean downKey = fallAmount > slowFallMax;

            if(cfg.doubtMax > 0f && !isFinalMoveSet) {

                // Do not fake doubt while the down key is pressed
                float doubt = Math.min(doubtRand, slowFallMax * 0.8f);
                float deltaH = fallAmount - lastMoveSwitch;

                if(doubt > fallAmount && !dangerRow(m.pb.mRow)) {
                    if(deltaH >= doubtFreqRand) {
                        input.setMove(MathUtils.random(0, m.b.length),
                                MathUtils.random(0, 3), false
                        );
                        lastMoveSwitch = fallAmount;
                        doubtFreqRand = MathUtils.random(cfg.doubtFreqMin, cfg.doubtFreqMax);
                    }
                }
                else {
                    input.setMove(bestMovePosition, bestMoveRotation, false);
                    isFinalMoveSet = true;
                }
            }

            input.update();
            input.fastFall = downKey;
        }
        prevState = m.getState();
    }

    /** Returns true when the play block row or the below have blocks that may obstruct moves */
    public boolean dangerRow(int row) {
        for(int i = 0; i < m.b.length; i++) {
            if(!m.isEmpty(i, row + 2)) return true;
        }
        return false;
    }

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
