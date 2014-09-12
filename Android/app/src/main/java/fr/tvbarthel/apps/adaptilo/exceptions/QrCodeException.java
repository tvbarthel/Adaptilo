package fr.tvbarthel.apps.adaptilo.exceptions;

/**
 * QrCode Exception throw when scanned QrCode is malformed
 */
public class QrCodeException extends Exception {

    /**
     * Default constructor.
     *
     * @param message exception message.
     */
    public QrCodeException(String message) {
        super(message);
    }
}
