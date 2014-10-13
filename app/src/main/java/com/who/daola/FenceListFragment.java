package com.who.daola;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
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

import com.who.daola.data.Fence;
import com.who.daola.data.FenceDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FenceListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FenceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FenceListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String TAG = FenceListFragment.class.getName();
    private OnFragmentInteractionListener mListener;
    private FenceDataSource mDataSource;
    private AbsListView mListView;
    private ArrayAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FenceListFragment.
     */
    public static FenceListFragment newInstance() {
        FenceListFragment fragment = new FenceListFragment();
        return fragment;
    }

    public FenceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initializeDataSources() {
        if (mDataSource == null) {
            mDataSource = new FenceDataSource(getActivity());
        }
        try {
            mDataSource.open();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening database: " + e);
            closeDataSources();
        }
    }

    private void closeDataSources() {
        if (mDataSource != null) {
            mDataSource.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geo_fence_list, container, false);

        mAdapter = new ArrayAdapter<Fence>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<Fence>());
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(getActivity(), AddFenceActivity.class);
            getActivity().startActivity(intent);
            return true;
        }
        return false;
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

    @Override
    public void onResume() {
        super.onResume();
        initializeDataSources();
        List<Fence> fences = mDataSource.getAllFences();
        mAdapter.clear();
        mAdapter.addAll(fences);
        mAdapter.notifyDataSetChanged();
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
            final Fence item = (Fence) parent.getItemAtPosition(position);
            Intent intent = new Intent(getActivity(), AddFenceActivity.class);
            intent.putExtra(AddFenceActivity.PARAM, item);
            getActivity().startActivity(intent);
            //mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
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
        public void onFragmentInteraction(Uri uri);
    }
}
