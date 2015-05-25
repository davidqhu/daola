package com.who.daola;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.who.daola.data.Fence;
import com.who.daola.data.Trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hud on 5/12/15.
 */
public class TriggerRecyclerAdapter extends RecyclerView.Adapter<TriggerRecyclerAdapter.TriggerViewHolder> {

    private List<Trigger> mTriggerList = new ArrayList<>();
    private List<Fence> mFenceList = new ArrayList<>();


    public void clear() {
        mTriggerList.clear();
        mFenceList.clear();
    }

    public void addAll(List<Trigger> triggerList, List<Fence> fenceList) {
        mTriggerList.addAll(triggerList);
        mFenceList.addAll(fenceList);
    }

    @Override
    public int getItemCount() {
        return mTriggerList.size();
    }

    @Override
    public void onBindViewHolder(TriggerViewHolder triggerViewHolder, int i) {
        Trigger trigger = mTriggerList.get(i);
        triggerViewHolder.mFence = mFenceList.get(i);
        triggerViewHolder.mFenceTextView.setText(mFenceList.get(i).getName());
        triggerViewHolder.mDuration.setText(Long.toString(trigger.getDuration()));
        triggerViewHolder.mTransition.setText(Integer.toString(trigger.getTransitionType()));
    }

    @Override
    public TriggerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_trigger_card, viewGroup, false);
        MapsInitializer.initialize(viewGroup.getContext());
        return new TriggerViewHolder(itemView);
    }

    public static class TriggerViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        protected MapView mMapView;
        protected TextView mFenceTextView;
        protected TextView mDuration;
        protected TextView mTransition;
        protected GoogleMap mMap;
        protected Fence mFence;
        protected LatLng mHere;

        public TriggerViewHolder(View v) {
            super(v);
            mMapView = (MapView) itemView.findViewById(R.id.map_view);
            mMapView.onCreate(null);
            mMapView.getMapAsync(this);
            mFenceTextView = (TextView) v.findViewById(R.id.trigger_fence);
            mDuration = (TextView) v.findViewById(R.id.trigger_duration);
            mTransition = (TextView) v.findViewById(R.id.trigger_transition);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            System.out.println("map ready");
            mMap = googleMap;

            mHere = new LatLng(mFence.getLatitude(), mFence.getLongitude());
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setAllGesturesEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.addMarker(new MarkerOptions().position(mHere));
            mMap.addCircle(new CircleOptions()
                    .center(mHere)
                    .radius(mFence.getRadius())
                    .strokeColor(Color.RED));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mHere, 15));

        }
    }
}