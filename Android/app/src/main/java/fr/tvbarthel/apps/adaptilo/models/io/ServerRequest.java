package fr.tvbarthel.apps.adaptilo.models.io;

import com.google.gson.annotations.SerializedName;

import fr.tvbarthel.apps.adaptilo.helpers.MessageDeserializerHelper;

/**
 * Encapsulate any request send to the server.
 * <p/>
 * Use to send message to the server with the current connection id
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

    /**
     * Constructor.
     *
     * @param connectionId external id send back during the registration process.
     * @param message      message send.
     */
    public ServerRequest(String connectionId, Message message) {
        mExternalId = connectionId;
        mMessage = message;
    }

    /**
     * GETTER AND SETTER
     */

    /**
     * Retrieve the external id.
     *
     * @return external id.
     */
    public String getExternalId() {
        return mExternalId;
    }

    /**
     * Set the external id.
     *
     * @param externalId external id.
     */
    public void setExternalId(String externalId) {
        this.mExternalId = externalId;
    }

    /**
     * Retrieve the message associated to the request.
     *
     * @return message.
     */
    public Message getMessage() {
        return mMessage;
    }

    /**
     * Set the message which will be send.
     *
     * @param message message.
     */
    public void setMessage(Message message) {
        this.mMessage = message;
    }
}
