package com.who.daola;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.who.daola.data.Fence;
import com.who.daola.data.FenceDataSource;
import com.who.daola.data.NotificationDataSource;
import com.who.daola.data.TriggerDataSource;

import java.sql.SQLException;

import static com.who.daola.RadiusPickerDialogFragment.RadiusPickerDialogFragmentListener;


public class AddFenceActivity extends Activity implements RadiusPickerDialogFragmentListener {


    private static final String TAG = AddFenceActivity.class.getName();

    private ImageView mAddImage;
    private double mLongitude;
    private double mLatitude;
    private EditText mRadius;
    private EditText mName;
    private FenceDataSource mFenceDS;
    private TriggerDataSource mTriggerDS;
    private NotificationDataSource mNotificationDS;
    private Activity mActivity;
    private Fence mFence;
    public static final String PARAM = "fence";
    public static final String RADIUS_ARGUMENT = "radius";
    public static final double DEFAULT_FENCE_RADIUS = 5;
    private final static int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fence);

        mFence = (Fence) getIntent().getSerializableExtra(PARAM);
        mName = (EditText) findViewById(R.id.name_edittext);
        mRadius = (EditText) findViewById(R.id.radius_edittext);
        mRadius.setText(Double.toString(DEFAULT_FENCE_RADIUS));

        if (mFence != null) {
            mName.setText(mFence.getName());
            mRadius.setText(String.valueOf(mFence.getRadius()));
            mLatitude = mFence.getLatitude();
            mLongitude = mFence.getLongitude();
        }
        restoreActionBar(mFence != null);


        mActivity = this;
    }

    private void initializeDataSources() {
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
            mFenceDS.open();
            mTriggerDS.open();
            mNotificationDS.open();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening database: " + e);
            closeDataSources();
        }
    }

    private void closeDataSources() {
        Log.i(TAG, "closing datasources");
        if (mFenceDS != null) {
            mFenceDS.close();
        }
        if (mTriggerDS != null) {
            mTriggerDS.close();
        }
        if (mNotificationDS != null) {
            mNotificationDS.close();
        }
    }

    @Override
    protected void onResume() {
        initializeDataSources();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDataSources();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_geo_fence, menu);
        if (mFence == null) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void restoreActionBar(boolean forEdit) {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        ActionBar.LayoutParams lp1 = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View customActionBar = null;
        if (forEdit) {
            customActionBar = getEditActionBarView();
        } else {
            customActionBar = getAddActionBarView();

        }
        actionBar.setCustomView(customActionBar, lp1);
    }

    private View getAddActionBarView() {
        View view = LayoutInflater.from(this).inflate(R.layout.action_bar_add, null);
        mAddImage = (ImageView) view.findViewById(R.id.add_target_image);
        mAddImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Integer, Void>() {
                    @Override
                    protected Void doInBackground(Void... arg0) {
                        mFenceDS.createFence(mName.getText().toString(),
                                Double.parseDouble(mRadius.getText().toString()),
                                mLatitude,
                                mLongitude);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Toast.makeText(getApplicationContext(), "fence added",
                                Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                    }

                    @Override
                    protected void onPreExecute() {
                    }
                }.execute((Void) null);

            }
        });
        return view;
    }

    private View getEditActionBarView() {
        View view = LayoutInflater.from(this).inflate(R.layout.action_bar_edit, null);
        mAddImage = (ImageView) view.findViewById(R.id.save_target_image);
        mAddImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Integer, Void>() {
                    @Override
                    protected Void doInBackground(Void... arg0) {
                        mFenceDS.updateFence(mFence.getId(), mName.getText().toString(),
                                Double.parseDouble(mRadius.getText().toString()),
                                mLatitude,
                                mLongitude);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Toast.makeText(getApplicationContext(), "fence saved",
                                Toast.LENGTH_SHORT).show();
                        mActivity.finish();
                    }

                    @Override
                    protected void onPreExecute() {
                    }
                }.execute((Void) null);
            }
        });
        return view;
    }

    public void onDeleteMenuClicked(MenuItem menu) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete_fence_message)
                .setTitle(R.string.dialog_delete_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                mFenceDS.deleteFence(mFence);
                mTriggerDS.deleteTriggerByFence(mFence.getId());
                mNotificationDS.deleteNotificationByFence(mFence.getId());
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

    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    /**
     * Launch Radius picker dialog when the radius textedit is clicked.
     */
    public void onRadiusEditTextClick(View view) {
        RadiusPickerDialogFragment dialog = new RadiusPickerDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(RADIUS_ARGUMENT, (int) Double.parseDouble(mRadius.getText().toString()));
        dialog.setArguments(arguments);
        dialog.show(getFragmentManager(), "");
    }

    /**
     * Set the radium textedit value from the radius picker dialog
     *
     * @param radius
     */
    @Override
    public void onRadiusSetClick(int radius) {
        mRadius.setText(Integer.toString(radius));
        FenceEditFragment fenceEditFragment = (FenceEditFragment) getFragmentManager().
                findFragmentById(R.id.fragement_edit_fence);
        fenceEditFragment.onMapClick(new LatLng(mLatitude, mLongitude));

    }

    public void onNameButtonClick(View v) {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            Context context = getApplicationContext();
            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }

            mName.setText(name);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
