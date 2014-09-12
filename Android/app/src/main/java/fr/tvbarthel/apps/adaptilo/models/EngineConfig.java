package fr.tvbarthel.apps.adaptilo.models;

import android.net.Uri;

/**
 * Configuration parameters for {@link fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine} should
 * come from a QrCode
 */
public class EngineConfig {

    /**
     * WebSocket server uri
     */
    private Uri mServerUri;

    /**
     * WebSocket server port
     */
    private int mServerPort;

    /**
     * Game room corresponding to the scanned instance
     */
    private String mGameRoom;

    /**
     * User role in the game
     */
    private String mUserRole;

    /**
     * Name of the game.
     */
    private String mGameName;

    /**
     * Boolean used to know if role must be replaced is already registered.
     * Default value is false.
     */
    private boolean mShouldReplace;

    /**
     * Stringify object to be readable in log for instance.
     *
     * @return object description with all params.
     */
    public String toString() {
        return "serverUri : " + mServerUri + "| port : " + mServerPort + "| room : "
                + mGameRoom + "| role : " + mUserRole + "| replace : " + mShouldReplace;
    }

    /**
     * GETTER and SETTER
     */

    /**
     * Retrieve server uri.
     *
     * @return server uri.
     */
    public Uri getServerUri() {
        return mServerUri;
    }

    /**
     * Define the server uri of the given config.
     *
     * @param mServerUri server uri.
     */
    public void setServerUri(Uri mServerUri) {
        this.mServerUri = mServerUri;
    }

    /**
     * Retrieve server port used to established a websocket connection.
     *
     * @return server port.
     */
    public int getServerPort() {
        return mServerPort;
    }

    /**
     * Define the server port of the given config.
     *
     * @param mServerPort server port.
     */
    public void setServerPort(int mServerPort) {
        this.mServerPort = mServerPort;
    }

    /**
     * Retrieve the room id of the current game.
     *
     * @return game room if.
     */
    public String getGameRoom() {
        return mGameRoom;
    }

    /**
     * Define the server room of the given config.
     *
     * @param mServerRoom server room id.
     */
    public void setGameRoom(String mServerRoom) {
        this.mGameRoom = mServerRoom;
    }

    /**
     * Retrieve the user role.
     *
     * @return user role.
     */
    public String getUserRole() {
        return mUserRole;
    }

    /**
     * Define the user role for the given config.
     *
     * @param mServerRole user role.
     */
    public void setUserRole(String mServerRole) {
        this.mUserRole = mServerRole;
    }

    /**
     * Retrieve the game id.
     *
     * @return game id.
     */
    public String getGameName() {
        return mGameName;
    }

    /**
     * Define the game name for the given config.
     *
     * @param mGameName game name.
     */
    public void setGameName(String mGameName) {
        this.mGameName = mGameName;
    }

    /**
     * Used to know if the server should replace the current role in the given room for the
     * given game if a player is already playing this role.
     *
     * @return true if the server should replace, false otherwise.
     */
    public boolean shouldReplace() {
        return mShouldReplace;
    }

    /**
     * Define the replacement policy for the given config.
     *
     * @param shouldReplace true if the server should replace the player when same config
     *                      is already used.
     */
    public void setShouldReplace(boolean shouldReplace) {
        this.mShouldReplace = shouldReplace;
    }
}
