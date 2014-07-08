package fr.tvbarthel.apps.adaptilo.server.models.io;

import com.google.gson.annotations.SerializedName;
import fr.tvbarthel.apps.adaptilo.server.helpers.MessageDeserializerHelper;

/**
 * use to send message to the server with the current connection id
 */
public class ServerRequest {

    /**
     * connection id used to identify each role instance
     */
    @SerializedName(MessageDeserializerHelper.NODE_EXTERNAL_ID)
    private String mExternalId;

    /**
     * message to deliver
     */
    @SerializedName(MessageDeserializerHelper.NODE_MESSAGE)
    private Message mMessage;


    public ServerRequest(String connectionId, Message message) {
        mExternalId = connectionId;
        mMessage = message;
    }

    /**
     * GETTER AND SETTER
     */

    public String getExternalId() {
        return mExternalId;
    }

    public void setExternalId(String mConnectionId) {
        this.mExternalId = mConnectionId;
    }

    public Message getMessage() {
        return mMessage;
    }

    public void setMessage(Message mMessage) {
        this.mMessage = mMessage;
    }
}
