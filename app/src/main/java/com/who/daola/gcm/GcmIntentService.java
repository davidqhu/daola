package com.who.daola.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.who.daola.MainActivity;
import com.who.daola.R;
import com.who.daola.data.KnowhereData;
import com.who.daola.data.KnowhereMessage;
import com.who.daola.data.KnowhereMessageDataSource;
import com.who.daola.data.TrackerTargetContract;
import com.who.daola.data.TrackerTargetDataSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GcmIntentService extends IntentService {
    public static final String TAG = GcmIntentService.class.getName();
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;
    private KnowhereMessageDataSource mKnowhereDS;
    private TrackerTargetDataSource mTargetDS;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initializeDataSources();
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(TAG, "Received: " + extras.toString());
                processMessage(extras);
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.

            }
        }
        closeDataSources();
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_drawer)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void processMessage(Bundle bundle) {
        try {
            // Insert the received message to the message database
            sendNotification("Received: " + bundle.toString());
            KnowhereMessage message = mKnowhereDS.createKnowhereMessage(bundle);

            switch (message.getMessageType()) {
                case Add:
                    // Add the data from message to the db
                    KnowhereData data = KnowhereData.getKnowhereData(bundle).
                            add(getApplicationContext());

                    String receiverId = bundle.getString(
                            TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REG_ID);
                    if (receiverId == null || receiverId.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Cannot send add message because new reg id found.");
                    }

                    // create ack response message and added to the database
                    Map<String, String> map = new HashMap<>();
                    map.put(KnowhereMessage.MESSAGE_ID, message.getMessageId());
                    map.put(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REMOTE_ID,
                            Long.toString(data.getId()));
                    KnowhereMessage ack = mKnowhereDS.createKnowhereMessage(
                            UUID.randomUUID().toString(), KnowhereMessage.Type.Ack.toString(),
                            KnowhereMessage.getPayloadStringFromMap(map),
                            System.currentTimeMillis());

                    // Send ack response
                    GcmHelper.sendMessage(receiverId, ack);
                    return;
                case Update:
                    return;
                case Delete:
                    return;
                case Notification:
                    return;
                case Ack:
                    String messageId = message.getMessageId();
                    System.out.println("ack mid " + messageId);
                    KnowhereMessage originalMessage = mKnowhereDS.getKnowhereMessage(messageId);
                    Bundle originalPayload =
                            KnowhereMessage.getBundleFromPayload(originalMessage.getPayload());
                    updateRemoteId(originalPayload, Long.parseLong(bundle.getString(
                            TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REMOTE_ID)));
                    return;
                default:
                    return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(GcmIntentService.class.getName(), e.getMessage());
        }
    }

    private void initializeDataSources() {
        if (mKnowhereDS == null) {
            mKnowhereDS = new KnowhereMessageDataSource(this);
        }
        if (mTargetDS == null) {
            mTargetDS = new TrackerTargetDataSource(this,
                    TrackerTargetContract.TrackerEntry.TABLE_NAME);
        }
        try {
            mKnowhereDS.open();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening database: " + e);
            closeDataSources();
        }
    }

    private void closeDataSources() {
        if (mKnowhereDS != null) {
            mKnowhereDS.close();
        }
    }

    public void updateRemoteId(Bundle originalPayload, long remoteId)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        originalPayload.putString(BaseColumns._ID,
                originalPayload.getString(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REMOTE_ID));
        originalPayload.putLong(TrackerTargetContract.TrackerTargetBaseColumns.COLUMN_REMOTE_ID, remoteId);
        //if the original add was for tracker then the update is for target
        originalPayload.putString(KnowhereData.TABLE_NAME, TrackerTargetContract.TargetEntry.TABLE_NAME);
        KnowhereData data = KnowhereData.getKnowhereData(originalPayload);
        data.fromBundle(originalPayload);
        data.update(this);
    }
}