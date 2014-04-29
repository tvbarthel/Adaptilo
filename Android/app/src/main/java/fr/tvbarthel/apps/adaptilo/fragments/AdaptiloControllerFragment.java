package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.Message;

/**
 * A simple {@link android.support.v4.app.Fragment} that represents a controller.
 * <p/>
 * The controller can send messages to a callback and can receive messages.
 */
abstract public class AdaptiloControllerFragment extends Fragment implements AdaptiloEngine.Callbacks {

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
     * parse onActivityResult to know if it was for AdaptiloEngine
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return false if result wasn't for AdaptiloEngine
     */
    public boolean parseLoadGameResult(int requestCode, int resultCode, Intent data) {
        boolean onActivityResultForAdaptiloEngine = false;
        EngineConfig config = mAdaptiloEngine.parseLoadGameResult(requestCode, resultCode, data);
        if (config != null) {
            onActivityResultForAdaptiloEngine = true;
        }
        return onActivityResultForAdaptiloEngine;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity holding AdpatiloControllerFragment must implements Callbacks");
        }

        mCallbacks = (Callbacks) activity;
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
