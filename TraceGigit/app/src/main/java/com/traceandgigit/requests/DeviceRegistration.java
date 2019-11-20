package com.traceandgigit.requests;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;

import com.traceandgigit.model.DeviceRegData;
import com.traceandgigit.retrofit.APICallback;
import com.traceandgigit.retrofit.APIRequest;
import com.traceandgigit.retrofit.APIResponses;
import com.traceandgigit.retrofit.RetrofitClientInstance;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceRegistration extends APIRequest {


    private Activity mActivity;
    private Call<DeviceRegData> registerDeviceAPICall;

    public DeviceRegistration(Activity mActivity,APICallback mListener) {
        super(mListener);
        this.mActivity = mActivity;
    }

    @Override
    protected void execute(RetrofitClientInstance clientInstance) {
        final DisplayMetrics dm = new DisplayMetrics();
        (mActivity).getWindowManager().getDefaultDisplay().getMetrics(dm);

        final int height = dm.heightPixels;
        final int width = dm.widthPixels;
        String devRes= width +"x"+ height;
        String serialNo = null;
        serialNo =  UUID.randomUUID().toString();
        registerDeviceAPICall = RetrofitClientInstance.getInstance().mRetrofitInterface.registerDevice(serialNo,
                        "Android",
                        Build.VERSION.RELEASE,
                        Build.MANUFACTURER,
                        Build.MODEL,
                        devRes,
                        "user",
                        "traceandgigit");

        registerDeviceAPICall.enqueue(new Callback<DeviceRegData>() {
            @Override
            public void onResponse(Call<DeviceRegData> call, Response<DeviceRegData> response) {
                APIResponses apiResponse = new APIResponses(response.body(),null);
                apiResponse.setSuccess(response.isSuccessful());
                if(null != response.body()
                        && null != response.body().clientKey
                        && null != response.body().deviceId
                        && null != response.body().expiresAt){
                    apiResponse.setMessage(response.body().message);
                }
                DeviceRegistration.this.onResponse(apiResponse);
            }

            @Override
            public void onFailure(Call<DeviceRegData> call, Throwable t) {
                DeviceRegistration.this.onFailure(t, 500);
            }
        });
    }
}
