package fr.tvbarthel.apps.adaptilo.fragments;

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
import fr.tvbarthel.apps.adaptilo.models.io.Message;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Simple controller with two huge buttons which look like drums.
 */
public class DrumsControllerFragment extends AdaptiloControllerFragment {

    /**
     * Controller title
     */
    private TextView mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault("fonts/Jumpman.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_drums_controller, container, false);

        mTitle = (TextView) fragmentView.findViewById(R.id.drums_controller_title);

        return fragmentView;
    }

    @Override
    protected SparseArray<EventType> getControllerKeys() {
        SparseArray<EventType> keys = new SparseArray<EventType>();
        keys.put(R.id.drums_controller_btn_left, EventType.KEY_LEFT);
        keys.put(R.id.drums_controller_btn_right, EventType.KEY_RIGHT);
        return keys;
    }

    @Override
    protected int getSelectButtonId() {
        return R.id.drums_controller_btn_select;
    }

    @Override
    protected int getStartButtonId() {
        return R.id.drums_controller_btn_start;
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
    public void onGameServerUnreachable() {

    }

    @Override
    public void onGameStart(String gameName) {
        onConnectedCallback();
    }

    @Override
    public void onMessageReceived(Message message) {

    }

    @Override
    public void onErrorReceived(Exception ex) {

    }

    @Override
    public void onConnectionClosed(int reason) {
        onDisconnectedCallback();
    }

    @Override
    protected void onUserEventSend(UserEvent userEvent) {

    }

    @Override
    protected void onSelectDialogShown() {

    }

    @Override
    public void onSelectDialogClosed(boolean optionSaved) {

    }

    @Override
    protected void onStartDialogShown() {

    }

    @Override
    public void onStartDialogClosed(int which) {

    }

    @Override
    public void onScannerSuccess(EngineConfig config) {

    }

    @Override
    public void onScannerError(QrCodeException ex) {

    }

    @Override
    public void onScannerCanceled() {

    }

    /**
     * Display visual callback when drums controller is connected to a game.
     */
    private void onConnectedCallback() {
        mTitle.setTextColor(getResources().getColor(R.color.holo_green_light));
    }

    /**
     * Display visual callback when drums controller is disconnected.
     */
    private void onDisconnectedCallback() {
        mTitle.setTextColor(getResources().getColor(R.color.holo_gray_bright));
    }
}
