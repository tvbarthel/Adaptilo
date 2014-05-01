package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

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
        Toast.makeText(getActivity(), "QrCode Malformed", Toast.LENGTH_LONG).show();
        Log.d(TAG, "scannerError : " + ex.getMessage());
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
        EngineConfig config = null;
        try {
            config = QrCodeHelper.verifyFromActivityResult(requestCode, resultCode, data);
        } catch (QrCodeException e) {
            scannerError(e);
        } finally {
            if (config != null) {
                scannerSuccess(config);
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
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
