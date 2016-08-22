package com.vpjardim.colorbeans.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.vpjardim.colorbeans.GameClass;

/**
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class DesktopLauncher {

	public static void main(String[] arg) {

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.addIcon("icon/desk256.png", Files.FileType.Internal);
        config.addIcon("icon/desk64.png", Files.FileType.Internal);
        config.addIcon("icon/desk32.png", Files.FileType.Internal);

        config.setWindowedMode(1080, 860);
        config.setTitle("Color Beans");

        new Lwjgl3Application(new GameClass(), config);
	}
}