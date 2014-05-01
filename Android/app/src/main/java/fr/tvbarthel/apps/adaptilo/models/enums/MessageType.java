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
     * send by the client when user perform an input, press a key for instance
     */
    USER_INPUT,

    /**
     * send by the {@link fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine} when it's ready.
     */
    ENGINE_READY

}
