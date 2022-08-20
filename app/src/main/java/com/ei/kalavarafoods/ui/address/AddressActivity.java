package com.ei.kalavarafoods.ui.address;

import android.app.ProgressDialog;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.base.BaseActivity;
import com.ei.kalavarafoods.ui.select_location.SelectLocationDialogFragment;
import com.ei.kalavarafoods.ui.select_location.adapter.LocationsAdapter;
import com.ei.kalavarafoods.ui.select_location.model.Location;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.utils.SharedPref;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressActivity extends BaseActivity implements LocationsAdapter.SelectLocation {

    private static final String TAG = "AddressActivity";
    private static final int REQUEST_ADDRESS = 0;
    ProgressDialog progressDialog;
    SessionManager mSessionManager;
    private AddressViewModel addressViewModel;

    @BindView(R.id.etPinCode)
    EditText etPinCode;
    @BindView(R.id.etAddress)
    EditText etAddress;
    @BindView(R.id.etLandmark)
    EditText etLandmark;
    @BindView(R.id.tvPlaces)
    TextView tvPlaces;
    @BindView(R.id.btnAddressCancel)
    Button btnAddressCancel;
    @BindView(R.id.btnAddressSave)
    Button btnAddressSave;

    String _postcode;
    String _place;
    String _address;
    String _landmark;
    String _uid;

    public static Intent start(Context context) {
        return new Intent(context, AddressActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_address);
        addressViewModel = ViewModelProviders.of(this).get(AddressViewModel.class);
        mSessionManager = new SessionManager(getApplicationContext());
        Log.e("addr", mSessionManager.getLocality() + " " + mSessionManager.getState() + " " + mSessionManager.getPostalCode());

        if (!mSessionManager.getPostalCode().equals("")) {
            etPinCode.setText(mSessionManager.getPostalCode());
            etPinCode.setSelection(etPinCode.length());
        }
        tvPlaces.setText(SharedPref.getString(SharedPref.Keys.SELECTED_LOCATION, ""));

        HashMap<String, String> user = mSessionManager.getUserDetails();

        _uid = mSessionManager.getUserId();

        btnAddressCancel.setOnClickListener(v -> onBackPressed());

        btnAddressSave.setOnClickListener(v -> postaddress());

        tvPlaces.setOnClickListener(view -> {
            SelectLocationDialogFragment.newInstance().show(getSupportFragmentManager(), null);
        });
    }

    public void postaddress() {
        Log.d(TAG, "postaddress");
        if (!validate()) {
            onpostaddressFailed();
            return;
        }
        btnAddressSave.setEnabled(false);
        progressDialog = new ProgressDialog(AddressActivity.this,
                R.style.AppTheme_Dark_Dialog);

        _postcode = etPinCode.getText().toString();
        _address = etAddress.getText().toString();
        _landmark = etLandmark.getText().toString();
        _place = SharedPref.getString(SharedPref.Keys.SELECTED_LOCATION_ID, "");

        addressViewModel.addAddress(_uid, _address, _postcode, _place, _landmark)
                .observe(this, addressResponse -> {
                    if (addressResponse.getAlert().equals(Constants.SUCCESS)){
                        Toast.makeText(this, addressResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
    }


    public void onpostaddressFailed() {
        Toast.makeText(getBaseContext(), "Please TryAgain", Toast.LENGTH_LONG).show();
        btnAddressSave.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String pincode = etPinCode.getText().toString();
        String address = etAddress.getText().toString();
        String landmark = etLandmark.getText().toString();
        String places = tvPlaces.getText().toString();

        if (pincode.isEmpty()) {
            etPinCode.setError("Not valid");
            valid = false;
        } else {
            etPinCode.setError(null);
        }
        if (address.isEmpty()) {
            etAddress.setError("Not valid");
            valid = false;
        } else {
            etAddress.setError(null);
        }
        if (landmark.isEmpty()) {
            etLandmark.setError("Not valid");
            valid = false;
        } else {
            etLandmark.setError(null);
        }
        if (places.isEmpty()) {
            tvPlaces.setError("Not valid");
            valid = false;
        } else {
            tvPlaces.setError(null);
        }

        return valid;
    }

    @Override
    public void onLocationSelected(Location location) {
        tvPlaces.setText(location.getLocationName());
        SharedPref.putString(SharedPref.Keys.SELECTED_LOCATION, location.getLocationName());
        SharedPref.putString(SharedPref.Keys.SELECTED_LOCATION_ID, location.getId());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_address;
    }

    @Override
    public boolean setToolbar() {
        return true;
    }
}
