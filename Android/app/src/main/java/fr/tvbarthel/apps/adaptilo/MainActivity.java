package fr.tvbarthel.apps.adaptilo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fr.tvbarthel.apps.adaptilo.engine.AdaptiloEngine;
import fr.tvbarthel.apps.adaptilo.fragments.AdaptiloControllerFragment;
import fr.tvbarthel.apps.adaptilo.fragments.BasicControllerFragment;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;
import fr.tvbarthel.apps.adaptilo.models.Message;


public class MainActivity extends FragmentActivity implements AdaptiloControllerFragment.Callbacks, AdaptiloEngine.Callbacks {

    /**
     * Adaptilo core engine
     */
    private AdaptiloEngine mAdaptiloEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new BasicControllerFragment())
                    .commit();
        }

        mAdaptiloEngine = new AdaptiloEngine(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EngineConfig config = mAdaptiloEngine.parseLoadGameResult(requestCode, resultCode, data);
        if (config == null) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSendMessageRequest(Message messageToSend) {
        // TODO handle the message
    }

    @Override
    public void onLoadGameRequest() {
        mAdaptiloEngine.loadGame(this);
    }

    @Override
    public void onMessageReceived(Message message) {
        //TODO transfer message to the controller
    }

    @Override
    public void onReplaceControllerRequest(int controllerId) {
        //TODO replace current controller
    }
}
