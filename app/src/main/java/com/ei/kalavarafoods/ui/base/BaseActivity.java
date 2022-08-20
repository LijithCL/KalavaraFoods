package com.ei.kalavarafoods.ui.base;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.dialogs.ProgressDialogFragment;


public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialogFragment progressDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        if (setToolbar()) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    public abstract int setLayout();

    public abstract boolean setToolbar();

    public void showProgress(){
        progressDialogFragment = ProgressDialogFragment.newInstance();
        progressDialogFragment.show(getSupportFragmentManager(), null);
    }

    public void dismissProgress(){
        if (progressDialogFragment != null){
            progressDialogFragment.dismiss();
        }
    }
}
