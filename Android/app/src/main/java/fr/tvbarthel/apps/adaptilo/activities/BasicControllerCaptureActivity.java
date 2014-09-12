package fr.tvbarthel.apps.adaptilo.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;

import fr.tvbarthel.apps.adaptilo.R;

/**
 * Activity used to scan a QrCode
 */
public class BasicControllerCaptureActivity extends CaptureActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Typeface minecraftiaTypeface = Typeface.createFromAsset(getAssets(), "fonts/Minecraftia.ttf");
        int defaultPadding = getResources().getDimensionPixelSize(R.dimen.default_padding);
        TextView statusView = (TextView) findViewById(R.id.status_view);
        statusView.setTypeface(minecraftiaTypeface);
        statusView.setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);
    }

}
