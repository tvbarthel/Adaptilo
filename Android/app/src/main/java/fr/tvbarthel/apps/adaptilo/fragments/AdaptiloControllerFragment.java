package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine;
import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.helpers.QrCodeHelper;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.Message;

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
     * catch and process messages send to your controller
     *
     * @param messageToHandle messages send from the server
     */
    abstract public void onMessage(Message messageToHandle);

    public AdaptiloControllerFragment() {
        mAdaptiloEngine = new AdaptiloEngine(this);
    }

    /**
     * load game config into the controller after scan success
     *
     * @param config
     */
    protected void scannerSuccess(EngineConfig config) {
        mAdaptiloEngine.setEngineConfig(config);
        Log.d(TAG, "scannerSuccess : " + config.toString());
    }

    /**
     * scanner error
     *
     * @param ex
     */
    protected void scannerError(QrCodeException ex) {
        Log.d(TAG, "scannerError : " + ex.getMessage());
    }

    protected void handleQrCodeResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            onScannerCanceled();
        } else {
            EngineConfig config = null;
            try {
                config = QrCodeHelper.verifyFromActivityResult(requestCode, resultCode, data);
            } catch (QrCodeException e) {
                scannerError(e);
            } finally {
                if (config != null) {
                    scannerSuccess(config);
                }
            }
        }
    }

    /**
     * QrCode scanner has been canceled.
     */
    abstract protected void onScannerCanceled();

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
