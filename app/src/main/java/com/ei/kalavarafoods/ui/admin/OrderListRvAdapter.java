package com.ei.kalavarafoods.ui.admin;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.api.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ULLAS BABU on 08-Feb-18.
 */

public class OrderListRvAdapter extends RecyclerView.Adapter<OrderListRvAdapter.ViewHold> {

    private Context context;
    private List<Order> orderList = new ArrayList<>();
    OrderListRvAdapter(Context context, List<Order> orderList){
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.rv_admin_order_list, parent, false);
        return new ViewHold(v);
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        Order order = orderList.get(position);
        holder.tvCustomerName.setText(order.getUsername());
        String address = order.getAddressType()+"\n"+
                order.getAddress()+"\n"+
                order.getAddressLandmark()+"\n"+
                order.getAddressCity()+"\n"+
                order.getAddrressState()+"\n"+
                order.getAddressPincode();
        holder.tvCustomerAddress.setText(address);
        holder.tvOrderId.setText(order.getOrderId());
        holder.tvOrderTime.setText(order.getOrderTime());
        String deliveryTime = order.getOrderDeliveryDate()+getDeliveryHour(order.getOrderDeliveryTime());
        holder.tvDeliveryTime.setText(deliveryTime);
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
        return orderList.size();
    }

    public class ViewHold extends RecyclerView.ViewHolder {

        TextView tvCustomerName, tvCustomerAddress, tvOrderId, tvOrderTime, tvDeliveryTime;

        public ViewHold(View itemView) {

            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            tvDeliveryTime = itemView.findViewById(R.id.tvDeliveryTime);

        }
    }
}
