package fr.tvbarthel.apps.adaptilo.server.models;

import org.java_websocket.WebSocket;

/**
 * Encapsulate data linked to a role in a given room.
 * Could be a player but also any other role defined by a WebSocket such as game field for example
 */
public class Role {

    /**
     * name used to describe the role
     */
    private String mName;

    /**
     * web socket connection used to reach the role
     */
    private WebSocket mConnection;

    /**
     * external id send with each message
     */
    private String mId;

    public Role() {

    }

    public Role(String name, WebSocket conn, String id) {
        mName = name;
        mConnection = conn;
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public WebSocket getConnection() {
        return mConnection;
    }

    public void setConnection(WebSocket mConnection) {
        this.mConnection = mConnection;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }
}
