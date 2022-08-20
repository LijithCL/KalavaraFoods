package com.ei.kalavarafoods.ui.main.account.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.api.Address;
import com.ei.kalavarafoods.ui.address.AddressEditActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHold> {
    private Context context;
    private List<Address> addressList;
    private DeleteAddress deleteAddress;

    public AddressAdapter(Fragment fragment, List<Address> addressList) {
        this.addressList = addressList;
        if (fragment instanceof DeleteAddress){
            this.deleteAddress = (DeleteAddress) fragment;
        }
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_address_item, parent, false);
        return new ViewHold(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHold holder, int position) {
        Address address = addressList.get(position);
        holder.tvPlace.setText(address.getAddressPlace());
        holder.tvAddress.setText(address.getAddress());
        holder.tvLandmark.setText(address.getAddressLandmark());
        holder.tvPostcode.setText(address.getAddressPincode());

        holder.ivRemove.setOnClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Address?")
                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel())
                    .setPositiveButton("DELETE", (dialog, which) -> deleteAddress.delete(address.getAddressId()))
                    .show();
        });

        holder.ivEdit.setOnClickListener(view -> {
            editAddress(address);
        });
    }

    private void editAddress(Address address) {
        Intent EditAddr = new Intent(context, AddressEditActivity.class);
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
        context.startActivity(EditAddr);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class ViewHold extends RecyclerView.ViewHolder {
        @BindView(R.id.tvPlace)
        TextView tvPlace;
        @BindView(R.id.tvAddress)
        TextView tvAddress;
        @BindView(R.id.tvLandmark)
        TextView tvLandmark;
        @BindView(R.id.tvPostcode)
        TextView tvPostcode;
        @BindView(R.id.ivRemove)
        ImageView ivRemove;
        @BindView(R.id.ivEdit)
        ImageView ivEdit;

        public ViewHold(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface DeleteAddress{
        void delete(String addressId);
    }
}
