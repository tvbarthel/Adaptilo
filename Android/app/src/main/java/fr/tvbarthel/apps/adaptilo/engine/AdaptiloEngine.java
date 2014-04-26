package fr.tvbarthel.apps.adaptilo.engine;

import android.app.Activity;

import java.net.URI;

import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.Message;
import fr.tvbarthel.apps.adaptilo.network.AdaptiloClient;

/**
 * Adaptilo engine used to handle interaction between the controller and the server
 */
public class AdaptiloEngine implements AdaptiloClient.Callbacks {

    /**
     * Engine config from QrCode to reach the right server in the right room with the wished role
     */
    private EngineConfig mEngineConfig;

    /**
     * WebSocket client used to communicate with the server
     */
    private AdaptiloClient mAdaptiloClient;

    /**
     * The current callbacks object for engine event
     */
    private Callbacks mCallbacks;

    /**
     * let the engine know if the WebSocket client is connected
     */
    private boolean mReadyToCommunicate;

    /**
     * Create a new AdaptiloEngine to process userInput.
     *
     * @param activity     holding activity, must implement
     *                     {@link fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine.Callbacks}
     * @param engineConfig should come from a QrCode
     */
    AdaptiloEngine(Activity activity, EngineConfig engineConfig) {

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity which hold engine must implements its Callbacks");
        }

        mCallbacks = (Callbacks) activity;

        mEngineConfig = engineConfig;
        mAdaptiloClient = new AdaptiloClient(URI.create(mEngineConfig.getServerUri()), this);
        mReadyToCommunicate = false;
    }

    /**
     * Start the engine. Should be linked to activity life cycle.
     * <p/>
     * If started in onStart() should stop it in onStop()
     * If started in onCreate() should stop it in onDestroy()
     * <p/>
     * You should usually not start and stop engine during your activity's onResume() and onPause(),
     * because these callbacks occur at every lifecycle transition and you should keep the
     * processing that occurs at these transitions to a minimum
     */
    public void start() {
        mAdaptiloClient.connect();
    }

    /**
     * Stop the engine. Should be linked to activity life cycle.
     * <p/>
     * If started in onStart() should stop it in onStop()
     * If started in onCreate() should stop it in onDestroy()
     * <p/>
     * You should usually not start and stop engine during your activity's onResume() and onPause(),
     * because these callbacks occur at every lifecycle transition and you should keep the
     * processing that occurs at these transitions to a minimum
     */
    public void stop() {
        mAdaptiloClient.close();
    }

    /**
     * Engine will process user input and send message to the WebSocket server
     *
     * @param message
     */
    public void sendUserInput(Message message) {
        if (mReadyToCommunicate) {
            mAdaptiloClient.send(message);
        }
    }

    @Override
    public void onOpen() {
        mReadyToCommunicate = true;
    }

    @Override
    public void onMessage(Message message) {
        switch (message.getType()) {
            case REPLACE_CONTROLLER:
                mCallbacks.onReplaceControllerRequest(1);
                break;
            default:
                mCallbacks.onMessageReceived(message);
                break;
        }
    }

    @Override
    public void onClose() {
        mReadyToCommunicate = false;
    }

    @Override
    public void onError(Exception ex) {

    }

    /**
     * Callbacks interface for Engine event
     */
    public interface Callbacks {
        /**
         * Engine received message for the controller
         *
         * @param message
         */
        public void onMessageReceived(Message message);

        /**
         * Engine received message to replace the current controller
         *
         * @param controllerId
         */
        public void onReplaceControllerRequest(int controllerId);
    }
}
