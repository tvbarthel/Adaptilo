package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import fr.tvbarthel.apps.adaptilo.R;

/**
 * Start dialog fragment for BasicController
 */
public class BasicControllerStartDialogFragment extends AdaptiloStartDialogFragment {

    /**
     * Empty constructor as needed (cf lint)
     */
    public BasicControllerStartDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = getAlertDialog();

        final LayoutInflater inflater
                = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_fragment_basic_controller_start, null);

        alertDialog.setView(dialogView);

        return alertDialog;
    }
}
