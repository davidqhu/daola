package com.who.daola;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.who.daola.gcm.GcmHelper;

public class SettingsFragment extends PreferenceFragment {
    private PreferenceScreen mScan;
    private PreferenceScreen mId;
    private EditTextPreference mSendMessage;
    private static String mScannedId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_fragment);

        mScan = (PreferenceScreen) getPreferenceManager().findPreference("pref_scan_qr_code");
        mScan.setSummary(mScannedId);
        mScan.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.initiateScan();
                return false;
            }
        });

        mId = (PreferenceScreen) getPreferenceManager().findPreference("pref_reg_id");
        mId.setSummary(GcmHelper.REG_ID);

        mSendMessage = (EditTextPreference) getPreferenceManager().findPreference("pref_send_message");
        mSendMessage.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (preference.getKey().equals("pref_send_message")) {
                            Toast.makeText(getActivity(), mSendMessage.getEditText().getText().toString(), Toast.LENGTH_LONG).show();
                            GcmHelper.sendTestMessage(mScannedId, mSendMessage.getEditText().getText().toString());
                        }
                        return false;
                    }
                }

        );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents()!=null) {
            mScannedId = scanResult.getContents().toString();
            mScan.setSummary(mScannedId);
        }
    }
}