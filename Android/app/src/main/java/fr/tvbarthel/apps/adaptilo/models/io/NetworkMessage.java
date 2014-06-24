package fr.tvbarthel.apps.adaptilo.models.io;

import fr.tvbarthel.apps.adaptilo.models.io.Message;

/**
 * use to send message to the server with the current connection id
 */
public class NetworkMessage {

    /**
     * connection id which identify the client on the server
     */
    private String mConnectionId;

    /**
     * message to deliver
     */
    private Message mMessage;


    public NetworkMessage(String connectionId, Message message) {
        mConnectionId = connectionId;
        mMessage = message;
    }

    /**
     * GETTER AND SETTER
     */

    public String getConnectionId() {
        return mConnectionId;
    }

    public void setConnectionId(String mConnectionId) {
        this.mConnectionId = mConnectionId;
    }

    public Message getMessage() {
        return mMessage;
    }

    public void setMessage(Message mMessage) {
        this.mMessage = mMessage;
    }
}
