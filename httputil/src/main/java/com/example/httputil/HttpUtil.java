package com.example.httputil;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by LeiYang on 2016/8/24 0024.
 */

public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener httpCallbackListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL(address);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");

                    /*时间太短可能无法从服务器获取信息*/
                    httpURLConnection.setReadTimeout(8000);
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    StringBuilder stringBuilder = new StringBuilder();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String stringLine;
                    while((stringLine = bufferedReader.readLine())!=null) {
                        stringBuilder.append(stringLine);
                    }
                    Log.i("info",stringBuilder.toString());
                    if (httpCallbackListener!=null) {
                        httpCallbackListener.onFinish(stringBuilder.toString());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    if (httpCallbackListener != null) {
                        httpCallbackListener.onError();
                    }
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();

    }
}
