package fr.tvbarthel.apps.adaptilo.models;

import com.google.gson.annotations.SerializedName;

import fr.tvbarthel.apps.adaptilo.helpers.MessageDeserializerHelper;

public class Message {

    @SerializedName(MessageDeserializerHelper.NODE_TYPE)
    private MessageType mType;

    @SerializedName(MessageDeserializerHelper.NODE_CONTENT)
    private Object mContent;

    public Message() {
    }

    public Message(MessageType type, Object content) {
        mType = type;
        mContent = content;
    }

    public MessageType getType() {
        return mType;
    }

    public void setType(MessageType type) {
        mType = type;
    }

    public Object getContent() {
        return mContent;
    }

    public void setContent(Object content) {
        mContent = content;
    }
}
