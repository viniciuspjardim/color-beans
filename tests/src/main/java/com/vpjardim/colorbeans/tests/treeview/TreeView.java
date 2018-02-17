/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.tests.treeview;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.vpjardim.colorbeans.ai.DebugNode;

/**
 * @author Vinícius Jardim
 * 2016/08/03
 */
public class TreeView<T extends DebugNode> extends Game {

    public TVNode<T> root;
    public Camera cam;
    public MainScreen screen;
    public Object lock = new Object();

    public static TreeView get() {
        return (TreeView)Gdx.app.getApplicationListener();
    }

    public static void dbgToTV(DebugNode dbg, TVNode tv) {

        tv.obj = dbg;
        tv.text = dbg.getText();
        tv.children.clear();

        for(int i = 0; i < dbg.getChildren().size; i++) {

            DebugNode newDbg = dbg.getChildren().get(i);
            TVNode<DebugNode> newTv = new TVNode<>(tv);
            tv.children.add(newTv);

            dbgToTV(newDbg, newTv);
        }
    }

    public TreeView(TVNode<T> root) {
        this.root = root;
    }

    @Override
    public void create() {

        cam = new Camera();

        screen = new MainScreen<>();
        setScreen(screen);
    }

    public void update() {

        synchronized(lock) {

            dbgToTV(root.obj, root);
            screen.isColor1 = true;
            screen.updateLevelSizes();
            screen.updatePositions();
        }
    }
}