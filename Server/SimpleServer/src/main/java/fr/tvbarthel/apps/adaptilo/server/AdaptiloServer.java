package fr.tvbarthel.apps.adaptilo.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.tvbarthel.apps.adaptilo.server.helpers.MessageDeserializerHelper;
import fr.tvbarthel.apps.adaptilo.server.models.Message;
import fr.tvbarthel.apps.adaptilo.server.models.MessageType;
import fr.tvbarthel.apps.adaptilo.server.models.NetworkMessage;
import fr.tvbarthel.apps.adaptilo.server.models.RegisterControllerRequest;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Random;

/**
 * A very simple server used to test the communication with the Android application.
 */
public class AdaptiloServer extends WebSocketServer {

    private static final String TAG = AdaptiloServer.class.getCanonicalName();

    private Gson mParser;

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
                final RegisterControllerRequest registerControllerRequest =
                        (RegisterControllerRequest) messageContent.getContent();
                final int seed = new Random().nextInt();
                final String givenId = registerControllerRequest.getGameName() +
                        registerControllerRequest.getGameRoom() +
                        registerControllerRequest.getGameRole() +
                        seed;
                System.out.println(TAG + " give ID -> " + givenId);
                answer = new Message(MessageType.CONNECTION_COMPLETED, givenId);
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
