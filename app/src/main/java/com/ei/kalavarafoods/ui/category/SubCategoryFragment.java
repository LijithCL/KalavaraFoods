package com.ei.kalavarafoods.ui.category;

import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ei.kalavarafoods.DBHelperDisplayItems;
import com.ei.kalavarafoods.ItemExpandedActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.ui.main.MainViewModel;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubCategoryFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private static final String FRAGMENT_PAGE_NO = "fragment_page_no";
    DBHelperDisplayItems DB_Items;

    private int mPage;

    Cart_RecycleAdapter recycleAdapter;
    List<HashMap<String, String>> onlineData;
    ProgressDialog pd;
    String catName;
    TextView _empty;
    String url;
    int id;
    private MainViewModel mMainViewModel;
    private CategoryViewModel mCategoryViewModel;
    private String subCategoryId;
    private List<ProductEntity> productEntityList = new ArrayList<>();
    private int mFragmentPageNo;

    public static SubCategoryFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SubCategoryFragment fragment = new SubCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCategoryViewModel = ViewModelProviders.of(getActivity()).get(CategoryViewModel.class);
        mCategoryViewModel.fragmentPageNo = getArguments().getInt(ARG_PAGE);
        mFragmentPageNo = getArguments().getInt(ARG_PAGE);

        DB_Items = new DBHelperDisplayItems(getActivity());
        if (savedInstanceState!= null){
//            subCategoryId = mMainViewModel.tabIdsList.get(savedInstanceState.getInt(FRAGMENT_PAGE_NO));
        }
        onlineData = new ArrayList<>();
//        onlineData = ((CategoryActivity) getActivity()).detailedString(mPage - 1);
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);

        mCategoryViewModel = ViewModelProviders.of(getActivity()).get(CategoryViewModel.class);
        DB_Items = new DBHelperDisplayItems(getActivity());
        onlineData = new ArrayList<>();

//        mFragmentPageNo =  getArguments().getInt(ARG_PAGE);
        subCategoryId = mCategoryViewModel.tabIdsList.get(mFragmentPageNo);
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        RecyclerView recyclerItemList = (RecyclerView) view.findViewById(R.id.recyle_view_list);
        _empty = (TextView) view.findViewById(R.id.empty_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
        recyclerItemList.setLayoutManager(linearLayoutManager);
        recyclerItemList.setHasFixedSize(true);
        recyclerItemList.setLayoutAnimation(controller);
        productEntityList = mMainViewModel.getProductsAsPerCategoryIdFromDb(subCategoryId);
        Log.i("productInPage>>>",productEntityList.get(0).getProductName());
        if (productEntityList != null) {
            recyclerItemList.setVisibility(View.VISIBLE);
            _empty.setVisibility(View.GONE);
            recycleAdapter = new Cart_RecycleAdapter(getActivity(), productEntityList);
            recyclerItemList.setAdapter(recycleAdapter);
        } else {
            recyclerItemList.setVisibility(View.GONE);
            _empty.setVisibility(View.VISIBLE);
        }
//        UpdateCartNumber();
        return view;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Recycler Class Below /////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class Cart_RecycleAdapter extends RecyclerView.Adapter<Cart_RecycleAdapter.ViewHolderRec> {
        private int previousPosition = 0;
        List<HashMap<String, String>> onlineData;
        List<ProductEntity> productEntityList;
        Context context;

        Cart_RecycleAdapter(Context context, List<ProductEntity> productEntityList) {
            this.onlineData = onlineData;
            this.context = context;
            this.productEntityList = productEntityList;
        }

        @Override
        public ViewHolderRec onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolderRec(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_recycle, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolderRec holder, int position) {
//            HashMap<String, String> map = onlineData.get(position);
            ProductEntity productEntity = productEntityList.get(position);

            //Download image using picasso library
            Picasso.with(context).load(productEntity.getProductImage())//map.get("image"))
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.iv);

            holder.title.setText(productEntity.getProductName());//map.get("title"));
            holder.item_id.setText(productEntity.getProductId());//map.get("id"));
            holder.size.setText(productEntity.getProductSize());//map.get("size"));
            holder.unit.setText(productEntity.getProductUnit());//map.get("unit"));
            if (!productEntity.getProductOfferprice().equals("noOffer")) {
                holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.price.setText(productEntity.getProductUnitprice());
                holder.price.setTextColor(ContextCompat.getColor(getContext(), R.color.med_gray));
                holder.price_offer_text.setVisibility(View.VISIBLE);
                holder.price_symbol_offer.setVisibility(View.VISIBLE);
                holder.price_offer.setVisibility(View.VISIBLE);
                holder.price_offer.setText(productEntity.getProductOfferprice());
            } else {
                holder.price_offer_text.setVisibility(View.GONE);
                holder.price_symbol_offer.setVisibility(View.GONE);
                holder.price_offer.setVisibility(View.GONE);
                holder.price.setText(productEntity.getProductUnitprice());
                holder.price.setTextColor(ContextCompat.getColor(getContext(), R.color.nav_item_text));
                holder.price.setPaintFlags(holder.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            String orderQuantity = mMainViewModel.getProductOrderQuantity(productEntity.getProductId());
            holder.count.setText(orderQuantity);
            holder.progressBar.setVisibility(View.GONE);
            if (new SessionManager(getContext()).getUserDetails().get(SharedPref.Keys.KEY_UID).equals("0"))
                holder.product_wish.setVisibility(View.GONE);
            else {
                if (productEntity.getProductWish().equals("1")) {
                    holder.product_wish.setImageResource(R.drawable.wishlist_icon_selected);
                    holder.selected = true;
                }else {
                    holder.product_wish.setImageResource(R.drawable.wishlist_icon_unselected);
                    holder.selected = false;
                }
            }

//            if (position > previousPosition) {
//                AnimationUtils.animate(holder, true);
//            } else {
//                AnimationUtils.animate(holder, false);
//            }
            previousPosition = position;
        }


        public String orderQty(String id) {
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
            return productEntityList.size();
        }

        public void update(String quantity, String item) {
            mMainViewModel.updateCartItem(quantity, item);
        }

        public class ViewHolderRec extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView iv;
            TextView title, size, price, count, item_id, price_offer, price_offer_text, price_symbol_offer, unit;
            ImageView Add_Cart;
            ImageView Remove_Cart;
            ImageButton product_wish;
            ProgressBar progressBar;
            boolean selected;


            public ViewHolderRec(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.thumbnail);
                title = (TextView) itemView.findViewById(R.id.title);
                item_id = (TextView) itemView.findViewById(R.id.item_id);
                size = (TextView) itemView.findViewById(R.id.quantity);
                unit = itemView.findViewById(R.id.unit);
                price = (TextView) itemView.findViewById(R.id.price);
                Add_Cart = (ImageView) itemView.findViewById(R.id.cart_add);
                Remove_Cart = (ImageView) itemView.findViewById(R.id.cart_remove);
                count = (TextView) itemView.findViewById(R.id.itemcount);
                product_wish = (ImageButton) itemView.findViewById(R.id.product_wish);
                progressBar = (ProgressBar) itemView.findViewById(R.id.product_wish_progressbar);
                price_offer = (TextView) itemView.findViewById(R.id.price_offer);
                price_offer_text = (TextView) itemView.findViewById(R.id.price_offer_text);
                price_symbol_offer = (TextView) itemView.findViewById(R.id.pricesymbol_offer);

                itemView.findViewById(R.id.expand);
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
                    itemExpanded(getAdapterPosition());
                } else if (v.getId() == title.getId()) {
                    Log.e("here on title", String.valueOf(getAdapterPosition()));
                    itemExpanded(getAdapterPosition());
                } else if (v.getId() == R.id.product_wish) {
                    if (selected){
                        removeFromWish(productEntityList.get(getAdapterPosition()).getProductId());
                        product_wish.setImageResource(R.drawable.wishlist_icon_unselected);
                        selected = false;
                    }else {
                        addToWish(productEntityList.get(getAdapterPosition()).getProductId());
                        product_wish.setImageResource(R.drawable.wishlist_icon_selected);
                        selected = true;
                    }
                }
            }

            private void itemExpanded(int adapterPosition) {
                HashMap<String, String> map = onlineData.get(adapterPosition);
                Log.e("title", map.get("title"));
//                Random r = new Random();
//                int code = r.nextInt(999999);
//                Log.e("original", String.valueOf(code));
//                if (code < 100000)
//                    code += 100000;
//                Log.e("random", String.valueOf(code));
                Intent i = new Intent(getActivity(), ItemExpandedActivity.class);
                ItemExpandedActivity.setData(map);
                startActivity(i);
            }
        }

        private void addToWish(String productId) {
            mMainViewModel.addToWish(productId);
        }

        private void removeFromWish(String productId) {
            mMainViewModel.removeWish(productId);
        }
    }
}