package com.ei.kalavarafoods.ui.cart;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ei.kalavarafoods.utils.ConnectionDetector;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.DBHelperDisplayItems;
import com.ei.kalavarafoods.ItemExpandedActivity;
import com.ei.kalavarafoods.NoInternetActivity;
import com.ei.kalavarafoods.ui.number_verification.NumberVerificationActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.network.VolleySingleTon;
import com.ei.kalavarafoods.ui.auth.LoginActivity;
import com.ei.kalavarafoods.ui.search.SearchResultsActivity;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.utils.SharedPref;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements View.OnClickListener{
    DBHelperDisplayItems cartDb;

    Button btnCheckout;

    String Name;
    String ItemId;
    String Quantity;
    String Price;
    String OrderQty;

    RecyclerView rvCartItems;
    Cart_RecycleAdapter recycleAdapter;
    List<HashMap<String, String>> onlineData;

    private int hot_number = 0;
    private TextView ui_hot = null;
    private TextView tvItemCountBadge = null;
    private TextView tvCartEmpty;
    private CardView cardAmountCalc;
    private TextView tvTotalAmount;

    ConnectionDetector cd;

    SessionManager session;
    private CartViewModel mCartViewModel;
    private List<ProductEntity> productsInCartList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        if (toolbar != null) {
            toolbar.setTitle("Cart");
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cd = new ConnectionDetector(getApplicationContext());
        connectionCheck();
        mCartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);
        session = new SessionManager(getApplicationContext());

        cartDb = new DBHelperDisplayItems(this);

        rvCartItems = (RecyclerView) findViewById(R.id.rvCartItems);
        cardAmountCalc = (CardView) findViewById(R.id.cardAmountCalc);
        tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmount);
        tvItemCountBadge = (TextView) findViewById(R.id.tvItemCountBadge);
        tvCartEmpty = (TextView) findViewById(R.id.tvCartEmpty);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        rvCartItems.setLayoutManager(linearLayoutManager);
        rvCartItems.setHasFixedSize(true);

        mCartViewModel.getCartItemsAsync().observe(this, productEntities -> {
            updateHotCount(productEntities.size());
        });

        displayCart();

//         LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
//                 new IntentFilter("custom-message"));

        btnCheckout = (Button) findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageView btn_clear_cart_all = (ImageView) findViewById(R.id.btn_clear_cart_all);
        btn_clear_cart_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Clear cart");
                builder.setMessage("You have been chosed for clearing cart.\nAre you sure to continue?");
                builder.setPositiveButton("Clear cart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCartViewModel.clearCart();
                        displayCart();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.setCancelable(false);
                builder.show();
            }
        });

    }

    protected void connectionCheck() {

        if (!cd.isConnectingToInternet()) {

            Intent in = new Intent(CartActivity.this, NoInternetActivity.class);
            startActivity(in);
        }

    }


//......>> cart


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
//        MenuInflater menuInflater = getSupportMenuInflater();
//        menuInflater.inflate(R.menu.menu_actionbar, menu);
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        final View menu_hotlist = menu.findItem(R.id.menu_hotlist).getActionView();
        ui_hot = (TextView) menu_hotlist.findViewById(R.id.hotlist_hot);
//        updateHotCount(hot_number);
        new MyMenuItemStuffListener(menu_hotlist, "Show hot message") {
            @Override
            public void onClick(View v) {
                onSearchRequested();
                startActivity(new Intent(CartActivity.this, SearchResultsActivity.class));
            }
        };
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onClick(View v) {

    }

    static abstract class MyMenuItemStuffListener implements View.OnClickListener, View.OnLongClickListener {
        private String hint;
        private View view;

        MyMenuItemStuffListener(View view, String hint) {
            this.view = view;
            this.hint = hint;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        abstract public void onClick(View v);

        @Override
        public boolean onLongClick(View v) {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            view.getLocationOnScreen(screenPos);
            view.getWindowVisibleDisplayFrame(displayFrame);
            final Context context = view.getContext();
            final int width = view.getWidth();
            final int height = view.getHeight();
            final int midy = screenPos[1] + height / 2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            Toast cheatSheet = Toast.makeText(context, hint, Toast.LENGTH_SHORT);
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                        screenWidth - screenPos[0] - width / 2, height);
            } else {
                cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();
            return true;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //......??endcard


    public void displayCart() {
        productsInCartList = mCartViewModel.getCartItems();
        Log.i("productLisrSize>>", productsInCartList.size()+"");
        if (productsInCartList.size() > 0) {
            tvCartEmpty.setVisibility(View.GONE);
            cardAmountCalc.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.VISIBLE);
            recycleAdapter = new Cart_RecycleAdapter(CartActivity.this, productsInCartList);
            rvCartItems.setAdapter(recycleAdapter);
            Log.e("recycleAdapter ", " Updated");
            updateHotCount(productsInCartList.size());
        }else {
            cardAmountCalc.setVisibility(View.INVISIBLE);
            rvCartItems.setVisibility(View.GONE);
            tvCartEmpty.setVisibility(View.VISIBLE);
        }
    }

    public void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
//        ui_hot.setVisibility(View.VISIBLE);
        tvItemCountBadge.setVisibility(View.VISIBLE);
        tvItemCountBadge.setText(Integer.toString(hot_number));

        List<ProductEntity> productEntities = mCartViewModel.getCartItems();
        tvTotalAmount.setText("" + CartOperations.subTotalRoom(productEntities));
        TextView payableAmount = (TextView) findViewById(R.id.payable_amt);
        TextView shippingAmount = (TextView) findViewById(R.id.shipping_amt);
        if (Float.valueOf(tvTotalAmount.getText().toString()) < 300)
            shippingAmount.setText("30.00");
        else shippingAmount.setText("0.00");
        float payableAmt = Float.valueOf(tvTotalAmount.getText().toString()) + Float.valueOf(shippingAmount.getText().toString());
        payableAmount.setText(String.valueOf(payableAmt));

    }

    public boolean dbCartHas(String id) {
        boolean doesHave = false;
        Cursor res = cartDb.getItems();
        if (res.getCount() == 0) {
            doesHave = false;
        }
        while (res.moveToNext()) {
            if (res.getString(1).equals(id)) {
                if (Integer.valueOf(res.getString(7)) > 0) {
                    doesHave = true;
                }
            }
        }
        return doesHave;
    }

    public void showMessage(String title, String Message) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartDb.close();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("Activity", " OnRestart");
        displayCart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Activity", " OnStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionCheck();
        displayCart();
        Log.e("Activity", " OnResume");
    }


/////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////// RecyclerView ///////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

    public class Cart_RecycleAdapter extends RecyclerView.Adapter<Cart_RecycleAdapter.ViewHolderRec> {

        DBHelperDisplayItems cartAdaptDb;
        String id;

//        List<HashMap<String, String>> onlineData;
        Context context;
        List<ProductEntity> productsInCart;

        Cart_RecycleAdapter(Context context, List<ProductEntity> productsInCart){//List<HashMap<String, String>> onlineData) {
//            this.onlineData = onlineData;
            this.context = context;
            this.productsInCart = productsInCart;
        }

        @Override
        public ViewHolderRec onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolderRec(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolderRec holder, int position) {
            ProductEntity productEntity = productsInCart.get(position);

            cartAdaptDb = new DBHelperDisplayItems(context);

            //Download image using picasso library
            Picasso.with(context).load(productEntity.getProductImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.iv);
            holder.item_id.setText(productEntity.getProductId());//map.get("id"));
            holder.count.setText(mCartViewModel.getProductOrderQuantity(productEntity.getProductId()));
            holder.title.setText(productEntity.getProductName());//map.get("title"));
            holder.size.setText(productEntity.getProductSize());//map.get("size"));
            holder.price.setText(productEntity.getProductUnitprice());//map.get("price"));
            holder.progressBar.setVisibility(View.GONE);
            if (new SessionManager(getApplicationContext()).getUserDetails().get(SharedPref.Keys.KEY_UID).equals("0"))
                holder.product_wish.setVisibility(View.GONE);
            else {
                if (productEntity.getProductWish().equals("1"))
                    holder.product_wish.setImageResource(R.drawable.wishlist_icon_selected);
                else holder.product_wish.setImageResource(R.drawable.wishlist_icon_unselected);
            }
        }

        @Override
        public int getItemCount() {
            return productsInCart.size();
        }

        public class ViewHolderRec extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView iv;
            TextView title, size, price, count, item_id;
            ImageView Add_Cart;
            ImageView Remove_Cart;
            ImageButton product_wish;
            ProgressBar progressBar;


            public ViewHolderRec(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.thumbnail);
                title = (TextView) itemView.findViewById(R.id.title);
                item_id = (TextView) itemView.findViewById(R.id.item_id);
                size = (TextView) itemView.findViewById(R.id.quantity);
                price = (TextView) itemView.findViewById(R.id.price);

                Add_Cart = (ImageView) itemView.findViewById(R.id.cart_add);
                Remove_Cart = (ImageView) itemView.findViewById(R.id.cart_remove);
                count = (TextView) itemView.findViewById(R.id.itemcount);
                product_wish = (ImageButton) itemView.findViewById(R.id.product_wish);
                progressBar = (ProgressBar) itemView.findViewById(R.id.product_wish_progressbar);

                itemView.findViewById(R.id.expand).setOnClickListener(this);
                title.setOnClickListener(this);
                Add_Cart.setOnClickListener(this);
                Remove_Cart.setOnClickListener(this);
                product_wish.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v.getId() == Add_Cart.getId()) {
                    int currentNos = Integer.parseInt(count.getText().toString());
                    count.setText(String.valueOf(++currentNos));
                    update(count.getText().toString(), item_id.getText().toString());

                } else if (v.getId() == Remove_Cart.getId()) {
                    int currentNos = Integer.parseInt(count.getText().toString());
                    if (currentNos > 0) {
                        count.setText(String.valueOf(--currentNos));
                    }
                    update(count.getText().toString(), item_id.getText().toString());
                } else if (v.getId() == R.id.expand) {
                    Log.e("here", String.valueOf(getAdapterPosition()));
                    itemExpanded(getAdapterPosition());
                } else if (v.getId() == title.getId()) {
                    Log.e("here on title", String.valueOf(getAdapterPosition()));
                    itemExpanded(getAdapterPosition());
                } else if (v.getId() == R.id.product_wish) {
                    progressBar.setVisibility(View.VISIBLE);
                    product_wish.setVisibility(View.GONE);
                    Log.e("position", String.valueOf(getAdapterPosition()));
                    addtoWish(getAdapterPosition(), progressBar, product_wish);
                }
            }

            private void itemExpanded(int adapterPosition) {
                HashMap<String, String> map = onlineData.get(adapterPosition);
                Log.e("title", map.get("title"));
                Intent i = new Intent(getApplicationContext(), ItemExpandedActivity.class);
                ItemExpandedActivity.setData(map);
                startActivity(i);
                finish();
            }

            public void update(String quantity, String item) {
//                boolean isUpdate = cartAdaptDb.updateItems(quantity, item);
//                if (isUpdate)
//                    Log.i("Updated From Adapter", "DB");
//
//                else
//                    Log.i("Not Updated", "DB");
//
//                cartAdaptDb.close();
                mCartViewModel.updateCartItem(quantity, item);
//                updateHotCount(getItemCount());
                displayCart();

            }
        }

        private void addtoWish(int adapterPosition, ProgressBar progressBar, ImageButton product_wish) {
            wishNetwork(adapterPosition, progressBar, product_wish);
        }

        private void wishNetwork(final int adapterPosition, final ProgressBar progressBar, final ImageButton product_wish) {
            final DBHelperDisplayItems dbHelperDisplayItems = new DBHelperDisplayItems(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_PRODUCT_WISH,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("addedResult", response);
                            progressBar.setVisibility(View.GONE);
                            product_wish.setVisibility(View.VISIBLE);
                            try {
                                JSONObject result = new JSONObject(response);
                                if (result.getString("alert").equals("success")) {
                                    HashMap<String, String> single_item = new HashMap<>();
                                    single_item.put("image", onlineData.get(adapterPosition).get("image"));
                                    single_item.put("title", onlineData.get(adapterPosition).get("title"));
                                    single_item.put("id", onlineData.get(adapterPosition).get("id"));
                                    single_item.put("size", onlineData.get(adapterPosition).get("size"));
                                    single_item.put("price", onlineData.get(adapterPosition).get("price"));
                                    single_item.put("wishlist_status", "1");
                                    Log.e("result", String.valueOf(dbHelperDisplayItems.updateWishProduct("1", onlineData.get(adapterPosition).get("id"))));
                                    onlineData.remove(adapterPosition);
                                    onlineData.add(adapterPosition, single_item);
                                    notifyItemChanged(adapterPosition);
                                } else {
                                    HashMap<String, String> single_item = new HashMap<>();
                                    single_item.put("image", onlineData.get(adapterPosition).get("image"));
                                    single_item.put("title", onlineData.get(adapterPosition).get("title"));
                                    single_item.put("id", onlineData.get(adapterPosition).get("id"));
                                    single_item.put("size", onlineData.get(adapterPosition).get("size"));
                                    single_item.put("price", onlineData.get(adapterPosition).get("price"));
                                    single_item.put("wishlist_status", "0");
                                    Log.e("result", String.valueOf(dbHelperDisplayItems.updateWishProduct("0", onlineData.get(adapterPosition).get("id"))));
                                    onlineData.remove(adapterPosition);
                                    onlineData.add(adapterPosition, single_item);
                                    notifyItemChanged(adapterPosition);
                                }
                            } catch (JSONException e) {
                                Log.e("error", e.toString());
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    product_wish.setVisibility(View.VISIBLE);
                    Log.e("error_addWhishList", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Constants.HEADER, Constants.API_KEY);
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("user_id", new SessionManager(getApplicationContext())
                                .getUserDetails().get(SharedPref.Keys.KEY_UID));
                        jsonObject.put("product_id", onlineData.get(adapterPosition).get("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    params.put("str", jsonObject.toString());
                    Log.e("params", params.toString());
                    return params;
                }

                @Override
                public Priority getPriority() {
                    return Priority.IMMEDIATE;
                }
            };
            VolleySingleTon.getsInstance().getmRequestQueue().add(stringRequest);
        }
    }

    public static Intent activityIntent(Context context){
        return new Intent(context, CartActivity.class);
    }
}
