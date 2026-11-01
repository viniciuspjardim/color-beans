package com.vpjardim.colorbeans.defaults;

public class Style {
    public int fontSizeVSmall;
    public int fontSizeSmall;
    public int fontSizeMedium;
    public int fontSizeBig;
    public int fontSizeVBig;
    public float padSmall;
    public float padMedium;
    public float padBig;
    public float padVBig;
    public float buttWidth;
    /** Side of the square icon buttons */
    public float iconButtSize;
    public float ribbonWidth;
    public float ribbonHeight;
    public float ribbonOverhang;

    public Style() {
        setDefaults();
    }

    /**
     * The defaults are sized for a screen 1080 px tall and at least 800 px wide, which fits the
     * menu. This returns the scale that fits that size into a screen of the given size
     */
    public static float scaleFor(float width, float height) {
        return Math.min(width / 800f, height / 1080f);
    }

    public void setDefaults() {
        fontSizeVSmall = 10;
        fontSizeSmall = 16;
        fontSizeMedium = 20;
        fontSizeBig = 30;
        fontSizeVBig = 42;

        padSmall = 8;
        padMedium = 16;
        padBig = 20;
        padVBig = 32;

        buttWidth = 240;
        iconButtSize = 96;

        ribbonWidth = 360;
        ribbonHeight = 212;
        ribbonOverhang = 121;
    }

    public void scale(float scale) {
        fontSizeVSmall = Math.round(fontSizeVSmall * scale);
        fontSizeSmall = Math.round(fontSizeSmall * scale);
        fontSizeMedium = Math.round(fontSizeMedium * scale);
        fontSizeBig = Math.round(fontSizeBig * scale);
        fontSizeVBig = Math.round(fontSizeVBig * scale);

        padSmall = padSmall * scale;
        padMedium = padMedium * scale;
        padBig = padBig * scale;
        padVBig = padVBig * scale;

        buttWidth = buttWidth * scale;
        iconButtSize = iconButtSize * scale;
        ribbonWidth = ribbonWidth * scale;
        ribbonHeight = ribbonHeight * scale;
        ribbonOverhang = ribbonOverhang * scale;
    }
}
