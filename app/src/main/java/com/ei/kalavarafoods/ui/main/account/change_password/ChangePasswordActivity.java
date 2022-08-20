package com.ei.kalavarafoods.ui.main.account.change_password;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.base.BaseActivity;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.utils.SharedPref;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePasswordActivity extends BaseActivity {
    private static final String TAG = "PassResetActivity";
    ProgressDialog progressDialog;
    SessionManager session;

    @BindView(R.id.reset_old_pass)
    EditText _OldPass;
    @BindView(R.id.reset_new_pass)
    EditText _NewPass1;
    @BindView(R.id.reset_cnfm_new_pass)
    EditText _NewPass2;
    @BindView(R.id.reset_update_button)
    Button _ResetButton;
    @BindView(R.id.showpass_checkbox)
    CheckBox _ShowPass;

    String _result,_UserEmail;

    public static Intent start(Context context) {
        return new Intent(context, ChangePasswordActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(getString(R.string.change_password));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(getApplicationContext());

        // get user data from mSessionManager
        HashMap<String, String> user = session.getUserDetails();

        // name
        _UserEmail = user.get(SharedPref.Keys.KEY_EMAIL);

         _ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                reset();
            }
        });

        _ShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    _OldPass.setTransformationMethod(null);
                    _NewPass1.setTransformationMethod(null);
                    _NewPass2.setTransformationMethod(null);
                }else {
                    _OldPass.setTransformationMethod(new PasswordTransformationMethod());
                    _NewPass1.setTransformationMethod(new PasswordTransformationMethod());
                    _NewPass2.setTransformationMethod(new PasswordTransformationMethod());

                }
                // cursor reset his position so we need set position to the end of text
                _OldPass.setSelection(_OldPass.getText().length());
                _NewPass1.setSelection(_NewPass1.getText().length());
                _NewPass2.setSelection(_NewPass2.getText().length());
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

    @Override
    public int setLayout() {
        return R.layout.activity_change_password;
    }

    @Override
    public boolean setToolbar() {
        return true;
    }

    public void reset() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _ResetButton.setEnabled(false);

        progressDialog = new ProgressDialog(ChangePasswordActivity.this,R.style.AppTheme_Dark_Dialog);
        // TODO: Implement your own signUp logic here.
        final String url = Constants.CHANGE_PASS;
        new HttpAsyncTaskChangePass().execute(url);
     }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Error !", Toast.LENGTH_LONG).show();
        _ResetButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String oldpass = _OldPass.getText().toString();
        String newPass1 = _NewPass1.getText().toString();
        String newPass2 = _NewPass2.getText().toString();

        if (oldpass.isEmpty() || oldpass.length() < 4) {
            _OldPass.setError("Enter valid old password");
            valid = false;
        } else {
            _OldPass.setError(null);
        }


        if (newPass1.isEmpty() || newPass1.length() < 4 || newPass1.length() > 10) {
            _NewPass1.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _NewPass1.setError(null);
        }

        if (!newPass1.equals(newPass2)) {
            _NewPass2.setError("Password doesn't match");
            valid = false;
        } else {
            _NewPass2.setError(null);
        }

        return valid;
    }



//    /**************************************************************************
//     * Async Login Task to authenticate
//     *************************************************************************/
    private class HttpAsyncTaskChangePass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {


            return POST(urls[0] );

        }
        @Override
        protected void onPreExecute() {


            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Sending Request...");
            progressDialog.show();


        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String Result) {
             progressDialog.dismiss();
            if(Result.equals("true")){
                Toast.makeText(ChangePasswordActivity.this,"Password Changed Successfully ", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            else{
                Toast.makeText(ChangePasswordActivity.this,"Error ! Please Try again ", Toast.LENGTH_SHORT).show();
                _OldPass.setText("");
                _OldPass.setError("Wrong Old password");
                _ResetButton.setEnabled(true);
            }



        }
    }

    //////////////

    public String POST(String url){
        InputStream inputStream = null;
        _result = "ERROR : Failed to fetch data!";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";


            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();

            jsonObject.accumulate("email",_UserEmail);
            jsonObject.accumulate("password",_OldPass.getText().toString());
            jsonObject.accumulate("passwordnew",_NewPass1.getText().toString());
//            jsonObject.accumulate("user_id","41");




            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.e("JSON POST", json);


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("str", json));


            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
//            httpPost.setEntity(se);

            HttpParams params = new BasicHttpParams();
            params.setParameter("str", jsonObject);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));



            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("X-Api-Key", Constants.API_KEY);
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream

            inputStream = httpResponse.getEntity().getContent();


            // 10. convert inputstream to string
            if(inputStream != null)
                _result = convertInputStreamToString(inputStream);
            else
                _result = "ERROR : Failed to fetch data!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        Log.e("Received String ", _result);
        // 11. return result

        String Continue = "false";

        try {
            JSONObject response = new JSONObject(_result);

            if(response.getString("msg").contains("Error")){

                Continue = "false";
            }
            else{
                Continue = "true";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("Exception in ", " PassResetActivity ");
        }

        return Continue;
    }



    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}