package com.ei.kalavarafoods.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.utils.UsesPermissions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import static com.ei.kalavarafoods.utils.Constants.REQUEST_PERMISSION_COARSE_AND_FINE_LOCATION;

public class CurrentLocMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mGoogleMap;
    private UsesPermissions mUsesPermissions;
    private boolean mLocationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_loc_map);

        mUsesPermissions = new UsesPermissions(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        updateLocationUI();

    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mLocationPermissionGranted){
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.setMaxZoomPreference(20);
        }else {
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            getLocationPermission();
        }
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{UsesPermissions.ACCESS_COARSE_LOCATION, UsesPermissions.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_COARSE_AND_FINE_LOCATION);
        }else {
            mLocationPermissionGranted = true;
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode){
            case REQUEST_PERMISSION_COARSE_AND_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                }
        }
    }
}
