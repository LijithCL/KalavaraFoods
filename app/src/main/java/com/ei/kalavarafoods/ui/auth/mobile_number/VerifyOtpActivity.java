package com.ei.kalavarafoods.ui.auth.mobile_number;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.auth.SignUpActivity;
import com.ei.kalavarafoods.ui.base.BaseActivity;
import com.ei.kalavarafoods.ui.main.MainActivity;
import com.ei.kalavarafoods.utils.AppUtils;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.utils.SharedPref;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerifyOtpActivity extends BaseActivity {

    @BindView(R.id.etOTP)
    EditText etOTP;
    @BindView(R.id.btnVerify)
    Button btnVerify;

    public static Intent start(Context context) {
        return new Intent(context, VerifyOtpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.verify_otp);
        String otp = getIntent().getStringExtra(Constants.OTP);
        String phone = getIntent().getStringExtra(Constants.PHONE);

        btnVerify.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(etOTP.getText())){
                verifyOTP(otp, phone);
            } else {
                AppUtils.showToast(this, "Enter OTP");
            }
        });
    }

    private void verifyOTP(String otp, String phone) {
        if (otp.equals(etOTP.getText().toString().trim())){
            SharedPref.putString(SharedPref.Keys.PHONE_NUMBER, phone);
            Intent toMain = new Intent(VerifyOtpActivity.this, MainActivity.class);
            toMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(toMain);
        } else {
            AppUtils.showToast(this, "OTP is not valid");
        }
    }

    @Override
    public int setLayout() {
        return R.layout.activity_verify_otp;
    }

    @Override
    public boolean setToolbar() {
        return true;
    }
}
