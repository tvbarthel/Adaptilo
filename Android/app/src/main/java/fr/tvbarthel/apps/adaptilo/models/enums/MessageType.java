package fr.tvbarthel.apps.adaptilo.models.enums;

public enum MessageType {

    /**
     * send by the server to replace the controller
     */
    REPLACE_CONTROLLER,

    /**
     * send by the server to communicate client id
     */
    CONNECTION_COMPLETED,

    /**
     * send by the server to perform vibration
     */
    VIBRATOR,

    /**
     * send by the server to perform vibration pattern
     */
    VIBRATOR_PATTERN,

    /**
     * send by the client when user perform an input, press a key for instance
     */
    USER_INPUT,

    /**
     * send by the {@link fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine} when it's ready.
     */
    ENGINE_READY,

    /**
     * send by the client when game should be paused
     */
    PAUSE,

    /**
     * send by the client when game should be resumed
     */
    RESUME,

    /**
     * send by the {@link fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine} when a game has been loaded
     */
    REGISTER_CONTROLLER,

    /**
     * send by the {@link fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine} when sensor events are fired
     */
    SENSOR_INPUT

}
