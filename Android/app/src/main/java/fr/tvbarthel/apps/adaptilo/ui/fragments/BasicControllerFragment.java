package fr.tvbarthel.apps.adaptilo.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.UserEvent;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;
import fr.tvbarthel.apps.adaptilo.models.io.ClosingError;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BasicControllerFragment extends AdaptiloControllerFragment {

    /**
     * Log cat.
     */
    private static final String TAG = BasicControllerFragment.class.getName();


    /**
     * A {@link android.widget.TextView} used to show messages to the user.
     */
    protected TextView mOnScreenMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault("fonts/Minecraftia.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_basic_controller, container, false);

        mOnScreenMessage = (TextView) fragmentView.findViewById(R.id.basic_controller_on_screen_message);

        return fragmentView;
    }

    @Override
    protected int getSelectButtonId() {
        return R.id.basic_controller_btn_select;
    }

    @Override
    protected int getStartButtonId() {
        return R.id.basic_controller_btn_start;
    }

    @Override
    protected SparseArray<EventType> getControllerKeys() {
        SparseArray<EventType> keys = new SparseArray<EventType>();
        keys.put(R.id.basic_controller_btn_a, EventType.KEY_A);
        keys.put(R.id.basic_controller_btn_b, EventType.KEY_B);
        keys.put(R.id.basic_controller_btn_arrow_up, EventType.KEY_ARROW_UP);
        keys.put(R.id.basic_controller_btn_arrow_down, EventType.KEY_ARROW_DOWN);
        keys.put(R.id.basic_controller_btn_arrow_left, EventType.KEY_ARROW_LEFT);
        keys.put(R.id.basic_controller_btn_arrow_right, EventType.KEY_ARROW_RIGHT);
        return keys;
    }

    @Override
    protected AdaptiloSelectDialogFragment getSelectDialogFragment() {
        return new BasicControllerSelectDialogFragment();
    }

    @Override
    protected AdaptiloStartDialogFragment getStartDialogFragment() {
        return new BasicControllerStartDialogFragment();
    }

    @Override
    public void onGameStart() {
        showOnScreenMessage(mAdaptiloEngine.getEngineConfig().getGameName());
    }

    @Override
    protected void onUserEventSend(UserEvent userEvent) {
        /**
         * Process a specific behavior for a given eventType and eventAction.
         *
         * Use {@link fr.tvbarthel.apps.adaptilo.models.UserEvent#getEventAction()}
         * with values {@link fr.tvbarthel.apps.adaptilo.models.enums.EventAction}
         *
         * As well as {@link fr.tvbarthel.apps.adaptilo.models.UserEvent#getEventType()}
         * with values {@link fr.tvbarthel.apps.adaptilo.models.enums.EventType}
         *
         * For instance :
         *
         * {@code
         *  if (userEvent.getEventAction() == EventAction.ACTION_KEY_DOWN) {
         *      switch (userEvent.getEventType()) {
         *          case KEY_A:
         *              showOnScreenMessage("KEY_A pressed");
         *              break;
         *          case KEY_B:
         *              showOnScreenMessage("KEY_B pressed");
         *              break;
         *          default:
         *              showOnScreenMessage("key pressed");
         *              break;
         *      }
         *  }
         * }
         */
    }

    @Override
    public void onGameServerUnreachable() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.basic_controller_network_unreachable_alert_title);
        builder.setMessage(R.string.basic_controller_network_unreachable_alert_content);
        builder.create().show();
    }

    @Override
    protected void onSelectDialogShown() {
        showOnScreenMessage(R.string.basic_controller_btn_select);
    }

    @Override
    protected void onStartDialogShown() {
        showOnScreenMessage(R.string.basic_controller_btn_start);
    }

    @Override
    public void onSelectDialogClosed(boolean optionSaved) {
        super.onSelectDialogClosed(optionSaved);
        EngineConfig config = mAdaptiloEngine.getEngineConfig();
        showOnScreenMessage(config != null ? config.getGameName() : "");
    }

    @Override
    public void onStartDialogClosed(int which) {
        super.onStartDialogClosed(which);
        switch (which) {
            case AdaptiloStartDialogFragment.BUTTON_DISCONNECT:
                showOnScreenMessage(R.string.basic_controller_message_disconnected);
                break;
            default:
                EngineConfig config = mAdaptiloEngine.getEngineConfig();
                showOnScreenMessage(config != null ? config.getGameName() : "");
                break;
        }
    }

    @Override
    protected void onScannerCanceled() {
        // hide the on screen message that should be R.string.basic_controller_message_loading
        hideOnScreenMessage();
    }

    @Override
    protected void onScannerError(QrCodeException ex) {
        super.onScannerError(ex);
        showOnScreenMessage(R.string.basic_controller_qrcode_scanner_error);
    }

    @Override
    protected void onScannerSuccess(EngineConfig config) {
        super.onScannerSuccess(config);
        showOnScreenMessage(config.getGameName());
    }


    @Override
    protected void onConnectionClose(int reason) {
        switch (reason) {
            case (ClosingError.REGISTRATION_REQUESTED_ROOM_IS_FULL):
                showOnScreenMessage(R.string.basic_controller_room_full);
                break;
        }
    }

    /**
     * Show an on screen message.
     *
     * @param resourceId the resource id of the string to be shown.
     */
    protected void showOnScreenMessage(int resourceId) {
        mOnScreenMessage.setVisibility(View.VISIBLE);
        mOnScreenMessage.setText(resourceId);
    }

    /**
     * Show an on screen message.
     *
     * @param message The string to be shown.
     */
    protected void showOnScreenMessage(String message) {
        mOnScreenMessage.setVisibility(View.VISIBLE);
        mOnScreenMessage.setText(message);
    }

    /**
     * Hide the on screen message.
     */
    protected void hideOnScreenMessage() {
        mOnScreenMessage.setVisibility(View.INVISIBLE);
    }
}
