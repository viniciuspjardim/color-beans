package com.vpjardim.colorbeans;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Phones only play in portrait. Tablets can rotate (sw600dp is Android's
        // tablet breakpoint)
        if (getResources().getConfiguration().smallestScreenWidthDp < 600)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new G(), config);
    }
}
