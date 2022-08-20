package com.ei.kalavarafoods.ui.admin;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.network.ApiInterface;
import com.ei.kalavarafoods.network.RetrofitClient;
import com.ei.kalavarafoods.model.api.OrderDetailItem;
import com.ei.kalavarafoods.model.api.OrderDetails;
import com.ei.kalavarafoods.viewmodel.OrderDetailsViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {

    RecyclerView rvOrderedItems;
    Button btnProceed;
    ProgressBar mProgressBar;
    String mStatus;
    String mOrderId;
    OrderDetailsViewModel mOrderDetailsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        mOrderDetailsViewModel = ViewModelProviders.of(this).get(OrderDetailsViewModel.class);
        Intent orderIntent = getIntent();


        TextView tvCustomerName = findViewById(R.id.tvCustomerName);
        TextView tvCustomerAddress = findViewById(R.id.tvCustomerAddress);
        TextView tvStatus = findViewById(R.id.tvStatus);
        btnProceed = findViewById(R.id.btnProceed);
        mProgressBar = findViewById(R.id.pbOrderDetails);
        rvOrderedItems = findViewById(R.id.rvInOrderDetail);
        rvOrderedItems.setLayoutManager(new LinearLayoutManager(this));

        tvCustomerName.setText(orderIntent.getStringExtra(Constants.CUSTOMER_NAME));
        tvCustomerAddress.setText(orderIntent.getStringExtra(Constants.CUSTOMER_ADDRESS));
        mStatus = orderIntent.getStringExtra(Constants.ORDER_STATUS);
        tvStatus.setText(mStatus);

        changeButtonText(mStatus);
        getOrderDetails(orderIntent);
    }

    private void changeButtonText(String status) {
        switch (status){
            case "Received":
                btnProceed.setText("To Process");
                break;
            case "Order Processed":
                btnProceed.setText("To Dispatch");
                break;
            case "Dispatched":
                btnProceed.setText("To Delivered");
                break;
            case "Delivered":
                btnProceed.setVisibility(View.INVISIBLE);
                break;
            case "Returned":
                btnProceed.setVisibility(View.INVISIBLE);
                break;
            default:
                btnProceed.setVisibility(View.INVISIBLE);
        }
    }

    private void getOrderDetails(Intent orderIntent) {
        String orderId = orderIntent.getStringExtra(Constants.ORDER_ID);
        mOrderId = orderId;
        JSONObject orderIdJson = new JSONObject();
        try {
            orderIdJson.put("order_id", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApiInterface apiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);
        mProgressBar.setVisibility(View.VISIBLE);
        apiInterface.getOrderDetails(orderIdJson.toString()).enqueue(new Callback<OrderDetailItem>() {
            @Override
            public void onResponse(Call<OrderDetailItem> call, Response<OrderDetailItem> response) {
                if (response.body() != null) {
                    List<OrderDetails> orderDetailsList = response.body().getOrderDetails();

                    rvOrderedItems.setAdapter(new RvItemDetails(orderDetailsList, OrderDetailsActivity.this));
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<OrderDetailItem> call, Throwable t) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void proceedBtnClicked(View view) {

        String statusToSend = "status";
        switch (mStatus) {
            case "Received":
                statusToSend = "Order Processed";
                break;
            case "Order Processed":
                statusToSend = "Dispatched";
                break;
            case "Dispatched":
                statusToSend = "Delivered";
                break;
        }

        final String finalStatusToSend = statusToSend;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Move to "+statusToSend+" ?");
        alertDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
            JSONObject sendStatusJson = new JSONObject();
            try {
                sendStatusJson.put("order_id", mOrderId);
                sendStatusJson.put("status", finalStatusToSend);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mOrderDetailsViewModel.updateOrderStatus(sendStatusJson);
            updateInServer(sendStatusJson.toString());

        }).setNegativeButton("No", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        }).show();
    }


    private void updateInServer(String s) {
        ApiInterface apiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);

        apiInterface.updateOrderStatus(s).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(OrderDetailsActivity.this, "Order Status Changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, "Failed!!>> "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    class RvItemDetails extends RecyclerView.Adapter<RvItemDetails.ViewHold>{
        private List<OrderDetails> orderDetailsList;
        private Context ctx;

        RvItemDetails(List<OrderDetails> orderDetailsList, Context ctx){
            this.orderDetailsList = orderDetailsList;
            this.ctx = ctx;
        }
        @Override
        public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ctx).inflate(R.layout.rv_order_details, parent, false);
            return new ViewHold(v);
        }

        @Override
        public void onBindViewHolder(ViewHold holder, int position) {
            OrderDetails orderDetails = orderDetailsList.get(position);

            Picasso.with(ctx)
                    .load(orderDetails.getProductImage())
                    .into(holder.ivOrderItem);
            holder.tvProductName.setText(orderDetails.getProductName());
            int size = Integer.parseInt(orderDetails.getProductSize());
            int intQuantity =  Integer.parseInt(orderDetails.getQuantity());
            String countQuantity = String.valueOf(size * intQuantity);
            String quantity = countQuantity+" "+ orderDetails.getProductUnit();
            holder.tvProductQuantity.setText(quantity);
            holder.tvPrice.setText(orderDetails.getPrice());
            holder.tvTotalPrice.setText(orderDetails.getTotal());
        }

        @Override
        public int getItemCount() {
            return orderDetailsList.size();
        }

        public class ViewHold extends RecyclerView.ViewHolder {
            ImageView ivOrderItem;
            TextView tvProductName, tvProductQuantity, tvPrice, tvTotalPrice;

            public ViewHold(View itemView) {
                super(itemView);

                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
                ivOrderItem = itemView.findViewById(R.id.ivOrderItem);
            }
        }
    }



}
