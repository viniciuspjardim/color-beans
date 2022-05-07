/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.android;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 *         2015/03/21
 */
public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // setImmersive(); // Commented because of Android bug
        super.onCreate(savedInstanceState);
        System.out.println("onCreate...");

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new G(), config);
    }

    public void setImmersive() {

        // TODO: find workaround for Android bug black belt
        // More info at https://github.com/libgdx/libgdx/issues/3500

        getWindow().getDecorView().setSystemUiVisibility(0);
        int newUiOptions = 0;

        // Navigation bar hiding: Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // UI flag does not get cleared when the user interacts with the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        System.out.println(android.R.attr.uiOptions + "; " + newUiOptions);

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }
}
