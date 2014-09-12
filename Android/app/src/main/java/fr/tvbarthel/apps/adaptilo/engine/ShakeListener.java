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
    private static final float SHAKE_BOUNDARY = 18.0f;

    /**
     * time in milli between two shake to considered that user is shaking
     */
    private static final int SHAKE_SPEED_BOUNDARY = 2000;

    /**
     * smoothing ratio for Low-Pass filter algorithm
     */
    private static final double LOW_PASS_FILTER_SMOOTHING = 3.0d;

    /**
     * last shake timestamp used to evaluated shake speed
     */
    private double mLastShakeTime = 0.0d;

    /**
     * last shake values used to evaluated distance between two shake
     */
    private float[] mLastShakeValues = new float[3];

    /**
     * last shake speed used for low pass filter algorithm
     */
    private double mLastSpeed = 0.0d;


    /**
     * called when shake is detected
     */
    public abstract void onShakeDetected();

    /**
     * called when device is being shaken
     *
     * @param speed shaking speed given in arbitrary unit.
     */
    public abstract void onShaking(double speed);


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float x = event.values[0];
            final float y = event.values[1];
            final float z = event.values[2];

            final float shakeAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));

            if (shakeAcceleration > SHAKE_BOUNDARY) {
                final double currentShake = System.currentTimeMillis();
                final double delay = currentShake - mLastShakeTime;
                if (delay <= SHAKE_SPEED_BOUNDARY) {
                    //user is shaking
                    final double shakeDist = Math.abs(x - mLastShakeValues[0])
                            + Math.abs(y - mLastShakeValues[1]) + Math.abs(z - mLastShakeValues[2]);
                    final double newSpeed = shakeDist / delay * 1000;
                    mLastSpeed = mLastSpeed + (newSpeed - mLastSpeed) / LOW_PASS_FILTER_SMOOTHING;
                    onShaking(mLastSpeed);
                } else {
                    //first shake
                    onShakeDetected();
                }
                mLastShakeTime = currentShake;
                mLastShakeValues[0] = x;
                mLastShakeValues[1] = y;
                mLastShakeValues[2] = z;
            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
