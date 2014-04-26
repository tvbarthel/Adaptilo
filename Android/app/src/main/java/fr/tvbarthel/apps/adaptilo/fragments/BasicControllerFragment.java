package fr.tvbarthel.apps.adaptilo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.models.Message;

public class BasicControllerFragment extends ControllerFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_basic_controller, container, false);
        return fragmentView;
    }

    @Override
    public void onMessage(Message messageToHandle) {
        // TODO handle the message
    }
}
