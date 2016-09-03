/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 * 21/03/2015
 */
public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // #debugCode
        // Log.d("Test1", "android.util.Log d");
        // Log.e("Test1", "android.util.Log e");

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new G(), config);
    }
}
