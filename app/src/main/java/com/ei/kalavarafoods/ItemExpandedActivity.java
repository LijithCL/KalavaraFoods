package com.ei.kalavarafoods;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ei.kalavarafoods.ui.cart.CartActivity;
import com.ei.kalavarafoods.ui.cart.CartOperations;
import com.ei.kalavarafoods.ui.cart.CheckoutActivity;
import com.ei.kalavarafoods.ui.auth.LoginActivity;
import com.ei.kalavarafoods.ui.number_verification.NumberVerificationActivity;
import com.ei.kalavarafoods.utils.AnimationUtils;
import com.ei.kalavarafoods.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ItemExpandedActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private static HashMap<String, String> map = new HashMap<>();
    private ImageView imageView;
    private RecyclerView recyclerView;
    private RCAdapter adapter;
    private DBHelperDisplayItems DB_Items;
    private LinearLayout layout_bottom;
    private int hot_number;
    private TextView ui_hot_bottom = null, SubTotalAmt = null, CartIcon2 = null;
    private Button checkoutbutton;
    private ImageView cartIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_expanded);
        findingResources();
        settingResources();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void settingResources() {
        DB_Items = new DBHelperDisplayItems(getApplicationContext());
        Picasso.with(this).load(map.get("image")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(imageView);
//        toolbar.setTitle(map.get("title"));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        adapter = new RCAdapter(this, map);
        recyclerView.setAdapter(adapter);

        UpdateCartNumber();
        checkoutbutton.setOnClickListener(this);
        cartIcon.setOnClickListener(this);
        CartIcon2.setOnClickListener(this);
    }

    private void UpdateCartNumber() {
        Integer Qty = 0;
        Cursor res = DB_Items.getCartItems();
        if (res.getCount() == 0) {
            layout_bottom.setVisibility(View.GONE);
        } else {
            layout_bottom.setVisibility(View.VISIBLE);
            while (res.moveToNext()) {
                if (Integer.valueOf(res.getString(7)) > 0) {
                    Qty++;
                }
            }
        }
        updateHotCount(Qty);
        Log.e("Cart ", " Updated");
    }

    private void updateHotCount(Integer qty) {
        hot_number = qty;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (hot_number == 0) {
                    ui_hot_bottom.setVisibility(View.GONE);
                } else {
                    ui_hot_bottom.setVisibility(View.VISIBLE);
                    ui_hot_bottom.setText(Integer.toString(hot_number));
                    Cursor cursor = DB_Items.getCartItems();
                    SubTotalAmt.setText("" + CartOperations.subTotal(cursor));
                }
            }
        });
    }

    private void findingResources() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(R.id.img_collapsing);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        layout_bottom = (LinearLayout) findViewById(R.id.layout_bottom);
        ui_hot_bottom = (TextView) findViewById(R.id.hotlist_hot_bottom);
        SubTotalAmt = (TextView) findViewById(R.id.subTotalAmt);
        checkoutbutton = (Button) findViewById(R.id.checkoutbutton);
        cartIcon = (ImageView) findViewById(R.id.hotlist_bell);
        CartIcon2 = (TextView) findViewById(R.id.subTotalAmt);
    }

    public static void setData(HashMap<String, String> map) {
        ItemExpandedActivity.map = map;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkoutbutton:
                if (new SessionManager(getApplicationContext()).isLoggedIn()) {
                    if (new SessionManager(getApplicationContext()).isNumberVerified()) {
                        Intent in = new Intent(getApplicationContext(), CheckoutActivity.class);
                        startActivity(in);
                    } else {
                        startActivity(new Intent(getApplicationContext(), NumberVerificationActivity.class));
                    }
                } else {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.putExtra("caller", "CheckOutLogin");
                    startActivity(i);
                }
                break;
            case R.id.hotlist_bell:
                Intent in = new Intent(this, CartActivity.class);
                startActivity(in);
                finish();
                break;
            case R.id.subTotalAmt:
                Intent i = new Intent(this, CartActivity.class);
                startActivity(i);
                finish();
                break;
            default:
                break;
        }
    }

    private class RCAdapter extends RecyclerView.Adapter<RCViewHolder> {
        private Context context;
        private LayoutInflater layoutInflater;
        private HashMap<String, String> map = new HashMap<>();
        private int previousPosition = 0;

        public RCAdapter(Context context, HashMap<String, String> map) {
            this.context = context;
            this.map = map;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.layout_product_details, parent, false);
            RCViewHolder viewHolder = new RCViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            holder.product_name.setText(map.get("title"));
            holder.product_quantity.setText(map.get("size"));
            holder.product_price.setText(map.get("price"));
            holder.product_item_id.setText(map.get("id"));
            String QtyInCart = orderQty(map.get("id"));
            holder.count.setText(QtyInCart);

            if (position > previousPosition) {
                AnimationUtils.animateHor(holder, true);
            } else {
                AnimationUtils.animateHor(holder, false);
            }
            previousPosition = position;
        }

        private String orderQty(String id) {
            String Qty = "0";
            Cursor res = DB_Items.getItems();
            if (res.getCount() == 0) {
                Qty = "0";
            }
            while (res.moveToNext()) {
                if (res.getString(1).equals(id)) {
                    Qty = res.getString(7);
                }
            }
            return Qty;
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    public class RCViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView product_name, product_quantity, product_price, product_item_id, count;
        private ImageView Add_Cart, Remove_Cart;

        public RCViewHolder(View itemView) {
            super(itemView);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            product_quantity = (TextView) itemView.findViewById(R.id.quantity);
            product_price = (TextView) itemView.findViewById(R.id.price);
            product_item_id = (TextView) itemView.findViewById(R.id.item_id);
            Add_Cart = (ImageView) itemView.findViewById(R.id.cart_add);
            Remove_Cart = (ImageView) itemView.findViewById(R.id.cart_remove);
            count = (TextView) itemView.findViewById(R.id.itemcount);

            Add_Cart.setOnClickListener(this);
            Remove_Cart.setOnClickListener(this);
            UpdateCartNumber();
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == Add_Cart.getId()) {
                int currentNos = Integer.parseInt(count.getText().toString());
                currentNos++;
                count.setText(String.valueOf(currentNos));
                update(String.valueOf(currentNos), product_item_id.getText().toString());
                Log.e("Value ", String.valueOf(currentNos));

            } else if (view.getId() == Remove_Cart.getId()) {
                int currentNos = Integer.parseInt(count.getText().toString());
                if (currentNos > 0) {
                    currentNos--;
                    count.setText(String.valueOf(currentNos));
                    update(String.valueOf(currentNos), product_item_id.getText().toString());
                    Log.e("Value ", String.valueOf(currentNos));
                }
            }
        }

        private void update(String Qty, String Item) {
            boolean isUpdate = DB_Items.updateItems(Qty, Item);
            if (isUpdate)
                Log.i("Updated", "DB");

            else
                Log.i("Not Updated", "DB");

            UpdateCartNumber();
        }
    }
}
