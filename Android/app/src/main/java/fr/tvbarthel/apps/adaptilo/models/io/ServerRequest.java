package fr.tvbarthel.apps.adaptilo.models.io;

import com.google.gson.annotations.SerializedName;

import fr.tvbarthel.apps.adaptilo.helpers.MessageDeserializerHelper;

/**
 * use to send message to the server with the current connection id
 */
public class ServerRequest {

    /**
     * connection id which identify the client on the server
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

    public String getExternalIdId() {
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
