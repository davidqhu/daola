package com.who.daola;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.who.daola.data.NotificationDataSource;
import com.who.daola.data.TrackerTarget;
import com.who.daola.data.TrackerTargetDataSource;
import com.who.daola.data.TriggerDataSource;

import java.sql.SQLException;

import static com.who.daola.data.TrackerTargetContract.TargetEntry.TABLE_NAME;

public class DetailedTargetActivity extends ActionBarActivity {

    public final String TAG = DetailedTargetActivity.class.getName();
    public static final int TAB_COUNT = 2;
    private TrackerTarget mTarget;
    private TrackerTargetDataSource mTargetDS;
    private TriggerDataSource mTriggerDS;
    private NotificationDataSource mNotificationDS;
    private TriggerPaperAdapter mAdapter;
    private ViewPager mPager;
    private static CharSequence[] mTabTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_target);
        mTarget = (TrackerTarget) getIntent().getSerializableExtra("target");

        mAdapter = new TriggerPaperAdapter(getSupportFragmentManager(), mTarget);
        mPager = (ViewPager) findViewById(R.id.target_pager);
        mPager.setAdapter(mAdapter);


        mTabTitle = new CharSequence[2];
        mTabTitle[0] = getString(R.string.target_tab_title_triggers);
        mTabTitle[1] = getString(R.string.target_tab_title_histories);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailed_target, menu);
        return true;
    }

    private void initializeDataSources() {
        if (mTargetDS == null) {
            mTargetDS = new TrackerTargetDataSource(this, TABLE_NAME);
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
        getSupportActionBar().setTitle(getString(R.string.target_title_prefix) +
                " " + mTarget.getName());
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
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static final class TriggerPaperAdapter extends FragmentPagerAdapter {
        private TrackerTarget mTarget;

        public TriggerPaperAdapter(FragmentManager fm, TrackerTarget target) {
            super(fm);
            mTarget = target;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return TriggerCardsFragment.newInstance(position, mTarget);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitle[position];
        }
    }
}
