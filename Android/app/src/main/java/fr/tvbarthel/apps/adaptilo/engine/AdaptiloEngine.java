package fr.tvbarthel.apps.adaptilo.engine;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.net.URI;

import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.Message;
import fr.tvbarthel.apps.adaptilo.network.AdaptiloClient;

/**
 * Adaptilo engine used to handle interaction between the controller and the server
 */
public class AdaptiloEngine implements AdaptiloClient.Callbacks {

    /**
     * LogCat
     */
    private static final String TAG = AdaptiloEngine.class.getName();

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
     * @param callbacks the {@link fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine.Callbacks} to be used.
     */
    public AdaptiloEngine(Callbacks callbacks) {
        mCallbacks = callbacks;
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
        if (mEngineConfig != null && mAdaptiloClient != null) {
            mAdaptiloClient.connect();
        }
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

    /**
     * Load an engine config. Should be retrieved from a QrCode.
     *
     * @param config
     */
    public void setEngineConfig(EngineConfig config) {
        mEngineConfig = config;
        mAdaptiloClient = new AdaptiloClient(URI.create(mEngineConfig.getServerUri()), this);
    }

    /**
     * start QrCode scanner to load a game config
     */
    public void loadGame(Activity activity) {
        IntentIntegrator.initiateScan(activity);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public EngineConfig parseLoadGameResult(int requestCode, int resultCode, Intent data) {
        EngineConfig config = null;

        IntentResult scanResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null && resultCode == Activity.RESULT_OK) {
            //TODO check if QrCode content is an engine config
            Log.d(TAG, "game config retrieved : " + scanResult.getContents());
            config = new EngineConfig();
        }

        return config;
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
