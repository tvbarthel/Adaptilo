package fr.tvbarthel.apps.adaptilo.server.models.io;

import com.google.gson.annotations.SerializedName;
import fr.tvbarthel.apps.adaptilo.server.helpers.MessageDeserializerHelper;

/**
 * Encapsulate data send to the server to create a game room
 */
public class RoomCreationRequest {

    /**
     * create a room for the given game
     */
    @SerializedName(MessageDeserializerHelper.NODE_GAME_NAME)
    private String mGameName;

    public String getGameName() {
        return mGameName;
    }

    public void setGameName(String mGameName) {
        this.mGameName = mGameName;
    }
}
