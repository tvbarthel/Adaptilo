package fr.tvbarthel.apps.adaptilo.server.models;

import fr.tvbarthel.apps.adaptilo.server.models.io.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulate role configuration, such as role name, role replacement policy and room creation policy.
 */
public final class RoleConfiguration {

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
     * A list of messages that will be sent during the role registration.
     */
    private List<Message> mInitialMessages;

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
        mInitialMessages = new ArrayList<Message>();
    }

    /**
     * GETTER && SETTER
     */

    public String getName() {
        return mName;
    }

    public boolean canBeReplaced() {
        return mCanBeReplaced;
    }

    public boolean canCreateRoom() {
        return mCanCreateRoom;
    }

    public int getMaxInstance() {
        return mMaxInstance;
    }

    public void addInitialMessage(Message initialMessage) {
        mInitialMessages.add(initialMessage);
    }

    public List<Message> getInitialMessages() {
        return mInitialMessages;
    }
}
