package com.traceandgigit.retrofit;

import android.content.Context;

public class APIService {
    private static APIService _self;

    public APIService() {
    }

    public static APIService getInstance() {
        if (_self == null) {
            _self = new APIService();
        }

        return _self;
    }

    public void execute(APIRequest apiRequest) {
        if (apiRequest != null) {
            apiRequest.execute(RetrofitClientInstance.getInstance());
        }

    }
}
