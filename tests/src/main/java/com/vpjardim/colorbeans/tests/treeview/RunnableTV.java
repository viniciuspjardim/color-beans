/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.tests.treeview;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * @author Vinícius Jardim
 * 12/08/2016
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
