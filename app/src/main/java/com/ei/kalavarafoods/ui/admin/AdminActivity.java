package com.ei.kalavarafoods.ui.admin;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.network.ApiInterface;
import com.ei.kalavarafoods.network.RetrofitClient;
import com.ei.kalavarafoods.model.api.Order;
import com.ei.kalavarafoods.model.api.OrderListItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {


    private List<Order> mOrderList;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private AdminViewModel mAdminViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAdminViewModel = ViewModelProviders.of(this).get(AdminViewModel.class);

        getOrderList();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders");

        mViewPager = findViewById(R.id.vpOrdersPager);
        setupViewPager(mViewPager);

        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        OrdersPagerAdapter pagerAdapter = new OrdersPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(AdminOrderFragment.newInstance("Received"), "Received");
        pagerAdapter.addFragment(AdminOrderFragment.newInstance("Order Processed"),"Processed");
        pagerAdapter.addFragment(AdminOrderFragment.newInstance("Dispatched"),"Dispatched");
        pagerAdapter.addFragment(AdminOrderFragment.newInstance("Delivered"),"Delivered");
        pagerAdapter.addFragment(AdminOrderFragment.newInstance("Returned"),"Returned");
        viewPager.setAdapter(pagerAdapter);
    }

    private void getOrderList() {
        ApiInterface apiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);


        apiInterface.getAdminOrderList().enqueue(new Callback<OrderListItem>() {
            @Override
            public void onResponse(Call<OrderListItem> call, Response<OrderListItem> response) {
                Log.d("reponse>>>", response.message());
                if (response.body() != null) {
                    OrderListItem orderListItem = response.body();
                    List<Order> orderList = orderListItem.getOrder();
                    if (orderList != null) {
                        mAdminViewModel.insertOrders(orderList);
                    }else {
                        Log.e("ResErr>>", "nulled uppp");
                    }
                }


            }

            @Override
            public void onFailure(Call<OrderListItem> call, Throwable t) {
                Log.e("NetworkFail>>>",t.getMessage());

            }
        });
    }

    public class OrdersPagerAdapter extends FragmentPagerAdapter{
        List<Fragment> mFragmentList = new ArrayList<>();
        List<String> mTitleList = new ArrayList<>();

        public OrdersPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }

    public static Intent activityIntent(Context context){
        return new Intent(context, AdminActivity.class);
    }
}
