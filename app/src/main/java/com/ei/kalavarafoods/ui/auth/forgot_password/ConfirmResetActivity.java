package com.ei.kalavarafoods.ui.auth.forgot_password;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.auth.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ConfirmResetActivity extends AppCompatActivity {

    @BindView(R.id.reset_code)
    EditText _resetCode;
    @BindView(R.id.btn_continue)
    Button _continueButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    String _userId;
    String _otpCode;
    String _uid;
    String _email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reset);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            _otpCode = bundle.getString("OTP");
            _userId = bundle.getString("UserId");
            _email = bundle.getString("UserMail");
        }

        _continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ConfirmResetActivity.this,LoginActivity.class);
                startActivity(in);
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void verify(){
        if(_resetCode.getText().toString().equals(_otpCode)){
            securitySuccess();
        }else{
            securityFail();
        }
    }

    public void securitySuccess(){
        Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ConfirmResetActivity.this, NewPasswordActivity.class);
        intent.putExtra("Email",_email);
        intent.putExtra("UserId",_uid);
        startActivity(intent);
    }

    public void securityFail(){
        Toast.makeText(getApplicationContext(), "Verification Failed", Toast.LENGTH_SHORT).show();
    }
}
