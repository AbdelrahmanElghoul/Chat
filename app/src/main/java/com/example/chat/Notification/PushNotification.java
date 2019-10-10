package com.example.chat.Notification;

import android.content.Context;

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
    final private String contentType = "application/json";
    public static String TITLE_KEY="title";
    public static String MESSAGE_KEY="message";
    public static String TO_KEY="to";
    public static String DATA_KEY="data";
    public static String AUTH_KEY="Authorization";
    public static String CONTENT_TYPE_KEY="Content-Type";
    private String ServerKey;
    private Context context;

    public PushNotification(Context context,String ServerKey){
        this.context=context;
        this.ServerKey=ServerKey;
            }

    public void Notify(String title,String message,String token){
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put(TITLE_KEY, title);
            notificationBody.put(MESSAGE_KEY, message);

            notification.put(TO_KEY, "/"+token);
            Timber.d(message);
            notification.put(DATA_KEY, notificationBody);
        } catch (JSONException e) {
            Timber.e(e);
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                response -> Timber.d(response.toString()),
                Timber::e
                ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(AUTH_KEY, "key=" +ServerKey);
                params.put(CONTENT_TYPE_KEY, contentType);
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
