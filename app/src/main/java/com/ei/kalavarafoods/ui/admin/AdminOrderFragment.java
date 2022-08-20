package com.ei.kalavarafoods.ui.admin;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.database.OrderEntity;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ULLAS BABU on 13-Feb-18.
 */

public class AdminOrderFragment extends Fragment {
    private static final String ARG_STATUS = "status";
    private RecyclerView mRvOrders;
    private AdminViewModel mAdminViewModel;
    private String mStatus;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public static AdminOrderFragment newInstance(String status){
        AdminOrderFragment adminOrderFragment = new AdminOrderFragment();
        Bundle argBundle = new Bundle();
        argBundle.putString(ARG_STATUS, status);
        adminOrderFragment.setArguments(argBundle);
        return adminOrderFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStatus = getArguments().getString(ARG_STATUS);
        mAdminViewModel = ViewModelProviders.of(this).get(AdminViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_admin_order, container, false);

        mRvOrders = v.findViewById(R.id.rvOrders);
        mRvOrders.setLayoutManager(new LinearLayoutManager(getActivity()));
        final CardView cvProgressBar = v.findViewById(R.id.cvProgressBar);

        cvProgressBar.setVisibility(View.VISIBLE);
        mAdminViewModel.getOrderListLiveData(mStatus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(orderEntityList -> {
                    RvOrderListAdapter rvOrderListAdapter = new RvOrderListAdapter(orderEntityList, getActivity());
                    mRvOrders.setAdapter(rvOrderListAdapter);
                    cvProgressBar.setVisibility(View.INVISIBLE);
                });

        return v;
    }




    private class RvOrderListAdapter extends RecyclerView.Adapter<RvOrderListAdapter.ViewHold>{
        List<OrderEntity> orderEntityList;
        Context context;
        RvOrderListAdapter(List<OrderEntity> orderEntityList, Context ctx){
            this.orderEntityList = orderEntityList;
            context = ctx;
        }
        @Override
        public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.rv_admin_order_list, parent, false);
            return new ViewHold(v);
        }

        @Override
        public void onBindViewHolder(ViewHold holder, int position) {
            OrderEntity orderEntity = orderEntityList.get(position);

            holder.tvStatus.setText(orderEntity.getStatus());
            holder.tvCustomerName.setText(orderEntity.getUsername());
            String address = orderEntity.getAddressType()+"\n"+
                    orderEntity.getAddress()+"\n"+
                    orderEntity.getAddressLandmark()+"\n"+
                    orderEntity.getAddressCity()+"\n"+
                    orderEntity.getAddrressState()+"\n"+
                    orderEntity.getAddressPincode();
            holder.tvCustomerAddress.setText(address);
            holder.tvOrderId.setText(orderEntity.getOrderId());
            holder.tvOrderTime.setText(orderEntity.getOrderTime());
            String deliveryTime = orderEntity.getOrderDeliveryDate()+getDeliveryHour(orderEntity.getOrderDeliveryTime());
            holder.tvDeliveryTime.setText(deliveryTime);
            holder.cvRecyclerItem.setOnClickListener(view -> {
                Intent orderDetailsIntent = new Intent(getActivity(), OrderDetailsActivity.class);
                orderDetailsIntent.putExtra(Constants.ORDER_ID, orderEntity.getOrderId());
                orderDetailsIntent.putExtra(Constants.ORDER_STATUS, orderEntity.getStatus());
                orderDetailsIntent.putExtra(Constants.CUSTOMER_NAME, orderEntity.getUsername());
                orderDetailsIntent.putExtra(Constants.CUSTOMER_ADDRESS, address);
                startActivity(orderDetailsIntent);
            });
        }

        String getDeliveryHour(String time){
            switch (time){
                case "1":
                    return " 10:00 AM to 12:00 PM";
                case "2":
                    return " 12:00 PM to 02:00 PM";
                case "3":
                    return " 02:00 PM to 04:00 PM";
                case "4":
                    return " 04:00 PM to 06:00 PM";
                case "5":
                    return " 06:00 PM to 08:00 PM";
                case "10":
                    return " With in two hours";
                default:
                    return "Delivery hour";
            }
        }

        @Override
        public int getItemCount() {
            return orderEntityList.size();
        }

        public class ViewHold extends RecyclerView.ViewHolder{
            TextView tvStatus, tvCustomerName, tvCustomerAddress, tvOrderId, tvOrderTime, tvDeliveryTime;
            CardView cvRecyclerItem;
            FrameLayout flHeading;

            public ViewHold(View itemView) {
                super(itemView);

                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
                tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
                tvOrderId = itemView.findViewById(R.id.tvOrderId);
                tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
                tvDeliveryTime = itemView.findViewById(R.id.tvDeliveryTime);
                cvRecyclerItem = itemView.findViewById(R.id.cvRecyclerItem);
                flHeading = itemView.findViewById(R.id.flheading);
                int color = selectColorAsPerStatus(mStatus);
                cvRecyclerItem.setCardBackgroundColor(color);
                flHeading.setBackgroundResource(color);

            }

            private int selectColorAsPerStatus(String mStatus) {
                switch (mStatus){
                    case "Received":
                        return R.color.order_received;
                    case "Order Processed":
                        return R.color.order_processed;
                    case "Dispatched":
                        return R.color.order_dispatched;
                    case "Delivered":
                        return R.color.order_delivered;
                    case "Returned":
                        return R.color.order_returned;
                    default:
                        return 0;

                }
            }
        }
    }


}
