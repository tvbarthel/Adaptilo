package fr.tvbarthel.apps.adaptilo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine;
import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment;
import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloSelectDialogFragment;
import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment;
import fr.tvbarthel.apps.adaptilo.helpers.QrCodeHelper;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.io.Message;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity which can handle {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment}
 * events
 */
public abstract class AdaptiloActivity extends FragmentActivity implements
        AdaptiloControllerFragment.Callbacks,
        AdaptiloSelectDialogFragment.Callbacks,
        AdaptiloStartDialogFragment.Callbacks,
        AdaptiloEngine.Callbacks {

    /**
     * Logcat
     */
    private static final String TAG = AdaptiloActivity.class.getName();

    /**
     * Adaptilo core part
     */
    protected AdaptiloEngine mAdaptiloEngine;

    /**
     * controller
     */
    protected AdaptiloControllerFragment mAdaptiloControllerFragment;

    /**
     * get default controller which will be displayed on activity launch
     *
     * @return first controller to be displayed
     */
    abstract AdaptiloControllerFragment getDefaultController();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mAdaptiloEngine = new AdaptiloEngine(this);
            mAdaptiloEngine.initEngine(this);

            setAdaptiloController(getDefaultController());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QrCodeHelper.REQUEST_CODE) {
            handleQrCodeResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdaptiloEngine.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdaptiloEngine.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdaptiloEngine.stop();
    }

    ////////////////////////////////////////////////////////////
    ///////////////// Controller Callbacks /////////////////////
    ////////////////////////////////////////////////////////////

    @Override
    public void onReplaceControllerRequest(AdaptiloControllerFragment controllerFragment) {
        setAdaptiloController(controllerFragment);
    }

    @Override
    public void onSendUserInputRequested(Message userInputMessage) {
        mAdaptiloEngine.sendUserInput(userInputMessage);
    }

    @Override
    public void onStartDialogRequest() {
        if (mAdaptiloEngine.isReadToCommunicate()) {
            //controller already running for a game. Let current controller implementation ask the
            // to the user if he want to load a new game, disconnect from the current one or resume.
            mAdaptiloControllerFragment.displayStartDialog();

            //pause game until onStartDialogClose is called
            mAdaptiloEngine.pause();

        } else {

            //current controller isn't running, launch scanner to load a game
            loadGame();
        }
    }

    @Override
    public void onSelectDialogRequest() {
        mAdaptiloControllerFragment.displaySelectDialog();

        //pause game until onSelectDialogClose is called
        mAdaptiloEngine.pause();
    }

    ////////////////////////////////////////////////////////////
    ///////////////// Select Dialog Callbacks //////////////////
    ////////////////////////////////////////////////////////////

    @Override
    public void onSelectDialogClose(boolean optionSaved) {
        mAdaptiloEngine.resume();

        //Propagate for visual callback or specific behavior.
        mAdaptiloControllerFragment.onSelectDialogClosed(optionSaved);
    }

    ////////////////////////////////////////////////////////////
    ////////////////// Start Dialog Callbacks //////////////////
    ////////////////////////////////////////////////////////////

    @Override
    public void onStartDialogClose(int which) {
        mAdaptiloEngine.resume();
        switch (which) {
            case AdaptiloStartDialogFragment.BUTTON_DISCONNECT:
                disconnect();
                break;
            case AdaptiloStartDialogFragment.BUTTON_NEW_GAME:
                loadNewGame();
        }

        //Propagate for visual callback or specific behavior.
        mAdaptiloControllerFragment.onStartDialogClosed(which);
    }

    ////////////////////////////////////////////////////////////
    ///////////////// Engine Callbacks /////////////////////////
    ////////////////////////////////////////////////////////////

    @Override
    public void onMessageReceived(final Message message) {
        switch (message.getType()) {
            case ENGINE_READY:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Let controller implement specific behavior when a game start
                        mAdaptiloControllerFragment.onGameStart(
                                mAdaptiloEngine.getEngineConfig().getGameName());
                    }
                });
                break;
            default:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //propagate custom messages to the controller
                        mAdaptiloControllerFragment.onMessageReceived(message);
                    }
                });
                break;
        }
    }

    @Override
    public void onErrorReceived(final Exception ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("ENETUNREACH")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Let controller implement specific behavior when game server is unreachable
                    mAdaptiloControllerFragment.onGameServerUnreachable();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //propagate custom errors to the controller
                    mAdaptiloControllerFragment.onErrorReceived(ex);
                }
            });
        }
    }

    @Override
    public void onConnectionClosed(final int closeCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdaptiloControllerFragment.onConnectionClosed(closeCode);
            }
        });
    }

    ////////////////////////////////////////////////////////////
    ///////////////////// Private part /////////////////////////
    ////////////////////////////////////////////////////////////

    /**
     * set controller which will be displayed to the user
     *
     * @param adaptiloController
     */
    private void setAdaptiloController(AdaptiloControllerFragment adaptiloController) {
        mAdaptiloControllerFragment = adaptiloController;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, mAdaptiloControllerFragment)
                .commit();
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
        QrCodeHelper.initiateQrCodeScan(
                this,
                BasicControllerCaptureActivity.class,
                getString(R.string.qr_code_scanner_prompt)
        );
    }

    /**
     * Load a new game when another one is already loaded.
     */
    private void loadNewGame() {
        disconnect();
        loadGame();
    }

    /**
     * Used to process QrCode scanner result when {@link #onActivityResult} is called.
     *
     * @param requestCode - The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode  - The integer result code returned by the child activity through its setResult().
     * @param data        - An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    private void handleQrCodeResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {

            //User cancel scanning Activity
            mAdaptiloControllerFragment.onScannerCanceled();

        } else {
            EngineConfig config = null;
            try {

                //verify is the scanned QrCode is an adaptilo one.
                config = QrCodeHelper.verifyFromActivityResult(requestCode, resultCode, data);

            } catch (QrCodeException e) {

                //QrCode doesn't match adaptilo scheme
                Log.d(TAG, "onScannerError : " + e.getMessage());

                //Let controller the possibility to display a visual callbacks
                mAdaptiloControllerFragment.onScannerError(e);

            } finally {
                if (config != null) {

                    //QrCode was well formed, start engine with the scanned config
                    mAdaptiloEngine.setEngineConfig(config);
                    mAdaptiloEngine.start();
                    Log.d(TAG, "onScannerSuccess : " + config.toString());

                    //Let controller the possibility to display a visual callbacks
                    mAdaptiloControllerFragment.onScannerSuccess(config);
                }
            }
        }
    }
}
