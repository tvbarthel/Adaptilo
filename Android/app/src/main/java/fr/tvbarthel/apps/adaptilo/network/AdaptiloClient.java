package fr.tvbarthel.apps.adaptilo.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

import fr.tvbarthel.apps.adaptilo.helpers.MessageDeserializerHelper;
import fr.tvbarthel.apps.adaptilo.models.io.Message;
import fr.tvbarthel.apps.adaptilo.models.io.ServerRequest;

/**
 * Simple {@link org.java_websocket.client.WebSocketClient} used communicate with the server
 * through{@link fr.tvbarthel.apps.adaptilo.models.io.Message}
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

    /**
     * JSON parser
     */
    private Gson mGsonParser;

    /**
     * identify client on the server
     */
    private String mConnectionId;

    public AdaptiloClient(URI serverURI, Callbacks callbacks) {
        super(serverURI);
        mCallbacks = callbacks;
        mGsonParser = initGsonParser();
    }

    public AdaptiloClient(URI serverUri, Draft draft, Callbacks callbacks) {
        super(serverUri, draft);
        mCallbacks = callbacks;
        mGsonParser = initGsonParser();
    }

    public AdaptiloClient(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout, Callbacks callbacks) {
        super(serverUri, draft, headers, connecttimeout);
        mCallbacks = callbacks;
        mGsonParser = initGsonParser();
    }

    /**
     * Send generic message to the server
     *
     * @param message
     */
    public void send(Message message) {
        final ServerRequest serverRequest = new ServerRequest(
                mConnectionId,
                message
        );
        this.send(mGsonParser.toJson(serverRequest));
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

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "onOpen");
        // Do not call Callback.onOpen() here !
        // The websocket is openned, but we need the connectionId
        // Before the AdaptiloClient can be used.
        mCallbacks.onConfigRequested();
    }

    @Override
    public void onMessage(String message) {
        final Message messageReceived = mGsonParser.fromJson(message, Message.class);
        switch (messageReceived.getType()) {
            case CONNECTION_COMPLETED:
                mConnectionId = (String) messageReceived.getContent();
                Log.d(TAG, "CONNECTION_COMPLETED : " + mConnectionId);
                mCallbacks.onOpen();
                break;
            default:
                Log.d(TAG, "onMessage : " + message);
                mCallbacks.onMessage(messageReceived);
                break;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose : " + code + " remote : " + remote);
        mCallbacks.onClose(code);
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
         * Connection open, wait for a config to complete connection
         */
        public void onConfigRequested();

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
         *
         * @param code error code to adapt behavior to each closing reason if needed
         */
        public void onClose(int code);

        /**
         * Client error
         *
         * @param ex
         */
        public void onError(Exception ex);
    }
}
