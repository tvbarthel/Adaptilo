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
    protected Callback mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ControllerFragment.Callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    abstract public void onMessage(Message messageToHandle);

    public static interface Callback {
        void sendMessage(Message messageToSend);
    }
}
