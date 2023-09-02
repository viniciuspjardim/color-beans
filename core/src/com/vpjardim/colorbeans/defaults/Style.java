package com.vpjardim.colorbeans.defaults;

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
        fontSizeVSmall = 10;
        fontSizeSmall = 16;
        fontSizeMedium = 20;
        fontSizeBig = 30;
        fontSizeVBig = 42;
        fontSizeXBig = 52;

        padSmall = 8;
        padMedium = 16;
        padBig = 20;
        padVBig = 32;

        buttWidth = 240;

        ribbonWidth = 360;
        ribbonHeight = 212;
        ribbonSide = 168;
        menuBgPad = 47;
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
