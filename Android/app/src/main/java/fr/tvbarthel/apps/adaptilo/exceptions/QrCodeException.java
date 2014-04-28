package fr.tvbarthel.apps.adaptilo.exceptions;

/**
 * QrCode Exception throw when scanned QrCode is malformed
 */
public class QrCodeException extends Exception {

    public QrCodeException(String message) {
        super(message);
    }
}
