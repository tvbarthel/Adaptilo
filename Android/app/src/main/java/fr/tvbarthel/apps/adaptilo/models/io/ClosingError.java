package fr.tvbarthel.apps.adaptilo.models.io;

/**
 * Fine here all closing error code.
 * <p/>
 * We could used an enum, but since closing error are send through the network we wanted to reduce
 * packet size in order to improve performances and to avoid consuming device network data.
 * <p/>
 * Be carefull to not use any reserved code define in {@link org.java_websocket.framing.CloseFrame}
 * <p/>
 * Code must match following conditions : 1000 < error code < 4999
 * {@link org.java_websocket.framing.CloseFrameBuilder#initCloseCode()}
 */
public final class ClosingError {

    /**
     * non instantiable class
     */
    private ClosingError() {

    }


    ////////////////////////////////////////
    /////////// REGISTRATION ERROR /////////
    ////////////////////////////////////////

    /**
     * Requested game name doesn't exist.
     */
    public static final int REGISTRATION_REQUESTED_GAME_NAME_UNKNOWN = 2000;

    /**
     * No rooms have been created yet for the requested game name.
     */
    public static final int REGISTRATION_NO_ROOM_CREATED = 2001;

    /**
     * Requested room id doesn't exist.
     */
    public static final int REGISTRATION_REQUESTED_ROOM_UNKNOW = 2003;

    /**
     * Requested role doesn't exist for the given game.
     */
    public static final int REGISTRATION_REQUESTED_ROLE_UNKNOWN = 2004;

    /**
     * Requested room is empty.
     */
    public static final int REGISTRATION_REQUESTED_ROOM_IS_EMPTY = 2005;


}
