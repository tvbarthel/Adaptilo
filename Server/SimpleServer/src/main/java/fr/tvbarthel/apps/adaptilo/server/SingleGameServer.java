package fr.tvbarthel.apps.adaptilo.server;

import fr.tvbarthel.apps.adaptilo.server.models.Role;
import fr.tvbarthel.apps.adaptilo.server.models.Room;
import fr.tvbarthel.apps.adaptilo.server.models.io.ClosingError;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Server that handle one game with multiple room
 */
public class SingleGameServer extends AdaptiloServer {

    /**
     * Used to log in console.
     */
    private static final String TAG = SingleGameServer.class.getCanonicalName();

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
    protected int registerRoleInRoom(String gameName, Role role, String roomId) {
        Room requestedRoom = null;

        if (!mGameName.equals(gameName)) {
            //current game name doesn't match requested one
            System.out.println(TAG + " current game name " + mGameName + " doesn't match requested one : " + gameName);
            return ClosingError.REGISTRATION_REQUESTED_GAME_NAME_UNKNOWN;
        }

        if (mGameRooms.isEmpty()) {
            //create room first
            System.out.println(TAG + " No created rooms for this game, need to create one first.");
            return ClosingError.REGISTRATION_NO_ROOM_CREATED;
        }

        if (!mAllowedRoles.contains(role.getName())) {
            //role not allowed for this game
            System.out.println(TAG + " Role : " + role.getName() + " doesn't allowed in this game.");
            return ClosingError.REGISTRATION_REQUESTED_ROLE_UNKNOWN;
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
            System.out.println(TAG + " Room with id : " + roomId + " doesn't exist.");
            return ClosingError.REGISTRATION_REQUESTED_ROOM_UNKNOW;
        }

        //TODO always replace for test purpose
        return requestedRoom.registerRole(role, true);
    }
}