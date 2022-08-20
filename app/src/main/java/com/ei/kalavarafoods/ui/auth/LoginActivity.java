package com.ei.kalavarafoods.ui.auth;

import android.content.Context;
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

import com.ei.kalavarafoods.ui.auth.forgot_password.ForgotPasswordActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.api.SignUpLoginPostResult;
import com.ei.kalavarafoods.ui.main.MainActivity;
import com.ei.kalavarafoods.utils.SessionManager;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
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

public class LoginActivity extends AppCompatActivity implements FacebookCallback<LoginResult>,
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int RC_GOOGLE_SIGN_IN = 1;

    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tvSignUpLink)
    TextView tvSignUpLink;
    @BindView(R.id.tvPasswordResetLink)
    TextView tvPasswordResetLink;
    @BindView(R.id.progressLogin)
    ProgressBar progressLogin;


    private String mEmail;
    private String mUserType;

    // Session Manager Class
    SessionManager session;
    private String callingActivity = "nothing";
    private LoginButton loginButton;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private String authToken;
    private AppCompatButton fb, google;
    private CallbackManager callbackManager;
    int responseCode;
    private TextView textView;
    private GoogleApiClient mGoogleApiClient;
    private boolean isPressed;
    private AuthViewModel mAuthViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
//        setupWindowAnimations();

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        if (toolbar != null)
            toolbar.setTitle("LOGIN");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuthViewModel = ViewModelProviders.of(this).get(AuthViewModel.class);
        // Session Manager
        session = new SessionManager(getApplicationContext());
        callingActivity = getIntent().getExtras().getString("caller");

        btnLogin.setOnClickListener(this);
        tvSignUpLink.setTypeface(Typeface.create("sans-serif-condensed-thin", Typeface.NORMAL));
        tvSignUpLink.setOnClickListener(this);
        tvPasswordResetLink.setTypeface(Typeface.create("sans-serif-condensed-thin", Typeface.NORMAL));
        tvPasswordResetLink.setOnClickListener(this);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setTypeface(Typeface.create("sans-serif-condensed-thin", Typeface.NORMAL));
        fb = (AppCompatButton) findViewById(R.id.fb);
        loginButton.setReadPermissions("email");
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {
                    Log.e("msg_name", currentProfile.getName());
                } else Log.e("current", "null profile");
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        loginButton.registerCallback(callbackManager, this);
        fb.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        google = (AppCompatButton) findViewById(R.id.google);
        google.setOnClickListener(this);
        isPressed = false;

        mAuthViewModel.liveDataSignUpPostResult.observe(this, signUpLoginPostResult -> {
            String message = signUpLoginPostResult.getMsg();
            Log.i("signResult>>>", signUpLoginPostResult.toString());
            if (signUpLoginPostResult.getAlert().equals("success")) {
                onLoginSuccess(signUpLoginPostResult);
                Log.i("Signin>>", message);
                btnLogin.setEnabled(true);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
            }

        });
        mAuthViewModel.liveDataIsLoading.observe(this, isLoading -> {
            if (isLoading){
                progressLogin.setVisibility(View.VISIBLE);
            } else {
                progressLogin.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fb:
                btnLogin.setEnabled(false);
                google.setEnabled(false);
                loginButton.performClick();
                break;
            case R.id.google:
                googleSignIn();
                break;
            case R.id.tvPasswordResetLink:
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                break;
            case R.id.tvSignUpLink:
                Intent intentSignupLink = new Intent(getApplicationContext(), SignUpActivity.class);
                intentSignupLink.putExtra("caller", callingActivity);
                startActivityForResult(intentSignupLink, REQUEST_SIGNUP);
                break;
            case R.id.btnLogin:
                validate();
                break;
        }
    }

    public void validate() {
        String email = etPhone.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty()) {
            etPhone.setError("enter a valid phone no");
        } else if (password.isEmpty() || password.length() < 4 || password.length() > 10){
            etPassword.setError("between 4 and 10 alphanumeric characters");
        } else {
            login();
        }
    }

    public void login() {
        Log.d(TAG, "Login");
        btnLogin.setEnabled(false);
        String phone = etPhone.getText().toString();
        mUserType = "normal";
        String password = etPassword.getText().toString();
        mAuthViewModel.loginUser(phone.trim(), password.trim());
    }

    public void onLoginSuccess(SignUpLoginPostResult signUpLoginPostResult) {
        btnLogin.setEnabled(true);
        google.setEnabled(true);
        etPhone.setText("");
        etPassword.setText("");
        String userId = signUpLoginPostResult.getUid();
        String userRole = signUpLoginPostResult.getUserrole();
        String email = signUpLoginPostResult.getEmail();
        mAuthViewModel.userDataToSharedPref("", userId, mUserType, userRole);
        if (callingActivity.equals("CheckOutLogin"))
            finish();
        else if(callingActivity.equals("ProfileActivity")){
            finish();
        }else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    public void googleSignIn() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            btnLogin.setEnabled(false);
            google.setEnabled(false);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        }else {
            if (googleApiAvailability.isUserResolvableError(resultCode)){
                googleApiAvailability.getErrorDialog(this, resultCode, 2404).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signUp logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
                return;
            }
        } else if (requestCode == RC_GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleGoogleSignInResult(result);
            } else {
                google.setEnabled(true);
                btnLogin.setEnabled(true);
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e("email", acct.getEmail());
            if (acct.getEmail() != null) {
                mEmail = acct.getEmail();
                mUserType = "social";
                mAuthViewModel.userLoginSocial(acct.getDisplayName(), mEmail, "google");
            }
            google.setEnabled(true);
            btnLogin.setEnabled(true);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    Log.e("sign_out", "successfully");
                }
            });
        } else {
            // Signed out, show unauthenticated UI.
            Log.e("fail", "here");
            google.setEnabled(true);
            btnLogin.setEnabled(true);
        }
    }


    @Override
    public void onSuccess(LoginResult loginResult) {
        authToken = loginResult.getAccessToken().getToken().toString();
        Log.e("onSuccess", authToken);
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(final JSONObject object, GraphResponse response) {
                Bundle bFacebookData = AuthViewModel.getFacebookData(object);
                textView = (TextView) findViewById(R.id.info);
                String name = bFacebookData.getString("first_name")+""+bFacebookData.getString("last_name");
                mEmail = bFacebookData.getString("email");
                mUserType = "social";
                Log.e("email",mEmail);
                textView.setText(mEmail);
                responseCode = 0;
                mAuthViewModel.userLoginSocial(name, mEmail, "fb");
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();
    }



    @Override
    public void onCancel() {
        btnLogin.setEnabled(true);
        google.setEnabled(true);
    }

    @Override
    public void onError(FacebookException error) {
        btnLogin.setEnabled(true);
        google.setEnabled(true);
        Log.e("fb_error", error.toString());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("on", "fail g+");
    }

    public static Intent activityIntent(Context context){
        return new Intent(context, LoginActivity.class);
    }

}
