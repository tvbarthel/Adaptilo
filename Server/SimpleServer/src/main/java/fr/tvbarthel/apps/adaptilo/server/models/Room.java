package fr.tvbarthel.apps.adaptilo.server.models;

import fr.tvbarthel.apps.adaptilo.server.models.io.ClosingError;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulate data linked to a room (same concept as chat room)
 */
public class Room {

    /**
     * More readable log.
     */
    private static final String TAG = Room.class.getCanonicalName();

    /**
     * external id used to identify current room. Should be unique.
     */
    private String mId;

    /**
     * room limit for roles
     */
    private int mMaxRoles;

    /**
     * roles available for the room
     */
    private List<String> mAvailableRoles;

    /**
     * current roles register to the room
     */
    private List<Role> mRoles;

    public Room(String rommId, int maxRoles) {
        mId = rommId;
        mRoles = new ArrayList<Role>();
        mAvailableRoles = new ArrayList<String>();
        mMaxRoles = maxRoles;
    }

    /**
     * find a role by its name. Useful when you need to retrieved the recipient of a message.
     *
     * @param roleName role name use as role description
     * @return matching role or null if not found
     */
    public Role findRoleByName(String roleName) {
        if (!mRoles.isEmpty()) {
            for (Role role : mRoles) {
                if (role.getName().equals(roleName)) {
                    return role;
                }
            }
        }
        return null;
    }

    /**
     * find a role by user external id. Useful when you have to identify a request from the network.
     * <p/>
     * since a role can be played by several controller at the same time, need to find role with the unique identifier
     * given during the registration process.
     *
     * @param id external id send with each network message
     * @return matching role or null if not found
     */
    public Role findRoleById(String id) {
        if (!mRoles.isEmpty()) {
            for (Role role : mRoles) {
                if (role.getId().equals(id)) {
                    return role;
                }
            }
        }
        return null;
    }

    /**
     * register a Role is the rome
     *
     * @param role          role to register
     * @param shouldReplace true if replacement is requested, set false if many client can play the same role
     * @return 0 if registration succeed or an
     *         {@link fr.tvbarthel.apps.adaptilo.server.models.io.ClosingError}
     */
    public int registerRole(Role role, boolean shouldReplace) {
        if (!mAvailableRoles.isEmpty()) {
            //check if given role is allowed
            if (!mAvailableRoles.contains(role.getName())) {
                return ClosingError.REGISTRATION_REQUESTED_ROLE_UNKNOWN;
            }
        }

        //check room size
        final int remainingSlot = shouldReplace ? mMaxRoles - mRoles.size() + 1 : mMaxRoles - mRoles.size();
        if (0 >= remainingSlot) {
            return ClosingError.REGISTRATION_REQUESTED_ROOM_IS_FULL;
        }

        //delete current registered role if replace is requested
        if (shouldReplace) {
            final Role roleToRemove = findRoleByName(role.getName());
            if (roleToRemove != null) {
                roleToRemove.getConnection().close(ClosingError.DISCONNECTION_DUE_TO_ROLE_REPLACEMENT);
                mRoles.remove(roleToRemove);
            }
            System.out.println(TAG + " role " + role.getName() + " replaced in room  " + this.getRoomId() + " extId : " + role.getId());
        } else {
            System.out.println(TAG + " role " + role.getName() + " added in room  " + this.getRoomId() + " extId : " + role.getId());
        }

        mRoles.add(role);
        return 0;
    }

    /**
     * Unregister a role with a given externalId.
     *
     * @param externalId external id used to identify each role
     * @return unregistered role or null if not found.
     */
    public Role unregisterRole(String externalId) {
        Role unregisteredRole = findRoleById(externalId);

        if (unregisteredRole != null) {
            mRoles.remove(unregisteredRole);
            System.out.println(TAG + " role : " + unregisteredRole.getName() + " | removed in room  : " + this.getRoomId() + " | extId : " + unregisteredRole.getId());
        }

        return unregisteredRole;
    }

    /**
     * GETTER & SETTER
     */

    public String getRoomId() {
        return mId;
    }

    public void setRoomId(String roomId) {
        this.mId = roomId;
    }

    public int getMaxRoles() {
        return mMaxRoles;
    }

    public void setMaxRoles(int mMaxRoles) {
        this.mMaxRoles = mMaxRoles;
    }

    public List<String> getAvailableRoles() {
        return mAvailableRoles;
    }

    public void setAvailableRoles(List<String> mAvailableRoles) {
        this.mAvailableRoles = mAvailableRoles;
    }

    public List<Role> getRoles() {
        return mRoles;
    }

}
