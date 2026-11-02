package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;

/**
 * Keeps the game's state as JSON texts in the libGDX {@link Preferences}: a file in
 * ~/.prefs/ on desktop, the shared preferences on Android, the user defaults on iOS and
 * the local storage in the browser. The classes saved here must be listed in the
 * gdx.reflect.include of GdxDefinition.gwt.xml, and can't have final fields, which the
 * browser's reflection can't set
 */
public class Store {
    public static final String CFG = "cfg";
    public static final String SCORES = "scores";
    public static final String DBG = "dbg";
    /** The {@link MatchSave} of each game mode */
    public static final String CAMPAIGN = "campaign";
    public static final String TRAINING = "training";

    private static final String NAME = "com.vpjardim.colorbeans";

    private static Preferences prefs() {
        return Gdx.app.getPreferences(NAME);
    }

    public static void save(String key, Object value) {
        try {
            Json json = new Json();
            json.setUsePrototypes(false);

            Preferences prefs = prefs();
            prefs.putString(key, json.toJson(value));
            prefs.flush();
        } catch (Exception e) {
            Dbg.err("Store", "Can't save " + key + ": " + e.getMessage());
        }
    }

    /** The value saved in the key, or null when there's none or it can't be read */
    public static <T> T load(String key, Class<T> type) {
        try {
            String text = prefs().getString(key, null);

            if (text == null)
                return null;

            return new Json().fromJson(type, text);
        } catch (Exception e) {
            Dbg.err("Store", "Can't load " + key + ": " + e.getMessage());
            return null;
        }
    }

    public static void remove(String key) {
        Preferences prefs = prefs();
        prefs.remove(key);
        prefs.flush();
    }
}
