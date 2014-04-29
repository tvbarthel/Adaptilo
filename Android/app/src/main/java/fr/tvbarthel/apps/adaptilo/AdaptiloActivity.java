package fr.tvbarthel.apps.adaptilo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment;
import fr.tvbarthel.apps.adaptilo.helpers.QrCodeHelper;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;

/**
 * Activity which can handle {@link fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment}
 * events
 */
public abstract class AdaptiloActivity extends FragmentActivity implements AdaptiloControllerFragment.Callbacks {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            setAdaptiloController(getDefaultController());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EngineConfig config = null;
        try {
            config = QrCodeHelper.verifyFromActivityResult(requestCode, resultCode, data);
        } catch (QrCodeException e) {
            mAdaptiloControllerFragment.scannerError(e);
        }

        if (config == null) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            mAdaptiloControllerFragment.scannerSuccess(config);
        }
    }

    @Override
    public void onReplaceControllerRequest(AdaptiloControllerFragment controllerFragment) {
        setAdaptiloController(controllerFragment);
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
