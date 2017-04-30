/*
 * Copyright 2017 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.input.GestureDetector;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.core.Dbg;

/**
 * @author Vinícius Jardim
 * 10/02/2017
 */
public class TouchInput2 extends GestureDetector.GestureAdapter implements InputBase {

    public Map map;

    private boolean horizontalEvent = false;
    private boolean verticalEvent = false;

    /** -1 left; 1 right */
    private int horizontal = 0;
    /** -1 up; 1 down */
    private int vertical = 0;

    /** -1 left; 1 right */
    private int horizontalOld = 0;
    /** -1 up; 1 down */
    private int verticalOld = 0;

    public int width = 400;
    public float[] div;
    public boolean hPanning = false;
    public boolean vPanning = false;
    public boolean move = false;
    public boolean draw = false;

    public int moveCurr;
    public int moveStart;

    public float touchX = 0f;
    public float touchY = 0f;
    public float dTouchX = 0f;
    public float dTouchY = 0f;

    @Override
    public void setTarget(TargetBase target) {
        if(target instanceof Map) {
            map = (Map) target;
            div = new float[map.N_COL + 1];
        }
    }

    @Override
    public void update() {

        if(!horizontalEvent) { horizontalOld = horizontal; }
        if(!verticalEvent) { verticalOld = vertical; }

        horizontalEvent = false;
        verticalEvent = false;

        if(move) move();
    }

    @Override
    public int getAxisX() { return horizontal; }

    @Override
    public int getAxisY() { return vertical; }

    @Override
    public int getAxisXOld() { return horizontalOld; }

    @Override
    public int getAxisYOld() { return verticalOld; }

    public void move() {

        if(map.isInState(Map.MState.PLAY_FALL)) {

            int deltaH = moveCurr - map.pb.b1x;

            if(deltaH == 0) horizontal = 0;
            else {
                horizontal = deltaH / Math.abs(deltaH);
                vertical = 0;
            }
        }
        else {

            vertical = 0;
        }
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        // #debugCode
        Dbg.dbg(Dbg.tag(this), "tap -> x = " + x + "; y = " + y + "; count = " + count +
                "; button = " + button);

        if(map == null) return false;

        if(y < G.height * 0.2f) {
            map.buttonStart(true);
            return false;
        }

        if(x > G.width / 2f)
            map.button1(true);
        else
            map.button3(true);

        // Returns false because the stage need this event on the PlayScreen
        // Todo ControllerInput or other input might have the same problem
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {

        // #debugCode
        Dbg.print(Dbg.tag(this) + ": pan ->   x = " + x + ";  y = " + y + "; dTouchX = " + deltaX +
                "; dTouchY = " + deltaY);

        if(!hPanning && !vPanning) {
            move = false;
            // draw = false;
            hPanning = false;
            vPanning = false;
            touchX = x;
            touchY = y;
            dTouchX = 0f;
            dTouchY = 0f;
            moveStart = map.pb.b1x;
        }

        draw = true;
        dTouchX += deltaX;
        dTouchY += deltaY;
        updateDiv();
        findCurrent();

        if(vertical == 0 && Math.abs(deltaX) >= Math.abs(deltaY) * 0.8) {

            hPanning = true;
            horizontalEvent = true;


            move = true;

        }
        else if(horizontal == 0 && !hPanning && deltaY > 0) {
            vPanning = true;
            verticalEvent = true;

            verticalOld = vertical;
            vertical = 1;
        }

        if(Math.abs(dTouchX) > width / map.N_COL / 2) verticalClean();

        // #debugCode
        Dbg.print(Dbg.tag(this) + ": pan2 -> tx = " + touchX + "; ty = " + touchY +
                "; dTouchX = " + this.dTouchX + "; dTouchY = " + this.dTouchY);

        return false;
    }

    public void updateDiv() {

        float colWidth = width / map.N_COL;
        float touchCurr = dTouchX + touchX;
        float x = touchX - (moveStart * colWidth) - colWidth / 2f;
        div[0] = x;
        div[div.length -1] = x + width;

        for(int i = 1; i < div.length -1; i++) {
            div[i] = x + i * colWidth;
            // float dist = div[i] + colWidth / 2 - touchCurr;
            // float perc = dist / width;
            // float inv = 1f * Math.signum(perc) - perc;
            // float pad = colWidth * inv;
            // // #debugCode
            // Dbg.print(Dbg.tag(this, false) + ": i = " + i + " =====");
            // Dbg.print(Dbg.tag(this, false) + ": div[i] = " + div[i]);
            // Dbg.print(Dbg.tag(this, false) + ": dist = " + dist);
            // Dbg.print(Dbg.tag(this, false) + ": perc = " + perc);
            // Dbg.print(Dbg.tag(this, false) + ": inv = " + inv);
            // Dbg.print(Dbg.tag(this, false) + ": pad = " + pad);
            // div[i] += pad;
            // // #debugCode
            // Dbg.print(Dbg.tag(this, false) + ": pan2 -> tx = " + touchX + "; ty = " + touchY +
            //         "; dTouchX = " + this.dTouchX + "; dTouchY = " + this.dTouchY);
        }
    }

    public void findCurrent() {

        float touchCurr = dTouchX + touchX;

        for(int i = 1; i < div.length -1; i++) {
            if(div[i] > touchCurr) {
                moveCurr = i -1;
                return;
            }
        }
        moveCurr = map.N_COL - 1;
    }

    @Override
    public boolean panStop (float x, float y, int pointer, int button) {

        // #debugCode
        Dbg.print(Dbg.tag(this) + ": panStop -> x = " + x + "; y = " + y);

        horizontalEvent = true;
        horizontalOld = horizontal;
        horizontal = 0;

        hPanning = false;
        vPanning = false;
        move = false;
        draw = false;
        moveCurr = 0;
        moveStart = 0;

        touchX = 0f;
        touchY = 0f;
        dTouchX = 0f;
        dTouchY = 0f;

        verticalClean();
        return false;
    }

    public void verticalClean() {
        if(vertical == 1) {
            verticalEvent = true;
            verticalOld = vertical;
            vertical = 0;
        }
    }
}