package com.ei.kalavarafoods.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.api.SignUpLoginPostResult;
import com.ei.kalavarafoods.ui.auth.mobile_number.VerifyOtpActivity;
import com.ei.kalavarafoods.utils.AppUtils;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.utils.SessionManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, FacebookCallback<LoginResult>, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    ProgressDialog progressDialog;

    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etPasswordConfirm)
    EditText etPasswordConfirm;
    @BindView(R.id.btn_signup)
    Button btnSignUp;
    @BindView(R.id.link_login)
    TextView tvLoginLink;
    @BindView(R.id.progressSignUp)
    ProgressBar progressSignUp;

    String _result;
    private String mEmail, callingActivity = null;
    boolean isPressed;
    private LoginButton loginButton;
    private AppCompatButton fb, google;
    private CallbackManager callbackManager;
    private TextView textView;
    private GoogleApiClient mGoogleApiClient;
    int responseCode;
    private static final int RC_GOOGLE_SIGN_IN = 1;
    private AuthViewModel mAuthViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        if (toolbar != null)
            toolbar.setTitle("Sign up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuthViewModel = ViewModelProviders.of(this).get(AuthViewModel.class);
        callingActivity = getIntent().getExtras().getString("caller");

        btnSignUp.setOnClickListener(this);
        tvLoginLink.setTypeface(Typeface.create("sans-serif-condensed-thin", Typeface.NORMAL));
        tvLoginLink.setOnClickListener(this);
        isPressed = false;

        textView = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        fb = (AppCompatButton) findViewById(R.id.fb);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, this);
        fb.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        google = (AppCompatButton) findViewById(R.id.google);
        google.setOnClickListener(this);

        mAuthViewModel.liveDataIsLoading.observe(this, isLoading -> {
            if (isLoading){
                progressSignUp.setVisibility(View.VISIBLE);
            }else {
                progressSignUp.setVisibility(View.INVISIBLE);
            }
        });

        mAuthViewModel.liveDataSignUpPostResult.observe(this, signUpLoginPostResult -> {
            String message = signUpLoginPostResult.getMsg();
            Log.i("signResult>>>", signUpLoginPostResult.toString());
            if (signUpLoginPostResult.getAlert().equals("success")) {
                onSignUpSuccess(signUpLoginPostResult);
                Log.i("Signin>>", message);
                btnSignUp.setEnabled(true);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                btnSignUp.setEnabled(true);
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fb:
                loginButton.performClick();
                break;
            case R.id.google:
                googleSignIn();
                break;
            case R.id.btn_signup:
                validate();
                break;
            case R.id.link_login:
                finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

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

    public void validate() {
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();
        String password = etPassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            etName.setError("at least 3 characters");
        } else if (phone.isEmpty() || phone.length() < 10){
            etPhone.setError("enter a valid phone number");
        } else if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            etPassword.setError("between 4 and 10 alphanumeric characters");
        } else if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())){
            etPasswordConfirm.setError("Passwords doesn't match");
        } else {
            signUp();
        }
    }

    public void signUp() {
        btnSignUp.setEnabled(false);
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();
        String password = etPassword.getText().toString();
        mAuthViewModel.signUpUser(name, phone, password);
    }

    private void onSignUpSuccess(SignUpLoginPostResult signUpLoginPostResult) {
        String userId = signUpLoginPostResult.getUid();
        String userRole = signUpLoginPostResult.getUserrole();
        String email = signUpLoginPostResult.getEmail();
        mAuthViewModel.userDataToSharedPref(email, userId,"normal", userRole);
        if (callingActivity.equals("CheckOutLogin")){
            finish();
        } else {
            postNumber(email);
        }
    }

    private void postNumber(String phone) {
        mAuthViewModel.getOtpMobile(phone)
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


    private void googleSignIn() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            progressSignUp.setVisibility(View.VISIBLE);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        }else {
            if (googleApiAvailability.isUserResolvableError(resultCode)){
                googleApiAvailability.getErrorDialog(this, resultCode, 2404).show();
            }
        }
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Bundle bFacebookData = AuthViewModel.getFacebookData(object);
                String email = bFacebookData.getString("email");
                mEmail = email;
                textView.setText(email);
                final String name = bFacebookData.getString("first_name")+" "+bFacebookData.getString("last_name");
                Log.d("name", name);
                Log.d("email", bFacebookData.getString("email"));
                mAuthViewModel.userLoginSocial(name, email, "fb");
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Log.e("vishnu", "result " + resultCode);
            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleGoogleSignInResult(result);
            } else {
                progressSignUp.setVisibility(View.INVISIBLE);
            }
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null && acct.getEmail() != null) {
                mEmail = acct.getEmail();
                String name = acct.getDisplayName();
                Log.e("email",mEmail);
                mAuthViewModel.userLoginSocial(name, mEmail, "google");
            }
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    Log.e("sign_out", "successfully");
                }
            });
        } else {
            progressSignUp.setVisibility(View.INVISIBLE);
        }
    }
}