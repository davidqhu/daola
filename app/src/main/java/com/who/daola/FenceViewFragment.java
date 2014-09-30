package com.who.daola;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.who.daola.data.Fence;


/**
 * A simple {@link Fragment} subclass.
 */
public class FenceViewFragment extends MapFragment {

    private Marker mMarker;
    private Circle mCircle;
    private LatLng mHere;
    private Fence mFence;

    public FenceViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        mFence = (Fence) getActivity().getIntent().getSerializableExtra(AddFenceActivity.PARAM);
        if (mFence != null) {
            mHere = new LatLng(mFence.getLatitude(), mFence.getLongitude());
            setMarker();
        }
        // Inflate the layout for this fragment
        return view;
    }


    private void setMarker() {
        if (this.getMap() != null) {
            mMarker = this.getMap().addMarker(new MarkerOptions().position(mHere)
                    .title(mFence.getName()));

            // Move the camera instantly to hamburg with a zoom of 15.
            this.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(mHere, 19));

            mCircle = this.getMap().addCircle(new CircleOptions()
                    .center(mHere)//new LatLng(-33.87365, 151.20689))
                    .radius(mFence.getRadius())
                    .strokeColor(Color.RED));
            this.getMap().animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        } else {
            Toast.makeText(getActivity(), "Map not available!", Toast.LENGTH_LONG).show();
        }
    }

}
