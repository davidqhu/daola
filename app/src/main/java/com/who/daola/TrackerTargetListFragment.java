package com.who.daola;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.who.daola.data.TrackerTarget;
import com.who.daola.data.TargetDataSource;
import com.who.daola.data.TrackerTargetContract;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class TrackerTargetListFragment extends Fragment implements AbsListView.OnItemClickListener {

    public static final String TAG = TrackerTargetListFragment.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "tableName";

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter mAdapter;
    private boolean mDataSourceChanged = false;
    private String mTableName;

    private TargetDataSource mDataSource;

    // TODO: Rename and change types of parameters
    public static TrackerTargetListFragment newInstance(String tableName) {

        if (tableName != TrackerTargetContract.TrackerEntry.TABLE_NAME &&
                tableName != TrackerTargetContract.TargetEntry.TABLE_NAME) {
            throw new IllegalArgumentException(String.format("Table name %s is invalid.", tableName));
        }
        TrackerTargetListFragment fragment = new TrackerTargetListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, tableName);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrackerTargetListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments()!=null) {
            mTableName = this.getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        mAdapter = new ArrayAdapter<TrackerTarget>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<TrackerTarget>());

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    private void initializeDataSources() {
        if (mDataSource == null) {
            mDataSource = new TargetDataSource(getActivity(), mTableName);
        }
        try {
            mDataSource.open();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening database: " + e);
            closeDataSources();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        initializeDataSources();

        List<TrackerTarget> values = mDataSource.getAllTargets();

        mAdapter.clear();
        mAdapter.addAll(values);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void closeDataSources() {
        if (mDataSource != null) {
            mDataSource.close();
        }
    }

    @Override
    public void onPause() {
        Log.i(TAG, "closing datasources");
        mDataSource.close();
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            final TrackerTarget item = (TrackerTarget) parent.getItemAtPosition(position);
            Intent intent = new Intent(getActivity(), DetailedTargetActivity.class);
            intent.putExtra(AddTargetActivity.PARAM, item);
            getActivity().startActivity(intent);
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(getActivity(), AddTargetActivity.class);
            getActivity().startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}
