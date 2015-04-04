package com.who.daola;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.who.daola.data.NotificationDataSource;
import com.who.daola.data.Target;
import com.who.daola.data.TargetDataSource;
import com.who.daola.data.TriggerDataSource;

import java.sql.SQLException;

public class DetailedTargetActivity extends Activity {

    public final String TAG = DetailedTargetActivity.class.getName();
    private Target mTarget;
    private TargetDataSource mTargetDS;
    private TriggerDataSource mTriggerDS;
    private NotificationDataSource mNotificationDS;
    private TextView mNickName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_target);

        mTarget = (Target) getIntent().getSerializableExtra("target");
        mNickName = (TextView) findViewById(R.id.first_name_textview);
        updateTextViews();
    }

    private void updateTextViews() {

        mNickName.setText(mTarget.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailed_target, menu);
        return true;
    }

    private void initializeDataSources() {
        if (mTargetDS == null) {
            mTargetDS = new TargetDataSource(this);
        }
        if (mTriggerDS == null) {
            mTriggerDS = new TriggerDataSource(this);
        }
        if (mNotificationDS == null) {
            mNotificationDS = new NotificationDataSource(this);
        }
        try {
            mTargetDS.open();
            mTriggerDS.open();
            mNotificationDS.open();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening database: " + e);
            closeDataSources();
        }
    }

    private void closeDataSources() {
        Log.i(TAG, "closing datasources");
        if (mTargetDS != null) {
            mTargetDS.close();
        }
        if (mTriggerDS != null) {
            mTriggerDS.close();
        }
        if (mNotificationDS != null) {
            mNotificationDS.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeDataSources();
        mTarget = mTargetDS.getTarget(mTarget.getId());
        updateTextViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDataSources();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, AddTargetActivity.class);
            intent.putExtra("target", mTarget);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_delete && mTarget != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_delete_target_message)
                    .setTitle(R.string.dialog_delete_title);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    mTargetDS.deleteTarget(mTarget);
                    mTriggerDS.deleteTriggerByTarget(mTarget.getId());
                    mNotificationDS.deleteNotificationByTarget(mTarget.getId());
                    finish();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
