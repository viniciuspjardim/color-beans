/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.animation;

import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;

/**
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class Animations {

    public transient Map m;

    public Animations(Map map) {
        m = map;
    }

    /** @return true if it's animating - animation not finished yet */
    public boolean gravityFall() {

        boolean animating = false;

        for(int i = 0; i < m.b.length; i++) {
            for(int j =  0; j < m.b[i].length; j++) {
                animating = animating | blockGravityFall(m.b[i][j], i);
            }
        }

        return animating;
    }

    private boolean blockGravityFall(Block b, int col) {

        if(b.py == 0f) return false;

        boolean animating = true;

        b.moveTime += G.delta;

        // position = 1/2 * a * t^2
        float shift = 0.5f * (m.gravityFallAcceleration + m.colAcceleration[col]) * b.moveTime * b.moveTime;

        // Initial py - shift. The block on the matrix on Map
        // has already felt (it`s on the bottom), but on the screen
        // animation is running and the block is falling.
        b.py = b.moveY - shift;

        if(b.py <= 0f) {
            animating = false;
            b.recycleFall();
            b.deformTime = m.afterGravityFallWait;
        }

        return animating;
    }

    /** @return true if it's animating - animation not finished yet */
    public boolean deform() {

        boolean animating = false;

        for(int i = 0; i < m.b.length; i++) {
            for(int j =  0; j < m.b[i].length; j++) {
                animating = animating | blockDeform(m.b[i][j]);
            }
        }

        return animating;
    }

    private boolean blockDeform(Block b) {

        if(b.deformTime == 0f) return false;

        boolean animating = true;

        float deformTime = m.afterGravityFallWait;

        b.deformTime -= G.delta;

        // Todo why it's working only when is not on trashBlocksTurn?
        if(b.color == Block.CLR_T && !m.trashBlocksTurn && m.trashSound == Map.TRASH_SOUND_NO) {
            m.trashSound = Map.TRASH_SOUND_YES;
        }

        // 4, 0, 3, 0, 4, 0, 3, 0 (tile index)

        if(b.deformTime <= 0f) {
            animating = false;
            b.deformTime = 0f;
            b.tile = 0;
        }
        else if(b.deformTime <= deformTime * 1f/7f) {
            b.tile = 3;
        }
        else if(b.deformTime <= deformTime * 2f/7f) {
            b.tile = 0;
        }
        else if(b.deformTime <= deformTime * 3f/7f) {
            b.tile = 4;
        }
        else if(b.deformTime <= deformTime * 4f/7f) {
            b.tile = 0;
        }
        else if(b.deformTime <= deformTime * 5f/7f) {
            b.tile = 3;
        }
        else if(b.deformTime <= deformTime * 6f/7f) {
            b.tile = 0;
        }
        else if(b.deformTime <= deformTime) {
            b.tile = 4;
        }

        return animating;
    }

    /** @return true if it's animating - animation not finished yet */
    public boolean playerFall() {

        boolean animating = false;

        animating = animating | blockPlayerFall(m.pb.b1);
        animating = animating | blockPlayerFall(m.pb.b2);

        return animating;
    }

    private boolean blockPlayerFall(Block b) {

        if(b.py == 0f || b.isEmpty()) return false;

        boolean animating = true;

        b.moveTime += G.delta;

        float shift =  G.delta / m.vPlayMoveWait2 * 2f;

        b.py -= shift * 2;

        if(b.py <= 0f) {

            animating = false;
            b.recycleFall();
        }

        return animating;
    }

    public void playHorizontal() {

        if(m.pb.moveX == 0) return;

        boolean animating = false;

        animating = animating | blockPlayHorizontal(m.pb.b1);
        animating = animating | blockPlayHorizontal(m.pb.b2);

        // End of animation
        if(!animating) {
            m.pb.moveX = 0;
        }
    }

    private boolean blockPlayHorizontal(Block b) {

        if(m.hPlayMoveTimer < m.hPlayMoveWait / 2f) {
            b.px = 0;
            return false;
        }

        b.px = m.pb.moveX - (m.pb.moveX * (m.hPlayMoveTimer / (m.hPlayMoveWait / 2f)));

        return true;
    }

    public void playRotation() {

        float fRotation = (float)m.pb.rotation;

        if(fRotation == m.pb.rotationAnim) return;

        // Correction in counterclockwise rotation.
        // 3 is equivalent to -1 and 0 to 4
        if(fRotation == 3 && m.pb.rotationAnim <= 0f)
            fRotation = -1f;
        if(fRotation == 0 && m.pb.rotationAnim >= 2f)
            fRotation = 4f;

        boolean isClockwise = fRotation > m.pb.rotationAnim;

        if(isClockwise) {
            m.pb.rotationAnim = 1f / m.rPlayMoveWait *
                    (m.rPlayMoveWait - m.rPlayMoveTimer) +
                    fRotation -1;

            if(m.pb.rotationAnim >= fRotation) {
                m.pb.rotationAnim = fRotation;
                if(m.pb.rotationAnim > 3f) m.pb.rotationAnim = 0f;
            }
        }
        else {
            m.pb.rotationAnim = -1f / m.rPlayMoveWait *
                    (m.rPlayMoveWait - m.rPlayMoveTimer) +
                    fRotation +1;

            if(m.pb.rotationAnim <= fRotation) {
                m.pb.rotationAnim = fRotation;
                if(m.pb.rotationAnim < 0f) m.pb.rotationAnim = 3f;
            }
        }
    }

    /** @return true if it's animating - animation not finished yet */
    public boolean labelDelete() {

        boolean animating = false;

        for(int i = 0; i < m.b.length; i++) {
            for(int j = 0; j < m.b[i].length; j++) {
                animating = animating | blockLabelDelete(m.b[i][j]);
            }
        }

        return animating;
    }

    private boolean blockLabelDelete(Block b) {

        if(b.toDelete == 0f || b.isEmpty()) return false;

        boolean animating = true;

        float blockTime = b.toDelete - G.delta;

        if(blockTime <= 0f) {

            b.recycle();
            blockTime = 0f;
            animating = false;
        }
        else if(blockTime <= m.delWait3) {

            b.visible = true;
        }
        else if(blockTime <= 2 * m.delWait3) {

            b.visible = false;
        }
        else if(blockTime <= 3 * m.delWait3) {

            b.visible = true;
        }

        b.toDelete = blockTime;

        return animating;
    }

    /** @return true if it's animating - animation not finished yet */
    public boolean gameOver() {

        boolean animating = false;

        for(int i = 0; i < m.b.length; i++) {
            for(int j =  0; j < m.b[i].length; j++) {
                m.b[i][j].tile = 0;
                animating = animating | blockGameOver(m.b[i][j], i);
            }
        }

        return animating;
    }

    private boolean blockGameOver(Block b, int col) {

        if(b.isEmpty()) return false;

        boolean animating = true;

        b.moveTime += G.delta;

        // position = 1/2 * a * t^2
        float shift = 0.5f * ((m.gravityFallAcceleration + m.colAcceleration[col]) / 4f) * b.moveTime *
                b.moveTime;
        b.py = -shift;

        if(b.py <= -m.N_ROW) {

            animating = false;
            //b.recycleFall();
        }

        return animating;
    }

    /**
     * Needs to be called before render when the map is loaded from a serialized source. This
     * because some references and objects are not serialized and it needs to be setup
     */
    public void deserialize(Map m) {
        this.m = m;
    }
}
