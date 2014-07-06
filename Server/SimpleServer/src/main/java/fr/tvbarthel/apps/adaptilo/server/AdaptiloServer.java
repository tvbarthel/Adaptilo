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
import fr.tvbarthel.apps.adaptilo.server.models.io.Message;
import fr.tvbarthel.apps.adaptilo.server.models.io.RegisterRoleRequest;
import fr.tvbarthel.apps.adaptilo.server.models.io.ServerRequest;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collection;

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
     * @param gameName      name of the game
     * @param role          role to register
     * @param roomId        room id in which role request registration
     * @param shouldReplace replacement policy when role is already registered
     * @return should return 0 if registration succeeds, else an
     *         {@link fr.tvbarthel.apps.adaptilo.server.models.io.ClosingError} matching a registration code.
     */
    protected abstract int registerRoleInRoom(String gameName, Role role, String roomId, boolean shouldReplace);

    /**
     * Handle role disconnection.
     *
     * @param gameName   name of the game
     * @param externalId role id given during the registration process
     * @param roomId     id of the room in which the given controller is playing
     * @return closing code send back to the controller through onClose
     */
    protected abstract int unregisterRoleInRoom(String gameName, String externalId, String roomId);

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
                final RegisterRoleRequest request = (RegisterRoleRequest) messageContent.getContent();
                answer = processRoleRegistration(conn, request);
                break;
            case UNREGISTER_ROLE_REQUEST:
                final RegisterRoleRequest unregisterRequest
                        = (RegisterRoleRequest) messageContent.getContent();
                final int closingCode = unregisterRoleInRoom(
                        unregisterRequest.getGameName(),
                        connectionId,
                        unregisterRequest.getGameRoom()
                );
                conn.close(closingCode);
                System.out.println(TAG + " connection closed :" + conn.toString());
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
            if (role != sender) {
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
        //generate unique external identifier
        final String givenId = generateExternalId(request.getGameName(), request.getGameRoom(), request.getGameRole());
        final Role roleToRegister = new Role(request.getGameRole(), conn, givenId);
        Message answer = null;

        final int registrationCode = registerRoleInRoom(
                request.getGameName(),
                roleToRegister,
                request.getGameRoom(),
                request.shouldReplace()
        );

        switch (registrationCode) {
            case 0:
                //registration completed, prepare server answer
                System.out.println(TAG + " registration completed, game :  " + request.getGameName()
                        + " room : " + request.getGameRole() + " role : " + request.getGameRole() + " id : " + givenId);

                //build answer to send to the requester
                answer = new Message(MessageType.CONNECTION_COMPLETED, givenId);
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
