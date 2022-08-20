package com.ei.kalavarafoods.ui.profile;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ReceipeActivity;
import com.ei.kalavarafoods.model.api.UserOrder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserOrdersActivity extends AppCompatActivity {

    @BindView(R.id.rvUserOrder)
    RecyclerView rvUserOrder;
    @BindView(R.id.progressOrder)
    ProgressBar progressOrder;

    private ProfileViewModel mProfileViewModel;
    private RvAdapterUserOrders mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);
        ButterKnife.bind(this);
        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        rvUserOrder.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProfileViewModel.getUserOrders();
        mProfileViewModel.liveDataUserOrders.observe(this, userOrders -> {
            mAdapter = new RvAdapterUserOrders(this, userOrders);
            rvUserOrder.setAdapter(mAdapter);
        });

        mProfileViewModel.liveDateIsLoading.observe(this, isLoading ->{
            if (isLoading){
                progressOrder.setVisibility(View.VISIBLE);
            }else {
                progressOrder.setVisibility(View.INVISIBLE);
            }
        });
    }




    public class RvAdapterUserOrders extends RecyclerView.Adapter<RvAdapterUserOrders.ViewHolder>{
        Context context;
        List<UserOrder> userOrders;

        public RvAdapterUserOrders(Context context, List<UserOrder> userOrders){
            this.context = context;
            this.userOrders = userOrders;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.rv_user_order, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            UserOrder userOrder = userOrders.get(position);
            holder.bindDataToView(userOrder);
        }

        @Override
        public int getItemCount() {
            return userOrders.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final String TAG = ViewHolder.class.getSimpleName();
            @BindView(R.id.tvOrderId)
            TextView tvOrderId;
            @BindView(R.id.tvOrderTime)
            TextView tvOrderTime;
            @BindView(R.id.history_cardview_item)
            CardView history_item;

            private UserOrder mUserOrder;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                history_item.setOnClickListener(v -> toReceipt());
            }

            void bindDataToView(UserOrder userOrder) {
                mUserOrder = userOrder;
//                Log.i(TAG, mUserOrder.toString());
                tvOrderId.setText(userOrder.getOrderId());
                tvOrderTime.setText(userOrder.getOrderTime());
            }


            private void toReceipt(){
                Bundle bundle = new Bundle();
                Log.i(TAG, mUserOrder.toString());
                bundle.putString("order_id", mUserOrder.getOrderId());// map.get("order_id"));
                bundle.putString("order_time", mUserOrder.getOrderTime());// map.get("order_time"));
                bundle.putString("order_delivery_date", mUserOrder.getOrderDeliveryDate());//map.get("order_delivery_date"));
                bundle.putString("order_amount", mUserOrder.getOrderAmount());// map.get("order_amount"));

                Intent toReceiptIntent = new Intent(context, ReceipeActivity.class);
                toReceiptIntent.putExtras(bundle);
                startActivity(toReceiptIntent);
            }
        }
    }
    public static Intent start(Context context){
        return new Intent(context, UserOrdersActivity.class);
    }
}
