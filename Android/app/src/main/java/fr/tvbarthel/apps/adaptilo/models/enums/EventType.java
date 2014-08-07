package fr.tvbarthel.apps.adaptilo.models.enums;

/**
 * enum of available events, "physical" button on your controller for instance
 */
public enum EventType {

    KEY_A,

    KEY_B,

    KEY_ARROW_LEFT,

    KEY_ARROW_UP,

    KEY_ARROW_RIGHT,

    KEY_ARROW_DOWN,

    KEY_LEFT,

    KEY_RIGHT,

    /**
     * linked to {@link fr.tvbarthel.apps.adaptilo.engine.ShakeListener}
     */
    SHAKER

}
