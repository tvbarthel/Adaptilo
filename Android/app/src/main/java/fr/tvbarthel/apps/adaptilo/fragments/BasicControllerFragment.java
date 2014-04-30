package fr.tvbarthel.apps.adaptilo.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.activities.BasicControllerCaptureActivity;
import fr.tvbarthel.apps.adaptilo.helpers.QrCodeHelper;
import fr.tvbarthel.apps.adaptilo.models.Message;
import fr.tvbarthel.apps.adaptilo.models.UserEvent;
import fr.tvbarthel.apps.adaptilo.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.models.enums.EventType;
import fr.tvbarthel.apps.adaptilo.models.enums.MessageType;

public class BasicControllerFragment extends AdaptiloControllerFragment {

    /**
     * controller keys which can be pressed by the user
     */
    final int[] keys = {
            R.id.basic_controller_btn_a,
            R.id.basic_controller_btn_b,
            R.id.basic_controller_btn_arrow_up,
            R.id.basic_controller_btn_arrow_down,
            R.id.basic_controller_btn_arrow_left,
            R.id.basic_controller_btn_arrow_right};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_basic_controller, container, false);


        Typeface minecraftiaTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Minecraftia.ttf");
        ((Button) fragmentView.findViewById(R.id.basic_controller_btn_select)).setTypeface(minecraftiaTypeFace);
        ((TextView) fragmentView.findViewById(R.id.basic_controller_game_name)).setTypeface(minecraftiaTypeFace);

        final TextView gameSlot = (TextView) fragmentView.findViewById(R.id.basic_controller_game_slot);
        gameSlot.setTypeface(minecraftiaTypeFace);

        final View.OnTouchListener keyListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EventAction action = null;
                EventType type = null;

                /**
                 * check action performed
                 */
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    action = EventAction.ACTION_KEY_DOWN;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    action = EventAction.ACTION_KEY_UP;
                }

                /**
                 * if action performed is handled, use right event type
                 */
                if (action != null) {
                    switch (v.getId()) {
                        case R.id.basic_controller_btn_a:
                            type = EventType.KEY_A;
                            break;
                        case R.id.basic_controller_btn_b:
                            type = EventType.KEY_B;
                            break;
                        case R.id.basic_controller_btn_arrow_left:
                            type = EventType.KEY_ARROW_LEFT;
                            break;
                        case R.id.basic_controller_btn_arrow_up:
                            type = EventType.KEY_ARROW_UP;
                            break;
                        case R.id.basic_controller_btn_arrow_right:
                            type = EventType.KEY_ARROW_RIGHT;
                            break;
                        case R.id.basic_controller_btn_arrow_down:
                            type = EventType.KEY_ARROW_DOWN;
                            break;
                    }

                    /**
                     * if action and type and handled, send message to the server
                     */
                    if (type != null) {
                        final UserEvent userEvent = new UserEvent(type, action);
                        mAdaptiloEngine.sendUserInput(new Message(MessageType.USER_INPUT, userEvent));
                    }
                }
                return false;
            }
        };


        for (int buttonId : keys) {
            final Button button = (Button) fragmentView.findViewById(buttonId);
            button.setOnTouchListener(keyListener);
            button.setTypeface(minecraftiaTypeFace);
        }


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
