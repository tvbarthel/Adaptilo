package fr.tvbarthel.apps.adaptilo.helpers;

import android.app.Activity;
import android.content.Intent;

import com.google.zxing.integration.android.IntentIntegrator;

import fr.tvbarthel.apps.adaptilo.activities.BasicControllerCaptureActivity;

public final class CaptureHelper {

    public static void initiateScan(Activity activity, CharSequence stringDesiredBarcodeFormats, String prompt) {
        Intent intent = IntentIntegrator.createScanIntent(activity, stringDesiredBarcodeFormats, prompt);
        intent.setClass(activity, BasicControllerCaptureActivity.class);
        activity.startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
    }

    private CaptureHelper() {
    }
}
