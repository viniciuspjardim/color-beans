package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.math.MathUtils;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.input.InputBase;

/**
 * @author Vinícius Jardim
 * 2017/03/29
 */

public abstract class AiCommon implements AiBase {

    // Todo fix bug that looks like ai 6 (campaign) fake doubt and down key is messing with it

    protected Map m;
    protected AiInput input;
    protected Map.MState prevState;
    protected Moves moves;

    protected boolean bestMoveDefined;
    protected int bestMovePosition;
    protected int bestMoveRotation;

    protected Cfg.Ai cfg;
    protected boolean downKey;
    protected float downKeyRand;
    protected float doubtRand;
    protected float doubtFreqRand;
    protected boolean trashMove;
    protected float lastMoveSwitch;
    protected boolean isFinalMoveSet;

    @Override
    public void init(Map map, Cfg.Ai cfg) {

        m = map;
        input = new AiInput();
        input.setTarget(m);
        m.input = input;
        prevState = null;
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
    public InputBase getInput() { return input; }

    protected abstract void entryPoint1();

    protected abstract void entryPoint2();

    protected abstract void entryPoint3();

    @Override
    public void update() {

        downKey = false;

        entryPoint1();

        // Is in the PLAY_FALL state
        if(m.isInState(Map.MState.PLAY_FALL)) {

            // And was in another state before. Just entered in the PLAY_FALL state
            if(!m.isInState(prevState)) {

                downKeyRand = MathUtils.random(cfg.downKeyMin, cfg.downKeyMax);
                doubtRand = MathUtils.random(cfg.doubtMin, cfg.doubtMax);
                doubtFreqRand = MathUtils.random(cfg.doubtFreqMin, cfg.doubtFreqMax);
                trashMove = cfg.trashMoves > MathUtils.random(1f);
                lastMoveSwitch = 0;
                isFinalMoveSet = false;

                bestMoveDefined = false;
                bestMovePosition = 0;
                bestMoveRotation = 0;

                entryPoint2();
            }

            entryPoint3();

            // If the best move is defined, it will not fake doubt and the final move is not set,
            // set the best move in the AI input
            if(bestMoveDefined && cfg.doubtMax == 0f && !isFinalMoveSet) {
                input.setMove(bestMovePosition, bestMoveRotation, false);
                isFinalMoveSet = true;
            }

            fakeDoubt();

            input.update();
            input.fastFall = downKey;
        }

        // Just lived the PLAY_FALL state
        if(!m.isInState(Map.MState.PLAY_FALL) && prevState == Map.MState.PLAY_FALL) {
            input.fastFall = false;
        }

        prevState = m.getState();
    }

    protected void fakeDoubt() {

        // Amount fallen until this frame: from 0 (top) to 1 (floor)
        float fallAmount = (float)Math.max(m.pb.b1y +1 - m.OUT_ROW, 0) / (float)m.N_ROW;

        float slowFallMax = 1 - downKeyRand;
        downKey = fallAmount > slowFallMax && bestMoveDefined;

        // If there is fake doubt and the final move is not yet set...
        if(cfg.doubtMax > 0f && !isFinalMoveSet) {

            // Do not fake doubt while the down key is pressed
            float doubt = Math.min(doubtRand, slowFallMax * 0.8f);
            float deltaH = fallAmount - lastMoveSwitch;

            // If the play blocks is before (fall less) the doubt limit...
            if(fallAmount < doubt && !dangerRow(m.pb.b1y)) {

                if(deltaH >= doubtFreqRand) {
                    input.setMove(MathUtils.random(0, m.b.length), MathUtils.random(0, 3), false);
                    lastMoveSwitch = fallAmount;
                    doubtFreqRand = MathUtils.random(cfg.doubtFreqMin, cfg.doubtFreqMax);
                }
            }
            // The fake doubt has ended and if the best move is defined it will be set
            else if(bestMoveDefined) {
                input.setMove(bestMovePosition, bestMoveRotation, false);
                isFinalMoveSet = true;
            }
        }
    }

    /** Returns true when the play block row or the below have blocks that may obstruct moves */
    protected boolean dangerRow(int row) {
        for(int i = 0; i < m.b.length; i++) {
            if(!m.isEmpty(i, row + 2)) return true;
        }
        return false;
    }
}
