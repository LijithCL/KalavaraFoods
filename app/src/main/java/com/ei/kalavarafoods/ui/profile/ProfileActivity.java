package com.ei.kalavarafoods.ui.profile;

import androidx.lifecycle.ViewModelProviders;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.cart.CartActivity;
import com.ei.kalavarafoods.ui.auth.LoginActivity;
import com.ei.kalavarafoods.ui.wallet.WalletActivity;
import com.ei.kalavarafoods.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.civProfilePhoto)
    CircleImageView civProfilePhoto;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.linearOrderHistory)
    LinearLayout linearOrderHistory;
    @BindView(R.id.linearWallet)
    LinearLayout linearWallet;
    @BindView(R.id.linearLogout)
    LinearLayout linearLogout;
    @BindView(R.id.linearLogIn)
    LinearLayout linearLogIn;
    @BindView(R.id.linearCart)
    LinearLayout linearCart;
    @BindView(R.id.linearAddress)
    LinearLayout linearAddress;

    private ProfileViewModel mProfileViewModel;
    private SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mSessionManager = new SessionManager(this);

        if (!mSessionManager.isLoggedIn()){
            linearOrderHistory.setVisibility(View.GONE);
            linearWallet.setVisibility(View.GONE);
            linearCart.setVisibility(View.GONE);
            linearLogout.setVisibility(View.GONE);
            linearAddress.setVisibility(View.GONE);
            linearLogIn.setVisibility(View.VISIBLE);
        }

        linearOrderHistory.setOnClickListener(this);
        linearWallet.setOnClickListener(this);
        linearCart.setOnClickListener(this);
        linearLogout.setOnClickListener(this);
        linearLogIn.setOnClickListener(this);
        linearAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearOrderHistory:
                startActivity(UserOrdersActivity.start(this));
                break;
            case R.id.linearWallet:
                startActivity(WalletActivity.activityIntent(this));
                break;
            case  R.id.linearCart:
                startActivity(CartActivity.activityIntent(this));
                break;
            case R.id.linearLogout:

                break;
            case R.id.linearLogIn:
                startActivity(LoginActivity.activityIntent(this)
                        .putExtra("caller","ProfileActivity"));
        }
    }
}
