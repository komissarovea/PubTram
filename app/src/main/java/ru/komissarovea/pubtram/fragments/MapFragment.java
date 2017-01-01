package ru.komissarovea.pubtram.fragments;


import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import ru.komissarovea.pubtram.MainActivity;
import ru.komissarovea.pubtram.R;
import ru.komissarovea.pubtram.data.Stop;
import ru.komissarovea.pubtram.data.StopsHelper;

public class MapFragment extends SupportMapFragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 1000;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final double SPEED_THRESH = 1;
    private static final String TAG = "Mapper";
    private static final int ZOOM_OFFSET = 6;

    private MainActivity activity;
    private GoogleApiClient locationClient;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private GoogleMap map;
    private LatLng mapCenter;
    private float currentZoom;
    private HashMap<Marker, Integer> markers = new HashMap<>();
    private SharedPreferences.Editor prefsEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        SharedPreferences prefs = activity.getSharedPreferences("SharedPreferences",
                Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        this.getMapAsync(this);

        // Keep screen on while this map location tracking activity is running
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initApiClient();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Enable or disable current location
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

            map.setOnMapClickListener(this);
            map.setOnMapLongClickListener(this);
            map.setOnMarkerClickListener(this);
            map.setOnInfoWindowClickListener(this);

            // Enable or disable current location
            map.setMyLocationEnabled(true);

            // Initialize type of map to normal
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Initialize 3D buildings enabled for map view
            map.setBuildingsEnabled(false);

            // Initialize whether indoor maps are shown if available
            map.setIndoorEnabled(false);

            // Initialize traffic overlay
            map.setTrafficEnabled(false);

            // Disable rotation gestures
            map.getUiSettings().setRotateGesturesEnabled(false);

            currentZoom = map.getMaxZoomLevel() - ZOOM_OFFSET;
            if (mapCenter != null) {
                // Move camera view and zoom to location
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter,
                        currentZoom));
                addMapMarkers();
            }
        } else {
            Toast.makeText(activity, getString(R.string.map_error),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        locationClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        locationClient.disconnect();
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                locationClient, this);
        if (map != null) {
            currentZoom = map.getCameraPosition().zoom;
            prefsEditor.putFloat("KEY_ZOOM", currentZoom);
            prefsEditor.commit();
        }
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

        // Indicate that a connection has been established
        Toast.makeText(activity, getString(R.string.connected_toast),
              Toast.LENGTH_SHORT).show();

        Location newLocation = LocationServices.FusedLocationApi.getLastLocation(
                locationClient);
        onLocationChanged(newLocation);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, this);
        //updateLocation();
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
        currentLocation = newLocation;

        float bearing = newLocation.getBearing();
        float speed = newLocation.getSpeed();
        float acc = newLocation.getAccuracy();

        // Get latitude and longitude of updated location
        double lat = newLocation.getLatitude();
        double lon = newLocation.getLongitude();
        mapCenter = new LatLng(lat, lon);

        Bundle locationExtras = newLocation.getExtras();
        // If there is no satellite info, return -1 for number of satellites
        int numberSatellites = -1;
        if (locationExtras != null) {
            Log.i(TAG, "Extras:" + locationExtras.toString());
            if (locationExtras.containsKey("satellites")) {
                numberSatellites = locationExtras.getInt("satellites");
            }
        }

        // Log some basic location information
        Log.i(TAG,
                "Lat=" + formatDecimal(lat, "0.00000") + " Lon="
                        + formatDecimal(lon, "0.00000") + " Bearing="
                        + formatDecimal(bearing, "0.0") + " deg Speed="
                        + formatDecimal(speed, "0.0") + " m/s" + " Accuracy="
                        + formatDecimal(acc, "0.0") + " m" + " Sats="
                        + numberSatellites);

        if (map != null) {
            if (speed < SPEED_THRESH) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, currentZoom));
            } else {
                changeCamera(map, mapCenter, map.getCameraPosition().zoom,
                        bearing, map.getCameraPosition().tilt, true);
            }
            addMapMarkers();
        } else {
            Toast.makeText(activity, getString(R.string.map_error),
                    Toast.LENGTH_LONG).show();
        }

        //updateLocation();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (markers.containsKey(marker)) {
            int stopID = markers.get(marker);
            activity.requestTransport(stopID);
            //Toast.makeText(activity, stopID.toString(), Toast.LENGTH_SHORT).show();
        }
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

    private void addMapMarkers() {
        // map.addMarker(
        // new MarkerOptions().title("Honolulu")
        // .snippet("Capitol of the state of Hawaii")
        // .position(honolulu)).setDraggable(true);

        for (Marker marker : markers.keySet()) {
            marker.remove();
        }
        markers.clear();
        ArrayList<Stop> nextStops = StopsHelper.getNextStops(currentLocation,
                500);
        if (nextStops != null && nextStops.size() > 0) {
            for (Stop stop : nextStops) {
                MarkerOptions mo = new MarkerOptions()
                        .title(stop.getName())
                        .snippet(stop.getInfo())
                        .position(
                                new LatLng(stop.getLatitude(), stop
                                        .getLongitude()))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                Marker marker = map.addMarker(mo);
                markers.put(marker, stop.getID());
            }
        }
    }

    private String formatDecimal(double number, String formatPattern) {
        DecimalFormat df = new DecimalFormat(formatPattern);
        return df.format(number);
    }

    private void changeCamera(GoogleMap map, LatLng center, float zoom,
                              float bearing, float tilt, boolean animate) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(center) // Sets the center of the map
                .zoom(zoom) // Sets the zoom
                .bearing(bearing) // Sets the bearing of the camera
                .tilt(tilt) // Sets the tilt of the camera relative to nadir
                .build(); // Creates a CameraPosition from the builder

        if (animate) {
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        } else {
            map.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
    }

    private void showErrorDialog(int errorCode) {
        Log.e(TAG, "Error_Code =" + errorCode);
        // Create an error dialog display here
    }
}
