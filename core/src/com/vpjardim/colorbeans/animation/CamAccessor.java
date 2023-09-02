package com.vpjardim.colorbeans.animation;

import com.badlogic.gdx.graphics.OrthographicCamera;

import aurelienribon.tweenengine.TweenAccessor;

public class CamAccessor implements TweenAccessor<OrthographicCamera> {
    public static final int POSITION = 0;
    public static final int ZOOM = 1;

    @Override
    public int getValues(OrthographicCamera target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION:
                returnValues[0] = target.position.x;
                returnValues[1] = target.position.y;
                return 2;
            case ZOOM:
                returnValues[0] = target.zoom;
                return 1;
            default:
                return -1;
        }
    }

    @Override
    public void setValues(OrthographicCamera target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION:
                target.position.x = newValues[0];
                target.position.y = newValues[1];
                target.update();
                break;
            case ZOOM:
                target.zoom = newValues[0];
                target.update();
                break;
            default:
                break;
        }
    }
}
