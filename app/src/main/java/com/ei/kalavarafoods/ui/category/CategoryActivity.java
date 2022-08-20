package com.ei.kalavarafoods.ui.category;

import android.app.Activity;
import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ei.kalavarafoods.utils.ConnectionDetector;
import com.ei.kalavarafoods.DBHelperDisplayItems;
import com.ei.kalavarafoods.NoInternetActivity;
import com.ei.kalavarafoods.ui.number_verification.NumberVerificationActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.model.database.CategoryEntity;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.ui.cart.CartActivity;
import com.ei.kalavarafoods.ui.cart.CartOperations;
import com.ei.kalavarafoods.ui.cart.CheckoutActivity;
import com.ei.kalavarafoods.ui.auth.LoginActivity;
import com.ei.kalavarafoods.ui.main.MainActivity;
import com.ei.kalavarafoods.ui.search.SearchResultsActivity;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ei.kalavarafoods.utils.Constants.CATEGORY_MAIN_ID;
import static com.ei.kalavarafoods.utils.Constants.CATEGORY_TITLE;


public class CategoryActivity extends AppCompatActivity {

    DBHelperDisplayItems DBItems;
    Toolbar toolbar;
    ProgressDialog pd;
    String categoryMain;
    Button checkoutbutton;
    LinearLayout layout_bottom;
    TextView SubTotalAmt;
    ConnectionDetector cd;
    ImageView cartIcon;
    TextView subTotalAmt;

//    @BindView(R.id.linearCartDisplayBottom)
//    LinearLayout linearCartDisplayBottom;
    @BindView(R.id.progressTabActivity)
    ProgressBar progressTabAcitvity;

    private static ArrayList<Activity> activities = new ArrayList<Activity>();
    private int hot_number = 0;
    private TextView ui_hot_bottom = null;
    private CategoryViewModel mCategoryViewModel;
    private List<String> tabNamesList = new ArrayList<>();
    private List<ProductEntity> productEntityList;
    private SharedPref mSharedPref;
    private List<CategoryEntity> mCategoryEntities = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);
        mSharedPref = new SharedPref(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activities.add(this);
        getSupportActionBar().setTitle(mSharedPref.getString(CATEGORY_TITLE, "0"));
        cd = new ConnectionDetector(getApplicationContext());
        connectionCheck();
        mCategoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        SubTotalAmt = (TextView) findViewById(R.id.subTotalAmt);

        DBItems = new DBHelperDisplayItems(this);

        categoryMain = mSharedPref.getString(CATEGORY_MAIN_ID, "0");

        mCategoryViewModel.getProducts(categoryMain);
        mCategoryViewModel.liveDataIsLoading.observe(this, isLoading ->{
            if (isLoading){
                progressTabAcitvity.setVisibility(View.VISIBLE);
            }else {
                progressTabAcitvity.setVisibility(View.INVISIBLE);
                ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
                viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));
            }
        });
        getIdsFormDb();

        ui_hot_bottom = (TextView) findViewById(R.id.hotlist_hot_bottom);
        checkoutbutton = (Button) findViewById(R.id.checkoutbutton);
        checkoutbutton.setOnClickListener(new View.OnClickListener() {
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
        layout_bottom = (LinearLayout) findViewById(R.id.layout_bottom);
//        layout_bottom.setVisibility(View.VISIBLE);
        mCategoryViewModel.getCartItems().observe(this, cartProductEntities -> {
            if (cartProductEntities.size() == 0){
                layout_bottom.setVisibility(View.GONE);
            }else {
                layout_bottom.setVisibility(View.VISIBLE);
                productEntityList = cartProductEntities;
                updateHotCount(cartProductEntities.size());
            }
        });
        cartIcon = (ImageView) findViewById(R.id.hotlist_bell);
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(CategoryActivity.this, CartActivity.class);
                startActivity(in);
            }
        });

        subTotalAmt = (TextView) findViewById(R.id.subTotalAmt);
        subTotalAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(CategoryActivity.this, CartActivity.class);
                startActivity(in);
            }
        });


    }

    public void getIdsFormDb(){
        mCategoryEntities.addAll(mCategoryViewModel.getSubcategoriesFromDb(categoryMain));
        for (CategoryEntity categoryEntity: mCategoryEntities){
            tabNamesList.add(categoryEntity.getCategoryName());
            mCategoryViewModel.tabIdsList.add(categoryEntity.getCategoryId());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void connectionCheck() {

        if (!cd.isConnectingToInternet()) {

            Intent in = new Intent(CategoryActivity.this, NoInternetActivity.class);
            startActivity(in);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
//        MenuInflater menuInflater = getSupportMenuInflater();
//        menuInflater.inflate(R.menu.menu_actionbar, menu);
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        final View menu_hotlist = menu.findItem(R.id.menu_hotlist).getActionView();
        new MyMenuItemStuffListener(menu_hotlist, "Show hot message") {
            @Override
            public void onClick(View v) {
//                if(CartNos() > 0){
//                    Toast.makeText(CategoryActivity.this, "Search.....", Toast.LENGTH_SHORT).show();
//                }
//                case R.id.search_main:
                onSearchRequested();
                startActivity(new Intent(CategoryActivity.this, SearchResultsActivity.class));
            }
        };
        return super.onCreateOptionsMenu(menu);
    }


    public void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (hot_number == 0) {
            ui_hot_bottom.setVisibility(View.GONE);
        } else {
            ui_hot_bottom.setVisibility(View.VISIBLE);
            ui_hot_bottom.setText(Integer.toString(hot_number));
            YoYo.with(Techniques.Tada).duration(1000).playOn(ui_hot_bottom);
            SubTotalAmt.setText("" + CartOperations.subTotalRoom(productEntityList));
            YoYo.with(Techniques.Tada).duration(1000).playOn(SubTotalAmt);
        }
    }

    static abstract class MyMenuItemStuffListener implements View.OnClickListener {
        //    static abstract class MyMenuItemStuffListener implements View.OnClickListener, View.OnLongClickListener {
        private String hint;
        private View view;

        MyMenuItemStuffListener(View view, String hint) {
            this.view = view;
            this.hint = hint;
            view.setOnClickListener(this);
        }

        @Override
        abstract public void onClick(View v);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activities.remove(this);
        for (Activity activity : activities)
            activity.finish();
        DBItems.close();
        if (null != pd && pd.isShowing()) {
            pd.dismiss();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionCheck();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return tabNamesList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SubCategoryFragment.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabNamesList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        /*Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);*/
        this.finish();
    }
}
