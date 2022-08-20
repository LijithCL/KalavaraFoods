package com.ei.kalavarafoods.ui.auth.forgot_password;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.auth.LoginActivity;
import com.ei.kalavarafoods.ui.main.MainActivity;
import com.ei.kalavarafoods.utils.Constants;

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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AMK on 3/9/16.
 */
public class NewPasswordActivity extends AppCompatActivity {
    ProgressDialog progressDialog;


    @BindView(R.id.input_newPassword)    EditText _newPassword;
    @BindView(R.id.input_confirmPassword)    EditText _confirmPassword;
    @BindView(R.id.btn_reset)    Button _resetButton;
    @BindView(R.id.link_login)    TextView _loginLink;

    String _uid;
    String _email;
    String _newPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpassword);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            _uid = bundle.getString("UserId");
            _email = bundle.getString("Email");
        }

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(NewPasswordActivity.this, LoginActivity.class);
                startActivity(in);
            }
        });

        _resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _resetButton.setEnabled(false);
                validatePassword();
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

    public void validatePassword(){
        Log.d("NewPasswordActivity", "change password");

        if (!validate()) {
            resetFailed();
            return;
        }
        progressDialog = new ProgressDialog(NewPasswordActivity.this,
                R.style.AppTheme_Dark_Dialog);

        new HttpAsyncTaskUserLogin().execute(Constants.RESET_PASS);

    }
    public void resetFailed(){
        _resetButton.setEnabled(true);
    }

    public void resetSucess( String response){
        _resetButton.setEnabled(true);
        String[] Response = response.split(",");
        Log.e("Response[0] is", Response[0]);
        Log.e("Response[1] is", Response[1]);

        if(Response[0].toLowerCase().contains("success")){
            Toast.makeText(NewPasswordActivity.this, "Password Reset Successfully", Toast.LENGTH_LONG).show();
             startActivity(new Intent(NewPasswordActivity.this,MainActivity.class));
         }
        else{
            Toast.makeText(NewPasswordActivity.this, "Error Resetting Password, Please Try Again", Toast.LENGTH_LONG).show();
          }
    }

    public boolean validate() {
        boolean valid = true;


        String newPassword = _newPassword.getText().toString();
        String confirmPassword = _confirmPassword.getText().toString();

        if (newPassword.isEmpty() || newPassword.length() < 4 || newPassword.length() > 10) {
            _newPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _newPassword.setError(null);
        }

        if (!newPassword.equals(confirmPassword)) {
            _confirmPassword.setError("password does not match");
            valid = false;
        } else {
            _confirmPassword.setError(null);
        }

        return valid;
    }




    //    /**************************************************************************
//     * Async Login Task to authenticate
//     *************************************************************************/
    private class HttpAsyncTaskUserLogin extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {


            return POST(urls[0] );

        }
        @Override
        protected void onPreExecute() {


            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();


        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String Result) {
             resetSucess(Result);
            progressDialog.dismiss();



        }
    }

    //////////////

    public String POST(String url){
        InputStream inputStream = null;
        String _result = "ERROR : Failed to fetch data!";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";


            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
//          JSONArray jsArray = new JSONArray(CartItemsList);
            jsonObject.accumulate("email",_email);
            jsonObject.accumulate("password",_newPassword.getText().toString());
 //         jsonObject.accumulate("items",jsArray);


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


        return _result;
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
