package com.traceandgigit.requests;

import android.app.Activity;
import com.traceandgigit.model.UserSignUp;
import com.traceandgigit.retrofit.APICallback;
import com.traceandgigit.retrofit.APIRequest;
import com.traceandgigit.retrofit.APIResponses;
import com.traceandgigit.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSignUpCall extends APIRequest {



    private Activity mActivity;
    private Call<UserSignUp> mUserSignUpCall;
    private Params mParams;

    public static class Params {
        String mobile;
        String password;
        String first;
        String last;
        String gender;
        String clientKey;
        public Params(String mobile, String password, String first, String last,String gender, String clientKey){
            this.mobile = mobile;
            this.password = password;
            this.first = first;
            this.last = last;
            this.gender = gender;
            this.clientKey = clientKey;
        }
    }
    public UserSignUpCall(Params params,APICallback mListener) {
        super(mListener);
        this.mParams = params;
    }

    @Override
    protected void execute(RetrofitClientInstance clientInstance) {

        mUserSignUpCall = RetrofitClientInstance.getInstance().mRetrofitInterface.userSignUp(mParams.mobile,
                mParams.password,
                mParams.first,
                mParams.last,
                mParams.gender,
                mParams.clientKey);


        mUserSignUpCall.enqueue(new Callback<UserSignUp>() {
            @Override
            public void onResponse(Call<UserSignUp> call, Response<UserSignUp> response) {
                APIResponses apiResponse = new APIResponses(response.body(),null);
                apiResponse.setSuccess(response.isSuccessful());
                if(null != response.body()){
                    apiResponse.setMessage(response.body().message);
                }
                UserSignUpCall.this.onResponse(apiResponse);
            }

            @Override
            public void onFailure(Call<UserSignUp> call, Throwable t) {

            }
        });

    }
}
