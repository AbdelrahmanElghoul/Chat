package com.example.chat.Notification;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class PushNotification {

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
//    private String serverKey = "key=" + "AAAAwouVYwo:APA91bH54sSDh3ETUtFeDT1mOmFhEv9JUK8aX28n8p3urcbqX9z4HapFnM4BbtBGOm1q5XxqHyMnWGadsQfQZhVtcc1So9gS7tuPDvkeewrqJG29taVO3S49QdDjpyL9GJTMLz5_iXBX";
    final private String contentType = "application/json";

    public void Notify(Context context,String NOTIFICATION_TITLE,String NOTIFICATION_MESSAGE,String TOKEN,String ServerKey){

       String TOPIC = "/"+TOKEN; //topic must match with what the receiver subscribed to

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Timber.e(e);
        }
        sendNotification(context,notification,ServerKey);
    }

    private void sendNotification(Context context,JSONObject notification,String ServerKey) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                response -> Timber.d(response.toString()),
                error -> {
                    Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                    Timber.e(error);
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" +ServerKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static class MySingleton {
        private  static MySingleton instance;
        private RequestQueue requestQueue;
        private Context ctx;

        private MySingleton(Context context) {
            ctx = context;
            requestQueue = getRequestQueue();
        }

        public static synchronized MySingleton getInstance(Context context) {
            if (instance == null) {
                instance = new MySingleton(context);
            }
            return instance;
        }

        public RequestQueue getRequestQueue() {
            if (requestQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            }
            return requestQueue;
        }

        public <T> void addToRequestQueue(Request<T> req) {
            getRequestQueue().add(req);
        }
    }
}
