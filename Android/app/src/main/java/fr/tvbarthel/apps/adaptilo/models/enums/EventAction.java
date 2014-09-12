package fr.tvbarthel.apps.adaptilo.models.enums;

/**
 * Action which can be perform by an {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType}
 */
public enum EventAction {
    /**
     * A key is pressed.
     */
    ACTION_KEY_DOWN,

    /**
     * A key is released.
     */
    ACTION_KEY_UP,

    /**
     * The event happened.
     * <p/>
     * For instance a clap has been detected.
     */
    ACTION_HAPPENED,

    /**
     * The event is happening.
     * <p/>
     * For instance the user is checking the device.
     */
    ACTION_DOING,

    /**
     * Action used to enable something.
     * <p/>
     * For instance the ShakeDetector should be enabled.
     */
    ACTION_ENABLE,

    /**
     * Action used to disable something.
     * <p/>
     * For instance the ShakeDetector should be disabled.
     */
    ACTION_DISABLE
}
