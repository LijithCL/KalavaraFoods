package com.ei.kalavarafoods.ui.main.account.change_phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.auth.AuthViewModel;
import com.ei.kalavarafoods.ui.auth.mobile_number.VerifyOtpActivity;
import com.ei.kalavarafoods.ui.base.BaseActivity;
import com.ei.kalavarafoods.utils.AppUtils;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.utils.SharedPref;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePhoneActivity extends BaseActivity {

    private ChangePhoneViewModel changePhoneViewModel;

    @BindView(R.id.tvOldNumber)
    TextView tvOldNumber;
    @BindView(R.id.etNewNumber)
    EditText etNewNumber;
    @BindView(R.id.btnUpdate)
    Button btnUpdate;

    public static Intent start(Context context) {
        return new Intent(context, ChangePhoneActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(getString(R.string.change_phone_number));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changePhoneViewModel = ViewModelProviders.of(this).get(ChangePhoneViewModel.class);

        tvOldNumber.setText(SharedPref.getString(SharedPref.Keys.PHONE_NUMBER,""));
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etNewNumber.getText())){
                   update(etNewNumber.getText().toString());
                }

            }
        });
    }

    private void update(String phone) {
        changePhoneViewModel.getOtpMobile(phone.trim())
                .observe(this, otpResponse -> {
                    if (otpResponse.getAlert().equals(Constants.SUCCESS)){
                        new SessionManager(this).putOTP(otpResponse.getOtp().toString());
                        Intent toVerifyIntent = VerifyOtpActivity.start(this);
                        toVerifyIntent.putExtra(Constants.OTP, otpResponse.getOtp().toString());
                        toVerifyIntent.putExtra(Constants.PHONE, phone);
                        startActivity(toVerifyIntent);
                    } else {
                        AppUtils.showToast(this, otpResponse.getMsg());
                    }
                });
    }

    @Override
    public int setLayout() {
        return R.layout.activity_change_phone;
    }

    @Override
    public boolean setToolbar() {
        return true;
    }
}
