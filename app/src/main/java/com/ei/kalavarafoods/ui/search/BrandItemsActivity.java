package com.ei.kalavarafoods.ui.search;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ei.kalavarafoods.ui.cart.CartOperations;
import com.ei.kalavarafoods.ui.cart.CartActivity;
import com.ei.kalavarafoods.ui.cart.CheckoutActivity;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.DBHelperDisplayItems;
import com.ei.kalavarafoods.ItemExpandedActivity;
import com.ei.kalavarafoods.ui.auth.LoginActivity;
import com.ei.kalavarafoods.ui.number_verification.NumberVerificationActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.model.api.Product;
import com.ei.kalavarafoods.utils.SharedPref;
import com.ei.kalavarafoods.viewmodel.BrandItemsViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BrandItemsActivity extends AppCompatActivity {

    private static final String TAG = BrandItemsActivity.class.getSimpleName() ;
    private RecyclerView mRvBrandSearch;
    private ProgressBar mPbBrandSearch;

    private CardView CheckButtonCart;
    private RelativeLayout cartImage;
    private TextView totalAmount, ui_hot_bottom;
    private Button Checkout;

    private int hot_number = 0;

    private DBHelperDisplayItems cartDb;
    private List<Product> mProductList;

    private BrandItemsViewModel mBrandItemsViewModel;
    private List<ProductEntity> cartProductEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_items);

        mBrandItemsViewModel = ViewModelProviders.of(this).get(BrandItemsViewModel.class);
        String brandId = getIntent().getStringExtra(Constants.BRAND_ID);
        String title = getIntent().getStringExtra(Constants.BRAND_NAME);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);


        cartDb = new DBHelperDisplayItems(this);
        mRvBrandSearch = findViewById(R.id.rvBrandSearch);
        mRvBrandSearch.setLayoutManager(new LinearLayoutManager(this));
        mPbBrandSearch = findViewById(R.id.pbBrandSearch);

        CheckButtonCart = (CardView) findViewById(R.id.checkoutbuttoncard_search);
        cartImage = (RelativeLayout) findViewById(R.id.cart_image_view);
        totalAmount = (TextView) findViewById(R.id.total_amt);
        ui_hot_bottom = (TextView) findViewById(R.id.hotlist_hot_bottom_new);
        Checkout = (Button) findViewById(R.id.checkoutbutton);



//        getBrandItems(brandId);
        mBrandItemsViewModel.getBrandProducts(brandId);
        mBrandItemsViewModel.liveDataIsLoding.observe(this, isLoading -> {
            if (isLoading){
                mPbBrandSearch.setVisibility(View.VISIBLE);
            } else {
                mPbBrandSearch.setVisibility(View.INVISIBLE);
            }
        });

        mBrandItemsViewModel.liveDataProductList.observe(this, products -> {
            mProductList = products;
            mRvBrandSearch.setAdapter(new RvSearchBrandAdapter(this, mProductList));
            addToDatabase(mProductList);

        });

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
                startActivity(new Intent(BrandItemsActivity.this, CartActivity.class));
            }
        });


        mBrandItemsViewModel.getCartItems().observe(this, productEntityList -> {
            cartProductEntities = productEntityList;
            if (cartProductEntities.size() == 0){
                CheckButtonCart.setVisibility(View.GONE);
                }else {
                CheckButtonCart.setVisibility(View.VISIBLE);
                updateHotCount(cartProductEntities.size());
            }
        });

    }

    private void addToDatabase(List<Product> products) {
        mBrandItemsViewModel.insertToProductsEntity(products)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "Added to Room Database");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (hot_number == 0) {
            CheckButtonCart.setVisibility(View.GONE);
            ui_hot_bottom.setVisibility(View.INVISIBLE);
        } else {
            CheckButtonCart.setVisibility(View.VISIBLE);
            ui_hot_bottom.setVisibility(View.VISIBLE);
            ui_hot_bottom.setText(String.valueOf(hot_number));
            totalAmount.setText(""+ CartOperations.subTotalRoom(cartProductEntities));
        }
    }



    private class RvSearchBrandAdapter extends RecyclerView.Adapter<RvSearchBrandAdapter.ViewHolderRec> {

        private Context context;
        private List<Product> productList;


        RvSearchBrandAdapter(BrandItemsActivity brandItemsActivity, List<Product> product) {
            this.context = brandItemsActivity;
            this.productList = product;
        }

        @Override
        public ViewHolderRec onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolderRec(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_cart_item_recycle, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolderRec holder, int position) {
            holder.bindDataToView(position);
        }

        public String orderQty(String id) {
            String Qty = "0";
            Cursor res = cartDb.getItems();
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

//                itemView.setOnClickListener(this);
                itemView.findViewById(R.id.expand).setOnClickListener(this);
                title.setOnClickListener(this);
                Add_Cart.setOnClickListener(this);
                Remove_Cart.setOnClickListener(this);
                product_wish.setOnClickListener(this);
            }

            public void bindDataToView(int position){
                Product product = productList.get(position);

                //Download image using picasso library
                Picasso.with(context).load(product.getProductImage())
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(iv);
                title.setText(product.getProductName());
                item_id.setText(product.getProductId());
                quantity.setText(product.getProductSize());
                unit.setText(product.getProductUnit());
                price.setText(product.getProductUnitprice());
                String QtyInCart = orderQty(product.getProductId());
                count.setText(QtyInCart);
                progressBar.setVisibility(View.GONE);
                if (!product.getProductOfferprice().equals("noOffer")) {
                   offer_price_layout.setVisibility(View.VISIBLE);
                   price_offer.setText(product.getProductOfferprice());
                   price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                   price.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.med_gray));
                } else {
                    price.setPaintFlags(price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    price.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.nav_item_text));
                    offer_price_layout.setVisibility(View.GONE);
                }
                if (new SessionManager(getApplicationContext()).getUserDetails().get(SharedPref.Keys.KEY_UID).equals("0"))
                    product_wish.setVisibility(View.GONE);
                else {
                    if (product.getProductWish().equals("1")) {
                        product_wish.setImageResource(R.drawable.wishlist_icon_selected);
                        selected = true;
                    }else {
                        product_wish.setImageResource(R.drawable.wishlist_icon_unselected);
                        selected = false;
                    }
                }
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
                    itemExpanded(getAdapterPosition());
                } else if (v.getId() == title.getId()) {
                    Log.e("here on title", String.valueOf(getAdapterPosition()));
                    itemExpanded(getAdapterPosition());
                } else if (v.getId() == R.id.product_wish) {
                    if (selected){
                        removeFromWish(productList.get(getAdapterPosition()).getProductId());
                        product_wish.setImageResource(R.drawable.wishlist_icon_unselected);
                        selected = false;
                    }else {
                        addToWish(productList.get(getAdapterPosition()).getProductId());
                        product_wish.setImageResource(R.drawable.wishlist_icon_selected);
                        selected = true;
                    }
                    Log.e("position", String.valueOf(getAdapterPosition()));

                }
            }

            private void itemExpanded(int adapterPosition) {
                Product product = mProductList.get(adapterPosition);
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("title", product.getProductName());
                item.put("image", product.getProductImage());
                item.put("id", product.getProductId());
                item.put("quantity", product.getProductUnit());
                item.put("price", product.getProductUnitprice());
                Log.e("title", product.getProductName());
                Intent i = new Intent(getApplicationContext(), ItemExpandedActivity.class);
                ItemExpandedActivity.setData(item);
                startActivity(i);
            }

            public void update(String Qty, String Item) {
                mBrandItemsViewModel.updateCartItem(Qty, Item);
            }
        }

        void addToWish(String  productId){
            mBrandItemsViewModel.addToWish(productId);
        }

        void removeFromWish(String productId){
            mBrandItemsViewModel.removeWish(productId);
        }
    }
}
