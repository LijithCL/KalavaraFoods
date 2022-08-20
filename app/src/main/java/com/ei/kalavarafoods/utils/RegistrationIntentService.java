package com.ei.kalavarafoods.utils;

import android.app.IntentService;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ei.kalavarafoods.network.VolleySingleTon;

import java.util.HashMap;
import java.util.Map;

public class RegistrationIntentService extends IntentService {
    public RegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra(Constants.AuthMode.MODE)) {
            sendToken(intent.getStringExtra(Constants.AuthMode.MODE), Constants.AuthMode.MODE);
            return;
        }
        SessionManager session = new SessionManager(getApplicationContext());
        sendToken(session.getUserDetails().get(SharedPref.Keys.KEY_EMAIL), getToken());
    }

    private String getToken() {
        return "";
    }

    private void sendToken(final String userEmail, final String token) {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_REGISTRATION_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.HEADER, Constants.API_KEY);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail);
                params.put("token", token);
                return params;
            }

            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };

        /*JSONObject reqObject = new JSONObject();
        try {
            reqObject.put("email", new SessionManager(getApplicationContext()).getUserDetails().get(SessionManager.KEY_EMAIL));
            reqObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = reqObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_UPDATE_REGISTRATION_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("VOLLEY", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.e("volley", "Unsupported Encoding while trying to get the bytes of %s using %s" + requestBody + "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.HEADER, Constants.API_KEY);
                return params;
            }
        };*/

        request.setRetryPolicy(new
                DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleTon.getsInstance().getmRequestQueue().add(request);
    }
}
