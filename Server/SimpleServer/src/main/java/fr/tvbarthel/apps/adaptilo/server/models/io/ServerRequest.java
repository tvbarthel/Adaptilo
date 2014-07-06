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
     * Game name.
     */
    @SerializedName(MessageDeserializerHelper.NODE_GAME_NAME)
    private String mGameName;

    /**
     * Room id.
     */
    @SerializedName(MessageDeserializerHelper.NODE_GAME_ROOM)
    private String mRoom;

    /**
     * message to deliver
     */
    @SerializedName(MessageDeserializerHelper.NODE_MESSAGE)
    private Message mMessage;


    public ServerRequest(String connectionId, String game, String room, Message message) {
        mExternalId = connectionId;
        mGameName = game;
        mRoom = room;
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

    public String getGameName() {
        return mGameName;
    }

    public void setGameName(String gameName) {
        this.mGameName = gameName;
    }

    public String getRoom() {
        return mRoom;
    }

    public void setRoom(String room) {
        this.mRoom = room;
    }
}
