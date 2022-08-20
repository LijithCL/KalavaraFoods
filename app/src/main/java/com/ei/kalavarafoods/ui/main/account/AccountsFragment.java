package com.ei.kalavarafoods.ui.main.account;
import android.app.ProgressDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ei.kalavarafoods.ui.address.AddressActivity;
import com.ei.kalavarafoods.ui.address.AddressEditActivity;
import com.ei.kalavarafoods.utils.ConnectionDetector;
import com.ei.kalavarafoods.ui.main.account.adapter.AddressAdapter;
import com.ei.kalavarafoods.ui.main.account.change_password.ChangePasswordActivity;
import com.ei.kalavarafoods.ui.main.account.change_phone.ChangePhoneActivity;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.DBHelperDisplayItems;
import com.ei.kalavarafoods.NoInternetActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.model.api.Address;
import com.ei.kalavarafoods.model.api.AddressItem;
import com.ei.kalavarafoods.utils.RegistrationIntentService;

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

public class AccountsFragment extends Fragment implements
        AddressAdapter.DeleteAddress {

    private static final String TAG = "AccountFragment>>>";
    ConnectionDetector cd;
    SessionManager mSessionManager;

    DBHelperDisplayItems cartDb;

    TextView _ChangePassLink, _SignOutLink;
    TextView _AddAddressLink, _options_note;
    String _uid, _result, _AddrId;
    RecyclerView _AddressList;
    Address_Adapter address_adapter;

    List<HashMap<String, String>> AddressList;

    List<Address> addressList = new ArrayList<>();
    private ProgressBar progressBar;
    private AccountsViewModel accountsViewModel;

    @BindView(R.id.tvChangePhone)
    TextView tvChangePhone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountsViewModel = ViewModelProviders.of(this).get(AccountsViewModel.class);
//        getAddressRetrofit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);
        ButterKnife.bind(this, view);
        cd = new ConnectionDetector(getActivity());
        connectionCheck();

        cartDb = new DBHelperDisplayItems(getActivity());
        _options_note = (TextView) view.findViewById(R.id.options_remainder);

        //new HttpAsyncTaskGetAddress().execute(Constants.LIST_ADDRESS);

        AddressList = new ArrayList<>();
        // UserId
        mSessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = mSessionManager.getUserDetails();
        _uid = mSessionManager.getUserId();


        _AddressList = (RecyclerView) view.findViewById(R.id.recyle_view_tab_account);
        LinearLayoutManager llm = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        _AddressList.setLayoutManager(llm);
        _AddressList.setHasFixedSize(true);

//		_addressItems = new AdapterAddressItem(getActivity(),AddressList);
//		_AddressList.setAdapter(_addressItems);


        _AddAddressLink = (TextView) view.findViewById(R.id.add_address);
        _ChangePassLink = (TextView) view.findViewById(R.id.change_pass);
        progressBar = view.findViewById(R.id.pb_accountsFrag);
        if (!new SessionManager(getContext()).getUserType().equals("normal")) {
            _ChangePassLink.setVisibility(View.GONE);
        } else _ChangePassLink.setVisibility(View.VISIBLE);
        _SignOutLink = (TextView) view.findViewById(R.id.logout);

        onClicks();



        ButterKnife.bind(this, view);

       getAddressRetrofit();

        return view;

    }

    private void onClicks() {
        _AddAddressLink.setOnClickListener(v -> startActivity(AddressActivity.start(getContext())));

        _ChangePassLink.setOnClickListener(v -> startActivity(ChangePasswordActivity.start(getContext())));

        tvChangePhone.setOnClickListener(view -> startActivity(ChangePhoneActivity.start(getContext())));

        _SignOutLink.setOnClickListener(v -> {
            AlertDialog.Builder exit_alertbldr = new AlertDialog.Builder(getActivity());
            exit_alertbldr.setTitle("Signout....?");
            exit_alertbldr.setNegativeButton("NO", (dialog, which) -> dialog.cancel());
            exit_alertbldr.setPositiveButton("YES", (dialog, which) -> {
                cartDb.ClearCart();
                mSessionManager.logoutUser();
                getActivity().startService(new Intent(getActivity(), RegistrationIntentService.class));
            });
            exit_alertbldr.show();
        });
    }

    public void getAddressRetrofit(){
        accountsViewModel.getUserAddresses(_uid).observe(getViewLifecycleOwner(), new Observer<AddressItem>() {
            @Override
            public void onChanged(@Nullable AddressItem addressItem) {
                if (addressItem != null) {
                    addressList = addressItem.getAddresses();
//                   Log.e(TAG, addressList.get(0).getAddress());
                    setAdapter(addressList);
                }
                if (progressBar.isShown()){
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void setAdapter(List<Address> addressList) {
        if (addressList.size() > 0) {
            _options_note.setVisibility(View.GONE);
            _AddressList.setAdapter(new AddressAdapter(this, addressList));
        }
    }

    protected void connectionCheck() {

        if (!cd.isConnectingToInternet()) {

            Intent in = new Intent(getActivity(), NoInternetActivity.class);
            startActivity(in);
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
       // reload();
        getAddressRetrofit();
        super.onResume();
    }

    @Override
    public void delete(String addressId) {
        _AddrId = addressId;
        new HttpAsyncTaskDeleteAddress().execute(Constants.DEL_ADDRESS);
    }

    //    /**************************************************************************
//     * Async Delete Address
//     *************************************************************************/
    private class HttpAsyncTaskDeleteAddress extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected String doInBackground(String... urls) {


            return DelAddress(urls[0]);

        }

        @Override
        protected void onPreExecute() {


        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String Result) {
           // reload();
            getAddressRetrofit();
        }
    }

//    public void reload() {
//        new HttpAsyncTaskGetAddress().execute(Constants.LIST_ADDRESS);
//    }

    //////////////

    public String DelAddress(String url) {
        InputStream inputStream = null;
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";


            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("address_id", _AddrId);

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

        parseDelResult(_result);

        return _result;
    }


    private void parseDelResult(String result) {
        try {
            JSONObject response = new JSONObject(result);

//			JSONArray SavedAddress = response.optJSONArray("addresses");
            if (response.getString("message").contains("Success")) {
// 				Toast.makeText(getActivity(), "An Error Occured, Please Try Again", Toast.LENGTH_SHORT).show();

            } else {
//				Toast.makeText(getActivity(), "An Error Occured, Please Try Again", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("Exception in ", " AccountsFragment.java >> parseDelResult() ");
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// Adapter Address Item ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    private class Address_Adapter extends RecyclerView.Adapter<VHAddressItem> {
        private Context context;
        //List<HashMap<String, String>> categoryList;
        List<Address> addressList;

        public Address_Adapter(Context context, List<Address> addressList) {
            this.context = context;
            //this.categoryList = categoryList;
            this.addressList = addressList;
        }

        @Override
        public VHAddressItem onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VHAddressItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_address, parent, false));
        }

        @Override
        public void onBindViewHolder(VHAddressItem addr_Item, final int position) {
           // final HashMap<String, String> map = categoryList.get(position);
            final Address address = addressList.get(position);

            Log.e("Received", "" + address.toString());

            addr_Item.Address_type.setText(address.getAddressType());//map.get("Type"));
            addr_Item.Address.setText(address.getAddress());//map.get("Address"));
            addr_Item.Pin.setText(address.getAddressPincode());//map.get("Pin"));
            addr_Item.City.setText(address.getAddressCity());//map.get("City"));
            addr_Item.State.setText(address.getAddrressState());//map.get("State"));
            addr_Item.Landmark.setText(address.getAddressLandmark());//map.get("Landmark"));
            addr_Item.ibDeleteAddress.setOnClickListener(view -> deleteAddress(address.getAddressId(), position));
            addr_Item.ibEditAddress.setOnClickListener(view -> editAddress(address));
        }

        private void editAddress(Address address) {
            Intent EditAddr = new Intent(getActivity(), AddressEditActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("Type", address.getAddressType());//map.get("Type"));
            bundle.putString("Id", address.getAddressId());//map.get("Id"));
            bundle.putString("UId", address.getUserId());//map.get("UId"));
            bundle.putString("Address", address.getAddress());//map.get("Address"));
            bundle.putString("Pin", address.getAddressPincode());//map.get("Pin"));
            bundle.putString("City", address.getAddressCity());//map.get("City"));
            bundle.putString("State", address.getAddrressState());//map.get("State"));
            bundle.putString("Landmark", address.getAddressLandmark());//map.get("Landmark"));

            EditAddr.putExtras(bundle);
            startActivity(EditAddr);
        }

        private void deleteAddress(final String id, final int position) {
            AlertDialog.Builder exit_alertbldr = new AlertDialog.Builder(getActivity());
            exit_alertbldr.setTitle("Delete Address....?");
            exit_alertbldr.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            exit_alertbldr.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _AddrId = id.toString();
                    new HttpAsyncTaskDeleteAddress().execute(Constants.DEL_ADDRESS);
                    networkCallToDelete(position);
                    addressList.remove(position);
                    notifyDataSetChanged();
                }
            });
            exit_alertbldr.show();
        }

        private void networkCallToDelete(int position) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("address_id", _AddrId);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return addressList.size();
        }
    }

    private class VHAddressItem extends RecyclerView.ViewHolder {
        TextView Address_type, Address, Pin, City, State, Landmark;
        ImageButton ibEditAddress, ibDeleteAddress;


        public VHAddressItem(View itemView) {
            super(itemView);
            Address_type = (TextView) itemView.findViewById(R.id.address_item);
            Address = (TextView) itemView.findViewById(R.id.address_view);
            Pin = (TextView) itemView.findViewById(R.id.pincode_view);
            City = (TextView) itemView.findViewById(R.id.city_view);
            State = (TextView) itemView.findViewById(R.id.state_view);
            Landmark = (TextView) itemView.findViewById(R.id.landmark_view);
            ibEditAddress = (ImageButton) itemView.findViewById(R.id.ibEditAddress);
            ibDeleteAddress = itemView.findViewById(R.id.ibDeleteAddress);
        }
    }
}