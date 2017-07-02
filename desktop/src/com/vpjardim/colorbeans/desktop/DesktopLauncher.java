/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class DesktopLauncher {

    public static void main(String[] arg) {

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setWindowIcon("icon/desk256.png", "icon/desk64.png", "icon/desk32.png");

        config.setWindowedMode(1280, 720);
        config.setTitle("Color Beans");
        config.setWindowSizeLimits(480, 320, 3840, 2160);
        config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        // config.useVsync(false);

        new Lwjgl3Application(new G(), config);
    }
}