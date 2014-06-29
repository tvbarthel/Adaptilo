package fr.tvbarthel.apps.adaptilo.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.helpers.SharedPreferencesHelper;

/**
 * Dialog fragment for BasicController options
 */
public class BasicControllerOptionsFragment extends AdaptiloSelectDialogFragment {

    /**
     * shared preferences used for vibrator policy
     */
    SharedPreferences mVibratorSharedPreferences;

    private ToggleButton mKeyVibrationToggleButton;

    private ToggleButton mServerVibrationToggleButton;

    private boolean mCurrentUserVibrationKeyPolicy;

    private boolean mCurrentUserVibrationServerPolicy;


    /**
     * Empty constructor as needed (cf lint)
     */
    public BasicControllerOptionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mVibratorSharedPreferences = activity.getSharedPreferences(
                SharedPreferencesHelper.VIBRATOR_PREFERENCE, Context.MODE_PRIVATE);

        mCurrentUserVibrationKeyPolicy =
                mVibratorSharedPreferences.getBoolean(
                        SharedPreferencesHelper.KEY_VIBRATE_ON_KEY_EVENT,
                        SharedPreferencesHelper.DEFAULT_VIBRATE_ON_KEY_EVENT);

        mCurrentUserVibrationServerPolicy =
                mVibratorSharedPreferences.getBoolean(
                        SharedPreferencesHelper.KEY_VIBRATE_ON_SERVER_EVENT,
                        SharedPreferencesHelper.DEFAULT_VIBRATE_ON_SERVER_EVENT);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = getAlertDialog();
        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.fragment_basic_controller_options, null);

        final Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Minecraftia.ttf");
        ((TextView) dialogView.findViewById(R.id.fragment_basic_controller_options_title)).setTypeface(typeface);
        ((TextView) dialogView.findViewById(R.id.fragment_basic_controller_options_key_label)).setTypeface(typeface);
        ((TextView) dialogView.findViewById(R.id.fragment_basic_controller_options_server_label)).setTypeface(typeface);

        mKeyVibrationToggleButton = (ToggleButton) dialogView.findViewById(R.id.fragment_basic_controller_options_key_toggle);
        mKeyVibrationToggleButton.setTypeface(typeface);
        mKeyVibrationToggleButton.setChecked(mCurrentUserVibrationKeyPolicy);

        mServerVibrationToggleButton = (ToggleButton) dialogView.findViewById(R.id.fragment_basic_controller_options_server_toggle);
        mServerVibrationToggleButton.setTypeface(typeface);
        mServerVibrationToggleButton.setChecked(mCurrentUserVibrationServerPolicy);

        alertDialog.setView(dialogView);

        //custom alert button
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setTypeface(typeface);
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setTypeface(typeface);
            }
        });

        return alertDialog;
    }

    @Override
    protected boolean saveOptions() {
        final SharedPreferences.Editor vibratorEditor = mVibratorSharedPreferences.edit();
        vibratorEditor.putBoolean(SharedPreferencesHelper.KEY_VIBRATE_ON_KEY_EVENT,
                mKeyVibrationToggleButton.isChecked());
        vibratorEditor.putBoolean(SharedPreferencesHelper.KEY_VIBRATE_ON_SERVER_EVENT,
                mServerVibrationToggleButton.isChecked());
        vibratorEditor.commit();

        return true;
    }
}
