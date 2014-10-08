package com.who.daola.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;
import com.who.daola.MainActivity;
import com.who.daola.R;
import com.who.daola.data.Fence;
import com.who.daola.data.FenceDataSource;
import com.who.daola.data.TargetDataSource;
import com.who.daola.data.Trigger;
import com.who.daola.data.TriggerContract;
import com.who.daola.data.TriggerDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dave on 9/22/2014.
 */
public class FenceTriggerService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationClient.OnAddGeofencesResultListener {


    /*
 * Define a request code to send to Google Play services
 * This code is returned in Activity.onActivityResult
 */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Holds the location client
    private LocationClient mLocationClient;
    // Stores the PendingIntent used to request geofence monitoring
    private PendingIntent mGeofenceRequestIntent;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;

    private static FenceTriggerService mInstance;
    private static final String TAG = FenceTriggerService.class.getName();
    private TriggerDataSource mTriggerDS;
    private TargetDataSource mTargetDS;
    private FenceDataSource mFenceDS;
    private List<Trigger> mTriggersList;

    public static final String INTENT_TYPE = "type";

    public static enum IntentType {
        FENCE_TRIGGERED, DATASOURCE_UPDATE, DATASOURCE_DELETE, DATASOURCE_ADD
    }


    public static FenceTriggerService getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("Service have not been started.");
        }
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
            mTargetDS = new TargetDataSource(this);
        }
        if (mFenceDS == null) {
            mFenceDS = new FenceDataSource(this);
        }
        if (mTriggerDS == null) {
            mTriggerDS = new TriggerDataSource(this);
        }
        try {
            mTargetDS.open();
            mFenceDS.open();
            mTriggerDS.open();
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

                if (intent==null){
                    Log.i(TAG, "Intent is null");
                }
                Log.i(TAG, intent.toString());
                IntentType type = (IntentType) intent.getSerializableExtra(INTENT_TYPE);
                if (type==null){
                    mTriggersList = mTriggerDS.getAllTriggers();
                    Log.i(TAG, "add geofences");
                    addGeofences();
                    return;
                }
                switch (type) {
                    case FENCE_TRIGGERED:
                        Log.i(TAG, "fence triggered");
                        showNotification();
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
        mLocationClient = new LocationClient(this, this, this);
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is underway
            mInProgress = true;
            // Request a connection from the client to Location Services
            mLocationClient.connect();
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
        return new Geofence.Builder()
                .setRequestId(trigger.getFence() + "." + trigger.getTarget())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER) //trigger.getTransitionType())
                .setCircularRegion(
                        fence.getLatitude(), fence.getLongitude(), fence.getRadius())
                .setExpirationDuration(trigger.getDuration())
                .build();
    }

    public PendingIntent getPendingIntent(IntentType type, Trigger trigger) {
        // Create an explicit Intent
        Intent intent = new Intent(this, FenceTriggerService.class);
        intent.putExtra(INTENT_TYPE, type);
        intent.putExtra(TriggerContract.TriggerEntry.COLUMN_FENCE, trigger.getFence());
        intent.putExtra(TriggerContract.TriggerEntry.COLUMN_TARGET, trigger.getTarget());

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
        for (final Trigger trigger : mTriggersList) {
            List<Geofence> fences = new ArrayList<Geofence>() {{add(toGeofence(mFenceDS.getFence(trigger.getFence()), trigger));}};
            // Send a request to add the current geofences
            mLocationClient.addGeofences(
                    fences,
                    getPendingIntent(IntentType.FENCE_TRIGGERED, trigger), this);

        }
    }

    @Override
    public void onDisconnected() {
        // Turn off the request flag
        mInProgress = false;
        // Destroy the current location client
        mLocationClient = null;
    }

    @Override
    public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
            // If adding the geofences was successful
            if (LocationStatusCodes.SUCCESS == statusCode) {
            /*
             * Handle successful addition of geofences here.
             * You can send out a broadcast intent or update the UI.
             * geofences into the Intent's extended data.
             */
                Log.i(TAG, "Geofence request added successfully for " + geofenceRequestIds[0]);
            } else {
                // If adding the geofences failed
            /*
             * Report errors here.
             * You can log the error using Log.e() or update
             * the UI.
             */
                Log.e(TAG, "Geofence request FAILED to add for " + geofenceRequestIds[0]);
            }
            // Turn off the in progress flag and disconnect the client
            mInProgress = false;
            mLocationClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Turn off the request flag
        mInProgress = false;
        /*
         * If the error has a resolution, start a Google Play services
         * activity to resolve it.
         */
        if (connectionResult.hasResolution()) {
            Log.e(TAG, "Connection failed but there is resolution.");
        } else {
            // Get the error code
            int errorCode = connectionResult.getErrorCode();
            Log.e(TAG, "Connection failed with error code: " + errorCode);
        }
    }

    private void showNotification(){
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

    private int getNextNotificationId(){
        return 5;
    }
}
