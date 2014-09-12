package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.UserEvent;
import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;
import fr.tvbarthel.apps.adaptilo.models.enums.MessageType;
import fr.tvbarthel.apps.adaptilo.models.io.Message;

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
 * {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment} must provide an
 * implementation of its {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloSelectDialogFragment}
 * and {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment}
 * <p/>
 * In addition, this abstract controller encapsulate the behavior for all buttons of your controller.
 * Simply implements {@link #getControllerKeys()} to map your button ids with the wished EventType.
 */
public abstract class AdaptiloControllerFragment extends Fragment {

    /**
     * Bundle key used to set the flag start to replace.
     * This means that the current controller has been started to replace a previous one.
     */
    public static final String BUNDLE_FLAG_START_TO_REPLACE = "bundle_key_start_to_replace_controller";

    /**
     * Bundle key used to set the current game name.
     * Used when controller is start to replace a previous one.
     */
    public static final String BUNDLE_CURRENT_GAME_NAME = "bundle_key_game_name";

    /**
     * Logcat
     */
    private static final String TAG = AdaptiloControllerFragment.class.getName();

    /**
     * dummy callbacks use when fragment isn't attached
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {

        @Override
        public void onSendUserInputRequested(Message userInputMessage) {

        }

        @Override
        public void onStartDialogRequest() {

        }

        @Override
        public void onSelectDialogRequest() {

        }
    };

    /**
     * The current callbacks object for controller event
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * Set to true when the current controller has been started to replace a previous one.
     */
    private boolean mIsStartToReplaceController;


    /**
     * Sparse array used to map buttonId and EventType for each buttons on the controller.
     */
    private final SparseArray<EventType> mKeys = getControllerKeys();

    /**
     * Default constructor.
     */
    public AdaptiloControllerFragment() {
    }

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
    protected abstract SparseArray<EventType> getControllerKeys();

    /**
     * Used to retrieve and initialize select button in controller implementation.
     *
     * @return select button id in controller implementation view.
     */
    protected abstract int getSelectButtonId();

    /**
     * Used to retrieve and initialize startPressed button in controller implementation.
     *
     * @return startPressed button id in controller implementation view.
     */
    protected abstract int getStartButtonId();

    /**
     * Called to retrieve implementations of
     * {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloSelectDialogFragment} for the current
     * {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment} implementation.
     *
     * @return Select dialog fragment.
     */
    protected abstract AdaptiloSelectDialogFragment getSelectDialogFragment();

    /**
     * Called to retrieve implementations of
     * {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment} for the current
     * {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment} implementation.
     *
     * @return Start dialog fragment.
     */
    protected abstract AdaptiloStartDialogFragment getStartDialogFragment();

    /**
     * Called when game server is unreachable.
     * <p/>
     * Should be used to warn user.
     */
    public abstract void onGameServerUnreachable();

    /**
     * Called when the controller is connected to the game server.
     * <p/>
     * Should be used to inform user that he can start to play.
     *
     * @param gameName name of the current game.
     */
    public abstract void onGameStart(String gameName);

    /**
     * Called when the controller received a message from the remote server.
     * <p/>
     * Allow controller implementation to define any specific behavior for each message event.
     *
     * @param message event send by the server
     */
    public abstract void onMessageReceived(Message message);

    /**
     * Called when the controller received an error from the remote server.
     * <p/>
     * Allow controller implementation to define any specific behavior/visual callback for each
     * error received.
     *
     * @param ex error data send by the remote server.
     */
    public abstract void onErrorReceived(Exception ex);

    /**
     * Called when connection was closed by the remote server.
     *
     * @param reason closing code in order to adapt visual callback if needed.
     */
    public abstract void onConnectionClosed(int reason);

    /**
     * Callback when a user event is send. Used when a specific behavior should be processed for a
     * given user event.
     *
     * @param userEvent user event send
     */
    protected abstract void onUserEventSend(UserEvent userEvent);

    /**
     * Called when the select dialog is displayed.
     * <p/>
     * Could be used to perform any visual callback.
     */
    protected abstract void onSelectDialogShown();

    /**
     * Called when matching select dialog as been closed by the user.
     *
     * @param optionSaved true when options has been saved.
     */
    public abstract void onSelectDialogClosed(boolean optionSaved);

    /**
     * Called when the start dialog is displayed.
     * <p/>
     * Could be used to perform any visual callback.
     */
    protected abstract void onStartDialogShown();

    /**
     * Called when matching startPressed dialog as been closed by the user.
     *
     * @param which identifier of clicked button
     *              {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment#BUTTON_RESUME}
     *              {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment#BUTTON_DISCONNECT}
     *              {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment#BUTTON_NEW_GAME}
     *              or 0 if dismissed or canceled.
     */
    public abstract void onStartDialogClosed(int which);


    /**
     * Game config was loaded into the engine after scan success
     *
     * @param config config loaded in game engine
     */
    public abstract void onScannerSuccess(EngineConfig config);

    /**
     * Scanner error
     *
     * @param ex Scanner exception.
     */
    public abstract void onScannerError(QrCodeException ex);

    /**
     * QrCode scanner has been canceled.
     */
    public abstract void onScannerCanceled();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity holding AdpatiloControllerFragment must implements Callbacks");
        }

        mCallbacks = (Callbacks) activity;

        Bundle args = getArguments();
        if (args != null && args.containsKey(BUNDLE_FLAG_START_TO_REPLACE)) {
            mIsStartToReplaceController = args.getBoolean(BUNDLE_FLAG_START_TO_REPLACE, false);
        } else {
            mIsStartToReplaceController = false;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mIsStartToReplaceController == true) {
            Bundle args = getArguments();
            String gameName;
            if (args != null && args.containsKey(BUNDLE_CURRENT_GAME_NAME)) {
                gameName = args.getString(BUNDLE_CURRENT_GAME_NAME);
            } else {
                gameName = "";
            }
            this.onGameStart(gameName);
            mIsStartToReplaceController = false;
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize select button with values matching implementation needs.
        final Button selectButton = (Button) view.findViewById(getSelectButtonId());
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onSelectDialogRequest();
            }
        });

        //initialize startPressed button with values matching implementation needs.
        final Button startButton = (Button) view.findViewById(getStartButtonId());
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onStartDialogRequest();
            }
        });

        initKeyButtons(view);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }


    /**
     * Called by the activity when controller should display the start dialog
     */
    public final void displayStartDialog() {

        //retrieve specific start dialog from controller implementation
        getStartDialogFragment().show(getFragmentManager(), "start_dialog_fragment");

        //let controller implement specific visual callback
        onStartDialogShown();
    }

    /**
     * Called by the activity when controller should display the select dialog
     */
    public final void displaySelectDialog() {

        //retrieve specific select dialog from controller implementation
        getSelectDialogFragment().show(getFragmentManager(), "select_dialog_fragment");

        //let controller implement specific visual callback
        onSelectDialogShown();
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

                    //inform activity that an user input should be send to the server
                    mCallbacks.onSendUserInputRequested(new Message(MessageType.USER_INPUT, userEvent));

                    //let contoller implementation the possibility to display visual callbacks or
                    //add a specific behavior according to the UserEvent send.
                    onUserEventSend(userEvent);
                }
                return false;
            }
        };

        //iterate through all button ids set in getControllerKeys() to attached the keyListener
        if (mKeys != null) {
            for (int i = 0; i < mKeys.size(); i++) {
                final Button button = (Button) controllerView.findViewById(mKeys.keyAt(i));
                button.setOnTouchListener(keyListener);
            }
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
        if (eventAction == null) {
            return null;
        }

        final EventType eventType = extractEventType(view);
        if (eventType == null) {
            return null;
        }

        return new UserEvent(eventType, eventAction);
    }

    /**
     * Extract the {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction}
     * from a {@link android.view.MotionEvent}.
     *
     * @param motionEvent the {@link android.view.MotionEvent} from which the
     *                    {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction} will be extracted.
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
     * Extract the {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType}
     * associated with a {@link android.view.View}.
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
         * Called when Activity should send an user input from the controller.
         *
         * @param userInputMessage user input to send to the sever.
         */
        public void onSendUserInputRequested(Message userInputMessage);

        /**
         * Called when user wants to open start dialog.
         */
        public void onStartDialogRequest();

        /**
         * Called when user want to open select dialog.
         */
        public void onSelectDialogRequest();
    }
}
