package com.who.daola;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class FenceEditFragment extends MapFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, View.OnFocusChangeListener {

    private double mLongitude;
    private double mLatitude;
    private EditText mRadius;
    private EditText mName;
    private Marker mMarker;
    private Circle mCircle;
    private LatLng mHere;
    private boolean mViewOnly = false;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    /*
 * Define a request code to send to Google Play services
 * This code is returned in Activity.onActivityResult
 */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private boolean mConnected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mViewOnly) return;
        // Connect the client.
        mGoogleApiClient.connect();
        mName = (EditText) getActivity().findViewById(R.id.name_edittext);
        mRadius = (EditText) getActivity().findViewById(R.id.radius_edittext);
        mName.setOnFocusChangeListener(this);
        mRadius.setOnFocusChangeListener(this);
        mLongitude = ((AddFenceActivity)getActivity()).getLongitude();
        mLatitude = ((AddFenceActivity) getActivity()).getLatitude();

        if (hasPreviousLocation()) {
            LatLng here = new LatLng(mLatitude,mLongitude);
            setMarker(here, mName.getText().toString());
            drawCircle(here, Double.parseDouble(mRadius.getText().toString()));
        }

    }

    public void disableEditing() {
        if (this.getMap() != null) {
            this.getMap().getUiSettings().setAllGesturesEnabled(false);
            this.getMap().getUiSettings().setZoomControlsEnabled(false);
        } else {
            Toast.makeText(getActivity(), "Map not available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);


        if (this.getMap() != null) {
            if (!mViewOnly) {
                this.getMap().setOnMapClickListener(this);
            }
        } else {
            Toast.makeText(getActivity(), "Map not available!", Toast.LENGTH_LONG).show();
        }
        return view;
    }

    public void setMarker(LatLng here, String name) {
        if (this.getMap() != null) {

            if (here != null) {
                if (mMarker != null) {
                    mMarker.remove();
                }
                mMarker = this.getMap().addMarker(new MarkerOptions().position(here)
                        .title(name));
                // Move the camera instantly to hamburg with a zoom of 15.
                this.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(here, 19));
//            Circle circle = this.getMap().addCircle(new CircleOptions()
//                    .center(here)//new LatLng(-33.87365, 151.20689))
//                    .radius(5)
//                    .strokeColor(Color.RED));
                // Zoom in, animating the camera.
                this.getMap().animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
            }

        } else {
            Toast.makeText(getActivity(), "Map not available!", Toast.LENGTH_LONG).show();
        }
    }

    /*
 * Called by Location Services when the request to connect the
 * client finishes successfully. At this point, you can
 * request the current location or start periodic updates
 */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
        if (!hasPreviousLocation()) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null) {
                LatLng here = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                setMarker(here, mName.getText().toString());
            } else {
                Toast.makeText(getActivity(), "location unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private boolean hasPreviousLocation() {
        if (mLongitude !=0 && mLatitude != 0) {
            Toast.makeText(getActivity(), "has previous fence", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(getActivity(), "no previous fence", Toast.LENGTH_SHORT).show();
        return false;
    }


    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }


    private void showErrorDialog(int errorCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_connection_failed_message + " Got error code: " + errorCode)
                .setTitle(R.string.dialog_connection_failed_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (this.getMap() != null) {
            this.getMap().clear();
            mHere = latLng;
            if (mMarker != null) {
                mMarker.remove();
            }
            mMarker = this.getMap().addMarker(new MarkerOptions().position(latLng)
                    .title(mName.getText().toString()));


            //this.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
            //this.getMap().animateCamera(CameraUpdateFactory.newLatLng(latLng));
            this.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19), 2000, null);

            mLatitude = latLng.latitude;
            mLongitude = latLng.longitude;

            ((AddFenceActivity)getActivity()).setLatitude(mLatitude);
            ((AddFenceActivity)getActivity()).setLongitude(mLongitude);
        }
        if (mRadius.getText().length() > 0) {
            drawCircle(latLng, Double.parseDouble(mRadius.getText().toString()));
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == mName && !hasFocus) {
            if (mMarker != null) {
                mMarker.setTitle(mName.getText().toString());
            }
        } else if (v == mRadius && !hasFocus) {
            if (mMarker != null && mHere != null) {
                String radius = mRadius.getText().toString();
                if (!radius.trim().equals("")) {
                    drawCircle(mHere, Double.parseDouble(radius));
                }
            }
        }
    }

    public void drawCircle(LatLng center, double radius) {
        if (mCircle != null) {
            mCircle.remove();
        }
        mCircle = this.getMap().addCircle(new CircleOptions()
                .center(center)//new LatLng(-33.87365, 151.20689))
                .radius(radius)
                .strokeColor(Color.RED));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AddTargetActivity) {
            mViewOnly = true;
        }
    }
}
