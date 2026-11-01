package com.vpjardim.colorbeans;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration conf = getResources().getConfiguration();
        G.tv = (conf.uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION;

        // Phones only play in portrait. Tablets can rotate (sw600dp is Android's
        // tablet breakpoint). A TV can be as few dp as a phone, but stays in landscape
        if (!G.tv && conf.smallestScreenWidthDp < 600)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new G(), config);
    }
}
