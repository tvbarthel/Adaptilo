package fr.tvbarthel.apps.adaptilo.ui.activities;

import android.os.Bundle;

import fr.tvbarthel.apps.adaptilo.ui.fragments.AdaptiloControllerFragment;
import fr.tvbarthel.apps.adaptilo.ui.fragments.BasicControllerFragment;


public class MainActivity extends AdaptiloActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    AdaptiloControllerFragment getDefaultController() {
        return new BasicControllerFragment();
    }
}
