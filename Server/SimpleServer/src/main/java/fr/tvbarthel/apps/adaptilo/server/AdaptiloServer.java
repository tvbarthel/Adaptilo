package fr.tvbarthel.apps.adaptilo.server;

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

    public AdaptiloServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println(TAG + " onOpen - " + conn.toString() + ", " + handshake.toString());
        // Fake a completed connection.
        final int connectionId = new Random().nextInt();
        System.out.println(TAG + " give ID -> " + connectionId);
        conn.send("{type:'CONNECTION_COMPLETED', content:" + connectionId + "}");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(TAG + " onClose - " + conn.toString() + ", " + code + ", " + reason + ", " + remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(TAG + " onMessage - " + conn.toString() + ", " + message);
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
}
