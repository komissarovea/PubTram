package ru.komissarovea.pubtram.fragments;


import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import ru.komissarovea.pubtram.MainActivity;
import ru.komissarovea.pubtram.R;
import ru.komissarovea.pubtram.adapters.StopAdapter;
import ru.komissarovea.pubtram.data.Stop;
import ru.komissarovea.pubtram.data.StopsHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class NextStopsFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener {

    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 1000;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "Mapper";

    private MainActivity activity;
    private ListView listView;
    private ArrayList<Stop> nextStops;
    private GoogleApiClient locationClient;
    private Location currentLocation;
    private LocationRequest locationRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();

        View rootView = inflater.inflate(R.layout.fragment_next_stops,
                container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        initApiClient();
        return rootView;
    }

    @Override
    public void onStart() {
        locationClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                locationClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    locationClient, locationRequest, this);
        }
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (nextStops != null && nextStops.size() > 0) {
            int stopID = nextStops.get(position).getID();
            activity.requestTransport(stopID);
            // Toast.makeText(activity, stopID.toString(),
            // Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                locationClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, this);
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(activity, getString(R.string.disconnected_toast),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(activity,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);

                // Thrown if Google Play services canceled the original
                // PendingIntent
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            // If no resolution is available, display a dialog with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        currentLocation = newLocation;
        updateLocation();
    }

    private void initApiClient() {
        if (locationClient == null) {
            locationClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            locationRequest = new LocationRequest();
            locationRequest.setInterval(UPDATE_INTERVAL);
            locationRequest.setFastestInterval(FASTEST_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

//            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                    .addLocationRequest(locationRequest);
//            PendingResult<LocationSettingsResult> result =
//                    LocationServices.SettingsApi.checkLocationSettings(locationClient,
//                            builder.build());
        }
    }

    public void showErrorDialog(int errorCode) {
        Log.e(TAG, "Error_Code =" + errorCode);
        // Create an error dialog display here
    }

    public void updateLocation() {
        nextStops = StopsHelper.getNextStops(currentLocation, 500);

        if (nextStops != null && nextStops.size() > 0) {
            StopAdapter adapter = new StopAdapter(activity, nextStops);
            listView.setAdapter(adapter);
            listView.invalidate();
            // Stop nearest = nextStops.get(0);
            // txtHeader.setText("total: " + nextStops.size() + ", nearest: "
            // + nearest.getName() + ", distance: "
            // + nearest.getCurrentDistance().doubleValue());
        } else {
            Toast.makeText(activity, getString(R.string.disconnected_toast),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
