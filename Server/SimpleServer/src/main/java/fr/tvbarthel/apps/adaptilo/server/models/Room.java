package fr.tvbarthel.apps.adaptilo.server.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulate data linked to a room (same concept as chat room)
 */
public class Room {

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
     * find a role by its id. Useful when you have to identify a request from the network.
     *
     * @param id role id send with each network message
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
     * @return
     */
    public boolean registerRole(Role role, boolean shouldReplace) {
        if (!mAvailableRoles.isEmpty()) {
            //check if given role is allowed
            if (!mAvailableRoles.contains(role.getName())) {
                return false;
            }
        }

        //check room size
        final int remainingSlot = shouldReplace ? mMaxRoles - 1 : mMaxRoles;
        if (mRoles.size() > remainingSlot) {
            return false;
        }

        //delete current registered role if replace if requested
        if (shouldReplace) {
            mRoles.remove(findRoleByName(role.getName()));
        }

        mRoles.add(role);
        return true;
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
