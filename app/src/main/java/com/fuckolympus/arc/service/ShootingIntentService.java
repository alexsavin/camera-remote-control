package com.fuckolympus.arc.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.fuckolympus.arc.session.Session;

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
    //private static final String EXTRA_PARAM1 = "com.fuckolympus.arc.service.extra.PARAM1";
    //private static final String EXTRA_PARAM2 = "com.fuckolympus.arc.service.extra.PARAM2";

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
    public static void startActionPartialPhase(Context context) {
        Intent intent = new Intent(context, ShootingIntentService.class);
        intent.setAction(ACTION_PARTIAL_PHASE);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
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
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionPartialPhase();
            } else if (ACTION_TOTALITY_PHASE.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionTotalityPhase();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPartialPhase() {
        Log.w(this.getClass().getName(), "start partial phase");

        // TODO: Handle action Foo
        //throw new UnsupportedOperationException("Not yet implemented");
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
}
