package fr.tvbarthel.apps.adaptilo.engine;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * shake listener used to detected shake events
 */
public abstract class ShakeListener implements SensorEventListener {

    /**
     * noise boundary for shake detection
     */
    private static final float SHAKE_BOUNDARY = 20.0f;

    /**
     * called when shake is detected
     */
    public abstract void onShakeDetected();

    /**
     * called when device is being shaken
     *
     * @param speed
     */
    public abstract void onShaking(float speed);


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float x = event.values[0];
            final float y = event.values[1];
            final float z = event.values[2];

            final float shakeAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));

            if (shakeAcceleration > SHAKE_BOUNDARY) {
                onShakeDetected();
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
