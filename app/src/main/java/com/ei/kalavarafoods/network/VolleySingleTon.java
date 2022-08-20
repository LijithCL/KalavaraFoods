package com.ei.kalavarafoods.network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ei.kalavarafoods.AppApplication;

public class VolleySingleTon {
    private static VolleySingleTon sInstance = null;
    private RequestQueue mRequestQueue;

    private VolleySingleTon() {
        mRequestQueue = Volley.newRequestQueue(AppApplication.getAppContext());
    }

    public static VolleySingleTon getsInstance() {
        if (sInstance == null) {
            sInstance = new VolleySingleTon();
        }
        return sInstance;
    }

    public RequestQueue getmRequestQueue() {
        return mRequestQueue;
    }
}
