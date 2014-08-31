package fr.tvbarthel.apps.adaptilo.engine;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Engine used to proceed clap detection.
 */
public class ClapEngine {

    /**
     * Log cat.
     */
    private static final String TAG = ClapEngine.class.getSimpleName();

    /**
     * Name of the temp file used while recording.
     */
    private static final String RECORDER_FILE_NAME = "clap.3gp";

    /**
     * Delay in milli seconds between two getMaxAmplitude.
     */
    private static final int TICKING_TIME_IN_MILLI = 200;

    private static final int AMPLITUDE_THRESHOLD = 18000;

    /**
     * MediaRecorder used to detected clap from microphone.
     */
    private MediaRecorder mMediaRecorder;

    /**
     * Temp file used while recording.
     */
    private File mTempFile;

    /**
     * True while the engine is running.
     */
    private boolean mRecording;

    /**
     * True when the engine is paused.
     */
    private boolean mIsPaused;

    /**
     * Last max amplitude recorded.
     */
    private int mLastMaxAmplitude;

    /**
     * Thread used to perform clap detection off the ui thread.
     */
    private Thread mThread;

    /**
     * Handler used to catch clap event from non ui Thread which perform the detection.
     */
    private ClapHandler mClapHandler;

    public ClapEngine(Context context, ClapListener listener) {
        mTempFile = new File(context.getExternalFilesDir(null), RECORDER_FILE_NAME);

        initMediaRecorder();

        initThread();

        //Handler attach to the ui thread.
        mClapHandler = new ClapHandler(listener);

        mLastMaxAmplitude = -1;
    }

    /**
     * Start the Clap Engine.
     * <p/>
     * Should be linked to the hosting activity life cycle.
     */
    public void start() {

        if (mMediaRecorder == null) {
            initMediaRecorder();
        }

        if (mThread == null) {
            initThread();
        }

        mRecording = true;

        //start thread
        mThread.start();
    }

    /**
     * Resume the Clap Engine.
     * <p/>
     * Should be linked to the hosting activity life cycle.
     */
    public void resume() {
        if (mIsPaused) {
            initMediaRecorder();
            mIsPaused = false;
        }
    }


    /**
     * Pause the Clap Engine.
     * <p/>
     * Should be linked to the hosting activity life cycle.
     */
    public void pause() {
        mIsPaused = true;
        releaseMediaRecorder();
    }

    /**
     * Stop the Clap Engine.
     * <p/>
     * Should be linked to the hosting activity life cycle.
     */
    public void stop() {
        if (mMediaRecorder != null) {
            releaseMediaRecorder();
        }

        mTempFile.delete();

        mThread.interrupt();
        mThread = null;

        mRecording = false;
        mIsPaused = true;
    }

    /**
     * Used to initialize the media recorder.
     */
    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(mTempFile.getAbsolutePath());
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to initialize the Thread on which clap processing while be achieved.
     */
    private void initThread() {
        mThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (mRecording) {
                    if (!mIsPaused) {
                        processNewAmplitude(mMediaRecorder.getMaxAmplitude());
                    }
                    try {
                        sleep(TICKING_TIME_IN_MILLI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /**
     * Process the max amplitude since the last call in order to detect if the user clap his hands.
     *
     * @param newAmplitude new amplitude from {@link android.media.MediaRecorder#getMaxAmplitude()}
     */
    private void processNewAmplitude(int newAmplitude) {
        if (mLastMaxAmplitude == -1) {
            mLastMaxAmplitude = newAmplitude;
        } else {
            int delta = Math.abs(mLastMaxAmplitude - newAmplitude);
            if (delta > AMPLITUDE_THRESHOLD) {

                //send message
                Message message = mClapHandler.obtainMessage(ClapHandler.CLAP_DETECTED);
                message.sendToTarget();
                mLastMaxAmplitude = 0;
            } else {
                mLastMaxAmplitude = newAmplitude;
            }
        }
    }

    /**
     * Release the media recorder to avoid recording failure for other application.
     */
    private void releaseMediaRecorder() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    /**
     * Handler which will be attached to the ui thread in order to encapsulate event propagation and
     * potential ui modification
     */
    private static class ClapHandler extends Handler {

        /**
         * "What" used when clap detected event is catch.
         */
        protected static final int CLAP_DETECTED = 0x00000010;
        /**
         * Listener used to catch clap event.
         */
        private final WeakReference<ClapListener> mListener;

        public ClapHandler(ClapListener listener) {
            mListener = new WeakReference<ClapListener>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLAP_DETECTED:

                    //propagate clap event on ui thread
                    mListener.get().onClapDetected();
            }
        }
    }
}
