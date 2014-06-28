package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.activities.BasicControllerCaptureActivity;
import fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine;
import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.helpers.QrCodeHelper;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.io.Message;

/**
 * A simple {@link android.support.v4.app.Fragment} that represents a controller.
 * <p/>
 * The controller can send messages to a callback and can receive messages.
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
     * catch and process messages send to your controller
     *
     * @param messageToHandle messages send from the server
     */
    abstract public void onMessage(Message messageToHandle);

    /**
     * Called when user want to start a new game while controller already connected to a
     * previous one.
     * <p/>
     * This let controller implementation decides which behavior to adopt. Basically the controller
     * should display a dailogBox to let the user choose between leaving the current game and
     * starting the scanner or stay connected to the current game.
     *
     * @return true if the controller should stop the current game (disconnection) and load a
     * new one.
     */
    abstract public AlertDialog onStartDialogNeeded();


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
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
        mAdaptiloEngine.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdaptiloEngine.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdaptiloEngine.resume();
    }

    @Override
    public void onMessageReceived(Message message) {
        this.onMessage(message);
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
     * Used to start Qr code scanner in order to load a game into your controller.
     * <p/>
     * Or prompt a dialog : Cancel | Disconnect | New Game
     * <p/>
     * Should be called when "start button" is pressed
     */
    protected void start() {
        if (mAdaptiloEngine.isReadToCommunicate()) {

            //controller already running for a game. Let current controller implementation ask the
            // to the user if he want to load a new game.
            AlertDialog alert = onStartDialogNeeded();

            //set up alertDialog to match wished behavior
            setUpStartDialog(alert);

            //display alert
            alert.show();
        } else {

            //current controller isn't running, start scanner to load a game
            QrCodeHelper.initiateQrCodeScan(this, BasicControllerCaptureActivity.class,
                    getString(R.string.qr_code_scanner_prompt));
        }
    }

    /**
     * load game config into the controller after scan success
     *
     * @param config
     */
    protected void onScannerSuccess(EngineConfig config) {
        mAdaptiloEngine.setEngineConfig(config);
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
     * Configure start dialog to obtain wished behavior.
     */
    private void setUpStartDialog(AlertDialog alert) {


        DialogInterface.OnClickListener startDialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:
                        //User decided to leave the current game and start a new one.
                        loadNewGame();
                        break;
                    case AlertDialog.BUTTON_NEUTRAL:
                        //User decided to leave the current game.
                        disconnect();
                }
            }
        };


        //set up new game button
        alert.setButton(
                AlertDialog.BUTTON_POSITIVE,
                getText(R.string.start_new_game_new_game),
                startDialogListener
        );


        //set up disconnect button
        alert.setButton(
                AlertDialog.BUTTON_NEUTRAL,
                getText(R.string.start_new_game_disconnect),
                startDialogListener
        );


        //set up cancel
        alert.setButton(
                AlertDialog.BUTTON_NEGATIVE,
                getText(R.string.start_new_game_cancel),
                (android.os.Message) null
        );
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
        //start scanner to load the new game
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
