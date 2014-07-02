package fr.tvbarthel.apps.adaptilo.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine;
import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.helpers.QrCodeHelper;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.UserEvent;
import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;
import fr.tvbarthel.apps.adaptilo.models.enums.MessageType;
import fr.tvbarthel.apps.adaptilo.models.io.Message;
import fr.tvbarthel.apps.adaptilo.ui.activities.BasicControllerCaptureActivity;

/**
 * A simple {@link android.support.v4.app.Fragment} that represents a controller.
 * <p/>
 * The controller encapsulate all basics message processing such as game_start,
 * gamer_server_unreachable.
 * <p/>
 * All controller must at least display select and start buttons for consistency in basic features
 * <p/>
 * This abstract controller encapsulate select and start button behavior. Implementations only
 * handle visual callback and dialog customization. Therefor any implementation of
 * {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloControllerFragment} must provide an
 * implementation of its {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloSelectDialogFragment}
 * and {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloStartDialogFragment}
 * <p/>
 * In addition, this abstract controller encapsulate the behavior for all buttons of your controller.
 * Simply implements {@link #getControllerKeys()} to map your button ids with the wished EventType.
 */
abstract public class AdaptiloControllerFragment extends Fragment implements AdaptiloEngine.Callbacks {

    /**
     * Logcat
     */
    private static final String TAG = AdaptiloControllerFragment.class.getName();

    /**
     * Adaptilo core part
     */
    protected AdaptiloEngine mAdaptiloEngine;

    /**
     * The current callbacks object for controller event
     */
    protected Callbacks mCallbacks = sDummyCallbacks;

    /**
     * dummy callbacks use when fragment isn't attached
     */
    private static final Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onReplaceControllerRequest(AdaptiloControllerFragment controllerFragment) {

        }
    };

    /**
     * Sparse array used to map buttonId and EventType for each buttons on the controller.
     */
    private final SparseArray<EventType> mKeys = getControllerKeys();

    /**
     * Used to map your button ids with the wished EventType.
     * <p/>
     * Basically, you should put all EventType as value with button res id as key.
     * <p/>
     * For instance : put(R.id.basic_controller_btn_a,EventType.KEY_A)
     * <p/>
     * Before define your own EventType, please refer to
     * {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType} to check if the event you are
     * trying to define isn't already listed.
     *
     * @return an array of EventType with associated button res id as key.
     */
    abstract protected SparseArray<EventType> getControllerKeys();

    /**
     * Used to retrieve and initialize select button in controller implementation.
     *
     * @return select button id in controller implementation view.
     */
    abstract protected int getSelectButtonId();

    /**
     * Used to retrieve and initialize startPressed button in controller implementation.
     *
     * @return startPressed button id in controller implementation view.
     */
    abstract protected int getStartButtonId();

    /**
     * Called to retrieve implementations of
     * {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloSelectDialogFragment} for the current
     * {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloControllerFragment} implementation.
     *
     * @return Select dialog fragment.
     */
    abstract protected AdaptiloSelectDialogFragment getSelectDialogFragment();

    /**
     * Called to retrieve implementations of
     * {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloStartDialogFragment} for the current
     * {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloControllerFragment} implementation.
     *
     * @return Start dialog fragment.
     */
    abstract protected AdaptiloStartDialogFragment getStartDialogFragment();

    /**
     * Called when game server is unreachable.
     * <p/>
     * Should be used to warn user.
     */
    abstract protected void onGameServerUnreachable();

    /**
     * Called when the controller is connected to the game server.
     * <p/>
     * Should be used to inform user that he can start to play.
     */
    abstract protected void onGameStart();

    /**
     * Called when connection was closed by the remote server.
     *
     * @param reason closing code in order to adapt visual callback if needed.
     */
    abstract protected void onConnectionClose(int reason);

    /**
     * Callback when a user event is send. Used when a specific behavior should be processed for a
     * given user event.
     *
     * @param userEvent user event send
     */
    abstract protected void onUserEventSend(UserEvent userEvent);

    /**
     * Called when the select dialog is displayed.
     * <p/>
     * Could be used to perform any visual callback.
     */
    abstract protected void onSelectDialogShown();

    /**
     * Called when the start dialog is displayed.
     * <p/>
     * Could be used to perform any visual callback.
     */
    abstract protected void onStartDialogShown();

    /**
     * QrCode scanner has been canceled.
     */
    abstract protected void onScannerCanceled();


    public AdaptiloControllerFragment() {
        mAdaptiloEngine = new AdaptiloEngine(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity holding AdpatiloControllerFragment must implements Callbacks");
        }

        mCallbacks = (Callbacks) activity;
        mAdaptiloEngine.initEngine(getActivity().getApplicationContext());
    }


    @Override
    public void onResume() {
        super.onResume();
        mAdaptiloEngine.resume();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize select button with values matching implementation needs.
        final Button selectButton = (Button) view.findViewById(getSelectButtonId());
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdaptiloEngine.pause();
                getSelectDialogFragment().show(getFragmentManager(), "select_dialog_fragment");
                onSelectDialogShown();
            }
        });

        //initialize startPressed button with values matching implementation needs.
        final Button startButton = (Button) view.findViewById(getStartButtonId());
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPressed();
                onStartDialogShown();
            }
        });

        initKeyButtons(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdaptiloEngine.pause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdaptiloEngine.stop();
    }

    @Override
    public void onMessageReceived(Message message) {
        switch (message.getType()) {
            case ENGINE_READY:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onGameStart();
                    }
                });
                break;
        }
    }

    @Override
    public void onErrorReceived(Exception ex) {
        if (ex.getMessage().contains("ENETUNREACH")) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onGameServerUnreachable();
                }
            });
        }
    }

    @Override
    public void onConnectionClosed(final int closeCode) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onConnectionClose(closeCode);
            }
        });
    }

    @Override
    public void onReplaceControllerRequest(AdaptiloControllerFragment adaptiloControllerFragment) {
        mCallbacks.onReplaceControllerRequest(adaptiloControllerFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QrCodeHelper.REQUEST_CODE) {
            handleQrCodeResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Called when matching select dialog as been closed by the user.
     *
     * @param optionSaved true when options has been saved.
     */
    public void onSelectDialogClosed(boolean optionSaved) {
        mAdaptiloEngine.resume();
    }

    /**
     * Called when matching startPressed dialog as been closed by the user.
     *
     * @param which identifier of clicked button
     *              {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloStartDialogFragment#BUTTON_RESUME}
     *              {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloStartDialogFragment#BUTTON_DISCONNECT}
     *              {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloStartDialogFragment#BUTTON_NEW_GAME}
     *              or 0 if dismissed or canceled.
     */
    public void onStartDialogClosed(int which) {
        mAdaptiloEngine.resume();
        switch (which) {
            case AdaptiloStartDialogFragment.BUTTON_DISCONNECT:
                disconnect();
                break;
            case AdaptiloStartDialogFragment.BUTTON_NEW_GAME:
                loadNewGame();
        }
    }

    /**
     * load game config into the controller after scan success
     *
     * @param config
     */
    protected void onScannerSuccess(EngineConfig config) {
        mAdaptiloEngine.setEngineConfig(config);
        mAdaptiloEngine.start();
        Log.d(TAG, "onScannerSuccess : " + config.toString());
    }

    /**
     * scanner error
     *
     * @param ex
     */
    protected void onScannerError(QrCodeException ex) {
        Log.d(TAG, "onScannerError : " + ex.getMessage());
    }

    /**
     * Used to process QrCode scanner result when {@link #onActivityResult} is called.
     *
     * @param requestCode - The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode  - The integer result code returned by the child activity through its setResult().
     * @param data        - An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    protected void handleQrCodeResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            onScannerCanceled();
        } else {
            EngineConfig config = null;
            try {
                config = QrCodeHelper.verifyFromActivityResult(requestCode, resultCode, data);
            } catch (QrCodeException e) {
                onScannerError(e);
            } finally {
                if (config != null) {
                    onScannerSuccess(config);
                }
            }
        }
    }

    /**
     * Used to startPressed Qr code scanner in order to load a game into your controller.
     * <p/>
     * Or prompt a dialog : Resume | Disconnect | New Game
     * <p/>
     * Called when "startPressed button" is pressed
     */
    private void startPressed() {
        if (mAdaptiloEngine.isReadToCommunicate()) {

            //controller already running for a game. Let current controller implementation ask the
            // to the user if he want to load a new game, disconnect from the current one or resume.
            getStartDialogFragment().show(getFragmentManager(), "start_dialog_fragment");
            mAdaptiloEngine.pause();

        } else {

            //current controller isn't running, startPressed scanner to load a game
            loadGame();
        }
    }

    /**
     * Disconnect controller from current game.
     */
    private void disconnect() {
        mAdaptiloEngine.stop();
    }

    /**
     * Start load game flow.
     */
    private void loadGame() {
        //startPressed scanner to load the new game
        QrCodeHelper.initiateQrCodeScan(AdaptiloControllerFragment.this, BasicControllerCaptureActivity.class,
                getString(R.string.qr_code_scanner_prompt));
    }

    /**
     * Load a new game when another one is already loaded.
     */
    private void loadNewGame() {
        disconnect();
        loadGame();
    }

    /**
     * Init the key buttons.
     *
     * @param controllerView the {@link android.view.View} for the controller's UI.
     */
    private void initKeyButtons(View controllerView) {
        final View.OnTouchListener keyListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final UserEvent userEvent = extractUserEvent(v, event);
                if (userEvent != null) {
                    mAdaptiloEngine.sendUserInput(new Message(MessageType.USER_INPUT, userEvent));
                    onUserEventSend(userEvent);
                }
                return false;
            }
        };

        //iterate through all button ids set in getControllerKeys() to attached the keyListener
        for (int i = 0; i < mKeys.size(); i++) {
            final Button button = (Button) controllerView.findViewById(mKeys.keyAt(i));
            button.setOnTouchListener(keyListener);
        }
    }

    /**
     * Extract a {@link fr.tvbarthel.apps.adaptilo.models.UserEvent}
     *
     * @param view        the {@link android.view.View} the touch event has been dispatched to.
     * @param motionEvent The MotionEvent object containing full information about the event.
     * @return
     */
    private UserEvent extractUserEvent(View view, MotionEvent motionEvent) {
        final EventAction eventAction = extractEventAction(motionEvent);
        if (eventAction == null) return null;
        final EventType eventType = extractEventType(view);
        if (eventType == null) return null;
        return new UserEvent(eventType, eventAction);
    }

    /**
     * Extract the {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction} from a {@link android.view.MotionEvent}.
     *
     * @param motionEvent the {@link android.view.MotionEvent} from which the {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction} will be extracted.
     * @return the extracted {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction}
     */
    private EventAction extractEventAction(MotionEvent motionEvent) {
        final int motionAction = motionEvent.getActionMasked();
        EventAction eventAction = null;
        if (motionAction == MotionEvent.ACTION_DOWN) {
            eventAction = EventAction.ACTION_KEY_DOWN;
        } else if (motionAction == MotionEvent.ACTION_UP) {
            eventAction = EventAction.ACTION_KEY_UP;
        }
        return eventAction;
    }

    /**
     * Extract the {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType} associated with a {@link android.view.View}.
     *
     * @param view the {@link android.view.View} the event has been dispatched to.
     * @return the extracted {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType}
     */
    protected EventType extractEventType(View view) {
        final int viewId = view.getId();

        //iterate through buttons res id to find which button was pressed.
        for (int i = 0; i < mKeys.size(); i++) {
            if (viewId == mKeys.keyAt(i)) {

                //return event linked to the button, as defined by getControllerKeys();
                return mKeys.valueAt(i);
            }
        }
        return null;
    }


    /**
     * Callbacks
     */
    public interface Callbacks {
        /**
         * change the current controller
         *
         * @param controllerFragment new controller to display
         */
        public void onReplaceControllerRequest(AdaptiloControllerFragment controllerFragment);
    }
}
