package fr.tvbarthel.apps.adaptilo.models;

/**
 * Configuration parameters for {@link fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine} should
 * come from a QrCode
 */
public class EngineConfig {

    /**
     * WebSocket server uri
     */
    private String mServerUri;

    /**
     * WebSocket server port
     */
    private String mServerPort;

    /**
     * Game room corresponding to the scanned instance
     */
    private String mGameRoom;

    /**
     * User role in the game
     */
    private String mUserRole;

    /**
     * GETTER and SETTER
     */

    public String getServerUri() {
        return mServerUri;
    }

    public void setServerUri(String mServerUri) {
        this.mServerUri = mServerUri;
    }

    public String getServerPort() {
        return mServerPort;
    }

    public void setServerPort(String mServerPort) {
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
}
