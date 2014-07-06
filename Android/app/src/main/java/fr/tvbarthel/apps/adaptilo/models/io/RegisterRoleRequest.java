package fr.tvbarthel.apps.adaptilo.models.io;

import com.google.gson.annotations.SerializedName;

import fr.tvbarthel.apps.adaptilo.helpers.MessageDeserializerHelper;

public class RegisterRoleRequest {

    @SerializedName(MessageDeserializerHelper.NODE_GAME_NAME)
    private String mGameName;

    @SerializedName(MessageDeserializerHelper.NODE_GAME_ROOM)
    private String mGameRoom;

    @SerializedName(MessageDeserializerHelper.NODE_GAME_ROLE)
    private String mGameRole;

    public RegisterRoleRequest() {

    }

    public RegisterRoleRequest(String name, String room, String role) {
        mGameName = name;
        mGameRoom = room;
        mGameRole = role;
    }

    public String getGameRole() {
        return mGameRole;
    }

    public void setGameRole(String mGameRole) {
        this.mGameRole = mGameRole;
    }

    public String getGameRoom() {
        return mGameRoom;
    }

    public void setGameRoom(String mGameRoom) {
        this.mGameRoom = mGameRoom;
    }

    public String getGameName() {
        return mGameName;
    }

    public void setGameName(String mGameName) {
        this.mGameName = mGameName;
    }


}
