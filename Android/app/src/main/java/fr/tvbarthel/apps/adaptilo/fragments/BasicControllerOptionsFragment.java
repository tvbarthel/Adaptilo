package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
public class BasicControllerOptionsFragment extends DialogFragment {

    /**
     * shared preferences used for vibrator policy
     */
    SharedPreferences mVibratorSharedPreferences;

    private ToggleButton mKeyVibrationToggleButton;

    private ToggleButton mServerVibrationToggleButton;

    private boolean mCurrentUserVibrationKeyPolicy;

    private boolean mCurrentUserVibrationServerPolicy;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mVibratorSharedPreferences = activity.getSharedPreferences(
                SharedPreferencesHelper.VIBRATOR_PREFERENCE, Context.MODE_PRIVATE);

        mCurrentUserVibrationKeyPolicy =
                mVibratorSharedPreferences.getBoolean(SharedPreferencesHelper.KEY_VIBRATE_ON_KEY_EVENT, true);

        mCurrentUserVibrationServerPolicy =
                mVibratorSharedPreferences.getBoolean(SharedPreferencesHelper.KEY_VIBRATE_ON_SERVER_EVENT, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

        builder.setNegativeButton(R.string.basic_controller_options_negative, null)
                .setPositiveButton(R.string.basic_controller_options_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveOptions();
                    }
                })
                .setView(dialogView);

        final AlertDialog alertDialog = builder.create();

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

    /**
     * save user preferences
     */
    private void saveOptions() {
        final SharedPreferences.Editor vibratorEditor = mVibratorSharedPreferences.edit();
        vibratorEditor.putBoolean(SharedPreferencesHelper.KEY_VIBRATE_ON_KEY_EVENT,
                mKeyVibrationToggleButton.isChecked());
        vibratorEditor.putBoolean(SharedPreferencesHelper.KEY_VIBRATE_ON_SERVER_EVENT,
                mServerVibrationToggleButton.isChecked());
        vibratorEditor.commit();
    }
}
