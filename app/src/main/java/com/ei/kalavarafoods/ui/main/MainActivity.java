package com.ei.kalavarafoods.ui.main;

import androidx.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.ei.kalavarafoods.ui.profile.UserOrdersActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ei.kalavarafoods.NotificationActivity;
import com.ei.kalavarafoods.ui.cart.CartActivity;
import com.ei.kalavarafoods.utils.ConnectionDetector;
import com.ei.kalavarafoods.ui.main.account.AccountsFragment;
import com.ei.kalavarafoods.ui.main.home.HomeFragment;
import com.ei.kalavarafoods.ui.main.wishlist.WishListFragment;
import com.ei.kalavarafoods.ui.select_location.SelectLocationDialogFragment;
import com.ei.kalavarafoods.ui.select_location.adapter.LocationsAdapter;
import com.ei.kalavarafoods.ui.select_location.model.Location;
import com.ei.kalavarafoods.utils.Constants;
import com.ei.kalavarafoods.ContactUsActivity;
import com.ei.kalavarafoods.utils.SharedPref;
import com.ei.kalavarafoods.utils.UsesPermissions;
import com.ei.kalavarafoods.ui.auth.LoginActivity;
import com.ei.kalavarafoods.NoInternetActivity;
import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.search.SearchResultsActivity;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.ui.wallet.WalletActivity;
import com.ei.kalavarafoods.network.Network;
import com.ei.kalavarafoods.network.VolleySingleTon;
import com.ei.kalavarafoods.ui.admin.AdminActivity;
import com.ei.kalavarafoods.utils.GPSTracker;
import com.ei.kalavarafoods.utils.RegistrationIntentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.ei.kalavarafoods.utils.Constants.REQUEST_PERMISSION_COARSE_AND_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        LocationsAdapter.SelectLocation {
    private static final String TAG = MainActivity.class.getSimpleName();
//    DBHelperDisplayItems cartDb;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;


    ConnectionDetector cd;
    // Session Manager Class
    SessionManager mSessionManger;
    TextView _headermail;

    private GPSTracker gpsTracker;
    private Geocoder geocoder;
    private BottomNavigationView bottomNavigationView;
    private int mSelectedItem;
    private UsesPermissions mUsesPermissions;
    private MainViewModel mMainViewModel;

    private boolean mCanExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cd = new ConnectionDetector(getApplicationContext());
        connectionCheck();
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUsesPermissions = new UsesPermissions(this);
        mSessionManger = new SessionManager(getApplicationContext());
        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        bottomNavigationView = findViewById(R.id.mainBottomNavView);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        if (!mSessionManger.isLoggedIn()){
            bottomNavigationView.setVisibility(View.GONE);
            pushFragment(new HomeFragment());
            mSessionManger.createLoginSession(Constants.DEFAULT_USER_EMAIL, String.valueOf(Constants.DEFAULT_USER_INT), "default_user", Constants.DEFAULT_ROLE);
        }else {
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                selectFragment(item);
                return false;
            });
        }

        if (Network.isOnline(this)) {
            Log.e("status", "network");
        }
        if (mSessionManger.isLoggedIn())
            getInstanceId();


        View view;
        Menu navMenu = navigationView.getMenu();
        View headerView = navigationView.getHeaderView(0);
        TextView tvHeaderEmail = headerView.findViewById(R.id.emailheader);
        tvHeaderEmail.setText(mSessionManger.getUserEmail());
        if (mSessionManger.isLoggedIn()) {
//            view = navKnownUser();
            setMenuForLoggedIn(navMenu);
        } else {
//            view = navDefaultUser();
            setMenuForDefault(navMenu);
        }
//        navigationView.setItemIconTintList(null);
//        navigationView.addView(view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initializing Drawer Layout and ActionBarToggle

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        if (SharedPref.getString(SharedPref.Keys.SELECTED_LOCATION, "").equals("")) {
            SelectLocationDialogFragment.newInstance().show(getSupportFragmentManager(), null);
        }

    }

    private void setMenuForLoggedIn(Menu navMenu) {
        navMenu.findItem(R.id.navig_login).setVisible(false);
        String userRole = mSessionManger.getUserRole();
        if (userRole.equals("admin")){
            navMenu.findItem(R.id.navig_admin).setVisible(true);
        }

    }

    private void setMenuForDefault(Menu navMenu) {
        navMenu.findItem(R.id.navig_logout).setVisible(false);
        navMenu.findItem(R.id.navig_cart).setVisible(false);
        navMenu.findItem(R.id.navig_wallet).setVisible(false);
    }


    private void selectFragment(MenuItem item) {
        switch (item.getItemId()){
            case R.id.bottom_nav_home:
                pushFragment(new HomeFragment());
                item.setChecked(true);
                break;

            case R.id.bottom_nav_account:
                pushFragment(new AccountsFragment());
                item.setChecked(true);
                break;

            case R.id.bottom_nav_wishlist:
                pushFragment(new WishListFragment());
                item.setChecked(true);
                break;
        }

        mSelectedItem = item.getItemId();
    }

    private void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null){
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null){
                ft.replace(R.id.flRealtabcontent, fragment);
                ft.commit();
            }
        }

    }

    private void getInstanceId() {
        startService(new Intent(this, RegistrationIntentService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationFetch();
    }

    private void locationFetch() {
        if (cd.isConnectingToInternet()) {
            int myVersion = Build.VERSION.SDK_INT;
            if (myVersion >= Build.VERSION_CODES.M) {
                if (mUsesPermissions.isPermissionGranted(UsesPermissions.ACCESS_COARSE_LOCATION)
                        || mUsesPermissions.isPermissionGranted(UsesPermissions.ACCESS_FINE_LOCATION)) {
                    if (mSessionManger.getPostalCode().equals(""))
                        getLocation(this);
                } else {
                    mUsesPermissions.requestPermission(new String[]{UsesPermissions.ACCESS_COARSE_LOCATION, UsesPermissions.ACCESS_FINE_LOCATION},
                            REQUEST_PERMISSION_COARSE_AND_FINE_LOCATION);
                }
            }else {
                getLocation(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_COARSE_AND_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mSessionManger.getPostalCode().equals(""))
                        getLocation(this);
                }
        }
    }

    private void getLocation(Context context) {
        gpsTracker = new GPSTracker(context);
        if (gpsTracker.canGetLocation()) {
            Log.e("loc", String.valueOf(gpsTracker.getLatitude()) + " " + String.valueOf(gpsTracker.getLongitude()));
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            new AsyncLocation().execute(geocoder);
            gpsTracker.stopUsingGPS();
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void setupWindowAnimations() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slideTransition = new Slide();
            slideTransition.setSlideEdge(Gravity.LEFT);
            slideTransition.setDuration(1000);
            getWindow().setReenterTransition(slideTransition);
            getWindow().setExitTransition(slideTransition);
        }
    }

    // Method to share either text or URL.
    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "MetroMart");
        share.putExtra(Intent.EXTRA_TEXT, "Download Metromart \n https://www.metromartapp.com");

        startActivity(Intent.createChooser(share, "Share....!"));
    }

    private void Rateus() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }


    public void logout() {
        startService(new Intent(this, RegistrationIntentService.class).
                putExtra(Constants.AuthMode.MODE, mSessionManger.getUserDetails().get(SharedPref.Keys.KEY_EMAIL)));
        mSessionManger.logoutUser();
        this.finish();
    }

    protected void connectionCheck() {
        if (!cd.isConnectingToInternet()) {
            Intent in = new Intent(MainActivity.this, NoInternetActivity.class);
            startActivity(in);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionCheck();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cartDb.drop();
        Log.i(TAG, "Main activity destroyed>>>>>>>>");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_main: {
                onSearchRequested();
                startActivity(new Intent(MainActivity.this, SearchResultsActivity.class));
                break;
            }
//            case R.id.profile_item:
//                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            MenuItem homeItemId = bottomNavigationView.getMenu().getItem(0);
            if (mSelectedItem == homeItemId.getItemId()) {
                if (mCanExit){
                    super.onBackPressed();
                    return;
                }
                mCanExit = true;
                Toast.makeText(this, "Please click again to Exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       mCanExit = false;
                    }
                }, 2000);
            } else {
                selectFragment(homeItemId);
            }
        }else {
            drawerLayout.closeDrawers();
        }
    }


    @Override
    public void onClick(View view) {

    }

    private void userLogout() {
        AlertDialog.Builder exit_alertbldr = new AlertDialog.Builder(MainActivity.this);
        exit_alertbldr.setTitle("Signout....?");
        exit_alertbldr.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        exit_alertbldr.setNegativeButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                cartDb.ClearCart();
                logout();
            }
        });
        exit_alertbldr.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.navig_login:
                startActivity(LoginActivity.activityIntent(this)
                        .putExtra("caller", "MainActivity"));
                break;
            case R.id.navig_logout:
                userLogout();
                break;
            case R.id.navig_home:
                drawerLayout.closeDrawers();
                break;
            case R.id.navig_admin:
                startActivity(AdminActivity.activityIntent(this));
                break;
            case R.id.navig_cart:
                startActivity(CartActivity.activityIntent(this));
                break;
            case R.id.navig_orders:{
                startActivity(UserOrdersActivity.start(this));
                break;
            }
            case R.id.navig_contactUs:
                startActivity(ContactUsActivity.activityIntent(this));
                break;
            case R.id.navig_notification:
                startActivity(NotificationActivity.activityIntent(this));
                break;
            case R.id.navig_wallet:
                startActivity(WalletActivity.activityIntent(this));
                break;
            case R.id.navig_rateUs:
                Rateus();
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onLocationSelected(Location location) {
        SharedPref.putString(SharedPref.Keys.SELECTED_LOCATION, location.getLocationName());
        SharedPref.putString(SharedPref.Keys.SELECTED_LOCATION_ID, location.getId());
        Log.e(TAG, "onLocationSelected: "+ SharedPref.getString(SharedPref.Keys.SELECTED_LOCATION, ""));
    }

    private class AsyncLocation extends AsyncTask<Geocoder, Void, List<Address>> {
        List<Address> address;
        String add;

        @Override
        protected void onPreExecute() {
            address = null;
            add = null;
        }

        @Override
        protected List<Address> doInBackground(Geocoder... geocoders) {
            try {
                address = geocoders[0].getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);

//                Log.i("Postal Code>>", address.get(0).getPostalCode());
            } catch (IOException e) {
                Log.e("exception", e.toString());
                locationFetchUsingGoogleAPI(gpsTracker);
                e.printStackTrace();
            }
            return address;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (addresses != null) {
                if (addresses.size() > 0) {
                    Log.e("activity_address", "size " + addresses.size());
                    mSessionManger.putPostalCode((addresses.get(0).getPostalCode() != null) ? addresses.get(0).getPostalCode() : "");
                    mSessionManger.putLocality((addresses.get(0).getLocality() != null) ? addresses.get(0).getLocality() : "");
                    mSessionManger.putState((addresses.get(0).getAdminArea() != null) ? addresses.get(0).getAdminArea() : "");
                    mSessionManger.putPostalCode(addresses.get(0).getPostalCode());
                    Log.e("add", addresses.get(0).toString());

                }
                String locality =  addresses.get(0).getLocality();
                if (!locality.equals(mSessionManger.getLocality())){
                    alertDialogForLocationChange(locality);
                }
            }
        }
    }

    private void alertDialogForLocationChange(String locality) {
        AlertDialog.Builder locationAlert = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.alert_location_prompt, null);
        locationAlert.setView(alertView);
        TextView tvPromptText = alertView.findViewById(R.id.tvPromptText);
        Button btnCurrentLoc = alertView.findViewById(R.id.btnCurrentLoc);
        tvPromptText.setText("Your Location is changed to "+locality+"\n"+
                "Delivery Service Available only for Kochi For Now");
        locationAlert.setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.dismiss()));
        locationAlert.setCancelable(false);
        locationAlert.show();
    }

    private void locationFetchUsingGoogleAPI(GPSTracker gpsTracker) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_LOCATION_GOOGLE_API + gpsTracker.getLatitude() + "," + gpsTracker.getLongitude() + "&sensor=true",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseLocation(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("loc_error", error.toString());
            }
        });
        VolleySingleTon.getsInstance().getmRequestQueue().add(stringRequest);
    }

    private void parseLocation(String response) {
        try {
            JSONObject locJsonObject = new JSONObject(response);
            if (locJsonObject.getJSONArray("results").length() > 0) {
                JSONObject locJsonObject2 = new JSONObject(locJsonObject.getJSONArray("results").get(0).toString());
                JSONArray locJsonArray = locJsonObject2.getJSONArray("address_components");
                for (int i = 0; i < locJsonArray.length(); i++) {
                    JSONObject items = new JSONObject(locJsonArray.get(i).toString());
                    Log.e("type", items.toString());
                    JSONArray type = items.getJSONArray("types");
                    for (int j = 0; j < type.length(); j++) {
                        if (type.get(j).toString().equals(mSessionManger.KEY_POSTAL_CODE))
                            mSessionManger.putPostalCode(items.getString("long_name"));
                        if (type.get(j).toString().equals(mSessionManger.KEY_LOCALITY))
                            mSessionManger.putLocality(items.getString("long_name"));
                        if (type.get(j).toString().equals(mSessionManger.KEY_STATE))
                            mSessionManger.putState(items.getString("long_name"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}