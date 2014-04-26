package fr.tvbarthel.apps.adaptilo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fr.tvbarthel.apps.adaptilo.fragments.BasicControllerFragment;
import fr.tvbarthel.apps.adaptilo.fragments.ControllerFragment;
import fr.tvbarthel.apps.adaptilo.models.Message;


public class MainActivity extends FragmentActivity implements ControllerFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new BasicControllerFragment())
                    .commit();
        }
    }

    @Override
    public void sendMessage(Message messageToSend) {
        // TODO handle the message
    }
}
