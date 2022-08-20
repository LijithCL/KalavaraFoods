package com.ei.kalavarafoods.network;

import com.ei.kalavarafoods.model.api.AddressItem;
import com.ei.kalavarafoods.model.api.BrandItem;
import com.ei.kalavarafoods.model.api.BrandProductsList;
import com.ei.kalavarafoods.model.api.CategoryList;
import com.ei.kalavarafoods.model.api.OrderDetailItem;
import com.ei.kalavarafoods.model.api.OrderListItem;
import com.ei.kalavarafoods.model.api.PostResult;
import com.ei.kalavarafoods.model.api.ProductItem;
import com.ei.kalavarafoods.model.api.SearchResult;
import com.ei.kalavarafoods.model.api.SignUpLoginPostResult;
import com.ei.kalavarafoods.model.api.SliderData;
import com.ei.kalavarafoods.model.api.UserOrdersItem;
import com.ei.kalavarafoods.ui.address.model.AddressResponse;
import com.ei.kalavarafoods.ui.number_verification.model.OtpResponse;
import com.ei.kalavarafoods.ui.select_location.model.LocationResponse;
import com.google.gson.JsonObject;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by ULLAS BABU on 01-Feb-18.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("usermob/signup")
    Call<SignUpLoginPostResult> signUpUser(@Field("str") String str);

    @FormUrlEncoded
    @POST("usermob/fbsignup")
    Call<SignUpLoginPostResult> signUpUserSocial(@Field("str") String str);

    @FormUrlEncoded
    @POST("usermob/signin")
    Call<SignUpLoginPostResult> loginUser(@Field("str") String str);

    @FormUrlEncoded
    @POST("usermob/forgetpassword")
    Call<JsonObject> forgotPassword(@Field("str") String str);

    @GET("shopmob/home_categories")
    Call<CategoryList> getCategories();

    @FormUrlEncoded
    @POST("shopmob/listaddress")
    Call<AddressItem> getUserAddresses(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/deleteaddress")
    Call<Void> deleteAddress(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/addaddress")
    Call<AddressResponse> addAddress(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/editaddress")
    Call<AddressResponse> editAddress(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/historyshopping")
    Call<UserOrdersItem> getUserOrder(@Field("str") String str);

    @GET("shopmob/maincategoryproducts")
    Call<ProductItem> getProducts(@QueryMap Map<String, String> params);

    @GET("shopmob/home_sliders")
    Call<SliderData> getSliderData();

    @GET("shopmob/userorder")
    Call<OrderListItem> getAdminOrderList();

    @GET("shopmob/brandlist")
    Call<BrandItem> getBrands();

    @FormUrlEncoded
    @POST("shopmob/searchbrand")
    Call<BrandProductsList> getBrandProduct(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/userorderitems")
    Call<OrderDetailItem> getOrderDetails(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/orderstatusupdate")
    Call<Void> updateOrderStatus(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/removemywish")
    Call<PostResult> removeWish(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/addmywish")
    Call<PostResult> addWish(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/search")
    Call<SearchResult> searchProducts(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/search")
    Observable<SearchResult> rxSearchProducts(@Field("str") String str);

    @FormUrlEncoded
    @POST("shopmob/proceedcheckout")
    Call<PostResult> placeOrder(@Field("str") String str);

    @GET("shopmob/mywishtlist")
    Call<ProductItem> getWishList(@Query("id") String userId);

    @GET("shopmob/delivery_locations")
    Call<LocationResponse> getDeliveryLocations();

    @FormUrlEncoded
    @POST("usermob/otpmobile")
    Call<OtpResponse> getOtpMobile(@Field("str") String str);
}
