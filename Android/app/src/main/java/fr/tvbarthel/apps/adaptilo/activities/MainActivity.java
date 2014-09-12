package fr.tvbarthel.apps.adaptilo.activities;

import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment;
import fr.tvbarthel.apps.adaptilo.fragments.DrumsControllerFragment;

/**
 * Activity launch as default one.
 * <p/>
 * The controller returned by {@link AdaptiloActivity#getDefaultController()} will be displayed.
 */
public class MainActivity extends AdaptiloActivity {

    @Override
    AdaptiloControllerFragment getDefaultController() {
        return new DrumsControllerFragment();
    }
}
