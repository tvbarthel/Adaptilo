package fr.tvbarthel.apps.adaptilo.server;

import fr.tvbarthel.apps.adaptilo.server.models.Role;
import fr.tvbarthel.apps.adaptilo.server.models.RoleConfiguration;
import fr.tvbarthel.apps.adaptilo.server.models.Room;
import fr.tvbarthel.apps.adaptilo.server.models.enums.MessageType;
import fr.tvbarthel.apps.adaptilo.server.models.io.ClosingCode;
import fr.tvbarthel.apps.adaptilo.server.models.io.Message;
import org.java_websocket.framing.CloseFrame;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
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
     * Game rooms.
     */
    private List<Room> mGameRooms;

    /**
     * Hash map used to quickly retrieve room from external id.
     */
    private HashMap<String, Room> mRoomsHashMap;

    /**
     * Hash map used to quickly retrieve role from external id.
     */
    private HashMap<String, Role> mRolesHashMap;


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
        mRoomsHashMap = new HashMap<String, Room>();
        mRolesHashMap = new HashMap<String, Role>();

        //TODO only for test purpose, should implement room creation
        //simulate room creation
        Room virtualRoom = new Room("virtual", 1);
        virtualRoom.setAvailableRoles(mAllowedRoles);
        mGameRooms.add(virtualRoom);
    }


    @Override
    protected int registerRole(String extId, String gameId, String roomId, Role role, boolean shouldReplace) {
        Room requestedRoom = null;

        if (!mGameName.equals(gameId)) {
            //current game name doesn't match requested one
            System.out.println(TAG + " current game name " + mGameName + " doesn't match requested one : " + gameId);
            return ClosingCode.REGISTRATION_REQUESTED_GAME_NAME_UNKNOWN;
        }

        if (mGameRooms.isEmpty()) {
            //create room first
            System.out.println(TAG + " No created rooms for this game, need to create one first.");
            return ClosingCode.REGISTRATION_NO_ROOM_CREATED;
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
            return ClosingCode.REGISTRATION_REQUESTED_ROLE_UNKNOWN;
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
                return ClosingCode.REGISTRATION_REQUESTED_ROOM_UNKNOW;
            }
        }

        int registeringCode = requestedRoom.registerRole(role, roleAllowed, shouldReplace);

        if (registeringCode == 0) {
            //registration in room succeed

            //add reference in HashMap for quick access
            mRoomsHashMap.put(extId, requestedRoom);
            mRolesHashMap.put(extId, role);

            //broadcast an event to the roles registered in the same room.
            broadcastMessage(
                    requestedRoom,
                    role,
                    new Message(MessageType.ON_ROLE_REGISTERED, role.getName())
            );
        }
        return registeringCode;
    }

    @Override
    protected int unregisterRole(String externalId) {

        //quickly retrieve matching Room and matching Role
        Room controllerRoom = mRoomsHashMap.get(externalId);
        Role controllerRole = mRolesHashMap.get(externalId);


        if (controllerRoom != null && controllerRole != null) {
            final boolean removed = controllerRoom.unregisterRole(controllerRole);

            if (removed) {
                //role instance well removed from its room

                //removed reference in quick access HashMap
                mRoomsHashMap.remove(externalId);
                mRolesHashMap.remove(externalId);
            }

            //broadcast role disconnection
            broadcastMessage(
                    controllerRoom,
                    controllerRole,
                    new Message(MessageType.ON_ROLE_UNREGISTERED, controllerRole.getName())
            );
        }
        return CloseFrame.NORMAL;
    }

    @Override
    protected Message onMessageReceived(String externalId, Message message) {

        //quickly retrieve matching role and matching room
        Room senderRoom = mRoomsHashMap.get(externalId);
        Role senderRole = mRolesHashMap.get(externalId);


        //simple broadcast all message to the whole sender's room
        if (senderRoom != null && senderRole != null) {
            broadcastMessage(senderRoom, senderRole, message);
        }

        return null;  //by default no answer are send back to the sender
    }


    @Override
    protected List<String> onRolesRequested(String extId) {
        List<String> roles = new ArrayList<String>();

        //single game server has only one RoleConfigurationList
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
