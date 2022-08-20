package com.ei.kalavarafoods;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.ei.kalavarafoods.db.notificaiton.NotificationDbContract;
import com.ei.kalavarafoods.db.notificaiton.model.NotificationItem;
import com.ei.kalavarafoods.ui.main.MainActivity;
import com.ei.kalavarafoods.utils.SessionManager;

public class SplashActivity extends AppCompatActivity implements NotificationDbContract.TableConstants {
    private SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mSessionManager = new SessionManager(this);
        final int SPLASH_TIME_OUT = 2000;

        new Handler().postDelayed(() -> {
            Intent i;
            i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            mSessionManager.putPostalCode("");
            mSessionManager.putLocality("");
            mSessionManager.putState("");
            if (getIntent().hasExtra("click_action")) {
                i = new Intent(SplashActivity.this, NotificationActivity.class);
                startActivity(i);
            }
            finish();
        }, SPLASH_TIME_OUT);
        handleFcmPushNotification();
    }

    private void handleFcmPushNotification() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            DBHelperDisplayItems dbHelperDisplayItems = new DBHelperDisplayItems(this);
            NotificationItem notificationItem = new NotificationItem();
            notificationItem.setTitle(bundle.getString(COLUMN_TITLE));
            notificationItem.setMessage(bundle.getString(COLUMN_MESSAGE));
            notificationItem.setImage(bundle.getString(COLUMN_IMAGE));
            dbHelperDisplayItems.insertNotification(notificationItem);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}