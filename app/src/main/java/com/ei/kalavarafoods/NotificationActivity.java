package com.ei.kalavarafoods;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.ei.kalavarafoods.db.notificaiton.model.NotificationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AMK on 3/17/16.
 */
public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecycleAdapter recycleAdapter;
    List<HashMap<String, String>> onlineData;
    ProgressDialog pd;
    Toolbar toolbar;
    TextView _empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_hub);
//        toolbar= (Toolbar) findViewById(R.id.anim_toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.recyle_view);
        _empty = (TextView) findViewById(R.id.empty_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


//        final String url = Constants.CATEGORY;
//        new AsyncHttpTasknot().execute(url);

        showNotificationFromLocalDb();
    }

    private void showNotificationFromLocalDb() {
        DBHelperDisplayItems dbHelperDisplayItems = new DBHelperDisplayItems(this);
        List<NotificationItem> notificationItemList = dbHelperDisplayItems.readNotificationList();
        if (notificationItemList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            _empty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            _empty.setVisibility(View.GONE);
            recycleAdapter = new RecycleAdapter(NotificationActivity.this, notificationItemList);

            recyclerView.setAdapter(recycleAdapter);
        }
    }

    public class AsyncHttpTasknot extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(NotificationActivity.this);
            pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pd.setMessage("Loading please wait...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();
                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            pd.dismiss();

            //check list is empty or not
            if (onlineData.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                _empty.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                _empty.setVisibility(View.GONE);
            }

//            if (result == 1) {
//                recycleAdapter = new RecycleAdapter(NotificationActivity.this, onlineData);
//                recyclerView.setAdapter(recycleAdapter);
//            } else {
//                Toast.makeText(NotificationActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("categories");
            onlineData = new ArrayList<>();
            System.out.println("suneesh:"+result);

            for (int i = 0; i < posts.length(); i++) {
                JSONObject postobj1 = posts.optJSONObject(i);

                HashMap<String, String> item = new HashMap<>();

//                item.put("id", postobj1.optString("product_id"));
                item.put("title", postobj1.optString("category_title"));
                item.put("sub_title", postobj1.optString("category_subtitle"));
//                item.put("thump", postobj1.optString("category_image"));

                Log.i("Main Categories ", "" + item);
                onlineData.add(item);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return super.onCreateOptionsMenu(menu);
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


    //////////////////////////////////////////////////////////////////////////////////////////

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolderRec> {
        private final List<NotificationItem> mNotificationItemList;
        Context context;

        public RecycleAdapter(Context context, List<NotificationItem> notificationItemList) {
            this.mNotificationItemList = notificationItemList;
            this.context = context;
        }

        @Override
        public ViewHolderRec onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ViewHolderRec(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_adapterlayout, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolderRec holder, int position) {
//            HashMap<String, String> map = onlineData.get(position);
            //Download image using picasso library
//            Picasso.with(context).load(map.get("thump"))
//                    .error(R.drawable.placeholder)
//                    .placeholder(R.drawable.placeholder)
//                    .into(holder.iv);

            NotificationItem notificationItem = mNotificationItemList.get(position);
            holder.tv.setText(notificationItem.getTitle());
            holder.tvsub.setText(notificationItem.getMessage());
        }

        @Override
        public int getItemCount() {
            return mNotificationItemList.size();
        }

        public class ViewHolderRec extends RecyclerView.ViewHolder {
            //            ImageView iv;
            TextView tv;
            TextView tvsub;
            CardView cardItemLayout;

            public ViewHolderRec(View itemView) {
                super(itemView);
//                iv = (ImageView) itemView.findViewById(R.id.thumbnail);
                tv = (TextView) itemView.findViewById(R.id.title);
                tvsub = (TextView) itemView.findViewById(R.id.sub_title);
                cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            }
        }
    }
    public static Intent activityIntent(Context context){
        return new Intent(context, NotificationActivity.class);
    }
}
