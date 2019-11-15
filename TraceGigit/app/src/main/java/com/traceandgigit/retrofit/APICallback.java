package com.traceandgigit.retrofit;

public interface APICallback<T> {

    /** Successful HTTP response. */
    void onResponse(APIResponses<T> response);

    /** Invoked when a network or unexpected exception occurred during the HTTP request. */
    void onFailure(Throwable t, int errorCode);
}
