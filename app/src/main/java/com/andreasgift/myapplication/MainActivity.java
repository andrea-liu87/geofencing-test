package com.andreasgift.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    int REQUEST_CODE = 0;
    int REQUEST_CODE_PERMISSION =1;
    int EVENT_ID = 22;
    public String ACTION_FILTER="com.andreasgift.proximityalert";

    private Double longitude;
    private Double latitude;
    private Float radius;

    private LocationManager lm;
    BroadcastReceiver broadcastReceiver;

    private GeofencingClient mGeofencingClient;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this,
                    new String []{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION);}
        else {
            lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        }

            longitude = 103.696732;
            latitude = 1.336724;
            radius = 1000f;
            mGeofenceList = new ArrayList<>();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        } else {
            Toast.makeText(getApplicationContext(),"permission is denied", Toast.LENGTH_LONG).show();
            this.finish();
        }
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        mGeofenceList.add(new Geofence.Builder()
        .setRequestId("geofence")
                .setCircularRegion(latitude,longitude,radius)
                .setExpirationDuration(-1)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("TAG","geofences is added");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG","geofences is failed");
                    }
                });
    }

    //Create geofencing request method
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    //Create pending intent method
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    //This method is to be ignored
    private void setProximityAlert()throws SecurityException {
        Intent intent = new Intent(ACTION_FILTER);
        intent.putExtra(ProximityAlertReciever.EVENT_ID_EXTRA, EVENT_ID);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        lm.addProximityAlert(
                latitude,
                longitude,
                radius,
                -1,
                pendingIntent
        );
    }

}
