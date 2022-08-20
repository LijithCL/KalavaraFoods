package com.ei.kalavarafoods.ui.main.wishlist;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ei.kalavarafoods.model.api.Product;
import com.ei.kalavarafoods.ui.cart.CartOperations;
import com.ei.kalavarafoods.ui.cart.CartActivity;
import com.ei.kalavarafoods.ui.cart.CheckoutActivity;
import com.ei.kalavarafoods.DBHelperDisplayItems;
import com.ei.kalavarafoods.ItemExpandedActivity;
import com.ei.kalavarafoods.ui.main.MainViewModel;
import com.ei.kalavarafoods.ui.number_verification.NumberVerificationActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class WishListFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private TextView noItems;
    private LinearLayout checkoutbuttoncardLayout, cartviewLayout;
    private TextView count_cartitems_TextView, payable_amount_cartitems_TextView;
    private AppCompatButton checkout_Button;
    private List<HashMap<String, String>> wishedItems = null;
    DBHelperDisplayItems DBItems;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    private ProgressBar progressBar;
    private MainViewModel mMainViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mMainViewModel.getWishList();
        mMainViewModel.liveDataIsLoading.observe(this,isLoading ->{
            if (isLoading){
                progressBar.setVisibility(View.VISIBLE);
            }else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_wishlist, container, false);
        mMainViewModel.liveDataWishList.observe(this, products -> {
            recyclerView.setAdapter(new Wishlist_RCAdapter(getContext(), products));
        });

        mMainViewModel.getCartItems().observe(this, cartProductEntities -> {
            if (cartProductEntities!= null) {
                if (cartProductEntities.size() == 0) {
                    checkoutbuttoncardLayout.setVisibility(View.GONE);
                } else {
                    checkoutbuttoncardLayout.setVisibility(View.VISIBLE);
                    count_cartitems_TextView.setText(String.valueOf(cartProductEntities.size()));
                    float payableAmount = CartOperations.subTotalRoom(cartProductEntities);
                    if (payableAmount > 300)
                        payable_amount_cartitems_TextView.setText(String.valueOf(payableAmount));
                    else
                        payable_amount_cartitems_TextView.setText(String.valueOf(payableAmount + 30));
                }
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        DBItems = new DBHelperDisplayItems(getContext());
        noItems = (TextView) view.findViewById(R.id.text_no_items);
        checkoutbuttoncardLayout = (LinearLayout) view.findViewById(R.id.checkoutbuttoncard);
        count_cartitems_TextView = (TextView) view.findViewById(R.id.checkoutbuttoncard_count_cartitems);
        payable_amount_cartitems_TextView = (TextView) view.findViewById(R.id.checkoutbuttoncard_payable_amount);
        checkout_Button = (AppCompatButton) view.findViewById(R.id.checkoutbutton);
        cartviewLayout = (LinearLayout) view.findViewById(R.id.checkoutbuttoncard_cartview);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator_layout);
        progressBar = view.findViewById(R.id.pb_wishFrag);
        checkout_Button.setOnClickListener(this);
        cartviewLayout.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();
//        getProductsWished();
        getCartView();
    }

    @Override
    public void onStop() {
        super.onStop();
//        DBItems.clearItems();
        DBItems.close();
    }

    private void getCartView() {
        Integer Qty = 0;
        Cursor cursor = DBItems.getCartItems();
        if (cursor.getCount() == 0) {
            checkoutbuttoncardLayout.setVisibility(View.GONE);
        } else {
            checkoutbuttoncardLayout.setVisibility(View.VISIBLE);
            while (cursor.moveToNext()) {
                if (Integer.valueOf(cursor.getString(7)) > 0)
                    Qty++;
            }
            count_cartitems_TextView.setText(String.valueOf(Qty));
            cursor = DBItems.getCartItems();
            int payableAmount = CartOperations.subTotal(cursor);
            if (payableAmount > 300)
                payable_amount_cartitems_TextView.setText(String.valueOf(payableAmount));
            else payable_amount_cartitems_TextView.setText(String.valueOf(payableAmount + 30));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkoutbutton:
                checkout_Button.setClickable(false);
                if (new SessionManager(getContext()).isNumberVerified()) {
                    startActivity(new Intent(getContext(), CheckoutActivity.class));
                } else {
                    startActivity(new Intent(getContext(), NumberVerificationActivity.class));
                }
                checkout_Button.setClickable(true);
                break;
            case R.id.checkoutbuttoncard_cartview:
                cartviewLayout.setClickable(false);
                startActivity(new Intent(getContext(), CartActivity.class));
                cartviewLayout.setClickable(true);
                break;
        }
    }

    public class Wishlist_RCAdapter extends RecyclerView.Adapter<Wishlist_RCAdapter.VHWishList> {
        private Context context;
        private List<Product> wishedItems;
        private int previousPosition = 0;

        public Wishlist_RCAdapter(Context context, List<Product> productsWished) {
            this.wishedItems = productsWished;
            this.context = context;
            Log.e("Wishllenght>>>", String.valueOf(this.wishedItems.size()));
        }

        @Override
        public VHWishList onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VHWishList(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_wished, parent, false));
        }

        @Override
        public void onBindViewHolder(VHWishList holder, int position) {
            Product product = wishedItems.get(position);
            Picasso.with(context).load(product.getProductImage())//map.get("image"))
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder).into(holder.iv);
            holder.title.setText(product.getProductName());//map.get("tvTitle"));
            holder.item_id.setText(product.getProductId());//map.get("id"));
            holder.quantity.setText(product.getProductSize());//map.get("unit"));
            holder.price.setText(product.getProductUnitprice());//map.get("price_new"));
            holder.progressBar.setVisibility(View.GONE);
            if (!product.getProductOfferprice().equals("noOffer")) {
                holder.offer_price_layout.setVisibility(View.VISIBLE);
                holder.price_offer.setText(product.getProductOfferprice());
                holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.price.setTextColor(ContextCompat.getColor(getContext(), R.color.med_gray));
            } else {
                holder.price.setPaintFlags(holder.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.price.setTextColor(ContextCompat.getColor(getContext(), R.color.nav_item_text));
                holder.offer_price_layout.setVisibility(View.GONE);
            }
            String QtyInCart = mMainViewModel.getProductOrderQuantity(product.getProductId());
            holder.count.setText(QtyInCart);
//            if (position > previousPosition) {
//                AnimationUtils.animateWishList(holder, true);
//            } else {
//                AnimationUtils.animateWishList(holder, false);
//            }
            previousPosition = position;
        }

        private String orderQty(String id) {
            String Qty = "0";
            Cursor res = DBItems.getItems();
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
            if (wishedItems.size() == 0)
                noItems.setVisibility(View.VISIBLE);
            else noItems.setVisibility(View.GONE);
            return wishedItems.size();
        }

        public class VHWishList extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView iv;
            TextView title, quantity, price, count, item_id, price_offer;
            ImageView Add_Cart;
            ImageView Remove_Cart;
            ImageButton product_wish;
            ProgressBar progressBar;
            LinearLayout offer_price_layout;

            public VHWishList(View itemView) {
                super(itemView);

                iv = (ImageView) itemView.findViewById(R.id.thumbnail);
                title = (TextView) itemView.findViewById(R.id.title);
                item_id = (TextView) itemView.findViewById(R.id.item_id);
                quantity = (TextView) itemView.findViewById(R.id.quantity);
                price = (TextView) itemView.findViewById(R.id.price);
                Add_Cart = (ImageView) itemView.findViewById(R.id.cart_add);
                Remove_Cart = (ImageView) itemView.findViewById(R.id.cart_remove);
                count = (TextView) itemView.findViewById(R.id.itemcount);
                product_wish = (ImageButton) itemView.findViewById(R.id.product_wish);
                progressBar = (ProgressBar) itemView.findViewById(R.id.product_wish_progressbar);
                offer_price_layout = (LinearLayout) itemView.findViewById(R.id.offer_price_layout);
                price_offer = (TextView) offer_price_layout.findViewById(R.id.price_offer);

                product_wish.setOnClickListener(this);
                Add_Cart.setOnClickListener(this);
                Remove_Cart.setOnClickListener(this);
                iv.setOnClickListener(this);
                title.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ItemExpandedActivity.class);
                switch (view.getId()) {
                    case R.id.product_wish:
                        removeWishAction(getAdapterPosition(), wishedItems.get(getAdapterPosition()));
                        break;
                    case R.id.cart_add:
                        int currentNos = Integer.parseInt(count.getText().toString());
                        currentNos++;
                        count.setText(String.valueOf(currentNos));
                        update(String.valueOf(currentNos), item_id.getText().toString());
                        break;
                    case R.id.cart_remove:
                        int currentNum = Integer.parseInt(count.getText().toString());
                        if (currentNum > 0) {
                            currentNum--;
                            count.setText(String.valueOf(currentNum));
                            update(String.valueOf(currentNum), item_id.getText().toString());
                            Log.e("Value ", String.valueOf(currentNum));
                        }
                        break;
//                    case R.id.thumbnail:
//                        ItemExpandedActivity.setData(itemToExpand(wishedItems.get(getAdapterPosition())));
//                        startActivity(intent);
//                        break;
//                    case R.id.title:
//                        ItemExpandedActivity.setData(itemToExpand(wishedItems.get(getAdapterPosition())));
//                        startActivity(intent);
//                        break;
                }
            }
        }


        private void removeWishAction(final int adapterPosition, final Product removingItem) {
            wishedItems.remove(adapterPosition);
            notifyItemRemoved(adapterPosition);
            snackbar = Snackbar.make(coordinatorLayout, "Removed an item", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                    wishedItems.add(adapterPosition, removingItem);
                    notifyItemInserted(adapterPosition);
                }
            });
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    //noinspection WrongConstant
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {
                        Log.e("dismissed", "on time out : removing item here");
                        removeWish(adapterPosition, removingItem);
                    }
                }
            });
            snackbar.show();
        }


        public void update(String quantity, String item) {
            mMainViewModel.updateCartItem(quantity, item);
        }

        private void removeWish(int adapterPosition,Product removingItem) {
            mMainViewModel.removeWish(adapterPosition, removingItem);
        }
    }
}