package com.ei.kalavarafoods.ui.number_verification;

import android.app.ProgressDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.SmsReceiver;
import com.ei.kalavarafoods.ui.number_verification.model.OtpResponse;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.utils.SessionManager;

public class NumberVerificationActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private static AppCompatActivity activity;
    private static ProgressDialog progressDialog;
    private Button btn_send_otp;
    private Button btn_verify_otp;
    private EditText et_mobile_no;
    private EditText et_otp_no;
    private LinearLayout layout_verify;
    private String _mobile_no;
    private int statusCode;
    private NumberVerificationViewModel numberVerificationViewModel;
    private OtpResponse otpResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_verification);
        activity = this;
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle("Verify Phone Number");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        numberVerificationViewModel = ViewModelProviders.of(this).get(NumberVerificationViewModel.class);
        et_mobile_no = (EditText) findViewById(R.id.et_mobile_no);
        et_otp_no = (EditText) findViewById(R.id.et_otp_no);
        btn_send_otp = (Button) findViewById(R.id.btn_send_otp);
        btn_verify_otp = (Button) findViewById(R.id.btn_verify_otp);
        layout_verify = (LinearLayout) findViewById(R.id.layout_verify);
        disableVerificationLayout();
        btn_send_otp.setOnClickListener(this);
        btn_verify_otp.setOnClickListener(this);
    }

    private void disableVerificationLayout() {
        for (int i = 0; i < layout_verify.getChildCount(); i++)
            layout_verify.getChildAt(i).setEnabled(false);
    }

    private void enableVerificationLayout() {
        for (int i = 0; i < layout_verify.getChildCount(); i++)
            layout_verify.getChildAt(i).setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                super.onBackPressed();
                break;
            }
                default: {
                    break;
                }
        }
        return true;
    }

    public static void stop() {
        activity.finish();
    }

    public static void showProgressbar() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("background");
        progressDialog.show();
    }

    public static void dismissProgressbar() {
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_otp:
                if (btn_send_otp.getText().equals("edit phone")) {
                    editingNum();
                } else
                    validateNum();
                break;
            case R.id.btn_verify_otp:
                verifyOTP();
        }
    }

    private void verifyOTP() {
        String userOTP = et_otp_no.getText().toString().trim();
        if (userOTP.equals(new SessionManager(this).getOTPnumber())) {
            new SessionManager(this).setNumberVerified(true);
            Toast.makeText(this, "otp verification successful", Toast.LENGTH_SHORT).show();
            stop();
        } else {
            Toast.makeText(this, "otp is incorrect", Toast.LENGTH_SHORT).show();
        }
    }

    private void editingNum() {
        et_mobile_no.setEnabled(true);
        btn_send_otp.setText("send OTP code");
        disableVerificationLayout();
    }

    private void validateNum() {
        _mobile_no = et_mobile_no.getText().toString().trim();
        if (isValidMobileNo(_mobile_no)) {
            et_mobile_no.setEnabled(false);
            btn_send_otp.setText("edit phone");
            btn_send_otp.setEnabled(false);
            enableSmsReceiver(this);
            String modified = "91" + _mobile_no.trim();
            numberVerificationViewModel.getOtpMobile(_mobile_no.trim()).observe(this, otpResponse -> {
                this.otpResponse = otpResponse;
                if (otpResponse.getAlert().equals(Constants.SUCCESS)){
                    enableVerificationLayout();
                    new SessionManager(this).putOTP(otpResponse.getOtp().toString());
                }
            });
        } else {
            Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }


    private void enableSmsReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, SmsReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Toast.makeText(context, "Enabled broadcast receiver", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidMobileNo(String mobile_no) {
        String regEx = "^[0-9]{10}$";
        return mobile_no.matches(regEx);
    }
}
