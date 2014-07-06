package fr.tvbarthel.apps.adaptilo.server;

import fr.tvbarthel.apps.adaptilo.server.models.Role;
import fr.tvbarthel.apps.adaptilo.server.models.RoleConfiguration;
import fr.tvbarthel.apps.adaptilo.server.models.Room;
import fr.tvbarthel.apps.adaptilo.server.models.enums.MessageType;
import fr.tvbarthel.apps.adaptilo.server.models.io.ClosingError;
import fr.tvbarthel.apps.adaptilo.server.models.io.Message;
import fr.tvbarthel.apps.adaptilo.server.models.io.ServerRequest;
import org.java_websocket.framing.CloseFrame;

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
     * Max roles for the given game
     */
    private int mMaxRoles;

    /**
     * game rooms
     */
    private List<Room> mGameRooms;

    /**
     * roles allowed
     */
    private List<RoleConfiguration> mAllowedRoles;


    /**
     * create a single game server which will be able to hold several rooms for a given game config.
     *
     * @param address     server address
     * @param gameName    name of the game
     * @param allowedRole available roles
     */
    public SingleGameServer(InetSocketAddress address, String gameName, List<RoleConfiguration> allowedRole) {
        super(address);

        mAllowedRoles = allowedRole;
        mGameName = gameName;

        // Initialize max roles to the sum of max instance for each role.
        mMaxRoles = 0;
        for (RoleConfiguration roleConfig : mAllowedRoles) {
            mMaxRoles += roleConfig.getMaxInstance();
        }

        mGameRooms = new ArrayList<Room>();

        //TODO only for test purpose, should implement room creation
        //simulate room creation
        Room virtualRoom = new Room("virtual", 1);
        virtualRoom.setAvailableRoles(mAllowedRoles);
        mGameRooms.add(virtualRoom);
    }

    @Override
    protected int registerRoleInRoom(String gameName, Role role, String roomId, boolean replace, boolean create) {
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

        RoleConfiguration roleAllowed = null;
        for (RoleConfiguration roleConfig : mAllowedRoles) {
            if (roleConfig.getName().equals(role.getName())) {
                roleAllowed = roleConfig;
            }
        }

        if (roleAllowed == null) {
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

            //check if creation is requested
            if (create) {

                //check if role is allowed to create room
                if (roleAllowed.canCreateRoom()) {

                    //creation request and creation allowed for the given rol
                    requestedRoom = new Room(roomId, mMaxRoles);
                    requestedRoom.setAvailableRoles(mAllowedRoles);
                    mGameRooms.add(requestedRoom);
                    System.out.println(TAG + " Room with id : " + roomId + " created.");

                } else {

                    //creation requested but not allowed for the given role
                    System.out.println(TAG + " Role : " + role.getName() + " not allowed to create a room.");
                    return ClosingError.REGISTRATION_REQUESTED_ROOM_UNKNOW;
                }
            } else {

                //room doesn't exist and creation policy to false
                return ClosingError.REGISTRATION_REQUESTED_ROOM_UNKNOW;
            }
        }

        int registeringCode = requestedRoom.registerRole(role, roleAllowed, replace);

        if (registeringCode == 0) {
            //registration succeed, broadcast an event to the roles registered in the same room.
            broadcastMessage(
                    requestedRoom,
                    role,
                    new Message(MessageType.ON_ROLE_REGISTERED, role.getName())
            );
        }
        return registeringCode;
    }

    @Override
    protected int unregisterRoleInRoom(String gameName, String externalId, String roomId) {
        Room controllerRoom = null;

        for (Room room : mGameRooms) {
            if (room.getRoomId().equals(roomId)) {
                //current controller room found
                controllerRoom = room;
                break;
            }
        }

        if (controllerRoom != null) {
            final Role controllerRole = controllerRoom.unregisterRole(externalId);

            //broadcast the role of the unregistered controller
            broadcastMessage(
                    controllerRoom,
                    controllerRole,
                    new Message(MessageType.ON_ROLE_UNREGISTERED, controllerRole.getName())
            );
        }
        return CloseFrame.NORMAL;
    }

    @Override
    protected Message onMessageReceived(ServerRequest message) {
        //broadcast all messages in sender room.
        final String senderRoomId = message.getRoom();
        final String senderExtId = message.getExternalId();

        //find sender room
        Room room = findRoomById(senderRoomId);

        if (room != null) {
            Role role = room.findRoleById(senderExtId);
            if (role != null) {
                broadcastMessage(room, role, message.getMessage());
            }
        }

        return null;  //by default no answer are send back to the sender
    }

    @Override
    protected List<String> onRolesRequested(String game) {
        List<String> roles = new ArrayList<String>();

        for (RoleConfiguration roleConfig : mAllowedRoles) {
            roles.add(roleConfig.getName());
        }
        return roles;
    }


    /**
     * Find a room.
     *
     * @param roomId room identifier
     * @return room matching identifier or null
     */
    protected Room findRoomById(String roomId) {
        Room room = null;
        for (Room r : mGameRooms) {
            if (r.getRoomId().equals(roomId)) {
                room = r;
            }
        }

        return room;
    }

}
