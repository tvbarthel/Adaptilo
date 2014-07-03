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
 * should implement a {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloSelectDialogFragment}
 * or reuse a default one.
 * <p/>
 * {@link AdaptiloControllerFragment#getSelectDialogFragment()}
 */
public abstract class AdaptiloSelectDialogFragment extends DialogFragment {

    /**
     * Dummy callback when fragment isn't attached yet.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onSelectDialogClose(boolean optionSaved) {

        }
    };

    /**
     * Current callbacks object.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * Alert dialog builder.
     */
    private AlertDialog.Builder mBuilder;

    /**
     * Used to know if options has been saved.
     */
    protected boolean mOptionsSaved;

    /**
     * Called when select dialog implementation must save options.
     *
     * @return true if options has been well saved.
     */
    protected abstract boolean saveOptions();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBuilder = new AlertDialog.Builder(getActivity());

        mOptionsSaved = false;

        //set default negative button to "Resume"
        mBuilder.setNegativeButton(R.string.select_dialog_options_negative, null);

        //set default position button to "Save and resume"
        mBuilder.setPositiveButton(R.string.select_dialog_options_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mOptionsSaved = saveOptions();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implements fragment callbacks");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mCallbacks.onSelectDialogClose(mOptionsSaved);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    /**
     * Retrieve alert dialog.
     *
     * @return initialized alert dialog.
     */
    protected AlertDialog getAlertDialog() {
        return mBuilder.create();
    }

    /**
     * Fragment callbacks.
     */
    public interface Callbacks {

        /**
         * Called when select dialog is dismissed.
         *
         * @param optionSaved true if option has been saved.
         */
        public void onSelectDialogClose(boolean optionSaved);
    }
}
