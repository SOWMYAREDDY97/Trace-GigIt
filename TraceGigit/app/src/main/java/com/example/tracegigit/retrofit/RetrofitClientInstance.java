package com.example.tracegigit.retrofit;

import com.traceandgigit.model.BaseResponseData;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public static String BASE_SCHEME = "http://";
    public static String BASE_HOST = "10.28.107.17:8000";


    //Returns the Retrofit Instance. This instance will handle all the API Calls,
    // their request Queuing and Response
    //Its a Singleton Method


    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_SCHEME + BASE_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    //Returns Retrofit Interface. An Interface where all the API Calls will be Configured
    public static retrofitInterface getRetrofitInterface(){
        return getRetrofitInstance().create(retrofitInterface.class);
    }

    private interface  retrofitInterface {

        @FormUrlEncoded
        @POST("/user/v1/registerDevice")
        Call<BaseResponseData> registerDevice(@Field("serialNo") String serialNo,
                                              @Field("os") String os,
                                              @Field("osVersion") String osVersion,
                                              @Field("make") String make,
                                              @Field("model") String model,
                                              @Field("resolution") String resolution,
                                              @Field("profile") String profile,
                                              @Field("clientSecret") String clientSecret);


    }
}
