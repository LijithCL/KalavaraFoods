package com.ei.kalavarafoods;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoInternetActivity extends Activity {

    Button retry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        retry =(Button)findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
