package fr.tvbarthel.apps.adaptilo.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ToggleButton;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.helpers.SharedPreferencesHelper;

/**
 * Select dialog fragment for BasicController options
 */
public class BasicControllerSelectDialogFragment extends AdaptiloSelectDialogFragment {

    /**
     * shared preferences used for vibrator policy
     */
    private SharedPreferences mVibratorSharedPreferences;

    private ToggleButton mKeyVibrationToggleButton;

    private ToggleButton mServerVibrationToggleButton;

    private boolean mCurrentUserVibrationKeyPolicy;

    private boolean mCurrentUserVibrationServerPolicy;


    /**
     * Empty constructor as needed (cf lint)
     */
    public BasicControllerSelectDialogFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mVibratorSharedPreferences = activity.getSharedPreferences(
                SharedPreferencesHelper.VIBRATOR_PREFERENCE, Context.MODE_PRIVATE);

        mCurrentUserVibrationKeyPolicy
                = mVibratorSharedPreferences.getBoolean(
                SharedPreferencesHelper.KEY_VIBRATE_ON_KEY_EVENT,
                SharedPreferencesHelper.DEFAULT_VIBRATE_ON_KEY_EVENT);

        mCurrentUserVibrationServerPolicy
                = mVibratorSharedPreferences.getBoolean(
                SharedPreferencesHelper.KEY_VIBRATE_ON_SERVER_EVENT,
                SharedPreferencesHelper.DEFAULT_VIBRATE_ON_SERVER_EVENT);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = getAlertDialog();
        final LayoutInflater inflater
                = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_fragment_basic_controller_select, null);

        mKeyVibrationToggleButton
                = (ToggleButton) dialogView.findViewById(R.id.fragment_basic_controller_options_key_toggle);
        mKeyVibrationToggleButton.setChecked(mCurrentUserVibrationKeyPolicy);

        mServerVibrationToggleButton
                = (ToggleButton) dialogView.findViewById(R.id.fragment_basic_controller_options_server_toggle);
        mServerVibrationToggleButton.setChecked(mCurrentUserVibrationServerPolicy);

        alertDialog.setView(dialogView);

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
