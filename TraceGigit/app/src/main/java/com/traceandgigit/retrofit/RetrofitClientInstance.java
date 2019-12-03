package com.traceandgigit.retrofit;

import com.traceandgigit.AppConstants;
import com.traceandgigit.model.DeviceRegData;
import com.traceandgigit.model.UserSignIn;
import com.traceandgigit.model.UserSignUp;
import com.traceandgigit.requests.SharedUtils;

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
    public static String BASE_HOST = "127.0.0.1:8000";
    public retrofitInterface mRetrofitInterface;
    public static RetrofitClientInstance _self;

    private RetrofitClientInstance(){
        init();
    }

    //Returns the Retrofit Instance. This instance will handle all the API Calls,
    // their request Queuing and Response
    //Its a Singleton Method

    public static RetrofitClientInstance getInstance() {
        if (_self == null || AppConstants.DID_API_CHANGED) {
            _self = new RetrofitClientInstance();
        }
        return _self;
    }

    private void init(){
        if (retrofit == null || AppConstants.DID_API_CHANGED) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_SCHEME + BASE_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            AppConstants.DID_API_CHANGED = false;
        }
        mRetrofitInterface = retrofit.create(retrofitInterface.class);
    }

    public interface  retrofitInterface {

        @FormUrlEncoded
        @POST("/user/v1/registerDevice")
        Call<DeviceRegData> registerDevice(@Field("serialNo") String serialNo,
                                           @Field("os") String os,
                                           @Field("osVersion") String osVersion,
                                           @Field("make") String make,
                                           @Field("model") String model,
                                           @Field("resolution") String resolution,
                                           @Field("profile") String profile,
                                           @Field("clientSecret") String clientSecret);

        @FormUrlEncoded
        @POST("user/v1/signUp")
        Call<UserSignUp> userSignUp(@Field("email") String mobile,
                                    @Field("password") String password,
                                    @Field("first") String first,
                                    @Field("clientKey") String clientKey);

        @FormUrlEncoded
        @POST("user/v1/signIn")
        Call<UserSignIn> userSignIn(@Field("email") String mobile,
                                    @Field("password") String password,
                                    @Field("clientKey") String clientKey);

    }
}
