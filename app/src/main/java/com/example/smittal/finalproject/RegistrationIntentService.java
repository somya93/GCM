package com.example.smittal.finalproject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by smittal on 8/20/15.
 */
public class RegistrationIntentService extends IntentService{

    private static final String TAG = "RegIntentService";
    protected static final String ProjectID = "675745861506";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try{
            synchronized (TAG){
                InstanceID instanceID = InstanceID.getInstance(this);
                String token =instanceID.getToken(ProjectID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + token);

                subscribeTopics(token);

                postRegID(token);

                sharedPreferences.edit().putBoolean(Preferences.SENT_TOKEN_TO_SERVER, true).apply();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to create/send token.");
            sharedPreferences.edit().putBoolean(Preferences.SENT_TOKEN_TO_SERVER, false).apply();
            e.printStackTrace();
        }

        Intent registrationComplete = new Intent(Preferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void subscribeTopics(String token) throws IOException {
        for(String topic:TOPICS){
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    public void postRegID(final String token){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String str = "{\"regID\":\"" + token + "\", \"deviceType\":\"Android\"}";

                Log.i(TAG, str);

                try {
                    URL url = new URL("http://172.28.124.252:8080/feedex-app/services/customers/12345678-5f39-43a9-bb30-126b7e119a4b/registration");
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestProperty("Content-Type", "application/json" );
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    OutputStream outputStream = conn.getOutputStream();

                    outputStream.write(str.getBytes());
                    outputStream.flush();
                    outputStream.close();

                    Log.i(TAG, "flushed");

                    InputStream inputStream = conn.getInputStream();
                    String resp = IOUtils.toString(inputStream);
                    Log.i(TAG, resp);

                    Log.i(TAG, "Token sent to server" );
                }
                catch(IOException e){
                    Log.i(TAG, "Token not sent. :(");
                    e.printStackTrace();
                }

                return token;
            }
        }.execute(null, null, null);
    }
}
