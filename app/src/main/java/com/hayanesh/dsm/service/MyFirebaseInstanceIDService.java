package com.hayanesh.dsm.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hayanesh.dsm.PrefManager;
import com.hayanesh.dsm.app.Config;

/**
 * Created by Hayanesh on 08-Jan-17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    PrefManager prefManager;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeRegIdInPref(refreshedToken);


        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Consumer").child(prefManager.getUserId()).child("RegID").setValue(token);
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        prefManager = new PrefManager(this.getApplicationContext());
        prefManager.setRegID(token);
    }
}
