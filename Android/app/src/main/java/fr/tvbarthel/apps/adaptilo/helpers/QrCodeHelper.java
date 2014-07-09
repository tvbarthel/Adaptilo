package fr.tvbarthel.apps.adaptilo.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
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
     * Logcat
     */
    private static final String TAG = QrCodeHelper.class.getName();

    /**
     * Request code used by startActivityForResult.
     */
    public static final int REQUEST_CODE = IntentIntegrator.REQUEST_CODE;

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
     * Start an activity for scanning a QrCode.
     *
     * @param fragment             the {@link android.support.v4.app.Fragment} calling startActivityForResult.
     * @param scannerActivityClass the activity that will be launched.
     * @param prompt               the text to be displayed on screen.
     */
    public static void initiateQrCodeScan(Fragment fragment, Class<? extends CaptureActivity> scannerActivityClass, String prompt) {
        Intent intent = IntentIntegrator.createScanIntent(fragment.getActivity(), IntentIntegrator.QR_CODE_TYPES, prompt);
        intent.setClass(fragment.getActivity(), scannerActivityClass);
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * verifiy if scanned content from onActivityResult id well-formed
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     * @throws QrCodeException
     */
    public static EngineConfig verifyFromActivityResult(int requestCode, int resultCode, Intent data) throws QrCodeException {
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
     * @throws QrCodeException
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

    /**
     * helper not instantiable
     */
    private QrCodeHelper() {
    }
}
