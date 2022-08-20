package com.ei.kalavarafoods;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import org.json.JSONArray;
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

/**
 * Created by AMK on 2/19/16.
 */
public class ReceipeActivity extends AppCompatActivity {


    String _result;
    String _order_id, _order_time,_order_delivery_date, _order_amount;
    private Double _grantTotal;

    //	List<String> AddressList;
    List<HashMap<String, String>> _Itemlist;


    @BindView(R.id.order_id) TextView Order_Id;
    @BindView(R.id.order_date) TextView Order_Date;
    @BindView(R.id.delivery_dat) TextView Delivery_Date;
//    @Bind(R.id.del_charge) TextView Delivery_Charge;
//    @Bind(R.id.sub_total_amt) TextView Sub_total;

    String _Shipping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.display_history_receipe);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _Itemlist = new ArrayList<>();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            _order_id = bundle.getString("order_id");

            final String url = Constants.HISTORY_ITEMS ;
            new HttpAsyncTaskGetRecept().execute(url);

            _order_delivery_date = bundle.getString("order_delivery_date");
            _order_time = bundle.getString("order_time");
            _order_amount = bundle.getString("order_amount");
            _grantTotal = 0.0;
        }

        Order_Id.setText(_order_id);
        Order_Date.setText(_order_time);
        Delivery_Date.setText(_order_delivery_date);

    }


        public void init() {

            if(Double.parseDouble(_order_amount) >= 350.0) {
//        Delivery_Charge.setText("50.0");
                _Shipping = "00.0";
            }else if(Double.parseDouble(_order_amount) <= 00.0){
//        Delivery_Charge.setText("00.0");
//        Sub_total.setText(_order_amount);
                _Shipping = "00.0";

            }else{
                _Shipping = "50.0";

            }
            TableLayout stk = (TableLayout) findViewById(R.id.table_main);
            TableRow tbrow0 = new TableRow(this);

            TextView tv0 = new TextView(this);
            tv0.setText(" Product Name");
            tv0.setTextColor(Color.BLACK);
            tv0.setGravity(Gravity.LEFT);
            tbrow0.addView(tv0);

            TextView tv1 = new TextView(this);
            tv1.setText(" Price ");
            tv1.setTextColor(Color.BLACK);
            tbrow0.addView(tv1);

            TextView tv2 = new TextView(this);
            tv2.setText(" Quantity ");
            tv2.setTextColor(Color.BLACK);
            tbrow0.addView(tv2);

            TextView tv3 = new TextView(this);
            tv3.setText(" Total ");
            tv3.setTextColor(Color.BLACK);
            tv3.setGravity(Gravity.RIGHT);
            tbrow0.addView(tv3);

            stk.addView(tbrow0);

            //////////////////////////////////////////

            TableRow tbrow6 = new TableRow(this);

            TextView ntv = new TextView(this);
            ntv.setText("............................ ");
            ntv.setTextColor(Color.BLACK);
            ntv.setGravity(Gravity.LEFT);
            tbrow6.addView(ntv);

            TextView ntv51 = new TextView(this);
            ntv51.setText("..............");
            ntv51.setTextColor(Color.BLACK);
            tbrow6.addView(ntv51);

            TextView ntv61 = new TextView(this);
            ntv61.setText("..............");
            ntv61.setTextColor(Color.BLACK);
            tbrow6.addView(ntv61);

            TextView ntv71 = new TextView(this);
            ntv71.setText("..............");
            ntv71.setTextColor(Color.BLACK);
            ntv71.setGravity(Gravity.RIGHT);
            tbrow6.addView(ntv71);

            stk.addView(tbrow6);
//////////////////////////////////////////////////
            if(_Itemlist.size() > 0){
            for (int i = 0; i < _Itemlist.size(); i++) {
                HashMap<String,String> map =_Itemlist.get(i);


                TableRow tbrow = new TableRow(this);
                tbrow.setWeightSum(1);

                TextView t1v = new TextView(this);
                t1v.setText(map.get("productname"));
                t1v.setTextColor(Color.GRAY);
                t1v.setGravity(Gravity.LEFT);
                tbrow.addView(t1v);

                TextView t2v = new TextView(this);
                t2v.setText(map.get("price"));
                t2v.setTextColor(Color.GRAY);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);

                TextView t3v = new TextView(this);
                t3v.setText(map.get("size"));
                t3v.setTextColor(Color.GRAY);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v);

                TextView t4v = new TextView(this);
                t4v.setText(map.get("ItemTotal"));
                _grantTotal += Double.valueOf(map.get("ItemTotal").toString());
                t4v.setTextColor(Color.GRAY);
                t4v.setGravity(Gravity.RIGHT);
                tbrow.addView(t4v);

                stk.addView(tbrow);
            }
            }
            if (_grantTotal>=350)
                _Shipping = "0.0";
            else _Shipping = "50.0";

            TableRow tbroww = new TableRow(this);
            stk.addView(tbroww);
            //////////////////////////////////////////

            TableRow tbrow60 = new TableRow(this);

            TextView ntvw = new TextView(this);
            ntvw.setText(".............. ");
            ntvw.setTextColor(Color.WHITE);
            ntvw.setGravity(Gravity.LEFT);
            tbrow60.addView(ntvw);

            TextView ntv51w = new TextView(this);
            ntv51w.setText("..............");
            ntv51w.setTextColor(Color.WHITE);
            tbrow60.addView(ntv51w);

            TextView ntv61w = new TextView(this);
            ntv61w.setText("..............");
            ntv61w.setTextColor(Color.WHITE);
            tbrow60.addView(ntv61w);

            TextView ntv71w = new TextView(this);
            ntv71w.setText("..............");
            ntv71w.setTextColor(Color.BLACK);
            ntv71w.setGravity(Gravity.RIGHT);
            tbrow60.addView(ntv71w);

            stk.addView(tbrow60);
//////////////////////////////////////////////////

//////////////////////////////////////////

            TableRow tbrow5 = new TableRow(this);

            TextView ntv4 = new TextView(this);
            ntv4.setText(" Delvery/ Extra Charges ");
            ntv4.setTextColor(Color.BLACK);
            ntv4.setGravity(Gravity.LEFT);
            tbrow5.addView(ntv4);

            TextView ntv5 = new TextView(this);
            ntv5.setText(" ");
            ntv5.setTextColor(Color.BLACK);
            tbrow5.addView(ntv5);

            TextView ntv6 = new TextView(this);
            ntv6.setText("  ");
            ntv6.setTextColor(Color.BLACK);
            tbrow5.addView(ntv6);

            TextView ntv7 = new TextView(this);
            ntv7.setText(_Shipping);
            ntv7.setTextColor(Color.BLACK);
            ntv7.setGravity(Gravity.RIGHT);
            tbrow5.addView(ntv7);

            stk.addView(tbrow5);
//////////////////////////////////////////////////
//////////////////////////////////////////

            TableRow tbrow4 = new TableRow(this);

            TextView tv4 = new TextView(this);
            tv4.setText(" Total ");
            tv4.setTextColor(Color.BLACK);
            tv4.setGravity(Gravity.LEFT);
            tbrow4.addView(tv4);

            TextView tv5 = new TextView(this);
            tv5.setText(" ");
            tv5.setTextColor(Color.BLACK);
            tbrow4.addView(tv5);

            TextView tv6 = new TextView(this);
            tv6.setText("  ");
            tv6.setTextColor(Color.BLACK);
            tbrow4.addView(tv6);

            TextView tv7 = new TextView(this);
            int total = (int) (_grantTotal+ Double.valueOf(_Shipping.toString()));
            tv7.setText(String.valueOf(total)+".0");
            tv7.setTextColor(Color.BLACK);
            tv7.setGravity(Gravity.RIGHT);
            tbrow4.addView(tv7);

            stk.addView(tbrow4);
//////////////////////////////////////////////////

            TableRow tbroww2 = new TableRow(this);
            stk.addView(tbroww2);

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


    //    /**************************************************************************
//     * Async Login Task to GetAddress
//     *************************************************************************/
    private class HttpAsyncTaskGetRecept extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected String doInBackground(String... urls) {


            return GetRecept(urls[0]);

        }
        @Override
        protected void onPreExecute() {


            pd=new ProgressDialog(ReceipeActivity.this);
            pd.setIndeterminate(true);
            pd.setMessage("Loading please wait...");
            pd.setCancelable(false);
            pd.show();


        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String Result) {
            pd.dismiss();
//            _addressItems = new AdapterHistoryItem(getActivity(),Historylist);
//            _Historylist.setAdapter(_addressItems);
////            onpostaddressSuccess();
            init();

        }
        public String GetRecept(String url){
            InputStream inputStream = null;
            try {

                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);

                String json = "";


                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("order_id",_order_id);

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

            parseResult(_result);

            return _result;
        }

        private void parseResult(String result) {
            try {
                JSONObject response = new JSONObject(result);

                JSONArray ItemList = response.optJSONArray("itemlist");
                _Itemlist = new ArrayList<>();
                if(response.getString("alert").equals("success")){

                if(ItemList.length() != 0 || ItemList.length() > 0 ){


                    for (int i = 0; i < ItemList.length(); i++) {

                        HashMap<String, String> item = new HashMap<>();
                        JSONObject addressElements = ItemList.optJSONObject(i);
//                        JSONObject addressElements = addressElements1.getJSONObject("order");

                        item.put("productname",addressElements.optString("productname"));
                        item.put("size",addressElements.optString("quantity"));
                        item.put("price",addressElements.optString("price"));

                        double quantity = Double.valueOf(addressElements.optString("quantity"));
                        double price = Double.valueOf(addressElements.optString("price"));
                        double ItemTotal = quantity * price ;

                        item.put("ItemTotal",String.valueOf(ItemTotal));

                        Log.i("Address CategoryItems ", "" + item);
                        _Itemlist.add(item);
                    }

                }
                }
                else{

//                AddressList.add("Sorry No Address Found");
                    Log.i("Error","Sorry No Address Found");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
//		AddressString = AddressList.toArray(new String[AddressList.size()]);
        }

        private  String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }
    }

    //////////////

    }
