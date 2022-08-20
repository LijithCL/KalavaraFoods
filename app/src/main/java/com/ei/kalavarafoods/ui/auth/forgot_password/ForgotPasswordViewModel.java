package com.ei.kalavarafoods.ui.auth.forgot_password;

import androidx.lifecycle.ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordViewModel extends ViewModel {

    public void forgotPassword(String number){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
