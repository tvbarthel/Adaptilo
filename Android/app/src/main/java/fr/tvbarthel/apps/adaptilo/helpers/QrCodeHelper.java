package fr.tvbarthel.apps.adaptilo.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import fr.tvbarthel.apps.adaptilo.exceptions.QrCodeException;
import fr.tvbarthel.apps.adaptilo.models.EngineConfig;

/**
 * helper which verify if scanned info is well formed
 */
public final class QrCodeHelper {

    /**
     * Request code used by startActivityForResult.
     */
    public static final int REQUEST_CODE = IntentIntegrator.REQUEST_CODE;

    /**
     * Logcat
     */
    private static final String TAG = QrCodeHelper.class.getName();

    /**
     * uri scheme of QrCode content
     */
    private static final String URI_SCHEME = "adaptilo";

    /**
     * param key for room
     */
    private static final String URI_QUERY_PARAM_ROOM = "room";

    /**
     * param key for room
     */
    private static final String URI_QUERY_PARAM_ROLE = "role";

    /**
     * Param key to know if role should be replace if already registered.
     */
    private static final String URI_QUERY_PARAM_SHOULD_REPLACE = "replace";

    /**
     * Param key to know if the room should be created when it doesn't exist yet.
     */
    private static final String URI_QUERY_PARAM_SHOULD_CREATE = "create";

    /**
     * helper not instantiable
     */
    private QrCodeHelper() {
    }

    /**
     * Start an activity for scanning a QrCode.
     *
     * @param activity             the {@link android.app.Activity} which will handle the result.
     * @param scannerActivityClass the activity that will be launched.
     * @param prompt               the text to be displayed on screen.
     */
    public static void initiateQrCodeScan(Activity activity,
                                          Class<? extends CaptureActivity> scannerActivityClass, String prompt) {
        Intent intent = IntentIntegrator.createScanIntent(activity, IntentIntegrator.QR_CODE_TYPES, prompt);
        intent.setClass(activity, scannerActivityClass);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Verify if scanned content from
     * {@link android.app.Activity#onActivityResult(int, int, android.content.Intent)}
     * id well-formed
     *
     * @param requestCode requestCode from onActivityResult
     * @param resultCode  resultCode from onActivityResult
     * @param data        intent from onActivityResult
     * @return well configured EngineConfig or null is the intent wasn't a QrCode result_ok.
     * @throws QrCodeException thrown when QrCode is malformed.
     */
    public static EngineConfig verifyFromActivityResult(int requestCode, int resultCode,
                                                        Intent data) throws QrCodeException {
        EngineConfig config = null;
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null && resultCode == Activity.RESULT_OK) {
            config = verify(scanResult.getContents());
        }
        return config;
    }

    /**
     * verify if scanned content is well formed
     *
     * @param content info from QrCode
     * @return null if content not well formed
     * @throws QrCodeException thrown when QrCode is malformed.
     */
    public static EngineConfig verify(String content) throws QrCodeException {
        EngineConfig config = new EngineConfig();

        Uri scannedUri = Uri.parse(content);

        final String room = scannedUri.getQueryParameter(URI_QUERY_PARAM_ROOM);
        final String role = scannedUri.getQueryParameter(URI_QUERY_PARAM_ROLE);
        final String replace = scannedUri.getQueryParameter(URI_QUERY_PARAM_SHOULD_REPLACE);
        final String create = scannedUri.getQueryParameter(URI_QUERY_PARAM_SHOULD_CREATE);
        final String scheme = scannedUri.getScheme();
        final int port = scannedUri.getPort();
        final String host = scannedUri.getHost();
        final String gameName = scannedUri.getPath().substring(1);
        boolean shouldReplace = false;

        if (!URI_SCHEME.equals(scheme)) {
            Log.e(TAG, "QrCode uri scheme must correspond to adaptilo");
            throw new QrCodeException("QrCode uri scheme must correspond to adaptilo");
        }

        if (room == null) {
            Log.i(TAG, "Room query param not found, Qr_Code used to create a new room and not for joining a new one.");
        }

        if (role == null) {
            Log.e(TAG, "role query param not found");
            throw new QrCodeException("role query param not found");
        }

        if (port == -1) {
            Log.e(TAG, "serverPort not found");
            throw new QrCodeException("serverPort not found");
        }

        if (host == null) {
            Log.e(TAG, "websocket host not found");
            throw new QrCodeException("websocket host not found");
        }

        if (gameName == null) {
            Log.e(TAG, "fail to decode path");
            throw new QrCodeException("fail to decode path");
        }

        if (replace != null && Boolean.valueOf(replace)) {
            shouldReplace = true;
        }

        config.setServerUri(scannedUri);
        config.setUserRole(role);
        config.setGameRoom(room);
        config.setServerPort(port);
        config.setGameName(gameName);
        config.setShouldReplace(shouldReplace);
        return config;
    }
}
