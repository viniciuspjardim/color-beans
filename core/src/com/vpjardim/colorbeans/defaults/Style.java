/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.defaults;

/**
 * @author Vinícius Jardim
 *         2017/01/04
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
    public float ribbonSide;
    public float menuBgPad;

    public Style() {
        setDefaults();
    }

    public void setDefaults() {
        fontSizeVSmall = 12;
        fontSizeSmall = 18;
        fontSizeMedium = 24;
        fontSizeBig = 36;
        fontSizeVBig = 48;
        fontSizeXBig = 60;

        padSmall = 8;
        padMedium = 18;
        padBig = 26;
        padVBig = 40;

        buttWidth = 270;

        ribbonWidth = 441;
        ribbonHeight = 256;
        ribbonSide = 168;
        menuBgPad = 8;
    }

    public void scale(float scale) {

        fontSizeVSmall = Math.round(fontSizeVSmall * scale);
        fontSizeSmall = Math.round(fontSizeSmall * scale);
        fontSizeMedium = Math.round(fontSizeMedium * scale);
        fontSizeBig = Math.round(fontSizeBig * scale);
        fontSizeVBig = Math.round(fontSizeVBig * scale);
        fontSizeXBig = Math.round(fontSizeXBig * scale);

        padSmall = padSmall * scale;
        padMedium = padMedium * scale;
        padBig = padBig * scale;
        padVBig = padVBig * scale;

        buttWidth = buttWidth * scale;
        ribbonWidth = ribbonWidth * scale;
    }
}
