package fr.tvbarthel.apps.adaptilo.server.models.enums;

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
     * send by the client when it's ready.
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
     * send by the client when a game has been loaded
     */
    REGISTER_CONTROLLER_REQUEST,

    /**
     * broadcast send by the server when a controller join a room
     */
    ON_CONTROLLER_REGISTERED,

    /**
     * send by the client when user want to disconnect
     */
    UNREGISTER_CONTROLLER_REQUEST,

    /**
     * broadcast send by the sever when a controller leave a room.
     */
    ON_CONTROLLER_UNREGISTERED,

    /**
     * send by the client when sensor events are fired
     */
    SENSOR


}
