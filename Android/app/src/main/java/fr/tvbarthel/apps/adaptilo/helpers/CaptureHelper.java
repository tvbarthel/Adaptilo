package fr.tvbarthel.apps.adaptilo.helpers;

import android.app.Activity;
import android.content.Intent;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;

/**
 * An helper class used to start a {@link com.google.zxing.client.android.CaptureActivity}
 */
public final class CaptureHelper {

    /**
     * Start an activity for scanning a QrCode.
     *
     * @param startActivity        the {@link android.app.Activity} calling startActivityForResult.
     * @param scannerActivityClass the activity that will be launched.
     * @param prompt               the text to be displayed on screen.
     */
    public static void initiateQrCodeScan(Activity startActivity, Class<? extends CaptureActivity> scannerActivityClass, String prompt) {
        Intent intent = IntentIntegrator.createScanIntent(startActivity, IntentIntegrator.QR_CODE_TYPES, prompt);
        intent.setClass(startActivity, scannerActivityClass);
        startActivity.startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
    }

    // Non instantiable class.
    private CaptureHelper() {
    }
}
