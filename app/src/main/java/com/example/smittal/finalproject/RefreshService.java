package com.example.smittal.finalproject;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by smittal on 8/20/15.
 */
public class RefreshService extends InstanceIDListenerService {
    private static final String TAG = "InstanceIDRefreshService";

    public void onTokenRefresh(){
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
