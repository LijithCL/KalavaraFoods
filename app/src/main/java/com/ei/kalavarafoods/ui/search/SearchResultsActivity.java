package com.ei.kalavarafoods.ui.search;


import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ei.kalavarafoods.model.api.Product;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.ui.cart.CartOperations;
import com.ei.kalavarafoods.ui.cart.CartActivity;
import com.ei.kalavarafoods.utils.ConnectionDetector;
import com.ei.kalavarafoods.ui.cart.CheckoutActivity;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.DBHelperDisplayItems;
import com.ei.kalavarafoods.ItemExpandedActivity;
import com.ei.kalavarafoods.ui.auth.LoginActivity;
import com.ei.kalavarafoods.NoInternetActivity;
import com.ei.kalavarafoods.ui.number_verification.NumberVerificationActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.model.api.Brand;
import com.ei.kalavarafoods.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    Button Checkout;
    private int hot_number = 0;
    private TextView ui_hot_bottom;

    CardView CheckButtonCart;
    TextView totalAmount;

    ConnectionDetector cd;

    EditText _searchedit;
    ImageView _clearbtn;
    TextView _empty;
    LinearLayout mLlBrandNames;
    Toolbar toolbar;
    ProgressBar mProgressBar;

    RecyclerView recyclerView, mRvBrandNames;
    RecycleAdapterSearch recycleAdapter;
    List<HashMap<String, String>> onlineData;
    ProgressDialog pd;

    RelativeLayout cartImage;


    String _result;

    DBHelperDisplayItems cartDb;
    private SearchViewModel mSearchViewModel;
    private List<ProductEntity> cartProductEntityList = new ArrayList<>();
    private List<Product> mProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        cd = new ConnectionDetector(getApplicationContext());
        connectionCheck();


        cartDb = new DBHelperDisplayItems(this);

        _searchedit = (EditText) findViewById(R.id.searchEditText);
        _clearbtn = (ImageView) findViewById(R.id.clear_button);
        _empty = (TextView) findViewById(R.id.empty_view);


        CheckButtonCart = (CardView) findViewById(R.id.checkoutbuttoncard_search);
        cartImage = (RelativeLayout) findViewById(R.id.cart_image_view);
        totalAmount = (TextView) findViewById(R.id.total_amt);
        ui_hot_bottom = (TextView) findViewById(R.id.hotlist_hot_bottom_new);
        Checkout = (Button) findViewById(R.id.checkoutbutton);
//        ui_hot = (TextView)findViewById(R.id.hotlist_hot);
//        ui_hot_bottom.setText("23");
//        updateHotCount(cartDb.getCartItems().getCount());


        recyclerView = (RecyclerView) findViewById(R.id.recyle_view_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        mRvBrandNames = findViewById(R.id.rvBrandNames);
        mRvBrandNames.setLayoutManager(new LinearLayoutManager(this));
        mLlBrandNames = findViewById(R.id.llBrandNames);
        mProgressBar = findViewById(R.id.pbSearch);

        onlineData = new ArrayList<>();

        Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent in = new Intent(SearchResultsActivity.this, CheckoutActivity.class);
//                startActivity(in);
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

        cartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchResultsActivity.this, CartActivity.class));
            }
        });


        _searchedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                final String url = Constants.SEARCH;
                if (!TextUtils.isEmpty(_searchedit.getText().toString())){
                    mLlBrandNames.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
//                    new AsyncHttpTasksearch().execute(url);
                    mSearchViewModel.searchProducts(_searchedit.getText().toString());
                } else {
                    mLlBrandNames.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mProductList.clear();

                }
            }
        });


        _searchedit.setFocusableInTouchMode(true);
        _searchedit.requestFocus();

        _searchedit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
//                    Toast.makeText(SearchResultsActivity.this, _searchedit.getText(), Toast.LENGTH_SHORT).show();
                    String searchstr = _searchedit.getText().toString();
                    if (TextUtils.isEmpty(searchstr)) {
                        _searchedit.setError("Cannot be Empty");
                    } else {
                        final String url = "http://www.thenexterp.com/MetroMart/shopmob/search";
                       // new AsyncHttpTasksearch().execute(url);
                    }
                    return true;
                }
                return false;
            }
        });

        _clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_searchedit.getText().toString().equals("")) {
                    _searchedit.setText("");
                }else {
                    finish();
                }
            }
        });

//        getBrands();
        mSearchViewModel.getBrands();

        mSearchViewModel.liveDataProductList.observe(this, products -> {
            if (products != null) {
//                Log.i("productName>>>", products.get(0).getProductName());
//                Log.i("productWish>>>", products.get(0).getProductWish());
                mProductList.addAll(products);
                recyclerView.setAdapter(new RecycleAdapterSearch(SearchResultsActivity.this, mProductList));
            }
        });

        mSearchViewModel.getCartItems().observe(this, cartProductEntities -> {
            if (cartProductEntities.size() == 0){
                CheckButtonCart.setVisibility(View.GONE);
            }else {
                CheckButtonCart.setVisibility(View.VISIBLE);
                cartProductEntityList = cartProductEntities;
                updateHotCount(cartProductEntityList);
            }
        });

        mSearchViewModel.liveDataIsLoading.observe(this, isLoading ->{
            if (isLoading){
                mProgressBar.setVisibility(View.VISIBLE);
            }else {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        mSearchViewModel.liveDataBrands.observe(this, brands -> {
            mRvBrandNames.setAdapter(new RvBrandsAdapter(SearchResultsActivity.this, brands));
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateHotCount(List<ProductEntity> cartProductEntityList) {
        hot_number = cartProductEntityList.size();
        if (hot_number == 0) {
            CheckButtonCart.setVisibility(View.GONE);
            ui_hot_bottom.setVisibility(View.INVISIBLE);
        } else {
            ui_hot_bottom.setVisibility(View.VISIBLE);
            ui_hot_bottom.setText(Integer.toString(hot_number));
            YoYo.with(Techniques.Tada).duration(1000).playOn(ui_hot_bottom);
//            Cursor cursor = cartDb.getCartItems();
            totalAmount.setText("" + CartOperations.subTotalRoom(cartProductEntityList));
            YoYo.with(Techniques.Tada).duration(1000).playOn(totalAmount);
        }
    }

    protected void connectionCheck() {

        if (!cd.isConnectingToInternet()) {

            Intent in = new Intent(SearchResultsActivity.this, NoInternetActivity.class);
            startActivity(in);
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        recyclerView.setAdapter(recycleAdapter);
    }


    //////////////////////////////////////////////////////////////////////////////////////////

    public class RecycleAdapterSearch extends RecyclerView.Adapter<RecycleAdapterSearch.ViewHolderRec> {


        List<HashMap<String, String>> onlineData;
        List<Product> productList;
        Context context;

        RecycleAdapterSearch(Context context, List<Product> productList) {
            this.productList = productList;
            this.context = context;
        }

        @Override
        public ViewHolderRec onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolderRec(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_cart_item_recycle, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolderRec holder, int position) {
//            HashMap<String, String> map = onlineData.get(position);
            Product product = productList.get(position);
//            Log.e("online", map.toString());
            //Download image using picasso library
            Picasso.with(context).load(product.getProductImage())//map.get("image"))
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.iv);
            holder.title.setText(product.getProductName());//map.get("title"));
            holder.item_id.setText(product.getProductId());//map.get("id"));
            holder.quantity.setText(product.getProductOrderQuantitiy());//map.get("quantity"));
            holder.unit.setText(product.getProductUnit());//map.get("unit"));
            holder.price.setText(product.getProductUnitprice());//map.get("price_new"));

            String QtyInCart = mSearchViewModel.getProductOrderQuantity(product.getProductId());
            holder.count.setText(QtyInCart);
            holder.progressBar.setVisibility(View.GONE);
            if (!product.getProductOfferprice().equals("noOffer")) {
                holder.offer_price_layout.setVisibility(View.VISIBLE);
                holder.price_offer.setText(product.getProductOfferprice());
                holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.price.setTextColor(ContextCompat.getColor(SearchResultsActivity.this, R.color.med_gray));
            } else {
                holder.price.setPaintFlags(holder.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.price.setTextColor(ContextCompat.getColor(SearchResultsActivity.this, R.color.nav_item_text));
                holder.offer_price_layout.setVisibility(View.GONE);
            }
            if (new SessionManager(getApplicationContext()).getUserDetails().get(SharedPref.Keys.KEY_UID).equals("0"))
                holder.product_wish.setVisibility(View.GONE);
            else {
                if (product.getProductWish().equals("1")) {
                    holder.product_wish.setImageResource(R.drawable.wishlist_icon_selected);
                    holder.selected = true;
                }else {
                    holder.product_wish.setImageResource(R.drawable.wishlist_icon_unselected);
                    holder.selected = false;
                }
            }
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        public class ViewHolderRec extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView iv;
            TextView title, quantity, price, count, item_id, price_offer, unit;
            CardView cardItemLayout;
            ImageView Add_Cart;
            ImageView Remove_Cart;
            ImageButton product_wish;
            ProgressBar progressBar;
            LinearLayout offer_price_layout;
            boolean selected;

            public ViewHolderRec(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.thumbnail);
                title = (TextView) itemView.findViewById(R.id.title);
                quantity = (TextView) itemView.findViewById(R.id.quantity);
                item_id = (TextView) itemView.findViewById(R.id.item_id);
                price = (TextView) itemView.findViewById(R.id.price);
                unit = itemView.findViewById(R.id.unit);
                cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);

                Add_Cart = (ImageView) itemView.findViewById(R.id.cart_add_);
                Remove_Cart = (ImageView) itemView.findViewById(R.id.cart_remove_);
                count = (TextView) itemView.findViewById(R.id.itemcount_);
                product_wish = (ImageButton) itemView.findViewById(R.id.product_wish);
                progressBar = (ProgressBar) itemView.findViewById(R.id.product_wish_progressbar);
                offer_price_layout = (LinearLayout) itemView.findViewById(R.id.offer_price_layout);
                price_offer = (TextView) offer_price_layout.findViewById(R.id.price_offer);


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
                    currentNos++;
                    count.setText(String.valueOf(currentNos));
                    update(String.valueOf(currentNos), item_id.getText().toString());
                    Log.e("Value ", String.valueOf(currentNos));

                } else if (v.getId() == Remove_Cart.getId()) {
                    int currentNos = Integer.parseInt(count.getText().toString());
                    if (currentNos > 0) {
                        currentNos--;
                        count.setText(String.valueOf(currentNos));
                        update(String.valueOf(currentNos), item_id.getText().toString());
                        Log.e("Value ", String.valueOf(currentNos));
                    }
                } else if (v.getId() == R.id.expand) {
                    Log.e("here", String.valueOf(getAdapterPosition()));
//                    itemExpanded(getAdapterPosition());
                } else if (v.getId() == title.getId()) {
                    Log.e("here on title", String.valueOf(getAdapterPosition()));
//                    itemExpanded(getAdapterPosition());
                } else if (v.getId() == R.id.product_wish) {
                    progressBar.setVisibility(View.VISIBLE);
                    product_wish.setVisibility(View.GONE);
                    Log.e("position", String.valueOf(getAdapterPosition()));
//                    addtoWish(getAdapterPosition(), progressBar, product_wish);
                    if (selected){
                        removeFromWish(productList.get(getAdapterPosition()).getProductId());
                        product_wish.setImageResource(R.drawable.wishlist_icon_unselected);
                        selected = false;
                    }else {
                        addToWish(productList.get(getAdapterPosition()).getProductId());
                        product_wish.setImageResource(R.drawable.wishlist_icon_selected);
                        selected = true;
                    }
                }
            }

            private void itemExpanded(int adapterPosition) {
                HashMap<String, String> map = onlineData.get(adapterPosition);
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("title", map.get("title"));
                item.put("image", map.get("image"));
                item.put("id", map.get("id"));
                item.put("quantity", map.get("unit"));
                item.put("price", map.get("price_new"));
                Log.e("title", map.get("title"));
                Intent i = new Intent(getApplicationContext(), ItemExpandedActivity.class);
                ItemExpandedActivity.setData(item);
                startActivity(i);
            }


        }

        private void addToWish(String productId) {
            mSearchViewModel.addToWish(productId);
        }

        private void removeFromWish(String productId) {
            mSearchViewModel.removeWish(productId);
        }
        public void update(String quantity, String item) {
            mSearchViewModel.updateCartItem(quantity, item);
        }

    }

    private class RvBrandsAdapter extends RecyclerView.Adapter<RvBrandsAdapter.ViewHolder> {

        private Context context;
        private List<Brand> brandList;

        RvBrandsAdapter(Context context, List<Brand> brandList) {
            this.context = context;
            this.brandList = brandList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.rv_brand_names, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Brand brand = brandList.get(position);

            if (brand.getBrandLogo().equals("")){
                Picasso.with(context)
                        .load(R.drawable.placeholderhor)
                        .into(holder.ivBrandImage);
            }else {
                Picasso.with(context)
                        .load(brand.getBrandLogo())
                        .into(holder.ivBrandImage);
            }
            holder.tvBrandName.setText(brand.getBrandName());
            holder.llBrandItem.setOnClickListener(view -> {
                Intent brandItemIntent = new Intent(context, BrandItemsActivity.class);
                brandItemIntent.putExtra(Constants.BRAND_ID, brand.getBrandId());
                brandItemIntent.putExtra(Constants.BRAND_NAME, brand.getBrandName());
                startActivity(brandItemIntent);
            });
        }

        @Override
        public int getItemCount() {
            return brandList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvBrandName;
            ImageView ivBrandImage;
            LinearLayout llBrandItem;
            public ViewHolder(View itemView) {
                super(itemView);

                llBrandItem = itemView.findViewById(R.id.llBrandItem);
                tvBrandName = itemView.findViewById(R.id.tvBrandName);
                ivBrandImage = itemView.findViewById(R.id.ivBrandImage);
            }
        }
    }
}