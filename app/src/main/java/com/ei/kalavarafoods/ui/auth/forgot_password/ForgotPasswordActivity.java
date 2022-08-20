package com.ei.kalavarafoods.ui.auth.forgot_password;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ei.kalavarafoods.utils.ConnectionDetector;
import com.ei.kalavarafoods.NoInternetActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.auth.LoginActivity;
import com.ei.kalavarafoods.ui.base.BaseActivity;
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
 * Created by AMK on 3/8/16.
 */
public class ForgotPasswordActivity extends BaseActivity {
    private static final String TAG = "ResetActivity";
    private static final int REQUEST_SIGNUP = 0;
    ProgressDialog progressDialog;

    @BindView(R.id.etMobileNo)
    EditText etMobileNo;
    @BindView(R.id.link_member_login)
    TextView _memberLoginLink;
    @BindView(R.id.btnReset)
    Button btnReset;

    String mobileNo;
    String _result;
    String _resetCode;
    String _uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNo = etMobileNo.getText().toString();
                reset();
            }
        });

        _memberLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ForgotPasswordActivity.this,
                        LoginActivity.class).putExtra("caller",getClass().getSimpleName().toString());
                startActivity(in);
            }
        });
    }


    @Override
    public int setLayout() {
        return R.layout.activity_forgot_password;
    }

    @Override
    public boolean setToolbar() {
        return true;
    }


    public void reset() {

        if (TextUtils.isEmpty(etMobileNo.getText())) {
            onResetFailed();
            return;
        }

        btnReset.setEnabled(false);
        progressDialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.AppTheme_Dark_Dialog);
        if (new ConnectionDetector(getApplicationContext()).isConnectingToInternet())
            new HttpAsyncTaskReset().execute(Constants.RESET_REQUEST);
        else {
            startActivity(new Intent(getApplicationContext(), NoInternetActivity.class));
            btnReset.setEnabled(true);
        }
    }

    public void ValidateResponse(String result) {

//        {"alert":"success","id":"41","otp":"6b4fa8e","msg":"One Time Password sent to your mailid."}

        result = result.replace("\"", "");
        result = result.replace("{", "");
        result = result.replace("}", "");


        String[] Response = result.split(",");
        Log.i("New Response", result);

        try {

            if (Response[0].toLowerCase().contains("success")) {

                String[] Response1 = Response[1].split(":");
                _uid = Response1[1];

                String[] Response2 = Response[2].split(":");
                _resetCode = Response2[1];
//                Toast.makeText(ResetActivity.this,message,Toast.LENGTH_LONG).show();

                onResetSucess();
            } else {
                String[] Response3 = Response[1].split(":");
                String message = Response3[1];
                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();

                onResetFailed();
            }

        } catch (Exception e) {
            Toast.makeText(ForgotPasswordActivity.this, "Internal Server Error", Toast.LENGTH_LONG).show();
        }
    }

    public void onResetFailed() {
        Toast.makeText(ForgotPasswordActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
        btnReset.setEnabled(true);
    }

    public void onResetSucess() {
        btnReset.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), ConfirmResetActivity.class);
        intent.putExtra("OTP", _resetCode);
        intent.putExtra("UserMail", mobileNo);
        intent.putExtra("UserId", _uid);
        btnReset.setEnabled(true);
        startActivityForResult(intent, REQUEST_SIGNUP);
    }


    //    /**************************************************************************
//     * Async Login Task to authenticate
//     *************************************************************************/
    private class HttpAsyncTaskReset extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }

        @Override
        protected void onPreExecute() {


            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Requesting...");
            progressDialog.show();


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String Result) {
            ValidateResponse(Result);
            progressDialog.dismiss();


        }
    }

    //////////////

    public String POST(String url) {
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
//          JSONArray jsArray = new JSONArray(CartItemsList);
            jsonObject.accumulate("phone", etMobileNo.getText().toString());
//          jsonObject.accumulate("items",jsArray);


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
            if (inputStream != null)
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
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
