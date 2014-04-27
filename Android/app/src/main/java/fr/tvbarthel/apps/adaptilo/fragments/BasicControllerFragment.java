package fr.tvbarthel.apps.adaptilo.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.models.Message;

public class BasicControllerFragment extends AdaptiloControllerFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_basic_controller, container, false);
        Typeface minecraftiaTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Minecraftia.ttf");
        ((Button) fragmentView.findViewById(R.id.basic_controller_btn_start)).setTypeface(minecraftiaTypeFace);
        ((Button) fragmentView.findViewById(R.id.basic_controller_btn_select)).setTypeface(minecraftiaTypeFace);
        ((Button) fragmentView.findViewById(R.id.basic_controller_btn_a)).setTypeface(minecraftiaTypeFace);
        ((Button) fragmentView.findViewById(R.id.basic_controller_btn_b)).setTypeface(minecraftiaTypeFace);
        ((TextView) fragmentView.findViewById(R.id.basic_controller_game_slot_text)).setTypeface(minecraftiaTypeFace);
        return fragmentView;
    }

    @Override
    public void onMessage(Message messageToHandle) {
        // TODO handle the message
    }
}
