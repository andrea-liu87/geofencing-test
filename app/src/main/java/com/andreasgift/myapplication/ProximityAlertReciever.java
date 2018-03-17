package com.andreasgift.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Andrea on 4/3/2018.
 */

public class ProximityAlertReciever extends BroadcastReceiver {
    public static final String EVENT_ID_EXTRA ="proximityalert";

    @Override
    public void onReceive(Context context, Intent intent) {

           Boolean entering = intent.getExtras().getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);

           if (entering) {
               Toast.makeText(context, "entering "+entering, Toast.LENGTH_LONG).show();
               Log.i("TAG-STATUS:",entering.toString());
           } else {
               Toast.makeText(context, "exiting "+entering, Toast.LENGTH_LONG).show();
               Log.i("TAG-STATUS:",entering.toString());
       }

    }
}
