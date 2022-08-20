package com.ei.kalavarafoods.ui.address;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.base.BaseActivity;
import com.ei.kalavarafoods.ui.select_location.SelectLocationDialogFragment;
import com.ei.kalavarafoods.ui.select_location.adapter.LocationsAdapter;
import com.ei.kalavarafoods.ui.select_location.model.Location;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.utils.SharedPref;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressEditActivity extends BaseActivity implements LocationsAdapter.SelectLocation {

    private static final String TAG = "AddressActivity";
    private static final int REQUEST_ADDRESS = 0;
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
    String _addressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addressViewModel = ViewModelProviders.of(this).get(AddressViewModel.class);
        Bundle bundle = getIntent().getExtras();

        addressViewModel.isLoading.observe(this, aBoolean -> {
            if (aBoolean) {
                showProgress();
            } else {
                dismissProgress();
            }
        });

        _uid = bundle.getString("UId");
        _addressId = bundle.getString("Id");
        etPinCode.setText(bundle.getString("Pin"));
        etAddress.setText(bundle.getString("Address"));
        etLandmark.setText(bundle.getString("Landmark"));
        tvPlaces.setText(bundle.getString("City"));

        btnAddressCancel.setOnClickListener(v -> onBackPressed());
        btnAddressSave.setOnClickListener(v -> postAddress());
        tvPlaces.setOnClickListener(view -> {
            SelectLocationDialogFragment.newInstance().show(getSupportFragmentManager(), null);
        });
    }

    @Override
    public int setLayout() {
        return R.layout.activity_address_edit;
    }

    @Override
    public boolean setToolbar() {
        return true;
    }

    public void postAddress() {
        Log.d(TAG, "postaddress");

        if (!validate()) {
            onPostAddressFailed();
            return;
        }
        btnAddressSave.setEnabled(false);
        _postcode = etPinCode.getText().toString();
        _address = etAddress.getText().toString();
        _landmark = etLandmark.getText().toString();
        _place = SharedPref.getString(SharedPref.Keys.SELECTED_LOCATION_ID, "");

        addressViewModel.editAddress(
                _addressId,
                _uid,
                _address,
                _postcode,
                _place,
                _landmark).observe(this, addressResponse -> {
                    if (addressResponse.getAlert().equals(Constants.SUCCESS)) {
                        Toast.makeText(AddressEditActivity.this, addressResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADDRESS) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    public void onPostAddressFailed() {
        Toast.makeText(getBaseContext(), "Please try again", Toast.LENGTH_LONG).show();
        btnAddressSave.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String pincode = etPinCode.getText().toString();
        String address = etAddress.getText().toString();
        String landmark = etLandmark.getText().toString();
        String place = tvPlaces.getText().toString();

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
        if (place.isEmpty()) {
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
}
