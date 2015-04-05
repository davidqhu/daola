package com.who.daola.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationStatusCodes;
import com.who.daola.MainActivity;
import com.who.daola.R;
import com.who.daola.data.Fence;
import com.who.daola.data.FenceDataSource;
import com.who.daola.data.NotificationDataSource;
import com.who.daola.data.TargetDataSource;
import com.who.daola.data.TrackerTargetContract;
import com.who.daola.data.Trigger;
import com.who.daola.data.TriggerDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dave on 9/22/2014.
 */
public class FenceTriggerService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = FenceTriggerService.class.getName();

    /*
 * Define a request code to send to Google Play services
 * This code is returned in Activity.onActivityResult
 */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    // Stores the PendingIntent used to request geofence monitoring
    private PendingIntent mGeofenceRequestIntent;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;

    private static FenceTriggerService mInstance;

    private List<Trigger> mTriggersList;


    private TriggerDataSource mTriggerDS;
    private TargetDataSource mTargetDS;
    private FenceDataSource mFenceDS;
    private NotificationDataSource mNotificationDS;
    private GoogleApiClient mGoogleApiClient;

    public static final String INTENT_TYPE = "type";

    public static final int ADD_FENCE = 1;
    public static final int FENCE_TRIGGERED = 2;
    public static final int DATASOURCE_UPDATE = 3;
    public static final int DATASOURCE_DELETE = 4;
    public static final int DATASOURCE_ADD = 5;

    /**
     * Get the service instance.
     *
     * @return null is returned if the service is not started.
     */
    public static FenceTriggerService getInstance() {
        return mInstance;
    }

    public void updateDataSource() {
        Log.i(TAG, "Update datasource request received");
        Log.i(TAG, "Target size: " + mTargetDS.getAllTargets().size());
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        mInstance = this;
        initializeDataSources();
    }

    private void initializeDataSources() {
        if (mTargetDS == null) {
            mTargetDS = new TargetDataSource(this, TrackerTargetContract.TargetEntry.TABLE_NAME);
        }
        if (mFenceDS == null) {
            mFenceDS = new FenceDataSource(this);
        }
        if (mTriggerDS == null) {
            mTriggerDS = new TriggerDataSource(this);
        }
        if (mNotificationDS == null) {
            mNotificationDS = new NotificationDataSource(this);
        }
        try {
            mTargetDS.open();
            mFenceDS.open();
            mTriggerDS.open();
            mNotificationDS.open();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening database: " + e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        doWork(intent);
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    public void doWork(final Intent intent) {
        new Thread(new Runnable() {
            public void run() {
                int intentAction = ADD_FENCE;
                if (intent != null && intent.getAction() != null) {
                    intentAction = Integer.parseInt(intent.getAction());
                }

                switch (intentAction) {
                    case ADD_FENCE:
                        Log.i(TAG, "add geofences");
                        mTriggersList = mTriggerDS.getAllTriggers();
                        addGeofences();
                        break;
                    case FENCE_TRIGGERED:
                        Log.i(TAG, "fence triggered");
                        showNotification();
                        createNotificationRecord(intent);
                        break;
                    case DATASOURCE_ADD:
                        Log.i(TAG, "datasource added");
                        break;

                    case DATASOURCE_UPDATE:
                        Log.i(TAG, "datasource updated");
                        break;
                    case DATASOURCE_DELETE:
                        break;
                    default:
                        Log.i(TAG, "datasource deleted");
                        break;
                }

            }
        }).start();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Shutting down service");
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    private boolean servicesConnected() {
        Intent intent = new Intent(this,
                FenceTriggerService.class);
        PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(TAG,
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            Log.d(TAG, "Google Play services is NOT available");
            stopSelf();
            return false;
        }

    }

    /**
     * Start a request for geofence monitoring by calling
     * LocationClient.connect().
     */
    public void addGeofences() {
        /*
         * Test for Google Play services after setting the request type.
         * If Google Play services isn't present, the proper request
         * can be restarted.
         */
        if (!servicesConnected()) {
            return;
        }
        /*
         * Create a new location client object. Since the current
         * activity class implements ConnectionCallbacks and
         * OnConnectionFailedListener, pass the current activity object
         * as the listener for both parameters
         */
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is underway
            mInProgress = true;
            // Request a connection from the client to Location Services
            mGoogleApiClient.connect();
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
        }
    }

    public Geofence toGeofence(Fence fence, Trigger trigger) {
        // Build a new Geofence object
        Geofence.Builder builder =  new Geofence.Builder()
                .setRequestId(trigger.getFence() + "." + trigger.getTarget())
                .setTransitionTypes(trigger.getTransitionType())
                .setCircularRegion(
                        fence.getLatitude(), fence.getLongitude(), fence.getRadius())
                .setExpirationDuration(trigger.getDuration());
        if (trigger.isTranstionTypeEnabled(Geofence.GEOFENCE_TRANSITION_DWELL)){
            // TODO set the actual delay
            builder.setLoiteringDelay(300000);
        }
        return builder.build();
    }

    public PendingIntent getPendingIntent(int intentAction, Trigger trigger) {
        // Create an explicit Intent
        Intent intent = new Intent(this, FenceTriggerService.class);
        intent.setAction(Integer.toString(intentAction));
        /*
         * Return the PendingIntent
         */
        return PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "LocationClient is connected.");
        for (final Trigger trigger : mTriggersList) {
            List<Geofence> fences = new ArrayList<Geofence>() {{
                add(toGeofence(mFenceDS.getFence(trigger.getFence()), trigger));
            }};

            // Send a request to add the current geofences
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
                    new GeofencingRequest.Builder().addGeofences(fences).build(),
                    getPendingIntent(FENCE_TRIGGERED, trigger));

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Turn off the request flag
        mInProgress = false;
        if (connectionResult.hasResolution()) {
            Log.e(TAG, "Connection failed but there is resolution.");
        } else {
            // Get the error code
            int errorCode = connectionResult.getErrorCode();
            Log.e(TAG, "Connection failed with error code: " + errorCode);
        }
    }

    private void createNotificationRecord(Intent intent) {
        // First check for errors
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            // Get the error code with a static method
            int errorCode = event.getErrorCode();
            // Log the error
            Log.e(TAG,
                    "Location Services error: " +
                            Integer.toString(errorCode));
            /*
             * You can also send the error code to an Activity or
             * Fragment with a broadcast Intent
             */
        /*
         * If there's no error, get the transition type and the IDs
         * of the geofence or geofences that triggered the transition
         */
        } else {
            // Get the type of transition (entry or exit)
            int transitionType =
                    event.getGeofenceTransition();
                    event.getGeofenceTransition();
            Log.i(TAG, "transition type: " + transitionType);
            // Test that a valid transition was reported
            if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                List<Geofence> triggerList =
                        event.getTriggeringGeofences();
                Log.i(TAG, "triggered fence count: " + triggerList.size());
                String[] triggerIds = new String[triggerList.size()];

                for (Geofence fence : triggerList) {
                    long fenceId = getFenceId(fence.getRequestId());
                    long targetId = getTargetId(fence.getRequestId());
                    Log.i(TAG, "fence triggered: " + fenceId + " target id: " + targetId);
                    mNotificationDS.createNotification(fenceId, targetId, System.currentTimeMillis(), transitionType);
                }
                /*
                 * At this point, you can store the IDs for further use
                 * display them, or display the details associated with
                 * them.
                 */
            } else {
                Log.e(TAG, "Geofence transition error: " + Integer.toString(transitionType));
            }
        }
    }

    private long getFenceId(String geoFenceId) {
        return Long.parseLong(geoFenceId.substring(0, geoFenceId.indexOf('.')));
    }

    private long getTargetId(String geoFenceId) {
        return Long.parseLong(geoFenceId.substring(geoFenceId.indexOf('.') + 1));
    }

    private void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_drawer)
                        .setContentTitle("Fence triggered")
                        .setContentText("Hello World!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(getNextNotificationId(), mBuilder.build());
    }


    private int getNextNotificationId() {
        return 5;
    }
}
