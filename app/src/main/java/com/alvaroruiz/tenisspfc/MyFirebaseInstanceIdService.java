package com.alvaroruiz.tenisspfc;
import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by alvaroruiz on 17/03/18.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "TOKEN";
    @Override
    public  void onTokenRefresh(){

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);

    }
    private void sendRegistrationToServer(String token) {

        MyApp myApp = (MyApp)getApplicationContext();
        MainActivity act = (MainActivity)myApp.getActivity();
        act.saveToken(token);

    }
}
