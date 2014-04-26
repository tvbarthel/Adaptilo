package fr.tvbarthel.apps.adaptilo.models;

public class Message {
    private MessageType mType;
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
