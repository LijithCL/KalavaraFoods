package com.ei.kalavarafoods;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ei.kalavarafoods.ui.number_verification.NumberVerificationActivity;
import com.ei.kalavarafoods.utils.SessionManager;

public class HttpService extends IntentService {
    private static String TAG = HttpService.class.getSimpleName();

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String otp = intent.getStringExtra("otp");
            verifyOtp(otp);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("stopped" ,"service");
        Toast.makeText(this,"stopped!!!",Toast.LENGTH_SHORT).show();
        NumberVerificationActivity.stop();
    }

    private void verifyOtp(String otp) {
        NumberVerificationActivity.showProgressbar();
//        StringRequest strReq = new StringRequest(Request.Method.POST, Constants.URL_VERIFY_OTP, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("otp_verify_response",response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("otp_verify_error",error.toString());
//                new SessionManager(getApplicationContext()).setNumberVerified(true);
//                progressBar.dismiss();
//                SmsReceiver.stopService();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                return super.getParams();
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return super.getHeaders();
//            }
//        };
//        VolleySingleTon.getsInstance().getmRequestQueue().add(strReq);
        if ((new SessionManager(getApplicationContext()).getOTPnumber()).equals(otp)){
            new SessionManager(getApplicationContext()).setNumberVerified(true);
            new SessionManager(getApplicationContext()).putOTP(null);
            SmsReceiver.stopService();
        }
        NumberVerificationActivity.dismissProgressbar();
    }
}
