package fr.tvbarthel.apps.adaptilo.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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

        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_fragment_basic_controller_start, null);
        final Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Minecraftia.ttf");

        ((TextView) dialogView.findViewById(R.id.dialog_fragment_basic_controller_start_title)).setTypeface(typeface);

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final AlertDialog alert = ((AlertDialog) dialog);
                alert.getButton(AdaptiloStartDialogFragment.BUTTON_RESUME).setTypeface(typeface);
                alert.getButton(AdaptiloStartDialogFragment.BUTTON_DISCONNECT).setTypeface(typeface);
                alert.getButton(AdaptiloStartDialogFragment.BUTTON_NEW_GAME).setTypeface(typeface);
            }
        });

        alertDialog.setView(dialogView);

        return alertDialog;
    }
}
