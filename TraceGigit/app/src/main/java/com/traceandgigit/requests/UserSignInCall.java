package com.traceandgigit.requests;

import android.app.Activity;

import com.traceandgigit.model.UserSignIn;
import com.traceandgigit.model.UserSignUp;
import com.traceandgigit.retrofit.APICallback;
import com.traceandgigit.retrofit.APIRequest;
import com.traceandgigit.retrofit.APIResponses;
import com.traceandgigit.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSignInCall extends APIRequest {



    private Activity mActivity;
    private Call<UserSignIn> mUserSignInCall;
    private Params mParams;

    public static class Params {
        String mobile;
        String password;
        String clientKey;
        public Params(String mobile, String password, String clientKey){
            this.mobile = mobile;
            this.password = password;
            this.clientKey = clientKey;
        }
    }
    public UserSignInCall(Params params, APICallback mListener) {
        super(mListener);
        this.mParams = params;
    }

    @Override
    protected void execute(RetrofitClientInstance clientInstance) {

        mUserSignInCall = RetrofitClientInstance.getInstance().mRetrofitInterface.userSignIn(mParams.mobile,
                mParams.password,
                mParams.clientKey);


        mUserSignInCall.enqueue(new Callback<UserSignIn>() {
            @Override
            public void onResponse(Call<UserSignIn> call, Response<UserSignIn> response) {
                APIResponses apiResponse = new APIResponses(response.body(),null);
                apiResponse.setSuccess(response.isSuccessful());
                if(null != response.body()){
                    apiResponse.setMessage(response.body().message);
                }
                UserSignInCall.this.onResponse(apiResponse);
            }

            @Override
            public void onFailure(Call<UserSignIn> call, Throwable t) {

            }
        });

    }
}