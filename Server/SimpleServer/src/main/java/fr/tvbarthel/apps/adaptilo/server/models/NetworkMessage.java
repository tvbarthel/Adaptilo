package fr.tvbarthel.apps.adaptilo.server.models;

/**
 * use to send message to the server with the current connection id
 */
public class NetworkMessage {

    /**
     * connection id which identify the client on the server
     */
    private int mConnectionId;

    /**
     * message to deliver
     */
    private Message mMessage;


    public NetworkMessage(int connectionId, Message message) {
        mConnectionId = connectionId;
        mMessage = message;
    }

    /**
     * GETTER AND SETTER
     */

    public int getConnectionId() {
        return mConnectionId;
    }

    public void setConnectionId(int mConnectionId) {
        this.mConnectionId = mConnectionId;
    }

    public Message getMessage() {
        return mMessage;
    }

    public void setMessage(Message mMessage) {
        this.mMessage = mMessage;
    }
}
