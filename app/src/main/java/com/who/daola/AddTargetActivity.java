package com.who.daola;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.who.daola.data.Fence;
import com.who.daola.data.FenceDataSource;
import com.who.daola.data.Target;
import com.who.daola.data.TargetDataSource;
import com.who.daola.data.Trigger;
import com.who.daola.data.TriggerContract;
import com.who.daola.data.TriggerDataSource;
import com.who.daola.service.FenceTriggerService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class AddTargetActivity extends Activity implements ActionMode.Callback {

    public static final SimpleDateFormat SIMPLE_DATE_FORMATTER = new SimpleDateFormat("MMM/dd/yyyy h:mm a");
    public static final String TAG = AddTargetActivity.class.getName();
    public static final String PARAM = "target";
    private ImageView mAddImage;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mNickName;
    private RadioGroup mRadioGroup;
    private Spinner mFencessSpinner;
    private RadioButton mRadioEnter;
    private RadioButton mRadioExit;
    private RadioButton mRadioBoth;
    private EditText mExpirationDateTimeEditText;
    private Activity mActivity;
    private TargetDataSource mTargetDS;
    private FenceDataSource mFenceDS;
    private TriggerDataSource mTriggerDS;
    private Target mTarget;
    private Fence mFence;
    private List<Trigger> mTriggers;
    private TriggerContract.TransitionType mCondition;
    private RadioButton mSelectedRadio = null;
    private FenceEditFragment mFenceEditFragment;
    private Calendar mExpirationDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_target);
        initializeDataSources();
        mActivity = this;
        mTarget = (Target) getIntent().getSerializableExtra(PARAM);
        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mNickName = (EditText) findViewById(R.id.nick_name);
        mFencessSpinner = (Spinner) findViewById(R.id.spinner_fence);
        mRadioEnter = (RadioButton) findViewById(R.id.radioButton_enter);
        mRadioExit = (RadioButton) findViewById(R.id.radioButton_exit);
        mRadioBoth = (RadioButton) findViewById(R.id.radioButton_both);
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup_condition);
        mExpirationDateTimeEditText = (EditText) findViewById(R.id.editText_expiration_datetime);
        mSelectedRadio = mRadioEnter;


        ArrayAdapter<Fence> adapter = new ArrayAdapter<Fence>(this,
                android.R.layout.simple_spinner_item, mFenceDS.getAllFences());

        mFencessSpinner.setAdapter(adapter);

        restoreActionBar(mTarget != null);
        if (mTarget != null) {
            mFirstName.setText(mTarget.getFirstName());
            mLastName.setText(mTarget.getLastName());
            mNickName.setText(mTarget.getNikeName());
            mTriggers = mTriggerDS.getAllTriggersForTarget(mTarget);
            if (!mTriggers.isEmpty()) {
                showTrigger(mTriggers.get(0));
            }
        }

        //startActionMode(this);
    }

    private void showTrigger(Trigger trigger) {
        System.out.println("duration: " + trigger.getDuration());
        mExpirationDateTime.setTimeInMillis(trigger.getDuration());
        mExpirationDateTimeEditText.setText(SIMPLE_DATE_FORMATTER.format(mExpirationDateTime.getTime()));

        int spinnerSize = mFencessSpinner.getCount();
        Fence fence = null;
        for (int i = 0; i < spinnerSize; i++) {
            fence = (Fence) mFencessSpinner.getItemAtPosition(i);
            if (fence.getId() == trigger.getFence()) {
                mFence = fence;
                mFencessSpinner.setSelection(i);
                break;
            }
        }

        ((RadioButton) mRadioGroup.getChildAt(trigger.getTransitionType() + 1)).setChecked(true);

        if (mFence != null) {
            Toast.makeText(this, "fence is not null", Toast.LENGTH_LONG).show();
            mFenceEditFragment = (FenceEditFragment) this.getFragmentManager().findFragmentById(R.id.fragment_target_fence);
            LatLng center = new LatLng(mFence.getLatitude(), mFence.getLongitude());
            mFenceEditFragment.setMarker(center, mFence.getName());
            mFenceEditFragment.drawCircle(center, mFence.getRadius());
            mFenceEditFragment.disableEditing();
        }
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
            closeDataSources();
        }
    }

    private void closeDataSources() {
        if (mTargetDS != null) {
            mTargetDS.close();
        }
        if (mTargetDS != null) {
            mFenceDS.close();
        }
        if (mTriggerDS != null) {
            mTriggerDS.close();
        }
    }

    public void restoreActionBar(boolean forEdit) {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        ActionBar.LayoutParams lp1 = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View customActionBar;
        if (forEdit) {
            customActionBar = getEditActionBarView();
        } else {
            customActionBar = getAddActionBarView();
        }
        actionBar.setCustomView(customActionBar, lp1);

    }

    private View getAddActionBarView() {
        View view = LayoutInflater.from(this).inflate(R.layout.action_bar_add, null); // layout which contains your button.
        mAddImage = (ImageView) view.findViewById(R.id.add_target_image);
        mAddImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (mFencessSpinner.getSelectedItemPosition() == 0 ) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity.getApplicationContext());
//                    builder.setMessage(R.string.dialog_add_target_alert_message).setTitle(R.string.dialog_add_target_alert_title)
//                            .setPositiveButton(R.string.ok, null);
//                    // Create the AlertDialog object and return it
//                    builder.create().show();
//                }

                new AsyncTask<Void, Integer, Void>() {
                    @Override
                    protected Void doInBackground(Void... arg0) {
                        Target target = mTargetDS.createTarget(mFirstName.getText().toString(), mLastName.getText().toString(), mNickName.getText().toString());
                        mTriggerDS.createTrigger(target.getId(), ((Fence) mFencessSpinner.getSelectedItem()).getId(), true, mExpirationDateTime.getTimeInMillis(), TriggerContract.getTransitionTypeFromId(mSelectedRadio.getId()));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Toast.makeText(getApplicationContext(), "added",
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
        View view = LayoutInflater.from(this).inflate(R.layout.action_bar_edit, null); // layout which contains your button.
        mAddImage = (ImageView) view.findViewById(R.id.save_target_image);
        mAddImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Integer, Void>() {
                    @Override
                    protected Void doInBackground(Void... arg0) {
                        mTargetDS.updateTarget(mTarget.getId(), mFirstName.getText().toString(), mLastName.getText().toString(), mNickName.getText().toString());
                        // A target can have no fence associated to it
                        if (!mFenceDS.getAllFences().isEmpty()) {
                            Trigger trigger = mTriggerDS.updateTrigger(mTarget.getId(), ((Fence) mFencessSpinner.getSelectedItem()).getId(), true, mExpirationDateTime.getTimeInMillis(), TriggerContract.getTransitionTypeFromId(mSelectedRadio.getId()));
                            PendingIntent pendingIntent = FenceTriggerService.getInstance().getPendingIntent(FenceTriggerService.DATASOURCE_UPDATE, trigger);
                            try {
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                Log.i(TAG, "intented was cencaled");
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        Toast.makeText(getApplicationContext(), "saved",
                                Toast.LENGTH_SHORT).show();
                        FenceTriggerService.getInstance().updateDataSource();
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

    public void onRadioButtonClicked(View view) {
        mSelectedRadio = (RadioButton) view;
    }

    public void onExpirationDateTimeClicked(View view) {

        Log.i(TAG, "time editbox clicked");
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.add_target, menu);

//        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.add_menu_item).getActionView();
//        mAddImage = (ImageView) badgeLayout.findViewById(R.id.add_target_image);
//        mAddImage.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "add", Toast.LENGTH_SHORT);
//            }
//        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        System.out.println("onOptionsItemSelected");
        Toast.makeText(getApplicationContext(), item.getItemId(), Toast.LENGTH_SHORT).show();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addClicked(View view) {
        Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
        System.out.println("addClicked");
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        menuItem.getItemId();

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        finish();
    }

    @Override
    protected void onResume() {
        initializeDataSources();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "closing datasources");
        closeDataSources();
        super.onPause();
    }

    public void updateDate(int year, int month, int day) {
        mExpirationDateTime.set(year, month, day);
    }

    public void updateTime(int hourOfDay, int minute) {
        mExpirationDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mExpirationDateTime.set(Calendar.MINUTE, minute);
        mExpirationDateTimeEditText.setText(SIMPLE_DATE_FORMATTER.format(mExpirationDateTime.getTime()));
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (view.isShown()) {
                ((AddTargetActivity) this.getActivity()).updateTime(hourOfDay, minute);
            }
        }
    }

    public final static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private DatePickerDialog mDatePickerDialog;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            long startDate = System.currentTimeMillis() - 1000;
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            mDatePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            mDatePickerDialog.getDatePicker().setMinDate(startDate);
            return mDatePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (view.isShown()) {
                Log.i(TAG, "date set");
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(this.getActivity().getFragmentManager(), "timePicker");
                ((AddTargetActivity) this.getActivity()).updateDate(year, month, day);
            }
        }
    }
}
