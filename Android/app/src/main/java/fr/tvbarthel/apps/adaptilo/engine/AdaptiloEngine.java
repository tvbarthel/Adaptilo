package fr.tvbarthel.apps.adaptilo.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;

import java.net.URI;

import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment;
import fr.tvbarthel.apps.adaptilo.fragments.BasicControllerFragment;
import fr.tvbarthel.apps.adaptilo.helpers.SharedPreferencesHelper;
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
     * default duration in milli
     */
    private static final int VIBRATOR_ON_KEY_DURATION = 10;

    /**
     * Context used to start and stop some systems services directly from the server
     */
    private Context mContext;

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
     * Vibrator object used to vibrate
     */
    private Vibrator mVibrator;

    /**
     * is user allowing vibration on key event ?
     */
    private boolean mVibrateOnKeyEvent;

    /**
     * is user allowing vibration on server event ?
     */
    private boolean mVibrateOnServerEvent;

    /**
     * shared preferences listener for vibrator preference
     */
    private SharedPreferences.OnSharedPreferenceChangeListener mVibratorPreferencesListener;

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
     * Initialize engine features which need context. Should be called directly since it's already
     * managed by the {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment}
     *
     * @param context
     */
    public void initEngine(Context context) {
        if (mContext == null) {
            mContext = context;
            initVibrator();
        }
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
        if (mVibrator != null && mVibrateOnKeyEvent) {
            mVibrator.vibrate(VIBRATOR_ON_KEY_DURATION);
        }

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

    @Override
    public void onOpen() {
        mReadyToCommunicate = true;
    }

    @Override
    public void onMessage(Message message) {
        switch (message.getType()) {
            case REPLACE_CONTROLLER:
                //TODO process message to know which controller to use
                mCallbacks.onReplaceControllerRequest(new BasicControllerFragment());
                break;
            case VIBRATOR:
                if (vibrateOnServerEventIsAllowed()) {
                    mVibrator.vibrate((Long) message.getContent());
                }
                break;
            case VIBRATOR_PATTERN:
                if (vibrateOnServerEventIsAllowed()) {
                    mVibrator.vibrate((long[]) message.getContent(), -1);
                }
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
     * Retrieve the vibrator as well as user preferences regarding to vibrator policy
     * <p/>
     * Since vibrator preference can pe change in select menu of controller, a listener should
     * be register in order to adapt vibrator behaviour to the most recent user preference
     */
    private void initVibrator() {
        SharedPreferences prefs = mContext.getSharedPreferences(
                SharedPreferencesHelper.VIBRATOR_PREFERENCE, Context.MODE_PRIVATE);

        mVibrateOnKeyEvent =
                prefs.getBoolean(SharedPreferencesHelper.KEY_VIBRATE_ON_KEY_EVENT,
                        SharedPreferencesHelper.DEFAULT_VIBRATE_ON_KEY_EVENT);

        mVibrateOnServerEvent =
                prefs.getBoolean(SharedPreferencesHelper.KEY_VIBRATE_ON_SERVER_EVENT,
                        SharedPreferencesHelper.DEFAULT_VIBRATE_ON_SERVER_EVENT);

        mVibratorPreferencesListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (SharedPreferencesHelper.KEY_VIBRATE_ON_KEY_EVENT.equals(key)) {
                    mVibrateOnKeyEvent = sharedPreferences.getBoolean(
                            SharedPreferencesHelper.KEY_VIBRATE_ON_KEY_EVENT,
                            SharedPreferencesHelper.DEFAULT_VIBRATE_ON_KEY_EVENT);

                } else if (SharedPreferencesHelper.KEY_VIBRATE_ON_SERVER_EVENT.equals(key)) {
                    mVibrateOnServerEvent = sharedPreferences.getBoolean(
                            SharedPreferencesHelper.KEY_VIBRATE_ON_SERVER_EVENT,
                            SharedPreferencesHelper.DEFAULT_VIBRATE_ON_SERVER_EVENT);
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(mVibratorPreferencesListener);

        // retrieve vibrator
        if (mVibrateOnServerEvent || mVibrateOnServerEvent) {
            mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    /**
     * return true if vibration could be perform on server event
     *
     * @return
     */
    private boolean vibrateOnServerEventIsAllowed() {
        return (mVibrator != null && mVibrateOnServerEvent);
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
         * @param adaptiloControllerFragment new controller to use
         */
        public void onReplaceControllerRequest(AdaptiloControllerFragment adaptiloControllerFragment);
    }
}
