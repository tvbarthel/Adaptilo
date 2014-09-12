package fr.tvbarthel.apps.adaptilo.activities;

import android.os.Bundle;

import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment;
import fr.tvbarthel.apps.adaptilo.fragments.DrumsControllerFragment;

/**
 * Activity launch as default one.
 *
 * The controller returned by {@link AdaptiloActivity#getDefaultController()} will be displayed.
 */
public class MainActivity extends AdaptiloActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    AdaptiloControllerFragment getDefaultController() {
        return new DrumsControllerFragment();
    }
}
