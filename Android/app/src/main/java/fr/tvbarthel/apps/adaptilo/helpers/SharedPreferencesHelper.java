package fr.tvbarthel.apps.adaptilo.helpers;

/**
 * Helper for shared preferences constants
 */
public final class SharedPreferencesHelper {

    /**
     * user preference regarding key event callback
     */
    public static final String KEY_VIBRATE_ON_KEY_EVENT = "preference_vibrate_key_event";

    /**
     * user allowing vibration on server demand ?
     */
    public static final String KEY_VIBRATE_ON_SERVER_EVENT = "preference_vibrate_server_event";

    /**
     * preferences for vibrator usage
     */
    public static final String VIBRATOR_PREFERENCE = "preference_for_vibrator";

    /**
     * non instantiable class
     */
    private SharedPreferencesHelper() {

    }
}
