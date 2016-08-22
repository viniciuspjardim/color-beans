package com.vpjardim.colorbeans.animation;

/**
 * @author Vinícius Jardim
 * 01/11/2015
 */
import com.badlogic.gdx.graphics.g2d.Sprite;

import aurelienribon.tweenengine.TweenAccessor;

public class SpriteAccessor implements TweenAccessor<Sprite> {

    public static final int ALPHA = 0;
    public static final int COLORS = 1;

    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {

        switch(tweenType) {
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            case COLORS:
                returnValues[0] = target.getColor().r;
                returnValues[1] = target.getColor().g;
                returnValues[2] = target.getColor().b;
                returnValues[3] = target.getColor().a;
                return 4;
            default:
                return -1;
        }
    }

    @Override
    public void setValues(Sprite target, int tweenType, float[] newValues) {

        switch(tweenType) {
            case ALPHA:
                target.setColor(
                        target.getColor().r,
                        target.getColor().g,
                        target.getColor().b,
                        newValues[0]);
                break;
            case COLORS:
                target.setColor(
                        newValues[0],
                        newValues[1],
                        newValues[2],
                        newValues[3]);
                break;
            default:
                break;
        }
    }
}