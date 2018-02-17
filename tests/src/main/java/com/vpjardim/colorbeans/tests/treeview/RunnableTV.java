/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.tests.treeview;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * @author Vinícius Jardim
 * 2016/08/12
 */
public class RunnableTV implements Runnable {

    public TreeView tv;

    @Override
    public void run() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setWindowedMode(1080, 860);
        config.setTitle("Tree View");

        Lwjgl3Application app = new Lwjgl3Application(tv, config);
    }
}
