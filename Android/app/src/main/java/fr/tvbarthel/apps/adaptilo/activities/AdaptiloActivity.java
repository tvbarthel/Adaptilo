package fr.tvbarthel.apps.adaptilo.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fr.tvbarthel.apps.adaptilo.R;
import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment;
import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloSelectDialogFragment;
import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloStartDialogFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity which can handle {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment}
 * events
 */
public abstract class AdaptiloActivity extends FragmentActivity implements
        AdaptiloControllerFragment.Callbacks,
        AdaptiloSelectDialogFragment.Callbacks,
        AdaptiloStartDialogFragment.Callbacks {

    /**
     * Logcat
     */
    private static final String TAG = AdaptiloActivity.class.getName();

    /**
     * controller
     */
    protected AdaptiloControllerFragment mAdaptiloControllerFragment;

    /**
     * get default controller which will be displayed on activity launch
     *
     * @return first controller to be displayed
     */
    abstract AdaptiloControllerFragment getDefaultController();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            setAdaptiloController(getDefaultController());
        }
    }

    @Override
    public void onReplaceControllerRequest(AdaptiloControllerFragment controllerFragment) {
        setAdaptiloController(controllerFragment);
    }

    @Override
    public void onSelectDialogClose(boolean optionSaved) {
        mAdaptiloControllerFragment.onSelectDialogClosed(optionSaved);
    }

    @Override
    public void onStartDialogClose(int which) {
        mAdaptiloControllerFragment.onStartDialogClosed(which);
    }

    /**
     * set controller which will be displayed to the user
     *
     * @param adaptiloController
     */
    public void setAdaptiloController(AdaptiloControllerFragment adaptiloController) {
        mAdaptiloControllerFragment = adaptiloController;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, mAdaptiloControllerFragment)
                .commit();
    }
}
