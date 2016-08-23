/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.tests.treeview;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.vpjardim.colorbeans.ai.DebugNode;

/**
 * @author Vinícius Jardim
 * 03/08/2016
 */
public class TVNode<T extends DebugNode> {

    public T obj;
    public String[] text;
    public Color color;
    public boolean selected;

    public Vector2 pos;
    public float size;

    public TVNode<T> parent;
    public TVNode<T> left;
    public TVNode<T> right;
    public TVNode<T> child;

    public Array<TVNode<T>> children;

    public TVNode() {

        obj = null;
        text = null;
        color = MainScreen.COLOR_1;
        selected = false;
        pos = new Vector2(0f, 0f);
        size = 130;
        parent = null;
        left = null;
        right = null;
        child = null;
        children = new Array<>();
    }

    public TVNode(T obj) {
        this();
        this.obj = obj;
    }

    public TVNode(TVNode parent) {
        this();
        this.parent = parent;
    }

    public TVNode(float x, float y, TVNode<T> parent) {
        this();
        pos.x = x;
        pos.y = y;
        this.parent = parent;
    }

    public TVNode(float x, float y, TVNode parent, T obj) {
        this();
        pos.x = x;
        pos.y = y;
        this.parent = parent;
        this.obj = obj;
    }

    public void countLevel(int level, IntArray levelSizes) {

        if(level +1 > levelSizes.size) {
            levelSizes.add(0);
        }

        levelSizes.incr(level, 1);

        for(int i = 0; i < children.size; i++) {
            children.get(i).countLevel(level + 1, levelSizes);
        }
    }

    public TVNode<T> find(float x, float y) {

        TreeView<T> tv = TreeView.get();
        TVNode<T> found = null;
        float hafSize = size / 2f;

        if(Math.abs(x - tv.cam.coordETX(pos.x)) <= hafSize && Math.abs(y - tv.cam.coordETY(pos.y)) <= hafSize)
            return this;

        for(int i = 0; i < children.size; i++) {
            found = children.get(i).find(x, y);

            if(found != null) break;
        }

        return found;
    }

    public void drawLine(ShapeRenderer sr) {

        TreeView<T> tv = TreeView.get();

        if(parent != null) {
            sr.line(
                    tv.cam.coordETX(pos.x),
                    tv.cam.coordETY(pos.y),
                    tv.cam.coordETX(parent.pos.x),
                    tv.cam.coordETY(parent.pos.y)
            );
        }

        for(int i = 0; i < children.size; i++) {
            children.get(i).drawLine(sr);
        }
    }

    public void drawShape(ShapeRenderer sr) {

        TreeView<T> tv = TreeView.get();

        Color c = color;

        if(selected) c = MainScreen.COLOR_3;

        sr.setColor(c);

        float hafSize = size / 2f;

        sr.rect(
                tv.cam.coordETX(pos.x) - hafSize,
                tv.cam.coordETY(pos.y) - hafSize,
                size,
                size
        );

        for(int i = 0; i < children.size; i++) {
            children.get(i).drawShape(sr);
        }
    }

    public void drawText(SpriteBatch batch) {

        if(text != null) {

            TreeView<T> tv = TreeView.get();
            float hafSize = size / 2f;

            for(int i = 0; i < text.length; i++) {

                tv.screen.font16.draw(
                        batch,
                        text[i],
                        tv.cam.coordETX(pos.x) - hafSize +6,
                        tv.cam.coordETY(pos.y) + hafSize -12 + (i * -16)
                );
            }
        }

        for(int i = 0; i < children.size; i++) {
            children.get(i).drawText(batch);
        }
    }
}