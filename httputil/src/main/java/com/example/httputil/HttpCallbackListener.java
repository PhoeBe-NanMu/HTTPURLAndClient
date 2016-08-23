package com.example.httputil;

/**
 * Created by LeiYang on 2016/8/24 0024.
 */

public interface HttpCallbackListener {
    void onFinish(final String s);
    void onError();
}
