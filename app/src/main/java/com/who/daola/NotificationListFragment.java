package com.who.daola;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.who.daola.data.Fence;
import com.who.daola.data.FenceDataSource;
import com.who.daola.data.Notification;
import com.who.daola.data.NotificationDataSource;

import java.sql.SQLException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NotificationListFragment extends Fragment  implements AbsListView.OnItemClickListener{

    private static final String TAG = NotificationListFragment.class.getName();
    private OnFragmentInteractionListener mListener;
    private NotificationDataSource mDataSource;
    private AbsListView mListView;
    private ArrayAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationListFragment.
     */
    public static NotificationListFragment newInstance() {
        NotificationListFragment fragment = new NotificationListFragment();
        return fragment;
    }
    public NotificationListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataSource = new NotificationDataSource(getActivity());
        try{
            mDataSource.open();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening database: " + e);
        }

        List<Notification> values = mDataSource.getAllNotifications();

        mAdapter = new ArrayAdapter<Notification>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            final Notification item = (Notification) parent.getItemAtPosition(position);
//            Intent intent = new Intent(getActivity(), NotificationViewActivity.class);
//            intent.putExtra(NotificationViewActivity.PARAM, item);
//            getActivity().startActivity(intent);
            Toast.makeText(this.getActivity(), "notification clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus
        //if (mDataSourceChanged) {
        List<Notification> fences = mDataSource.getAllNotifications();
        mAdapter.clear();
        mAdapter.addAll(fences);
        mAdapter.notifyDataSetChanged();
        //}
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
