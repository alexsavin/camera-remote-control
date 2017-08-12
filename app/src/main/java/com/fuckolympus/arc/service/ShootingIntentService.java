package com.fuckolympus.arc.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.fuckolympus.arc.camera.api.CameraApi;
import com.fuckolympus.arc.camera.api.ShutterMode;
import com.fuckolympus.arc.camera.command.Command;
import com.fuckolympus.arc.camera.command.CommandChain;
import com.fuckolympus.arc.session.Session;
import com.fuckolympus.arc.util.Callback;
import com.fuckolympus.arc.util.DefaultFailureCallback;

import java.util.Calendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 *
 * helper methods.
 */
public class ShootingIntentService extends IntentService {

    public static final String FRAME_TAKEN_ACTION = "com.fuckolympis.arc.FRAME_TAKEN_ACTION";
    public static final String SHOOTING_COMPLETE_ACTION = "com.fuckolympis.arc.SHOOTING_COMPLETE_ACTION";
    public static final String EXTENDED_DATA_STATUS = "com.fuckolympis.arc.STATUS";

    private static final String ACTION_PARTIAL_PHASE = "com.fuckolympus.arc.service.action.PARTIAL_PHASE";
    private static final String ACTION_TOTALITY_PHASE = "com.fuckolympus.arc.service.action.TOTALITY_PHASE";

    private static final String FRAMES_COUNT = "com.fuckolympus.arc.service.extra.FRAMES_COUNT";
    private static final String MS_INTERVAL = "com.fuckolympus.arc.service.extra.MS_INTERVAL";
    private static final String TOTALITY_TIME = "com.fuckolympus.arc.service.extra.TOTALITY_TIME";
    private static final String SHUT_SPEED_SET = "com.fuckolympus.arc.service.extra.SHUT_SPEED_SET";

    private Session session;

    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;

    public ShootingIntentService() {
        super("ShootingIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        session = Session.getInstance(this.getApplicationContext());
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionPartialPhase(Context context, long framesNumber, long msInterval, long totalityTime) {
        Intent intent = new Intent(context, ShootingIntentService.class);
        intent.setAction(ACTION_PARTIAL_PHASE);
        intent.putExtra(FRAMES_COUNT, framesNumber);
        intent.putExtra(MS_INTERVAL, msInterval);
        intent.putExtra(TOTALITY_TIME, totalityTime);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionTotalityPhase(Context context, String[] shutSpeedSet) {
        Intent intent = new Intent(context, ShootingIntentService.class);
        intent.setAction(ACTION_TOTALITY_PHASE);
        intent.putExtra(SHUT_SPEED_SET, shutSpeedSet);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // acquire locks
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.fuckolympus.arc.power.lock");
            wakeLock.acquire();

            WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "com.fuckolympus.arc.wifi.lock");
            wifiLock.acquire();

            final String action = intent.getAction();
            if (ACTION_PARTIAL_PHASE.equals(action)) {
                final long framesCount = intent.getLongExtra(FRAMES_COUNT, 0L);
                final long msInterval = intent.getLongExtra(MS_INTERVAL, 30000L);
                final long totalityTime = intent.getLongExtra(TOTALITY_TIME, Long.MAX_VALUE);
                handleActionPartialPhase(framesCount, msInterval, totalityTime);
            } else if (ACTION_TOTALITY_PHASE.equals(action)) {
                final String[] shutSpeedSet = intent.getStringArrayExtra(SHUT_SPEED_SET);
                handleActionTotalityPhase(shutSpeedSet);
            }
        }
    }

    private void handleActionPartialPhase(final long framesCount, final long msInterval, long totalityTime) {
        Log.w(this.getClass().getName(), "start partial phase. Frames count: " + framesCount);
        shoot(1L, framesCount, msInterval, totalityTime);
    }

    private void handleActionTotalityPhase(final String[] shutSpeedSet) {
        Log.w(this.getClass().getName(), "start totality phase. Frames count: " + shutSpeedSet.length);

        CommandChain.CommandChainBuilder builder = new CommandChain.CommandChainBuilder();
        for (final String shutSpeedValue : shutSpeedSet) {
            builder.addCommand(new Command<String>() {
                @Override
                public void apply(Callback<String> nextCommandCallback, Callback<String> failureCallback) {
                    session.getCameraApi().switchToRecMode(nextCommandCallback, failureCallback);
                }
            }).addCommand(new Command<String>() {
                @Override
                public void apply(Callback<String> nextCommandCallback, Callback<String> failureCallback) {
                    session.getCameraApi().setCameraProp(CameraApi.SHUTSPEEDVALUE_PROP, shutSpeedValue.trim(),
                            nextCommandCallback, failureCallback);
                }
            }).addCommand(new Command<String>() {
                @Override
                public void apply(Callback<String> nextCommandCallback, Callback<String> failureCallback) {
                    session.getCameraApi().switchToShutterMode(nextCommandCallback, failureCallback);
                }
            }).addCommand(new Command<String>() {
                @Override
                public void apply(Callback<String> nextCommandCallback, Callback<String> failureCallback) {
                    session.getCameraApi().executeShutter(ShutterMode.FST_SND_PUSH,
                            nextCommandCallback, failureCallback);
                }
            }).addCommand(new Command<String>() {
                @Override
                public void apply(final Callback<String> nextCommandCallback, Callback<String> failureCallback) {
                    session.getCameraApi().executeShutter(ShutterMode.SND_FST_RELEASE,
                            new Callback<String>() {
                                @Override
                                public void apply(final String arg) {
                                    Log.w(ShootingIntentService.this.getClass().getName(), "shooting with shutter speed " + shutSpeedValue);

                                    Intent localIntent = new Intent(FRAME_TAKEN_ACTION).putExtra(EXTENDED_DATA_STATUS, shutSpeedValue);
                                    LocalBroadcastManager.getInstance(ShootingIntentService.this).sendBroadcast(localIntent);

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!shutSpeedValue.equals(shutSpeedSet[shutSpeedSet.length - 1])) {
                                                nextCommandCallback.apply(arg);
                                            } else {
                                                Intent completeIntent = new Intent(SHOOTING_COMPLETE_ACTION);
                                                LocalBroadcastManager.getInstance(ShootingIntentService.this).sendBroadcast(completeIntent);

                                                // release locks
                                                wifiLock.release();
                                                wakeLock.release();
                                            }
                                        }
                                    }, calculateDelay(shutSpeedValue));
                                }
                            }, failureCallback);
                }
            });
        }

        CommandChain chain = builder.build();
        chain.run(this);
    }

    private int calculateDelay(String shutSpeedValue) {
        return 4500 + (shutSpeedValue.contains("\"") ? (1000 * Integer.valueOf(shutSpeedValue.trim().replace("\"", ""))) : 100);
    }

    private void shoot(final long currentFrame, final long frameCount, final long msInterval, final long totalityTime) {
        session.getCameraApi().executeShutter(ShutterMode.FST_SND_PUSH,
                new Callback<String>() {
                    @Override
                    public void apply(String arg) {
                        session.getCameraApi().executeShutter(ShutterMode.SND_FST_RELEASE,
                                new Callback<String>() {
                                    @Override
                                    public void apply(final String arg) {
                                        Log.w(ShootingIntentService.this.getClass().getName(), "current frame " + currentFrame);

                                        Intent localIntent = new Intent(FRAME_TAKEN_ACTION).putExtra(EXTENDED_DATA_STATUS, String.valueOf(currentFrame));
                                        LocalBroadcastManager.getInstance(ShootingIntentService.this).sendBroadcast(localIntent);

                                        if (currentFrame < frameCount && beforeTotality(totalityTime)) {
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    shoot(currentFrame + 1, frameCount, msInterval, totalityTime);
                                                }
                                            }, msInterval);
                                        } else {
                                            Intent completeIntent = new Intent(SHOOTING_COMPLETE_ACTION);
                                            LocalBroadcastManager.getInstance(ShootingIntentService.this).sendBroadcast(completeIntent);

                                            // release locks
                                            wifiLock.release();
                                            wakeLock.release();
                                        }
                                    }
                                },
                                new DefaultFailureCallback(ShootingIntentService.this));
                    }
                }, new DefaultFailureCallback(ShootingIntentService.this));
    }

    private boolean beforeTotality(long totalityTime) {
        Calendar c = Calendar.getInstance();
        long ms = c.getTimeInMillis();
        return ms < (totalityTime - 30000);
    }
}
