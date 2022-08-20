package com.ei.kalavarafoods.ui.cart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ei.kalavarafoods.ui.address.AddressActivity;
import com.ei.kalavarafoods.utils.ConnectionDetector;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.DBHelperDisplayItems;
import com.ei.kalavarafoods.NoInternetActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.main.MainActivity;
import com.ei.kalavarafoods.utils.SessionManager;
//import com.razorpay.PaymentResultListener;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckoutActivity extends AppCompatActivity //implements PaymentResultListener
{

    ConnectionDetector cd;
    SessionManager mSessionManager;

    @BindView(R.id.tvTotalAmount)
    TextView tvTotalAmount;
    @BindView(R.id.tvShippingAmount)
    TextView tvShippingAmount;
    @BindView(R.id.tvPayableAmount)
    TextView tvPayableAmount;
    @BindView(R.id.ivSelectAddress)
    ImageView ivSelectAddress;
    @BindView(R.id.ivAddAddress)
    ImageView ivAddAddress;

    @BindView(R.id.ckt_delinery_time_rg)
    RadioGroup rgCheckOutDeliveryTime;
    @BindView(R.id.ckt_delivery_radio_now)
    RadioButton rbExpress;
    @BindView(R.id.ckt_delivery_radio_today)
    RadioButton rbToday;
    @BindView(R.id.ckt_delivery_radio_tomorrow)
    RadioButton rbTomorrow;

//    @BindView(R.id.ckt_delinery_time_rg)
//    RadioGroup rgCheckOutDeliveryTime;
//    @BindView(R.id.ckt_delivery_radio_now)
//    RadioButton rbExpress;
//    @BindView(R.id.ckt_delivery_radio_today)
//    RadioButton rbToday;
//    @BindView(R.id.ckt_delivery_radio_tomorrow)
//    RadioButton rbTomorrow;


    private CartViewModel mCartViewModel;
    private List<ProductEntity> mItemsInCart = new ArrayList<>();

    static TextView finaldate;
    TextView AddressSet;
    Button Place_Order;

    List<HashMap<String, String>> _Addresslist;


    List<String> AddressList;
    static List<HashMap<String, String>> CartItemsList;


    String[] AddressString;
    DBHelperDisplayItems cartDb;

    static String CartItems;
    float subTotal;

    static String Order_Price, Delivery_Time;


    RadioGroup  Del_Time_rg2;
    RadioGroup paymentOption;
    RadioButton radioButton;
    RadioButton TWELVE, SIX;

    int paymentType;


    String SelectedTimeSchedule;
    String DeliveryDate, _result, mUserId, deliveryCharge;
    String _DeliveryAddress;

    float totalToPay;
    
    boolean _AddressAvail;

    private Intent mainActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        mCartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);

        mItemsInCart.addAll(mCartViewModel.getCartItems());
//        // Session class instance
//        mSessionManager = new SessionManager(getApplicationContext());
//        /** * check user login * */
//        mSessionManager.checkLogin();
        mainActivityIntent = new Intent(CheckoutActivity.this, MainActivity.class);
        cd = new ConnectionDetector(getApplicationContext());
        connectionCheck();

        // UserId
        mSessionManager = new SessionManager(this);
        mUserId = mSessionManager.getUserId();
        finaldate = (TextView) findViewById(R.id.selecteddateid);

        paymentOption = findViewById(R.id.radioPaymentOption);


        //--->time
        final Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        final SimpleDateFormat date = new SimpleDateFormat("KK:mm");
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        final String date1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        final int cHour = cal.get(Calendar.HOUR_OF_DAY);
        final int cMinutes = cal.get(Calendar.MINUTE);

//        com.razorpay.Checkout.preload(getApplicationContext());

        Log.e("Time is " + cHour, "" + cMinutes);

        //tomorrow date
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final String tomorrowAsString = dateFormat.format(tomorrow);


        DeliveryDate = date1;

        SelectedTimeSchedule = "10";


        cartDb = new DBHelperDisplayItems(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        final String url = "www.thenexterp.com/MetroMart/shopmob/listaddress";
        new HttpAsyncTaskGetAddress().execute(Constants.LIST_ADDRESS);
        CartItemsList = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        Cursor res = cartDb.getCartItems();

//        while (res.moveToNext()) {
//
//            buffer.append("{" + res.getString(1) + ":" + res.getString(7) + "}");
//
//            HashMap<String, String> item = new HashMap<>();
//            item.put("product_id", res.getString(1));
//            item.put("product_variant_id", "");
////                item.put("title", res.getString(2));
////                item.put("size", res.getString(3));
////                item.put("image", res.getString(6));
//            item.put("product_quantity", res.getString(7));
//            item.put("product_price", res.getString(5));
//
//            CartItemsList.add(item);
//
//            int ItemTotal = Integer.valueOf(res.getString(5)) * Integer.valueOf(res.getString(7));
//
//            subTotal += ItemTotal;
//        }

        subTotal = CartOperations.subTotalRoom(mItemsInCart);
        Log.e("List CategoryItems ", "" + CartItemsList);
        CartItems = buffer.toString();


//        AddAddr = (ImageView) findViewById(R.id.ckt_add_address);

        ivAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(CheckoutActivity.this, AddressActivity.class);
                startActivity(in);
            }
        });
        /////////  Address
        AddressSet = (TextView) findViewById(R.id.ckt_address_set_text);
//         = (ImageView) findViewById(R.id.ckt_sel_address);
        ivSelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alt_bld_address = new AlertDialog.Builder(CheckoutActivity.this);
                alt_bld_address.setTitle("Choose an Address").setCancelable(false);
                alt_bld_address.setSingleChoiceItems(AddressString, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        Toast.makeText(getApplicationContext(),
                                "Selected Address is = " + AddressString[item], Toast.LENGTH_SHORT).show();
                        AddressSet.setText(AddressString[item]);
                        for (int i = 0; i < _Addresslist.size(); i++) {
                            HashMap<String, String> map = _Addresslist.get(i);
                            if (map.get("address_type").equals(AddressSet.getText().toString())) {
                                AddressSet.setTag(map.get("address_id"));
                            }
                        }

                    }
                });
                alt_bld_address.setPositiveButton("SET", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                AlertDialog alertaddress = alt_bld_address.create();
                alertaddress.show();
            }
        });


        if (cHour > 16 || (cHour == 16 && cMinutes > 0)) {

            rbExpress.setEnabled(false);
            rbToday.setEnabled(false);

        }
        rbExpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deliveryexp = rbExpress.getText().toString();
                finaldate.setText(date1 + " , " + deliveryexp);
                Log.e("sel rad", rbExpress.getText().toString());
                DeliveryDate = date1;
                SelectedTimeSchedule = "10";
            }
        });

        rbToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder customDialog
                        = new AlertDialog.Builder(CheckoutActivity.this);
                customDialog.setTitle("Scheduled Delivery");
                LayoutInflater layoutInflater
                        = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.checkout_time, null);
                ButterKnife.bind(this, view);
                Del_Time_rg2 = (RadioGroup) view.findViewById(R.id.ckt_two_rg);
                TWELVE = (RadioButton) view.findViewById(R.id.ckt_delivery_2);
                TWELVE.setTag(2);
                SIX = (RadioButton) view.findViewById(R.id.ckt_delivery_5);
                SIX.setTag(5);


                if (cHour > 12 || (cHour == 12 && cMinutes > 0)) {
                    TWELVE.setEnabled(false);
                }

                if (cHour > 16 || (cHour == 16 && cMinutes > 0)) {
                    SIX.setEnabled(false);
                }

                Del_Time_rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                        String timetoday = radioButton.getText().toString();
                        finaldate.setText(date1 + " , " + timetoday);
                        SelectedTimeSchedule = "" + radioButton.getTag();
                        DeliveryDate = date1;

                    }
                });

                customDialog.setPositiveButton("SET", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                });

                customDialog.setView(view);
                customDialog.show();
            }
        });

        rbTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder customDialog
                        = new AlertDialog.Builder(CheckoutActivity.this);
                customDialog.setTitle("Scheduled Delivery");

                LayoutInflater layoutInflater
                        = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.checkout_time, null);

                Del_Time_rg2 = (RadioGroup) view.findViewById(R.id.ckt_two_rg);
                TWELVE = (RadioButton) view.findViewById(R.id.ckt_delivery_2);
                TWELVE.setTag(2);
                SIX = (RadioButton) view.findViewById(R.id.ckt_delivery_5);
                SIX.setTag(5);


                Del_Time_rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {


                        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                        String timetomorrow = radioButton.getText().toString();
                        finaldate.setText(tomorrowAsString + " , " + timetomorrow);
                        SelectedTimeSchedule = "" + radioButton.getTag();
                        DeliveryDate = tomorrowAsString;

                    }
                });
                customDialog.setPositiveButton("SET", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                });

                customDialog.setView(view);
                customDialog.show();
            }
        });

//        Del_Charge = (TextView) findViewById(R.id.ckt_shipping_amt);
//        Total_sub = (TextView) findViewById(R.id.ckt_total_amt);
//        Total = (TextView) findViewById(R.id.ckt_payable_amt);

        



        Place_Order = (Button) findViewById(R.id.place_order);
        Place_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Order_Price = String.valueOf(subTotal);
                Delivery_Time = finaldate.toString();
                paymentType = paymentOption.getCheckedRadioButtonId();

                String DeliverySelected = finaldate.getText().toString();
                _DeliveryAddress = AddressSet.getText().toString();
                if (_DeliveryAddress.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Select Delivery Address", Toast.LENGTH_LONG).show();

                } else if (DeliverySelected.equals("")) {

                    Toast.makeText(getApplicationContext(), "Please Select a Delivery Time", Toast.LENGTH_LONG).show();

                } else {

                    if (paymentType == R.id.radioOnline){

                        try {
                            checkoutWithRazorPay();
                            //TODO after payment place order to server
//                            new HttpAsyncTaskPlaceOrder().execute(Constants.CHECKOUT);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else {

                        try {

                            new HttpAsyncTaskPlaceOrder().execute(Constants.CHECKOUT);
//                            mCartViewModel.placeOrder(mItemsInCart, DeliveryDate,
//                                    SelectedTimeSchedule, deliveryCharge, AddressSet.getTag() );
                        } catch (Exception e) {
                            //TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }


            }
        });
        setDataInView();
    }

    private void setDataInView(){
        if (subTotal < 350) {
            tvTotalAmount.setText("" + subTotal + ".00");
            tvShippingAmount.setText("50.00");
            deliveryCharge = "50.00";
            totalToPay = subTotal + 50;
            tvPayableAmount.setText("" + totalToPay + ".00");
        } else {
            tvTotalAmount.setText("" + subTotal + ".00");
            tvShippingAmount.setText("00.00");
            deliveryCharge = "00.00";
            totalToPay = subTotal;
            tvPayableAmount.setText("" + totalToPay + ".00");
        }
    }
    private void checkoutWithRazorPay() {

        final Activity activity = this;
        String amount = ""+totalToPay*100;

//        final com.razorpay.Checkout co = new com.razorpay.Checkout();
//        co.setImage(R.mipmap.ic_launcher);
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Kalavara Foods");
            options.put("description", "Online Grocery");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            preFill.put("email", "test@razorpay.com");
            preFill.put("contact", "9876543210");

            options.put("prefill", preFill);

//            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    protected void connectionCheck() {

        if (!cd.isConnectingToInternet()) {

            Intent in = new Intent(CheckoutActivity.this, NoInternetActivity.class);
            startActivity(in);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionCheck();
        new HttpAsyncTaskGetAddress().execute(Constants.LIST_ADDRESS);
//        Log.e("test ","onResume");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new HttpAsyncTaskGetAddress().execute(Constants.LIST_ADDRESS);
//        Log.e("test ", "onRestart");


    }

//    @Override
//    public void onPaymentSuccess(String s) {
//
//    }
//
//    @Override
//    public void onPaymentError(int i, String s) {
//
//    }

    //    /**************************************************************************
//     * Async Login Task to GetAddress
//     *************************************************************************/
    private class HttpAsyncTaskGetAddress extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected String doInBackground(String... urls) {


            return GetAddress(urls[0]);

        }

        @Override
        protected void onPreExecute() {

            pd = new ProgressDialog(CheckoutActivity.this);
            pd.setIndeterminate(true);
            pd.setMessage("Loading please wait...");
            pd.setCancelable(true);
            pd.show();


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String Result) {
            if (!_AddressAvail) {
                ivSelectAddress.setVisibility(View.INVISIBLE);
            } else {
                ivSelectAddress.setVisibility(View.VISIBLE);
            }
            pd.dismiss();
//            onpostaddressSuccess();


        }
    }

    //////////////

    public String GetAddress(String url) {
        InputStream inputStream = null;
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";


            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("user_id", mUserId);

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
            params.setParameter("addstr", jsonObject);
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

        parseResult(_result);

        return _result;
    }


    private void parseResult(String result) {
        _AddressAvail = false;
        try {
            JSONObject response = new JSONObject(result);

            JSONArray SavedAddress = response.optJSONArray("addresses");
            if (response.getString("alert").equals("failed")) {
                Log.i("Error", "Sorry No Address Found");
            }
            if (SavedAddress.length() != 0 || SavedAddress.length() > 0) {
                _Addresslist = new ArrayList<>();
                AddressList = new ArrayList<String>();

                for (int i = 0; i < SavedAddress.length(); i++) {
                    HashMap<String, String> item = new HashMap<>();
                    JSONObject addressElements = SavedAddress.optJSONObject(i);

                    item.put("address_type", addressElements.optString("address_type"));
                    item.put("address_id", addressElements.optString("address_id"));

                    AddressList.add(addressElements.optString("address_type"));
//                     AddressList.add(addressElements.optString("activity_address"));

                    Log.i("Main Categories ", "" + item);
                    _Addresslist.add(item);
                }
                AddressString = AddressList.toArray(new String[AddressList.size()]);
                _AddressAvail = true;
            } else {

//                AddressList.add("Sorry No Address Found");
                Log.i("Error", "Sorry No Address Found");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        try {
//            JSONObject response = new JSONObject(result);
//            JSONArray posts = response.optJSONArray("addresses");
//            AddressList= new ArrayList<>();
//
//            for (int i = 0; i < posts.length(); i++) {
//                JSONObject postobj1 = posts.optJSONObject(i);
////                JSONObject postobj2 = postobj1.getJSONObject("Fruits");
//                AddressList.add(postobj2.optString("product_name"));
//
//                Log.i("Added to Array at "+i +" ",postobj2.optString("product_name"));
//                Log.e("Executed" ,"JSON");
//            }
//            AddressString = AddressList.toArray(new String[AddressList.size()]);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("Exception ", "Caught");
//        }


    }

    //////////////

    public String POST(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";


            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            JSONArray jsArray = new JSONArray();
            for (int i = 0; i < mItemsInCart.size(); i++) {
                JSONObject object = new JSONObject();
                try {
                    object.accumulate("product_id", mItemsInCart.get(i).getProductId());//get("product_id"));
                    object.accumulate("product_price", mItemsInCart.get(i).getProductUnitprice());//.get("product_price"));
                    object.accumulate("product_variant_id", "");//get("product_variant_id"));
                    object.accumulate("product_quantity", mItemsInCart.get(i).getProductOrderQuantitiy());//get("product_quantity"));
                    jsArray.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            jsonObject.accumulate("deliverydate", DeliveryDate);
            jsonObject.accumulate("deliverytime", SelectedTimeSchedule);
            jsonObject.accumulate("deliverycharge", deliveryCharge);
            jsonObject.accumulate("addressid", AddressSet.getTag());
//            jsonObject.accumulate("address_type",_DeliveryAddress);
            jsonObject.accumulate("user_id", mUserId);
            jsonObject.accumulate("items", jsArray);


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
//            json = "deliverytime: 2016-02-12 00:41:09\n" +
//                    "items: [ {\"product_id\":\"12\", \"product_variant_id\":\"24\",\"product_quantity\":\"2\", \"product_price\":\"240\"} , {\"product_id\":\"12\", \"product_variant_id\":\"24\",\"product_quantity\":\"7\", \"product_price\":\"340\"} , {\"product_id\":\"32\", \"product_variant_id\":\"54\",\"product_quantity\":\"6\", \"product_price\":\"270\"} ]";
            Log.e("JSON POST", json);


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("str", json));

//            ContentValues values = new ContentValues();
//            values.put("str",json);


            // 5. set json to StringEntity
//            StringEntity se = new StringEntity(json);

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
                result = convertInputStreamToString(inputStream);
            else
                result = "Error...........!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        Log.e("Received String ", result);
        // 11. return result


        return result;
    }


    private class HttpAsyncTaskPlaceOrder extends AsyncTask<String, Void, String> {
        ProgressDialog progd;

        @Override
        protected String doInBackground(String... urls) {


            return POST(urls[0]);
        }

        @Override
        protected void onPreExecute() {

            progd = new ProgressDialog(CheckoutActivity.this);
            progd.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progd.setMessage("Placing Order......");
            progd.setCancelable(false);
            progd.show();

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            ClearCart(result);

            Log.e("result = ", result);
            progd.dismiss();


        }
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

    public void ClearCart(String _result) {

        if (_result.toLowerCase().contains("success message")) {
            Log.e("_result", _result);
//            Cursor cart_items = cartDb.getCartItems();
//            while(cart_items.moveToNext()){
//                cartDb.DeleteCartItems(cart_items.getString(1));
//            }
//            cartDb.ClearCart();
            mCartViewModel.clearCart();

            tvTotalAmount.setText("00.00");
            tvPayableAmount.setText("00.00");
            finaldate.setText("");
            rgCheckOutDeliveryTime.clearCheck();
            new AlertDialog.Builder(this)
                    .setMessage("Order Placed, Thank You")
                    .setNeutralButton("Ok", (dialog, which) -> {
                        dialog.dismiss();
                        if (paymentType != R.id.radioOnline) {
                            startActivity(mainActivityIntent);
                        }
                    })
                    .show();
//            Toast.makeText(getApplicationContext(), "Order Placed, Thank You", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "An Error Occurred, Please Try Again", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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


}
