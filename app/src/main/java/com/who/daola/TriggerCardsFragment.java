package com.who.daola;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.who.daola.data.Fence;
import com.who.daola.data.FenceDataSource;
import com.who.daola.data.NotificationDataSource;
import com.who.daola.data.TrackerTarget;
import com.who.daola.data.Trigger;
import com.who.daola.data.TriggerDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hud on 5/3/15.
 */
public class TriggerCardsFragment extends Fragment {

    private static final String ARG_POSITION = "p";
    private static final String ARG_TARGET = "t";
    public static final String TAG = TriggerCardsFragment.class.getName();
    private TriggerRecyclerAdapter mAdapter;
    private TrackerTarget mTarget;
    private TriggerDataSource mTriggerDS;
    private FenceDataSource mFenceDS;
    private NotificationDataSource mNotificationDS;
    private int mPosition;
    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static Fragment newInstance(int num, TrackerTarget target) {
        TriggerCardsFragment triggerCardsFragment = new TriggerCardsFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, num);
        args.putSerializable(ARG_TARGET, target);
        triggerCardsFragment.setArguments(args);

        return triggerCardsFragment;
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args!=null){
            mTarget = (TrackerTarget) args.getSerializable(ARG_TARGET);
            mPosition = args.getInt(ARG_POSITION);
        }
        View v = inflater.inflate(R.layout.fragment_trigger_cards, container, false);

        RecyclerView recList = (RecyclerView) v.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity().getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        mAdapter = new TriggerRecyclerAdapter();
        recList.setAdapter(mAdapter);
        return v;
    }

    private void initializeDataSources() {
        if (mTriggerDS == null) {
            mTriggerDS = new TriggerDataSource(this.getActivity().getApplicationContext());
        }
        if (mFenceDS == null) {
            mFenceDS = new FenceDataSource(this.getActivity().getApplicationContext());
        }
        if (mNotificationDS == null) {
            mNotificationDS = new NotificationDataSource(this.getActivity().getApplicationContext());
        }
        try {
            mTriggerDS.open();
            mFenceDS.open();
            mNotificationDS.open();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening database: " + e);
            closeDataSources();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeDataSources();
        List<Trigger> triggers = mTriggerDS.getAllTriggersForTarget(mTarget);
        List<Fence> fences = new ArrayList<>();
        for (Trigger trigger: triggers){
            fences.add(mFenceDS.getFence(trigger.getFence()));
        }
        mAdapter.clear();
        mAdapter.addAll(triggers, fences);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        closeDataSources();
        super.onPause();
    }
    private void closeDataSources() {
        Log.i(TAG, "closing datasources");
        if (mTriggerDS != null) {
            mTriggerDS.close();
        }
        if (mFenceDS != null) {
            mFenceDS.close();
        }
        if (mNotificationDS != null) {
            mNotificationDS.close();
        }
    }
}
