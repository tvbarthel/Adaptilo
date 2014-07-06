package fr.tvbarthel.apps.adaptilo.server.models;

/**
 * Encapsulate role configuration, such as role name, role replacement policy and room creation policy.
 */
public class RoleConfiguration {

    /**
     * role identifier
     */
    private String mName;

    /**
     * Replacement policy, true if the given role can be replaced.
     */
    private boolean mCanBeReplaced;

    /**
     * Room creation policy, true if the given role is allowed to create a room.
     */
    private boolean mCanCreateRoom;

    /**
     * Maximum number of instance for the given role.
     */
    private int mMaxInstance;

    /**
     * Role configuration.
     *
     * @param name          Role identifier.
     * @param canBeReplaced Replacement policy, true if the given role can be replaced.
     * @param canCreateRoom Room creation policy, true if the given role is allowed to create a room.
     * @param maxInstance   maximum number of role instance
     */
    public RoleConfiguration(String name, boolean canBeReplaced, boolean canCreateRoom, int maxInstance) {
        mName = name;
        mCanBeReplaced = canBeReplaced;
        mCanCreateRoom = canCreateRoom;
        mMaxInstance = maxInstance;
    }

    /**
     * GETTER && SETTER
     */

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public boolean canBeReplaced() {
        return mCanBeReplaced;
    }

    public void setCanBeReplaced(boolean canBeReplaced) {
        this.mCanBeReplaced = canBeReplaced;
    }

    public boolean canCreateRoom() {
        return mCanCreateRoom;
    }

    public void setCanCreateRoom(boolean canCreateRoom) {
        this.mCanCreateRoom = canCreateRoom;
    }

    public int getMaxInstance() {
        return mMaxInstance;
    }

    public void setMaxInstance(int maxInstance) {
        this.mMaxInstance = maxInstance;
    }
}
