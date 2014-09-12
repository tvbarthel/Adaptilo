package fr.tvbarthel.apps.adaptilo.helpers;

/**
 * define sensor listener state
 */
public final class SensorListenerHelper {

    /**
     * create listener
     */
    public static final int START = 0x00000000;

    /**
     * register listener to the sensor
     */
    public static final int RESUME = 0x00000001;

    /**
     * unregister listener to the sensor
     */
    public static final int PAUSE = 0x00000002;

    /**
     * delete listener
     */
    public static final int STOP = 0x00000003;

    /**
     * Helper non instantiable.
     */
    private SensorListenerHelper() {

    }
}
