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

    public String toString() {
        return "serverUri : " + mServerUri + "| port : " + mServerPort + "| room : "
                + mGameRoom + "| role : " + mUserRole + "| replace : " + mShouldReplace;
    }

    /**
     * GETTER and SETTER
     */

    public Uri getServerUri() {
        return mServerUri;
    }

    public void setServerUri(Uri mServerUri) {
        this.mServerUri = mServerUri;
    }

    public int getServerPort() {
        return mServerPort;
    }

    public void setServerPort(int mServerPort) {
        this.mServerPort = mServerPort;
    }

    public String getGameRoom() {
        return mGameRoom;
    }

    public void setGameRoom(String mServerRoom) {
        this.mGameRoom = mServerRoom;
    }

    public String getUserRole() {
        return mUserRole;
    }

    public void setUserRole(String mServerRole) {
        this.mUserRole = mServerRole;
    }

    public String getGameName() {
        return mGameName;
    }

    public void setGameName(String mGameName) {
        this.mGameName = mGameName;
    }

    public boolean shouldReplace() {
        return mShouldReplace;
    }

    public void setShouldReplace(boolean shouldReplace) {
        this.mShouldReplace = shouldReplace;
    }
}
