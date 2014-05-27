package fr.tvbarthel.apps.adaptilo.server;

import fr.tvbarthel.apps.adaptilo.server.models.Role;
import fr.tvbarthel.apps.adaptilo.server.models.Room;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Server that handle one game with multiple room
 */
public class SingleGameServer extends AdaptiloServer {

    /**
     * name fo the game
     */
    private String mGameName;

    /**
     * game rooms
     */
    private List<Room> mGameRooms;

    /**
     * roles allowed
     */
    private List<String> mAllowedRoles;


    /**
     * create a single game server which will be able to hold several rooms for a given game config.
     *
     * @param address     server address
     * @param gameName    name of the game
     * @param allowedRole available roles
     */
    public SingleGameServer(InetSocketAddress address, String gameName, List<String> allowedRole) {
        super(address);

        mAllowedRoles = allowedRole;
        mGameName = gameName;

        mGameRooms = new ArrayList<Room>();

        //TODO only for test purpose, should implement room creation
        //simulate room creation
        Room virtualRoom = new Room("virtual", 1);
        virtualRoom.setAvailableRoles(mAllowedRoles);
        mGameRooms.add(virtualRoom);
    }


    @Override
    protected boolean registerRoleInRoom(Role role, String roomId) {
        Room requestedRoom = null;

        if (mGameRooms.isEmpty()) {
            //create room first
            return false;
        }

        if (!mAllowedRoles.contains(role.getName())) {
            //role not allowed for this game
            return false;
        }

        for (Room room : mGameRooms) {
            if (room.getRoomId().equals(roomId)) {
                //request room found
                requestedRoom = room;
                break;
            }
        }

        if (requestedRoom == null) {
            //request room not found
            return false;
        }

        //TODO always replace for test purpose
        return requestedRoom.registerRole(role, true);
    }
}
