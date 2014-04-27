package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import fr.tvbarthel.apps.adaptilo.models.Message;

/**
 * A simple {@link android.support.v4.app.Fragment} that represents a controller.
 * <p/>
 * The controller can send messages to a callback and can receive messages.
 */
abstract public class AdaptiloControllerFragment extends Fragment {
    protected Callbacks mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callbacks) {
            mCallbacks = (Callbacks) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ControllerFragment.Callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    abstract public void onMessage(Message messageToHandle);

    /**
     * Callbacks interfaces for basic controller action
     */
    public static interface Callbacks {
        /**
         * allow controller to send user Input
         *
         * @param messageToSend
         */
        public void onSendMessageRequest(Message messageToSend);

        /**
         * user input to load a game into the controller
         */
        public void onLoadGameRequest();
    }
}
