package fr.tvbarthel.apps.adaptilo.helpers;

import android.net.Uri;
import android.util.Log;

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
     * verify if scanned content is well formed
     *
     * @param content info from QrCode
     * @return null if content not well formed
     */
    public static EngineConfig verify(String content) throws QrCodeException {
        EngineConfig config = new EngineConfig();

        Uri scannedUri = Uri.parse(content);

        final String room = scannedUri.getQueryParameter(URI_QUERY_PARAM_ROOM);
        final String role = scannedUri.getQueryParameter(URI_QUERY_PARAM_ROLE);
        final String scheme = scannedUri.getScheme();
        final int port = scannedUri.getPort();
        final String host = scannedUri.getHost();
        final String gameName = scannedUri.getPath();

        if (!URI_SCHEME.equals(scheme)) {
            Log.e(TAG, "QrCode uri scheme must correspond to adaptilo");
            throw new QrCodeException("QrCode uri scheme must correspond to adaptilo");
        }

        if (room == null) {
            Log.e(TAG, "room query param not found");
            throw new QrCodeException("room query param not found");
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

        config.setServerUri(host + gameName);
        config.setUserRole(role);
        config.setGameRoom(room);
        config.setServerPort(port);
        return config;
    }

    /**
     * helper not instantiable
     */
    private QrCodeHelper() {

    }
}
