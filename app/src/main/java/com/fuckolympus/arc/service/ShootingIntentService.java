package com.fuckolympus.arc.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.fuckolympus.arc.camera.api.ShutterMode;
import com.fuckolympus.arc.session.Session;
import com.fuckolympus.arc.util.Callback;
import com.fuckolympus.arc.util.DefaultFailureCallback;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ShootingIntentService extends IntentService {

    private static final String ACTION_PARTIAL_PHASE = "com.fuckolympus.arc.service.action.PARTIAL_PHASE";
    private static final String ACTION_TOTALITY_PHASE = "com.fuckolympus.arc.service.action.TOTALITY_PHASE";

    // TODO: Rename parameters
    private static final String FRAMES_COUNT = "com.fuckolympus.arc.service.extra.FRAMES_COUNT";
    private static final String MS_INTERVAL = "com.fuckolympus.arc.service.extra.MS_INTERVAL";

    private Session session;

    public ShootingIntentService() {
        super("ShootingIntentService");
        session = Session.getInstance();
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionPartialPhase(Context context, long framesNumber, long msInterval) {
        Intent intent = new Intent(context, ShootingIntentService.class);
        intent.setAction(ACTION_PARTIAL_PHASE);
        intent.putExtra(FRAMES_COUNT, framesNumber);
        intent.putExtra(MS_INTERVAL, msInterval);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionTotalityPhase(Context context) {
        Intent intent = new Intent(context, ShootingIntentService.class);
        intent.setAction(ACTION_TOTALITY_PHASE);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PARTIAL_PHASE.equals(action)) {
                final long framesCount = intent.getLongExtra(FRAMES_COUNT, 0L);
                final long msInterval = intent.getLongExtra(MS_INTERVAL, 5000L);
                handleActionPartialPhase(framesCount, msInterval);
            } else if (ACTION_TOTALITY_PHASE.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionTotalityPhase();
            }
        }
    }

    private void handleActionPartialPhase(final long framesCount, final long msInterval) {
        Log.w(this.getClass().getName(), "start partial phase. Frames count: " + framesCount);

        shoot(1L, framesCount, msInterval);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionTotalityPhase() {
        Log.w(this.getClass().getName(), "start totality phase");

        // TODO: Handle action Baz
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void shoot(final long currentFrame, final long frameCount, final long msInterval) {
        session.getCameraApi().executeShutter(ShootingIntentService.this, ShutterMode.FST_SND_PUSH,
                new Callback<String>() {
                    @Override
                    public void apply(String arg) {
                        session.getCameraApi().executeShutter(ShootingIntentService.this, ShutterMode.SND_FST_RELEASE,
                                new Callback<String>() {
                                    @Override
                                    public void apply(final String arg) {
                                        Log.w(ShootingIntentService.this.getClass().getName(), "current frame " + currentFrame);
                                        if (currentFrame < frameCount) {
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    shoot(currentFrame + 1, frameCount, msInterval);
                                                }
                                            }, msInterval);
                                        }
                                    }
                                },
                                new DefaultFailureCallback(ShootingIntentService.this));
                    }
                }, new DefaultFailureCallback(ShootingIntentService.this));
    }
}
