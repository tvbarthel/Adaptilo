package fr.tvbarthel.apps.adaptilo.models.io;

import com.google.gson.annotations.SerializedName;

import fr.tvbarthel.apps.adaptilo.helpers.MessageDeserializerHelper;
import fr.tvbarthel.apps.adaptilo.models.enums.MessageType;

/**
 * Encapsulate the structure of a message send or received.
 */
public class Message {

    /**
     * Message type.
     * <p/>
     * {@link fr.tvbarthel.apps.adaptilo.models.enums.MessageType}
     */
    @SerializedName(MessageDeserializerHelper.NODE_TYPE)
    private MessageType mType;

    /**
     * Message content.
     * <p/>
     * Could be any object.
     */
    @SerializedName(MessageDeserializerHelper.NODE_CONTENT)
    private Object mContent;

    /**
     * Empty constructor.
     */
    public Message() {
    }

    /**
     * Constructor.
     *
     * @param type    message type.
     * @param content message content.
     */
    public Message(MessageType type, Object content) {
        mType = type;
        mContent = content;
    }

    /**
     * Retrieve the message type.
     *
     * @return current message type.
     */
    public MessageType getType() {
        return mType;
    }

    /**
     * Set the message type.
     *
     * @param type new type.
     */
    public void setType(MessageType type) {
        mType = type;
    }

    /**
     * Retrieve the message content.
     *
     * @return current message content.
     */
    public Object getContent() {
        return mContent;
    }

    /**
     * Set the message content.
     *
     * @param content new content.
     */
    public void setContent(Object content) {
        mContent = content;
    }
}
