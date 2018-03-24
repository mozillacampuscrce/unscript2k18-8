package com.codeblooded.chehra.teacher.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.codeblooded.chehra.teacher.Constants;
import com.codeblooded.chehra.teacher.util.RestClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * Created by tejas on 11/1/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token){
        SharedPreferences userPrefs = getApplicationContext().getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        String JWT = userPrefs.getString(Constants.TOKEN, "");
        if(JWT.equals("")) return ;

        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + JWT)};

        RequestParams params = new RequestParams();
        params.put("registration_id",token);
        params.put("cloud_message_type","FCM");

        Log.e(TAG,"sendRegistrationToServer :- "+token);
        RestClient.post("token/create/",headers,params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

}
