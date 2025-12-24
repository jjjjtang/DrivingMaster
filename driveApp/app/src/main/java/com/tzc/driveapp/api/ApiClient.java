package com.tzc.driveapp.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.tzc.driveapp.App;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://124.220.99.159:8081/";
    private static volatile Retrofit retrofit;


    public static void reset() {
        retrofit = null;
    }

    private static SharedPreferences getSharedPreferences() {
        Context context = App.getContext();
        return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null) {

                    // 添加日志拦截器
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(chain -> {
                                Request original = chain.request();

                                SharedPreferences sp = getSharedPreferences();
                                String token = sp.getString("token", null);

                                Request.Builder builder = original.newBuilder();
                                if (token != null && !token.isEmpty()) {
                                    builder.header("Authorization", token);
                                    System.out.println("DEBUG: 添加Authorization头:  " + token);
                                } else {
                                    System.out.println("DEBUG: token为空");
                                }

                                return chain.proceed(builder.build());
                            })
                            .addInterceptor(loggingInterceptor) // 添加日志
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }
}