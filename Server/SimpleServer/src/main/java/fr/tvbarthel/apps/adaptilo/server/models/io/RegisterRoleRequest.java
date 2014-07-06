package fr.tvbarthel.apps.adaptilo.server.models.io;

import com.google.gson.annotations.SerializedName;
import fr.tvbarthel.apps.adaptilo.server.helpers.MessageDeserializerHelper;

public class RegisterRoleRequest {

    @SerializedName(MessageDeserializerHelper.NODE_GAME_NAME)
    private String mGameName;

    @SerializedName(MessageDeserializerHelper.NODE_GAME_ROOM)
    private String mGameRoom;

    @SerializedName(MessageDeserializerHelper.NODE_GAME_ROLE)
    private String mGameRole;

    @SerializedName(MessageDeserializerHelper.NODE_SHOULD_REPLACE)
    private boolean mShouldReplace;

    @SerializedName(MessageDeserializerHelper.NODE_SHOULD_CREATE)
    private boolean mShouldCreate;

    public RegisterRoleRequest() {

    }

    /**
     * Registration request.
     *
     * @param name    game identifier.
     * @param room    room identifier.
     * @param role    role identifier.
     * @param replace replacement policy, true if role should be replaced when already taken.
     * @param create  creation policy, true if the room should be created when it doesn't exist.
     */
    public RegisterRoleRequest(String name, String room, String role,
                               boolean replace, boolean create) {
        mGameName = name;
        mGameRoom = room;
        mGameRole = role;
        mShouldReplace = replace;
        mShouldCreate = create;
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

    public boolean shouldReplace() {
        return mShouldReplace;
    }

    public void setShouldReplace(boolean shouldReplace) {
        this.mShouldReplace = shouldReplace;
    }

    public boolean shouldCreate() {
        return mShouldCreate;
    }

    public void setShouldCreate(boolean shouldCreate) {
        this.mShouldCreate = shouldCreate;
    }
}
