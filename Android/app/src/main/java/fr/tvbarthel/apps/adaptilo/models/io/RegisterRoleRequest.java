package fr.tvbarthel.apps.adaptilo.models.io;

import com.google.gson.annotations.SerializedName;

import fr.tvbarthel.apps.adaptilo.helpers.MessageDeserializerHelper;

/**
 * Request send to the server to register a new role.
 */
public class RegisterRoleRequest {

    /**
     * Game name.
     */
    @SerializedName(MessageDeserializerHelper.NODE_GAME_NAME)
    private String mGameName;

    /**
     * Game room.
     */
    @SerializedName(MessageDeserializerHelper.NODE_GAME_ROOM)
    private String mGameRoom;

    /**
     * Role.
     */
    @SerializedName(MessageDeserializerHelper.NODE_GAME_ROLE)
    private String mGameRole;

    /**
     * Replacement policy.
     */
    @SerializedName(MessageDeserializerHelper.NODE_SHOULD_REPLACE)
    private boolean mShouldReplace;

    /**
     * Empty constructor.
     */
    public RegisterRoleRequest() {

    }

    /**
     * Registration request.
     *
     * @param name    game identifier.
     * @param room    room identifier.
     * @param role    role identifier.
     * @param replace replacement policy, true if role should be replaced when already taken.
     */
    public RegisterRoleRequest(String name, String room, String role,
                               boolean replace) {
        mGameName = name;
        mGameRoom = room;
        mGameRole = role;
        mShouldReplace = replace;
    }

    /**
     * Retrieve the current role.
     *
     * @return game role.
     */
    public String getGameRole() {
        return mGameRole;
    }

    /**
     * Set the role which will be played after registration.
     *
     * @param mGameRole role.
     */
    public void setGameRole(String mGameRole) {
        this.mGameRole = mGameRole;
    }

    /**
     * Retrieve the current room.
     *
     * @return game room.
     */
    public String getGameRoom() {
        return mGameRoom;
    }

    /**
     * Set the room in which the player will be registered.
     *
     * @param mGameRoom game room.
     */
    public void setGameRoom(String mGameRoom) {
        this.mGameRoom = mGameRoom;
    }

    /**
     * Retrieve the current name.
     *
     * @return game name.
     */
    public String getGameName() {
        return mGameName;
    }

    /**
     * Set the game played by the player.
     *
     * @param mGameName game name.
     */
    public void setGameName(String mGameName) {
        this.mGameName = mGameName;
    }

    /**
     * Retrieve replacement policy.
     *
     * @return true if role should be replaced when already taken.
     */
    public boolean shouldReplace() {
        return mShouldReplace;
    }

    /**
     * Define the replacement policy.
     *
     * @param shouldReplace true if role should be replaced when already taken.
     */
    public void setShouldReplace(boolean shouldReplace) {
        this.mShouldReplace = shouldReplace;
    }
}
