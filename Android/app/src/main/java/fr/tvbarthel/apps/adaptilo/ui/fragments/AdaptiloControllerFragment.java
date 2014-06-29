package fr.tvbarthel.apps.adaptilo.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine;
import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.helpers.QrCodeHelper;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.io.Message;
import fr.tvbarthel.apps.adaptilo.ui.activities.BasicControllerCaptureActivity;

/**
 * A simple {@link android.support.v4.app.Fragment} that represents a controller.
 * <p/>
 * The controller can send messages to a callback and can receive messages.
 * <p/>
 * All controller must at least display select and start buttons for consistency in basic features
 * <p/>
 * This abstract controller encapsulate select and start button behavior. Implementations only
 * handle visual callback and dialog customization. Therefor any implementation of
 * {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloControllerFragment} must provide an
 * implementation of its {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloSelectDialogFragment}
 * and {@link fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloStartDialogFragment}
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
     * Called when game server is unreachable.
     * <p/>
     * Should be used to warn user.
     */
    abstract public void onGameServerUnreachable();

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize select button with values matching implementation needs.
        final Button selectButton = (Button) view.findViewById(getSelectButtonId());
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdaptiloEngine.pause();
                getSelectDialogFragment().show(getFragmentManager(), "select_dialog_fragment");
            }
        });

        //initialize startPressed button with values matching implementation needs.
        final Button startButton = (Button) view.findViewById(getStartButtonId());
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPressed();
            }
        });
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
