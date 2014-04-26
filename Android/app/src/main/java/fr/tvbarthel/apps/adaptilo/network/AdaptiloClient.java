package fr.tvbarthel.apps.adaptilo.network;

import android.util.Log;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

import fr.tvbarthel.apps.adaptilo.models.Message;

/**
 * Simple {@link org.java_websocket.client.WebSocketClient} used communicate with the server
 * through{@link fr.tvbarthel.apps.adaptilo.models.Message}
 */
public class AdaptiloClient extends WebSocketClient {

    /**
     * Log cat
     */
    private static final String TAG = AdaptiloClient.class.getName();

    /**
     * The current callbacks object for AdaptiloClient events
     */
    private Callbacks mCallbacks;

    private Gson mGsonParser;

    public AdaptiloClient(URI serverURI, Callbacks callbacks) {
        super(serverURI);
        mCallbacks = callbacks;
        mGsonParser = new Gson();
    }

    public AdaptiloClient(URI serverUri, Draft draft, Callbacks callbacks) {
        super(serverUri, draft);
        mCallbacks = callbacks;
        mGsonParser = new Gson();
    }

    public AdaptiloClient(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout, Callbacks callbacks) {
        super(serverUri, draft, headers, connecttimeout);
        mCallbacks = callbacks;
        mGsonParser = new Gson();
    }

    /**
     * Send generic message to the server
     *
     * @param message
     */
    public void send(Message message) {
        final String stringMessage = mGsonParser.toJson(message);
        this.send(message);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "onOpen");
        mCallbacks.onOpen();
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage : " + message);
        final Message jsonMessage = mGsonParser.fromJson(message, Message.class);
        mCallbacks.onMessage(jsonMessage);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose : " + reason);
        mCallbacks.onClose();
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError : " + ex.getMessage());
        mCallbacks.onError(ex);
    }

    /**
     * Callbacks interface
     */
    public interface Callbacks {
        /**
         * Client ready to send message
         */
        public void onOpen();

        /**
         * Client received message from the server
         *
         * @param message
         */
        public void onMessage(Message message);

        /**
         * Client disconnected
         */
        public void onClose();

        /**
         * Client error
         *
         * @param ex
         */
        public void onError(Exception ex);
    }
}
