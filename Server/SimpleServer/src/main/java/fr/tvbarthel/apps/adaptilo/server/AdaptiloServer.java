package fr.tvbarthel.apps.adaptilo.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.tvbarthel.apps.adaptilo.server.helpers.MessageDeserializerHelper;
import fr.tvbarthel.apps.adaptilo.server.models.Event;
import fr.tvbarthel.apps.adaptilo.server.models.Role;
import fr.tvbarthel.apps.adaptilo.server.models.Room;
import fr.tvbarthel.apps.adaptilo.server.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.server.models.enums.EventType;
import fr.tvbarthel.apps.adaptilo.server.models.enums.MessageType;
import fr.tvbarthel.apps.adaptilo.server.models.io.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

/**
 * Abstract server used to communicate with Adaptilo App.
 * Handle all basic features such as :
 * |    connection and id generation / disconnection
 * |    register room and QrCode generation for role/ unregister
 * |    pause / resume
 */
public abstract class AdaptiloServer extends WebSocketServer {

    private static final String TAG = AdaptiloServer.class.getCanonicalName();

    /**
     * parser for json serialization / deserialization
     */
    private Gson mParser;

    /**
     * handle role registration
     *
     * @param externalId    unique external id used to identify the role instance.
     * @param gameId        id of the game
     * @param roomId        room id in which role request registration
     * @param role          the role instance to register
     * @param shouldReplace replacement policy when role is already registered
     * @return should return 0 if registration succeeds, else an
     *         {@link fr.tvbarthel.apps.adaptilo.server.models.io.ClosingCode} matching a registration code.
     */
    protected abstract int registerRole(String externalId, String gameId, String roomId, Role role, boolean shouldReplace);

    /**
     * Handle role disconnection.
     *
     * @param externalId role id given during the registration process
     * @return closing code send back to the controller through onClose
     */
    protected abstract int unregisterRole(String externalId);

    /**
     * Let server implementation decide how to proceed message which aren't handled by default.
     *
     * @param externalId sender external id
     * @param message    message received.
     * @return message for the sender or null if no answer should be send back
     */
    protected abstract Message onMessageReceived(String externalId, Message message);

    /**
     * A client request role names. Basically asked by the game field to display QrCodes.
     *
     * @param extId requester external id
     * @return map of role : url
     */
    protected abstract List<String> onRolesRequested(String extId);

    public AdaptiloServer(InetSocketAddress address) {
        super(address);
        mParser = initGsonParser();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println(TAG + " onOpen - " + conn.toString() + ", " + handshake.toString());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(TAG + " onClose - " + conn.toString() + ", " + code + ", " + reason + ", " + remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        //extract basic info for all message
        System.out.println(TAG + " onMessage - " + conn.toString() + ", " + message);
        final ServerRequest messageReceived = mParser.fromJson(message, ServerRequest.class);
        final Message messageContent = messageReceived.getMessage();
        final String connectionId = messageReceived.getExternalId();
        Message answer = null;

        //process each message type
        switch (messageContent.getType()) {
            case REGISTER_ROLE_REQUEST:

                //a role want to join a room
                final RegisterRoleRequest request = (RegisterRoleRequest) messageContent.getContent();
                answer = processRoleRegistration(conn, request);
                break;
            case UNREGISTER_ROLE_REQUEST:

                //a role want to leave a room
                final int closingCode = unregisterRole(connectionId);
                conn.close(closingCode);
                System.out.println(TAG + " connection closed :" + conn.toString());
                break;
            case ROLES_REQUEST:

                //a role request roles' urls for its room
                List<String> rolesUrl
                        = onRolesRequested(messageReceived.getExternalId());
                answer = new Message(MessageType.ON_ROLES_RETRIEVED, rolesUrl);
                break;
            default:
                answer = onMessageReceived(connectionId, messageContent);
                break;
        }

        if (answer != null) {
            conn.send(mParser.toJson(answer));
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println(TAG + " onError - " + conn.toString() + ", " + ex.getLocalizedMessage());
    }

    public void sendToAll(String text) {
        Collection<WebSocket> con = connections();
        synchronized (con) {
            for (WebSocket c : con) {
                c.send(text);
            }
        }
    }

    /**
     * allow to test vibrator pattern
     * TODO remove, only for test purpose
     */
    public void vibratePattern(long[] pattern) {
        sendToAll(mParser.toJson(new Message(MessageType.VIBRATOR_PATTERN, pattern)));
    }

    /**
     * allow to test vibrator
     * TODO remove, only for test purpose
     *
     * @param duration duration of the vibration
     */
    public void vibrate(Long duration) {
        sendToAll(mParser.toJson(new Message(MessageType.VIBRATOR, duration)));
    }

    /**
     * allow to test shaker activation and deactivation
     * TODO remove, only for test purpose
     */
    public void enableShaker(boolean enable) {
        final EventAction action = enable ? EventAction.ACTION_ENABLE : EventAction.ACTION_DISABLE;
        final Event enableShaker = new Event(EventType.SHAKER, action);
        sendToAll(mParser.toJson(new Message(MessageType.SENSOR, enableShaker)));
    }

    /**
     * Used to send a message to any roles in a given room except the sender.
     *
     * @param room    room in which message should be broadcast.
     * @param sender  source of the broadcast.
     * @param message message to send
     */
    protected void broadcastMessage(Room room, Role sender, Message message) {
        for (Role role : room.getRoles()) {
            if (role != sender && role.getConnection().isOpen()) {
                role.getConnection().send(mParser.toJson(message));
            }
        }
    }

    /**
     * process to the registration of a role
     *
     * @param request registration request from network
     * @return answer which should be send back
     */
    private Message processRoleRegistration(WebSocket conn, RegisterRoleRequest request) {

        //check if registration is for a new room or for joining an existed one.
        String roomId = request.getGameRoom();

        //use roomId send for joining room registration or generate a new one for new registration
        if (roomId == null) {
            roomId = generateRoomId(request.getGameName());
            System.out.println(TAG + " processRoleRegistration, new room :  " + roomId);
        } else {
            System.out.println(TAG + " processRoleRegistration, join room :  " + roomId);
        }

        //generate unique external identifier
        final String givenId = generateExternalId(request.getGameName(), roomId, request.getGameRole());

        final Role roleToRegister = new Role(request.getGameRole(), conn, givenId);
        Message answer = null;

        final int registrationCode = registerRole(
                givenId,
                request.getGameName(),
                roomId,
                roleToRegister,
                request.shouldReplace()
        );

        switch (registrationCode) {
            case 0:
                //registration completed, prepare server answer
                System.out.println(TAG + " registration completed, game :  " + request.getGameName()
                        + " room : " + request.getGameRole() + " role : " + request.getGameRole() + " id : " + givenId);

                //build answer to send to the requester
                answer = new Message(MessageType.CONNECTION_COMPLETED, new RegisterRoleResponse(givenId, roomId));
                break;
            default:
                //registration fail, close connection and send registration error code
                conn.close(registrationCode);
        }
        return answer;
    }

    /**
     * generate external id according to game, room, role and randomized seed
     *
     * @param game game requested
     * @param room room requested
     * @param role role requested
     * @return external id generated
     */
    private String generateExternalId(String game, String room, String role) {
        final long seed = System.currentTimeMillis();
        return game + room + role + seed;
    }

    /**
     * Generate a room id.
     *
     * @param game name of the game
     * @return unique room id
     */
    private String generateRoomId(String game) {
        final long seed = System.currentTimeMillis();
        return seed + game;
    }

    /**
     * initialize gson parser with right adapter for custom object
     *
     * @return gson parser
     */
    private Gson initGsonParser() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Message.class, new MessageDeserializerHelper());

        return builder.create();
    }
}
