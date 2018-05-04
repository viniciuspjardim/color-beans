/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.desktop;

import com.badlogic.gdx.backends.jogamp.JoglNewtApplication;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplicationConfiguration;
import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 * 2015/03/21
 */
public class DesktopLauncher {

    public static void main(String[] arg) {
        JoglNewtApplicationConfiguration config = new JoglNewtApplicationConfiguration();
        config.fullscreen = true;
        new JoglNewtApplication(new G(), config);
    }
}