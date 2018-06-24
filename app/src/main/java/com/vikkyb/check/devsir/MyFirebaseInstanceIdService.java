package com.vikkyb.check.devsir;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

//Base class to handle Firebase Instance ID token refresh events.
//
//Any app interested in the Instance ID, or using Instance ID tokens, can extend this class and implement onTokenRefresh()
// to receive token refresh events.



public class MyFirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token in logcat
        Log.e(TAG, "Refreshed token: " + refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }
}