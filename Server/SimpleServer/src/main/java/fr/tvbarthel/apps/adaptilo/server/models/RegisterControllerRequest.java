package fr.tvbarthel.apps.adaptilo.server.models;

import com.google.gson.annotations.SerializedName;

public class RegisterControllerRequest {

    @SerializedName("gameName")
    private String mGameName;

    @SerializedName("gameRoom")
    private String mGameRoom;

    @SerializedName("gameRole")
    private String mGameRole;

    public RegisterControllerRequest() {

    }

    public RegisterControllerRequest(String name, String room, String role) {
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

    public void setmameName(String mGameName) {
        this.mGameName = mGameName;
    }


}
