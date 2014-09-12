package fr.tvbarthel.apps.adaptilo.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;

import org.java_websocket.framing.CloseFrame;

import java.net.URI;

import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment;
import fr.tvbarthel.apps.adaptilo.fragments.BasicControllerFragment;
import fr.tvbarthel.apps.adaptilo.fragments.DrumsControllerFragment;
import fr.tvbarthel.apps.adaptilo.helpers.SensorListenerHelper;
import fr.tvbarthel.apps.adaptilo.helpers.SharedPreferencesHelper;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.Event;
import fr.tvbarthel.apps.adaptilo.models.SensorEvent;
import fr.tvbarthel.apps.adaptilo.models.UserEvent;
import fr.tvbarthel.apps.adaptilo.models.enums.ControllerType;
import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;
import fr.tvbarthel.apps.adaptilo.models.enums.MessageType;
import fr.tvbarthel.apps.adaptilo.models.io.ClosingCode;
import fr.tvbarthel.apps.adaptilo.models.io.Message;
import fr.tvbarthel.apps.adaptilo.models.io.RegisterRoleRequest;
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
    private static final int VIBRATOR_ON_KEY_DURATION = 15;

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
     * sensor manager
     */
    private SensorManager mSensorManager;

    /**
     * accelerometer
     */
    private Sensor mAccelerometer;

    /**
     * listener for shake event
     */
    private ShakeListener mShakeListener;

    /**
     * Engine used to catch event when user claps his hands.
     */
    private ClapEngine mClapEngine;


    /**
     * Create a new AdaptiloEngine to process userInput.
     *
     * @param callbacks the {@link fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine.Callbacks} to be used.
     */
    public AdaptiloEngine(Callbacks callbacks) {
        mCallbacks = callbacks;
        mReadyToCommunicate = false;
    }

    @Override
    public void onConfigRequested() {
        //send the current loaded config
        mAdaptiloClient.send(new Message(MessageType.REGISTER_ROLE_REQUEST, new RegisterRoleRequest(
                mEngineConfig.getGameName(),
                mEngineConfig.getGameRoom(),
                mEngineConfig.getUserRole(),
                mEngineConfig.shouldReplace()
        )));
    }

    @Override
    public void onOpen() {
        mReadyToCommunicate = true;
        mCallbacks.onMessageReceived(new Message(MessageType.ENGINE_READY, null));
    }

    @Override
    public void onMessage(Message message) {
        switch (message.getType()) {
            case REPLACE_CONTROLLER:
                ControllerType type = ((ControllerType) message.getContent());
                AdaptiloControllerFragment f = retrieveControllerFragment(type);
                if (f != null) {
                    mCallbacks.onReplaceControllerRequest(f);
                }
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

            case SENSOR:
                processSensorEvent((Event) message.getContent());
                break;
            default:
                mCallbacks.onMessageReceived(message);
                break;
        }
    }

    @Override
    public void onClose(int code) {
        mReadyToCommunicate = false;
        String close = "Connection closed : ";
        switch (code) {
            case ClosingCode.REGISTRATION_REQUESTED_GAME_NAME_UNKNOWN:
                Log.e(TAG, close + "REGISTRATION_REQUESTED_GAME_NAME_UNKNOWN");
                break;
            case ClosingCode.REGISTRATION_NO_ROOM_CREATED:
                Log.e(TAG, close + "REGISTRATION_NO_ROOM_CREATED");
                break;
            case ClosingCode.REGISTRATION_REQUESTED_ROOM_UNKNOW:
                Log.e(TAG, close + "REGISTRATION_REQUESTED_ROOM_UNKNOW");
                break;
            case ClosingCode.REGISTRATION_REQUESTED_ROLE_UNKNOWN:
                Log.e(TAG, close + "REGISTRATION_REQUESTED_ROLE_UNKNOWN");
                break;
            case ClosingCode.REGISTRATION_REQUESTED_ROOM_IS_FULL:
                Log.e(TAG, close + "REGISTRATION_REQUESTED_ROOM_IS_FULL");
                break;
            case ClosingCode.DISCONNECTION_DUE_TO_ROLE_REPLACEMENT:
                Log.e(TAG, close + "DISCONNECTION_DUE_TO_ROLE_REPLACEMENT");
                break;
            case CloseFrame.NORMAL:
                Log.e(TAG, close + "CloseFrame.NORMAL");
                break;
            default:
                Log.e(TAG, close + "Error code unknown.");
                break;
        }

        //send close event to the controller. Visual callbacks should be display to warn the user.

        mCallbacks.onConnectionClosed(code);
    }


    @Override
    public void onError(Exception ex) {
        mCallbacks.onErrorReceived(ex);
    }

    /**
     * Initialize engine features which need context. Should be called directly since it's already
     * managed by the {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment}
     *
     * @param context context holding the engine.
     */
    public void initEngine(Context context) {
        if (mContext == null) {
            mContext = context;
            initVibrator();

            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
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
        if (mEngineConfig != null) {
            // convert android.net.Uri to java.net.URI
            URI serverUri = URI.create(mEngineConfig.getServerUri().toString());
            mAdaptiloClient = new AdaptiloClient(
                    serverUri,
                    this
            );
            mAdaptiloClient.connect();
        }

        initClapEngine();
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
        if (mAdaptiloClient != null) {

            mAdaptiloClient.send(
                    new Message(MessageType.UNREGISTER_ROLE_REQUEST, null)
            );

            mAdaptiloClient = null;
            mEngineConfig = null;
        }

        if (mClapEngine != null) {
            mClapEngine.stop();
        }
    }

    /**
     * pause the engine
     */
    public void pause() {
        if (mAdaptiloClient != null && mReadyToCommunicate) {
            mAdaptiloClient.send(new Message(MessageType.PAUSE, null));
        }

        //shake detection
        if (mShakeListener != null) {
            shakeDetection(SensorListenerHelper.PAUSE);
        }

        //clap engine
        if (mClapEngine != null) {
            mClapEngine.pause();
        }
    }

    /**
     * resume the engine
     */
    public void resume() {
        if (mAdaptiloClient != null && mReadyToCommunicate) {
            mAdaptiloClient.send(new Message(MessageType.RESUME, null));
        }

        //shake detection
        if (mShakeListener != null) {
            shakeDetection(SensorListenerHelper.RESUME);
        }

        //clap engine
        if (mClapEngine != null) {
            mClapEngine.resume();
        }
    }

    /**
     * Engine will process user input and send message to the WebSocket server
     *
     * @param message content send to the websocket server.
     */
    public void sendUserInput(Message message) {
        vibrateOnUserEvent((UserEvent) message.getContent());
        if (mReadyToCommunicate) {
            mAdaptiloClient.send(message);
        }
    }

    /**
     * Load an engine config. Should be retrieved from a QrCode.
     *
     * @param config data used to define a game/server configuration.
     */
    public void setEngineConfig(EngineConfig config) {
        mEngineConfig = config;
    }

    /**
     * Retrieve loaded engine config.
     *
     * @return current game config or null if no game has been loaded successfully.
     */
    public EngineConfig getEngineConfig() {
        return mEngineConfig;
    }

    /**
     * Use to know if engine is ready to communicate with server.
     *
     * @return true if connected to a game server.
     */
    public boolean isReadToCommunicate() {
        return mReadyToCommunicate;
    }

    /**
     * handle message from server regarding sensor
     *
     * @param content
     */
    private void processSensorEvent(Event content) {
        final EventAction action = content.getEventAction();
        switch (content.getEventType()) {
            case SHAKER:
                if (action == EventAction.ACTION_ENABLE) {
                    shakeDetection(SensorListenerHelper.START);
                } else if (action == EventAction.ACTION_DISABLE) {
                    shakeDetection(SensorListenerHelper.STOP);
                }
                break;
            case CLAP:
                if (action == EventAction.ACTION_ENABLE) {
                    if (mClapEngine.isPaused()) {
                        mClapEngine.resume();
                    } else {
                        mClapEngine.start();
                    }
                } else if (action == EventAction.ACTION_DISABLE) {
                    mClapEngine.stop();
                }
                break;
            default:
                break;
        }
    }

    /**
     * manage shake detection
     *
     * @param state {@link fr.tvbarthel.apps.adaptilo.helpers.SensorListenerHelper}
     */
    private void shakeDetection(int state) {
        switch (state) {
            case SensorListenerHelper.START:
                initAccelerometer();
                if (mShakeListener == null) {
                    mShakeListener = new ShakeListener() {
                        @Override
                        public void onShakeDetected() {
                            //send shake detected
                            if (mReadyToCommunicate) {
                                final SensorEvent sensorEvent
                                        = new SensorEvent(EventType.SHAKER, EventAction.ACTION_HAPPENED, 0);
                                final Message message = new Message(MessageType.SENSOR, sensorEvent);
                                mAdaptiloClient.send(message);
                            }
                        }

                        @Override
                        public void onShaking(double speed) {
                            //send shaking event
                            if (mReadyToCommunicate) {
                                final SensorEvent sensorEvent
                                        = new SensorEvent(EventType.SHAKER, EventAction.ACTION_DOING, speed);
                                final Message message = new Message(MessageType.SENSOR, sensorEvent);
                                mAdaptiloClient.send(message);
                            }
                        }
                    };
                }
                mSensorManager.registerListener(mShakeListener,
                        mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case SensorListenerHelper.RESUME:
                mSensorManager.registerListener(mShakeListener,
                        mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case SensorListenerHelper.PAUSE:
                mSensorManager.unregisterListener(mShakeListener, mAccelerometer);
                break;
            case SensorListenerHelper.STOP:
                mSensorManager.unregisterListener(mShakeListener, mAccelerometer);
                mShakeListener = null;
                break;
            default:
                break;
        }
    }

    /**
     * initialize accelerometer
     */
    private void initAccelerometer() {
        if (mAccelerometer == null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    /**
     * Initialize engine used to detect clap event.
     */
    private void initClapEngine() {
        mClapEngine = new ClapEngine(mContext, new ClapListener() {
            @Override
            public void onClapDetected() {
                //send clap event
                if (mReadyToCommunicate) {
                    final SensorEvent sensorEvent
                            = new SensorEvent(EventType.CLAP, EventAction.ACTION_HAPPENED, 0);
                    final Message message = new Message(MessageType.SENSOR, sensorEvent);
                    mAdaptiloClient.send(message);
                }
            }
        });
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

        mVibrateOnKeyEvent
                = prefs.getBoolean(SharedPreferencesHelper.KEY_VIBRATE_ON_KEY_EVENT,
                SharedPreferencesHelper.DEFAULT_VIBRATE_ON_KEY_EVENT);

        mVibrateOnServerEvent
                = prefs.getBoolean(SharedPreferencesHelper.KEY_VIBRATE_ON_SERVER_EVENT,
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
        if (mVibrateOnServerEvent || mVibrateOnKeyEvent) {
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
     * vibrate according to user input
     *
     * @param userEvent
     */
    private void vibrateOnUserEvent(UserEvent userEvent) {
        if (mVibrator != null && mVibrateOnKeyEvent) {
            switch (userEvent.getEventAction()) {
                case ACTION_KEY_DOWN:
                    mVibrator.vibrate(VIBRATOR_ON_KEY_DURATION);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Used to match a {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment}
     * according to an {@link fr.tvbarthel.apps.adaptilo.models.enums.ControllerType}
     *
     * @param type given controller type
     * @return matching controller or null
     */
    private AdaptiloControllerFragment retrieveControllerFragment(ControllerType type) {
        AdaptiloControllerFragment controller = null;
        switch (type) {
            case BASIC_CONTROLLER:
                controller = new BasicControllerFragment();
                break;
            case DRUMS_CONTROLLER:
                controller = new DrumsControllerFragment();
                break;
            default:
                break;
        }
        return controller;
    }

    /**
     * Callbacks interface for Engine event
     */
    public interface Callbacks {
        /**
         * Engine received message for the controller
         * <p/>
         * Any visual callback linked to a given message must be run on the ui thread.
         *
         * @param message data send by the server.
         */
        public void onMessageReceived(Message message);

        /**
         * Called when engine an error.
         * <p/>
         * Any visual callback linked to a given error must be run on the ui thread.
         *
         * @param ex error exception
         */
        public void onErrorReceived(Exception ex);

        /**
         * Called when the connection is closed by the remote server.
         *
         * @param closeCode code which identify the closing reason
         *                  {@link fr.tvbarthel.apps.adaptilo.models.io.ClosingCode}
         */
        public void onConnectionClosed(int closeCode);

        /**
         * Engine received message to replace the current controller
         *
         * @param adaptiloControllerFragment new controller to use
         */
        public void onReplaceControllerRequest(AdaptiloControllerFragment adaptiloControllerFragment);
    }
}
