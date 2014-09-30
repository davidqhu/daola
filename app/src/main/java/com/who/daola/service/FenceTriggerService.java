package com.who.daola.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.who.daola.data.FenceDataSource;
import com.who.daola.data.TargetDataSource;
import com.who.daola.data.TriggerDataSource;

import java.sql.SQLException;

/**
 * Created by dave on 9/22/2014.
 */
public class FenceTriggerService extends Service {

    private static FenceTriggerService mInstance;
    private static final String TAG = FenceTriggerService.class.getName();
    private TriggerDataSource mTriggerDS;
    private TargetDataSource mTargetDS;
    private FenceDataSource mFenceDS;

    public static FenceTriggerService getInstance(){
        if (mInstance==null){
            throw new RuntimeException("Service have not been started.");
        }
        return mInstance;
    }

    public void updateDataSource(){
        Log.i(TAG, "Update datasource request received");
        Log.i(TAG, "Target size: " + mTargetDS.getAllTargets().size());
    }

    @Override
    public void onCreate() {
        mInstance = this;
        initializeDataSources();
        //TODO verify gmscore connection
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
        Log.i(TAG, "Starting service");
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        doWork();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    public void doWork() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    //get datasource
                    Log.i(TAG, "I'm alive");
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e){

                    }
                    //request geofence update
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
}
