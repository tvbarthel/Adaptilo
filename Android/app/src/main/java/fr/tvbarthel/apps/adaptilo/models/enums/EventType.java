package fr.tvbarthel.apps.adaptilo.models.enums;

/**
 * enum of available events, "physical" button on your controller for instance
 */
public enum EventType {

    /**
     * For a key A on a controller.
     */
    KEY_A,

    /**
     * For a key B on a controller.
     */
    KEY_B,

    /**
     * For a pad arrow left on a controller.
     */
    KEY_ARROW_LEFT,

    /**
     * For a pad arrow up on a controller.
     */
    KEY_ARROW_UP,

    /**
     * For a pad arrow right on a controller.
     */
    KEY_ARROW_RIGHT,

    /**
     * For a pad arrow down on a controller.
     */
    KEY_ARROW_DOWN,

    /**
     * For a key left on a controller.
     */
    KEY_LEFT,

    /**
     * For a key right on a controller.
     */
    KEY_RIGHT,

    /**
     * linked to {@link fr.tvbarthel.apps.adaptilo.engine.ShakeListener}
     */
    SHAKER,

    /**
     * linked to {@link fr.tvbarthel.apps.adaptilo.engine.ClapEngine}
     */
    CLAP

}
