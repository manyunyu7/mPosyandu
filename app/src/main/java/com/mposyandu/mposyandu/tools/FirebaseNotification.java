package com.mposyandu.mposyandu.tools;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mposyandu.mposyandu.Controller;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseNotification {
    private String body;
    private String title;
    private String topics_id;

    public FirebaseNotification(String body, String title, String id) {
        this.body = body;
        this.title = title;
        this.topics_id = id;
    }

    public void PushNotification() {
        JSONObject params = makeJSON();
        JsonObjectRequest Notification = new JsonObjectRequest(Request.Method.POST, Database.getNotif(), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json; charset=UTF-8");
                header.put("Authorization", "key=AIzaSyAWidWg8D3DJteH_u8fRDXfoW_aG-M_wxc");
                return header;
            }
        };
        Controller.getmInstance().addToRequestQueue(Notification);
    }

    public JSONObject makeJSON() {
        Map<String, String> notification = new HashMap<>();
        notification.put("body",body);
        notification.put("title",title);
        JSONObject obj_notification = new JSONObject(notification);

        Map<String, Object> params = new HashMap<>();
        params.put("notification",obj_notification);
        params.put("to", "/topics/"+topics_id);

        return new JSONObject(params);
    }
}
