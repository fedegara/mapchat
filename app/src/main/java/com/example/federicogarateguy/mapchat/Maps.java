package com.example.federicogarateguy.mapchat;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Maps extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager lm;
    private static final int FINELOCATIONCODE = 1;
    private FirebaseAuth auth;
    private ArrayMap<String, Marker> markers = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        auth = FirebaseAuth.getInstance();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINELOCATIONCODE);
            }
        } else {
            requestLocation();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user_location");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("datasnapshot", dataSnapshot.toString());
                addMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("datasnapshot", dataSnapshot.toString());
                addMarker(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("user_location");
        MapChatLocation new_location = new MapChatLocation(location.getLatitude(), location.getLongitude(), auth.getCurrentUser().getEmail());
        mRef.child(auth.getCurrentUser().getUid()).setValue(new_location);
        Log.d("user_location", "Se envio una nueva user_location");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINELOCATIONCODE) {
            int i = 0;
            while (i < permissions.length && !permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                i++;
            }
            if (i < permissions.length) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();
                }
            }
        }
    }

    private void requestLocation() {
        try {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    private void addMarker(DataSnapshot dataSnapshot) {
        MapChatLocation new_marker_data = dataSnapshot.getValue(MapChatLocation.class);
        if (markers.get(new_marker_data.email) != null) {
            markers.get(new_marker_data.email).remove();
            markers.remove(new_marker_data.email);
            Log.d("removed marker", new_marker_data.email);
        }
        LatLng new_marker = new LatLng(new_marker_data.latitude, new_marker_data.longitude);
        Marker marker = mMap.addMarker(new MarkerOptions().position(new_marker).title(new_marker_data.email));
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new_marker));
        markers.put(new_marker_data.email, marker);
        Log.d("new marker", new_marker_data.email);
        Log.d("new map marker", "Lat:" + new_marker_data.latitude + " Long:" + new_marker_data.longitude);
    }
}
