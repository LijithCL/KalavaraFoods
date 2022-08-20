package com.ei.kalavarafoods.ui.main.home;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ei.kalavarafoods.model.database.MainCategoryEntity;
import com.ei.kalavarafoods.ui.category.CategoryActivity;
import com.ei.kalavarafoods.utils.ConnectionDetector;
import com.ei.kalavarafoods.Fast_Delivery;
import com.ei.kalavarafoods.NoInternetActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.api.Slide;
import com.ei.kalavarafoods.ui.main.MainViewModel;
import com.ei.kalavarafoods.ui.main.SliderAdapter;
import com.ei.kalavarafoods.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static com.ei.kalavarafoods.utils.Constants.CATEGORY_MAIN_ID;
import static com.ei.kalavarafoods.utils.Constants.CATEGORY_TITLE;

public class HomeFragment extends Fragment {

    ConnectionDetector cd;

    private String TAG = HomeFragment.class.getSimpleName();
    private static ArrayList<Activity> activities = new ArrayList<>();
    private ProgressBar progressBar;
    private RecyclerView rvCategoryGrid;
    private ViewPager mPagerImageSlider;
    private LinearLayout mLinearDotsLayout;
    private TextView[] mTvArrayDots;

    private MainViewModel mMainViewModel;

    private CategoryRecyclerAdapter recycleAdapter;
    private List<MainCategoryEntity> mMainCategories = new ArrayList<>();
    private List<String> categoryMainIds = new ArrayList<>();
    private SliderAdapter mSliderAdapter;
    private int mNoOfSlides;
    private int mCurrentPage;
    private SharedPref mSharedPref;
    final long DELAY_MS = 500;
    final long PERIOD_MS = 2500;
    private Timer timer;
    private Handler handler;
    private Runnable Update;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetector(getActivity());
        connectionCheck();
        super.onCreate(savedInstanceState);
        mMainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Override
    public void onResume() {
        connectionCheck();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        activities.add(getActivity());
        mSharedPref = new SharedPref(getActivity());
        progressBar = v.findViewById(R.id.pb_homeFrag);
        mPagerImageSlider = v.findViewById(R.id.pagerImageSlider);
        rvCategoryGrid = (RecyclerView) v.findViewById(R.id.recycler_view);
        mLinearDotsLayout = v.findViewById(R.id.linearDotsLayout);

        mMainViewModel.getCategories();

        ImageView fast_delivery;
        fast_delivery = (ImageView) v.findViewById(R.id.fast_delivery_image_id);
        fast_delivery.setOnClickListener(v1 -> {
            Intent in = new Intent(getActivity(), Fast_Delivery.class);
            startActivity(in);
        });

        rvCategoryGrid.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvCategoryGrid.setItemAnimator(new DefaultItemAnimator());
        rvCategoryGrid.setNestedScrollingEnabled(false);

        mMainViewModel.getMainCategories().observe(getViewLifecycleOwner(), mainCategoryEntities -> {
            recycleAdapter = new CategoryRecyclerAdapter(getActivity(), mainCategoryEntities);
            rvCategoryGrid.setAdapter(recycleAdapter);
            rvCategoryGrid.getAdapter().notifyDataSetChanged();
            rvCategoryGrid.invalidate();
        });

        mMainViewModel.liveDataCategoryMainIds.observe(getViewLifecycleOwner(),
                categoryMainIds -> this.categoryMainIds = categoryMainIds );

        mMainViewModel.liveDataIsLoading.observe(getViewLifecycleOwner(), isLoading ->{
            if (isLoading){
                progressBar.setVisibility(View.VISIBLE);
            }else {
                progressBar.setVisibility(View.INVISIBLE);

            }
        });

        getSliderData();

        return v;
    }

    private void getSliderData(){
        mMainViewModel.getSliderData();
        mMainViewModel.liveDataSlideList.observe(getViewLifecycleOwner(), slideList -> setUpSlider(slideList));
    }

    private void setUpSlider(List<Slide> slideList) {
        mNoOfSlides = slideList.size();
        mSliderAdapter = new SliderAdapter(getActivity(), slideList);
        mPagerImageSlider.setAdapter(mSliderAdapter);
        mPagerImageSlider.setCurrentItem(1);
        mPagerImageSlider.addOnPageChangeListener(sliderPageChangeListener);
        addDotsIndicator(1);

//        int NUM_PAGES = slideList.size();
//        Log.e(TAG, "setUpSlider: Size "+ NUM_PAGES);
//        handler = new Handler();
//        Update = () -> {
//            if (mCurrentPage == NUM_PAGES) {
//                mCurrentPage = 0;
//            }
//            mPagerImageSlider.setCurrentItem(mCurrentPage++, true);
//        };
//
//        timer = new Timer(); // This will create a new Thread
//        timer.schedule(new TimerTask() { // task to be scheduled
//            @Override
//            public void run() {
//                handler.post(Update);
//            }
//        }, DELAY_MS, PERIOD_MS);
    }

    @Override
    public void onPause() {
        super.onPause();
//        handler.removeCallbacks(Update);
//        timer.cancel();
    }

    private void addDotsIndicator(int position){
        mTvArrayDots = new TextView[mNoOfSlides];
        mLinearDotsLayout.removeAllViews();

        for (int i=0; i< mTvArrayDots.length; i++){
            mTvArrayDots[i] = new TextView(getActivity());
            mTvArrayDots[i].setText(Html.fromHtml("&#8226;"));
            mTvArrayDots[i].setTextSize(30);
            mTvArrayDots[i].setTextColor(getContext().getResources().getColor(R.color.whiteTransparent));
            mLinearDotsLayout.addView(mTvArrayDots[i]);
        }

        if (mTvArrayDots.length > 0){
            mTvArrayDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }


    ViewPager.OnPageChangeListener sliderPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };



    @Override
    public void onStop() {
        super.onStop();
    }

    protected void connectionCheck() {
        if (!cd.isConnectingToInternet()) {
            Intent in = new Intent(getActivity(), NoInternetActivity.class);
            startActivity(in);
        }
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++ Recycler Class below +++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolderRec> {
        List<MainCategoryEntity> categoryList;
        Context context;

        CategoryRecyclerAdapter(Context context, List<MainCategoryEntity> categoryList) {
            this.categoryList = categoryList;
            this.context = context;
        }

        @Override
        public ViewHolderRec onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_category_grid_layout, parent, false);
            return new ViewHolderRec(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolderRec holder, int position) {
            MainCategoryEntity mainCategoryEntity = categoryList.get(holder.getAdapterPosition());
            holder.bindDataToView(mainCategoryEntity);
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        class ViewHolderRec extends RecyclerView.ViewHolder implements View.OnClickListener {
            CardView cardClickHandle;
            ImageView ivCategoryImage;
            TextView tvTitle, tvDescription;

            ViewHolderRec(View itemView) {
                super(itemView);
                cardClickHandle = itemView.findViewById(R.id.cardClickHandle);
                ivCategoryImage = (ImageView) itemView.findViewById(R.id.ivCategoryImage);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
//                tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            }

            void bindDataToView(MainCategoryEntity mainCategoryEntity){
                Picasso.with(context)
                        .load(mainCategoryEntity.categoryImage)
                        .into(ivCategoryImage);

                tvTitle.setText(mainCategoryEntity.categoryTitle);
//                tvDescription.setText(mainCategoryEntity.categorySubTitle);
                cardClickHandle.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.cardClickHandle) {
                    Intent toCategoryActivity = new Intent(getActivity(), CategoryActivity.class);
                    Bundle bundle = new Bundle();
                    if (categoryMainIds.isEmpty()) {
                        Toast.makeText(getContext(), "Response error", Toast.LENGTH_SHORT).show();
                    } else {
//                        bundle.putString(CATEGORY_MAIN_ID, categoryMainIds.get(getAdapterPosition()));
                        toCategoryActivity.putExtras(bundle);
//                        mMainViewModel.categoryMainId = categoryMainIds.get(getAdapterPosition());
                        mSharedPref.putString(CATEGORY_MAIN_ID, categoryMainIds.get(getAdapterPosition()));
                        mSharedPref.putString(CATEGORY_TITLE, categoryList.get(getAdapterPosition()).getCategoryTitle());
                        startActivity(toCategoryActivity);
                    }
                }
            }
        }
    }
}