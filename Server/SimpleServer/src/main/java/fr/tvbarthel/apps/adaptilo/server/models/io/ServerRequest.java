package fr.tvbarthel.apps.adaptilo.server.models.io;

import com.google.gson.annotations.SerializedName;
import fr.tvbarthel.apps.adaptilo.server.helpers.MessageDeserializerHelper;

/**
 * use to send message to the server with the current connection id
 */
public class ServerRequest {

    /**
     * connection id which identify the client on the server
     */
    @SerializedName(MessageDeserializerHelper.NODE_CONNECTION_ID)
    private String mConnectionId;

    /**
     * message to deliver
     */
    @SerializedName(MessageDeserializerHelper.NODE_MESSAGE)
    private Message mMessage;


    public ServerRequest(String connectionId, Message message) {
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
