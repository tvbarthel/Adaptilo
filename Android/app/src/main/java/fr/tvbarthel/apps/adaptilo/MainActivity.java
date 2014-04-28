package fr.tvbarthel.apps.adaptilo;

import android.os.Bundle;

import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment;
import fr.tvbarthel.apps.adaptilo.fragments.BasicControllerFragment;


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
