package com.who.daola;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class FenceEditFragment extends MapFragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, View.OnFocusChangeListener {

    private EditText mLongitude;
    private EditText mLatitude;
    private EditText mRadius;
    private EditText mName;
    private Marker mMarker;
    private Circle mCircle;
    private LatLng mHere;
    private boolean mViewOnly = false;

    /*
 * Define a request code to send to Google Play services
 * This code is returned in Activity.onActivityResult
 */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationClient mLocationClient;
    private Location mCurrentLocation;
    private boolean mConnected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getActivity(), this, this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mViewOnly) return;
        // Connect the client.
        mLocationClient.connect();
        mName = (EditText) getActivity().findViewById(R.id.name_edittext);
        mLongitude = (EditText) getActivity().findViewById(R.id.longitude_edittext);
        mLatitude = (EditText) getActivity().findViewById(R.id.latitude_textedit);
        mRadius = (EditText) getActivity().findViewById(R.id.radius_edittext);
        mName.setOnFocusChangeListener(this);
        mRadius.setOnFocusChangeListener(this);

        if (hasPreviousLocation()) {
            LatLng here = new LatLng(Double.parseDouble(mLatitude.getText().toString()),
                    Double.parseDouble(mLongitude.getText().toString()));
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
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);


        if (this.getMap() != null) {
            if (!mViewOnly){
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
            mCurrentLocation = mLocationClient.getLastLocation();
            if (mCurrentLocation != null) {
                LatLng here = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                setMarker(here, mName.getText().toString());
            } else {
                Toast.makeText(getActivity(), "location unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean hasPreviousLocation() {
        if (mLongitude != null && mLongitude.getText().toString().length() > 0 && mLatitude != null && mLatitude.getText().toString().length() > 0) {
            Toast.makeText(getActivity(), "has previous fence", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(getActivity(), "no previous fence", Toast.LENGTH_SHORT).show();
        return false;
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(getActivity(), "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
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
        if (connectionResult.hasResolution()) {
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
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());
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

            mLatitude.setText(Double.toString(latLng.latitude));
            mLongitude.setText(Double.toString(latLng.longitude));
        }
        if (mRadius.getText().length()>0) {
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
