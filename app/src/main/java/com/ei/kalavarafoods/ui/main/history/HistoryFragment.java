package com.ei.kalavarafoods.ui.main.history;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ei.kalavarafoods.utils.ConnectionDetector;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.DBHelperDisplayItems;
import com.ei.kalavarafoods.NoInternetActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ReceipeActivity;
import com.ei.kalavarafoods.utils.SessionManager;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

public class HistoryFragment extends Fragment {

    ConnectionDetector cd;
    SessionManager session;
    DBHelperDisplayItems cartDb;
    String _uid, _result, _AddrId;
    RecyclerView _Historylist;
    AdapterHistoryItem _addressItems;

    //	List<String> AddressList;
    List<HashMap<String, String>> Historylist;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ButterKnife.bind(getActivity());
        // Inflate the layout for this fragment
        View V = inflater.inflate(R.layout.tab_history, container, false);
        cd = new ConnectionDetector(getActivity());
        connectionCheck();

        progressBar = V.findViewById(R.id.pb_historyFrag);
        cartDb = new DBHelperDisplayItems(getActivity());

        new HttpAsyncTaskGetHistory().execute(Constants.SHOP_HISTORY);

        Historylist = new ArrayList<>();
        // UserId
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        _uid = user.get("pass");


        _Historylist = (RecyclerView) V.findViewById(R.id.recycler_history);
        LinearLayoutManager llm = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        _Historylist.setLayoutManager(llm);
        _Historylist.setHasFixedSize(true);

//		_addressItems = new AdapterAddressItem(getActivity(),AddressList);
//		_AddressList.setAdapter(_addressItems);

        return V;

    }

    protected void connectionCheck() {

        if (!cd.isConnectingToInternet()) {

            Intent in = new Intent(getActivity(), NoInternetActivity.class);
            startActivity(in);
        }

    }

    //    /**************************************************************************
//     * Async Login Task to GetAddress
//     *************************************************************************/
    private class HttpAsyncTaskGetHistory extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected String doInBackground(String... urls) {
            return GetHistory(urls[0]);
        }


        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String Result) {
            if (progressBar.isShown()) {
                progressBar.setVisibility(View.GONE);
            }
            _addressItems = new AdapterHistoryItem(getActivity(), Historylist);
            _Historylist.setAdapter(_addressItems);
//            onpostaddressSuccess();
        }
    }

    //////////////

    public String GetHistory(String url) {
        InputStream inputStream = null;
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("user_id", _uid);
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

        parseResult(_result);

        return _result;
    }


    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);

            JSONArray SavedAddress = response.optJSONArray("history");
            if (response.getString("alert").equals("failed")) {
                Log.i("Error", "No CategoryItems Found");
            }
            Historylist = new ArrayList<>();
            if (SavedAddress.length() != 0 || SavedAddress.length() > 0) {


                for (int i = 0; i < SavedAddress.length(); i++) {

                    HashMap<String, String> item = new HashMap<>();
                    JSONObject addressElements1 = SavedAddress.optJSONObject(i);
                    JSONObject addressElements = addressElements1.getJSONObject("order");
                    item.put("order_id", addressElements.optString("order_id"));
                    item.put("order_userid", addressElements.optString("order_userid"));
                    item.put("order_delivery_date", addressElements.optString("order_delivery_date"));
                    item.put("order_delivery_time", addressElements.optString("order_delivery_time"));
                    item.put("order_amount", addressElements.optString("order_amount"));
                    item.put("order_deliveryaddress", addressElements.optString("order_deliveryaddress"));
                    item.put("order_time", addressElements.optString("order_time"));

                    Historylist.add(item);
                }
                Collections.sort(Historylist, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> temp1, HashMap<String, String> temp2) {
                        return Integer.parseInt(temp2.get("order_id")) - Integer.parseInt(temp1.get("order_id"));
                    }
                });
            } else {
                Log.e("Error", "Sorry No Address Found");
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    public void onResume() {
        reload();
        super.onResume();
    }

    //    /**************************************************************************
//     * Async Delete Address
//     *************************************************************************/


    public void reload() {
//		new HttpAsyncTaskGetHistory().execute(Constants.HISTORY_ITEMS);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Adapter Address Item ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public class AdapterHistoryItem extends RecyclerView.Adapter<AdapterHistoryItem.ViewHolderHistoryItem> {

        List<HashMap<String, String>> onlineData;
        Context context;

        AdapterHistoryItem(Context context, List<HashMap<String, String>> onlineData) {

            this.onlineData = onlineData;
            this.context = context;
            Log.e("Constructor", "Called ");
        }

        @Override
        public ViewHolderHistoryItem onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolderHistoryItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_user_order, parent, false));

        }

        @Override
        public void onBindViewHolder(final ViewHolderHistoryItem addr_Item, final int position) {

            final HashMap<String, String> map = onlineData.get(position);

            Log.e("Received", "" + map);

            addr_Item.Invoice_no.setText(map.get("order_id"));
            addr_Item.Order_Date.setText(map.get("order_time"));

            addr_Item.HistoryItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("order_id", map.get("order_id"));
                    bundle.putString("order_time", map.get("order_time"));
                    bundle.putString("order_delivery_date", map.get("order_delivery_date"));
                    bundle.putString("order_amount", map.get("order_amount"));

                    Intent in = new Intent(getActivity(), ReceipeActivity.class);
                    in.putExtras(bundle);
                    startActivity(in);

                }
            });

        }

        @Override
        public int getItemCount() {
            return onlineData.size();
        }

        public class ViewHolderHistoryItem extends RecyclerView.ViewHolder {
            TextView Invoice_no, Order_Date;
            CardView HistoryItem;

            public ViewHolderHistoryItem(View itemView) {
                super(itemView);

                HistoryItem = (CardView) itemView.findViewById(R.id.history_cardview_item);
//                Invoice_no = (TextView) itemView.findViewById(R.id.history_invoice_no);
//                Order_Date = (TextView) itemView.findViewById(R.id.history_date);
            }
        }

    }
}