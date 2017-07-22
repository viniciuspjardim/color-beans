/*
 * Copyright 2017 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.defaults;

import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 * 04/01/2017
 */
public class Style {

    public int fontSizeVSmall;
    public int fontSizeSmall;
    public int fontSizeMedium;
    public int fontSizeBig;
    public int fontSizeVBig;
    public int fontSizeXBig;

    public float padSmall;
    public float padMedium;
    public float padBig;
    public float padVBig;

    public float buttWidth;
    public float ribbonWidth;
    public float ribbonHeight;

    public Style() {
        setDefaults();
    }

    public void setDefaults() {

        fontSizeVSmall = 8;
        fontSizeSmall  = 12;
        fontSizeMedium = 16;
        fontSizeBig    = 24;
        fontSizeVBig   = 32;
        fontSizeXBig   = 40;

        padSmall       = 6;
        padMedium      = 12;
        padBig         = 18;
        padVBig        = 26;

        buttWidth      = 180;
        ribbonWidth    = 420;
        ribbonHeight   = 128;
    }

    public void scale(float scale) {

        fontSizeVSmall = Math.round(fontSizeVSmall * scale);
        fontSizeSmall  = Math.round(fontSizeSmall * scale);
        fontSizeMedium = Math.round(fontSizeMedium * scale);
        fontSizeBig    = Math.round(fontSizeBig * scale);
        fontSizeVBig   = Math.round(fontSizeVBig * scale);
        fontSizeXBig   = Math.round(fontSizeXBig * scale);

        padSmall       = padSmall * scale;
        padMedium      = padMedium * scale;
        padBig         = padBig * scale;
        padVBig        = padVBig * scale;

        buttWidth      = buttWidth * scale;
        ribbonWidth    = ribbonWidth * scale;

        if(G.res == G.RES_MEDIUM)
            ribbonHeight = 256;
    }
}
