package fr.tvbarthel.apps.adaptilo.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import fr.tvbarthel.apps.adaptilo.R;

/**
 * Encapsulate common select dialog behavior for an Adaptilo controller.
 * <p/>
 * Each custom {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment}
 * should implement a {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment}
 * or reuse a default one.
 * <p/>
 * {@link AdaptiloControllerFragment#getStartDialogFragment()}
 */
public abstract class AdaptiloStartDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener {

    /**
     * Rename identifier for negative button for more understanding.
     */
    public static final int BUTTON_RESUME = AlertDialog.BUTTON_NEGATIVE;

    /**
     * Rename identifier for neutral button for more understanding.
     */
    public static final int BUTTON_DISCONNECT = AlertDialog.BUTTON_NEUTRAL;

    /**
     * Rename identifier for positive button for more understanding.
     */
    public static final int BUTTON_NEW_GAME = AlertDialog.BUTTON_POSITIVE;

    /**
     * Dummy callback when fragment not attached.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onStartDialogClose(int which) {

        }
    };

    /**
     * Current callback object.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * Builder used to initialize alert dialog with common part.
     */
    private AlertDialog mAlertDialog;

    /**
     * The button id of the clicked one.
     */
    private int mSelectedChoice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAlertDialog = new AlertDialog.Builder(getActivity()).create();

        mSelectedChoice = 0;

        //set up resume button
        mAlertDialog.setButton(
                BUTTON_RESUME,
                getText(R.string.start_dialog_resume),
                this
        );

        //set up disconnect button
        mAlertDialog.setButton(
                BUTTON_DISCONNECT,
                getText(R.string.start_dialog_disconnect),
                this
        );

        //set up new game button
        mAlertDialog.setButton(
                BUTTON_NEW_GAME,
                getText(R.string.start_dialog_new_game),
                this
        );

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mCallbacks.onStartDialogClose(mSelectedChoice);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mSelectedChoice = which;
    }

    /**
     * Retrieve initialized alert dialog.
     *
     * @return initialized alert dialog.
     */
    protected AlertDialog getAlertDialog() {
        return mAlertDialog;
    }

    public interface Callbacks {
        /**
         * Called when start dialog closed.
         *
         * @param which identifier of clicked button
         *              {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment#BUTTON_RESUME}
         *              {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment#BUTTON_DISCONNECT}
         *              {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment#BUTTON_NEW_GAME}
         *              or 0 if dismissed or canceled.
         */
        public void onStartDialogClose(int which);
    }
}
