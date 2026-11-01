package com.vpjardim.colorbeans.defaults;

import com.badlogic.gdx.Gdx;
import com.vpjardim.colorbeans.G;

public class Style {
    /** Screens with a short side under this many dp are phones. Android's tablet breakpoint */
    private static final float PHONE_MAX_DP = 600f;
    /** On phones, the least dp a design pixel takes, so the text is big enough to read */
    private static final float PHONE_MIN_DP = 0.7f;

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

    /**
     * The UI scale: {@link #scaleFor(float, float)}, raised on phones. A phone has about
     * as many pixels as a tablet on a much smaller screen, so fitting the design alone
     * leaves its text too small. The screens that don't fit then zoom out or scroll
     */
    public static float uiScaleFor(float width, float height) {
        float scale = scaleFor(width, height);
        float dp = dpSize();

        // A TV can be as few dp as a phone, but it's far from the player
        if (!G.tv && dp > 0f && Math.min(width, height) / dp < PHONE_MAX_DP)
            scale = Math.max(scale, PHONE_MIN_DP * dp);

        return scale;
    }

    /** Pixels in a dp, or 0 where it isn't known, like on desktop */
    private static float dpSize() {
        switch (Gdx.app.getType()) {
            case Android:
            case iOS:
                return Gdx.graphics.getDensity();
            case WebGL:
                // Browsers take a CSS pixel as 1/96 inch. On phones it's about a dp, so this
                // is the device pixel ratio
                return Gdx.graphics.getPpiX() / 96f;
            default:
                return 0f;
        }
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
