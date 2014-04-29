package fr.tvbarthel.apps.adaptilo.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.activities.BasicControllerCaptureActivity;
import fr.tvbarthel.apps.adaptilo.helpers.QrCodeHelper;
import fr.tvbarthel.apps.adaptilo.models.Message;

public class BasicControllerFragment extends AdaptiloControllerFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_basic_controller, container, false);
        Typeface minecraftiaTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Minecraftia.ttf");
        ((Button) fragmentView.findViewById(R.id.basic_controller_btn_select)).setTypeface(minecraftiaTypeFace);
        ((Button) fragmentView.findViewById(R.id.basic_controller_btn_a)).setTypeface(minecraftiaTypeFace);
        ((Button) fragmentView.findViewById(R.id.basic_controller_btn_b)).setTypeface(minecraftiaTypeFace);
        ((TextView) fragmentView.findViewById(R.id.basic_controller_game_name)).setTypeface(minecraftiaTypeFace);

        final TextView gameSlot = (TextView) fragmentView.findViewById(R.id.basic_controller_game_slot);
        gameSlot.setTypeface(minecraftiaTypeFace);

        Button startButton = (Button) fragmentView.findViewById(R.id.basic_controller_btn_start);
        startButton.setTypeface(minecraftiaTypeFace);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQrCodeScanner();

                // TODO remove, for test purpose only
                int visibility = gameSlot.getVisibility();
                if (visibility == View.VISIBLE) {
                    gameSlot.setVisibility(View.GONE);
                } else {
                    gameSlot.setVisibility(View.VISIBLE);
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void onMessage(Message messageToHandle) {
        // TODO handle the message
    }

    /**
     * start QrCode scanner to load a game config.
     */
    public void startQrCodeScanner() {
        QrCodeHelper.initiateQrCodeScan(getActivity(), BasicControllerCaptureActivity.class,
                getString(R.string.qr_code_scanner_prompt));
    }

}
