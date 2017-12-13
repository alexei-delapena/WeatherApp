package com.project.afinal.weatherapp;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by DaljitSohi on 2017-12-08.
 */

public class DeviceLocation{

    private static final String TAG = "DeviceLocation";
    private static FusedLocationProviderClient fusedLocationProviderClient;
    private static Location mLocation;
    private Context context;

    //Constructor
    public DeviceLocation(Context contxt) {
        this.context = contxt;
    }//end DeviceLocation()

    public void getDeviceLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 231);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    updateLocation(location);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error while getting device locations");
            }
        });

    }//end getDeviceLocation()

    private void updateLocation(Location location){
        mLocation = location;
    }//end updateLocation()

    public Location printLocation(){
        Log.d(TAG, "Current Location: " + mLocation); ///THIS IS NULL ---> CAN'T FIGURE OUT WHYYYYYYYYYY
        return mLocation;
    }
}//end DeviceLocation.class
