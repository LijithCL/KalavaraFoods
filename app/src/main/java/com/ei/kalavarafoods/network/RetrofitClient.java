package com.ei.kalavarafoods.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ULLAS BABU on 01-Feb-18.
 */

public class RetrofitClient {

    public static Retrofit getRetrofit(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("X-Api-Key", "extantmetro");
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        httpClient.addInterceptor(logging);
        OkHttpClient client = httpClient.build();
        return new Retrofit.Builder()
                .baseUrl("http://sh007.hostgator.tempwebhost.net/~j4jobaoz/KalavaraFoods/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getRxRetrofit(){
        return new Retrofit.Builder()
                .baseUrl("http://www.extantinfotech.com/KalavaraFoods/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static ApiInterface getApiInterface(){
        return getRetrofit().create(ApiInterface.class);
    }
}
