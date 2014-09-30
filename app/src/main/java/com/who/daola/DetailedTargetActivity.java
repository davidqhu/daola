package com.who.daola;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.who.daola.data.Target;
import com.who.daola.data.TargetDataSource;

import java.sql.SQLException;

public class DetailedTargetActivity extends Activity {

    private Target mTarget;
    private TargetDataSource mDatasource;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mNickName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_target);

        mTarget = (Target) getIntent().getSerializableExtra("target");
        mFirstName = (TextView) findViewById(R.id.first_name_textview);
        mLastName = (TextView) findViewById(R.id.last_name_textview);
        mNickName = (TextView) findViewById(R.id.nick_name_textview);
        updateTextViews();
        mDatasource = new TargetDataSource(this);
        try{
            mDatasource.open();
        } catch (SQLException e) {
            Log.e("ItemFragment", "Error opening database: " + e);
        }
    }

    private void updateTextViews(){
        mFirstName.setText(mTarget.getFirstName());
        mLastName.setText(mTarget.getLastName());
        mNickName.setText(mTarget.getNikeName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailed_target, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        mTarget = mDatasource.getTarget(mTarget.getId());
        updateTextViews();
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
        } else if (item.getItemId() == R.id.action_delete && mTarget!=null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_delete_target_message)
                    .setTitle(R.string.dialog_delete_target_title);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    mDatasource.deletePeople(mTarget);
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
