package fr.tvbarthel.apps.adaptilo.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.tvbarthel.apps.adaptilo.server.helpers.MessageDeserializerHelper;
import fr.tvbarthel.apps.adaptilo.server.models.*;
import fr.tvbarthel.apps.adaptilo.server.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.server.models.enums.EventType;
import fr.tvbarthel.apps.adaptilo.server.models.enums.MessageType;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Random;

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
     * @param role
     * @param roomId
     * @return should return true if registration succeeds
     */
    protected abstract boolean registerRoleInRoom(Role role, String roomId);

    public AdaptiloServer(InetSocketAddress address) {
        super(address);
        mParser = initGsonParser();

    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println(TAG + " onOpen - " + conn.toString() + ", " + handshake.toString());
        // Fake a completed connection.
//        final int connectionId = new Random().nextInt();
//        System.out.println(TAG + " give ID -> " + connectionId);
//        conn.send("{type:'CONNECTION_COMPLETED', content:" + connectionId + "}");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(TAG + " onClose - " + conn.toString() + ", " + code + ", " + reason + ", " + remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        //extract basic info for all message
        System.out.println(TAG + " onMessage - " + conn.toString() + ", " + message);
        final NetworkMessage messageReceived = mParser.fromJson(message, NetworkMessage.class);
        final Message messageContent = messageReceived.getMessage();
        final String connectionId = messageReceived.getConnectionId();
        Message answer = null;

        //process each message type
        switch (messageContent.getType()) {
            case REGISTER_CONTROLLER:
                final RegisterControllerRequest request = (RegisterControllerRequest) messageContent.getContent();
                answer = registerController(conn, request);
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
     * process to the registration of a controller
     *
     * @param request registration request from network
     * @return answer which should be send back
     */
    private Message registerController(WebSocket conn, RegisterControllerRequest request) {
        //generate unique external identifier
        final String givenId = generateExternalId(request.getGameName(), request.getGameRoom(), request.getGameRole());
        final Role roleToRegister = new Role(request.getGameRole(), conn, givenId);
        Message answer = null;

        if (registerRoleInRoom(roleToRegister, request.getGameRoom())) {
            //registration completed, prepare server answer
            System.out.println(TAG + " registration completed, game :  " + request.getGameName() + " room : " + request.getGameRole() + " role : " + request.getGameRole() + " id : " + givenId);
            answer = new Message(MessageType.CONNECTION_COMPLETED, givenId);
        } else {
            //registration request mal formed, close connection
            conn.close();
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
        final int seed = new Random().nextInt();
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
