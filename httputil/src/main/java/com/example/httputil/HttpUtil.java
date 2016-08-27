package com.example.httputil;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by LeiYang on 2016/8/24 0024.
 */

public class HttpUtil {

    public static void sendHttpRequest(final String address, final HttpCallbackListener httpCallbackListener) {

        /*非主线程中无法使用Toast，Looper.prepare();Looper.loop();将Toast包裹起来*/
        Toast.makeText(MyApplication.getContext(), "This is MyApplication.getContext() --> Context", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL(address);
                    Log.i("info",url.toString());
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");

//                    Log.i("info","------------------" + MyApplication.getContext());
                    /*时间太短可能无法从服务器获取信息*/
                    httpURLConnection.setReadTimeout(10*1000);
                    httpURLConnection.setConnectTimeout(10*1000);
                    httpURLConnection.setDoInput(true);

                    /*不可以设置setDoInput为true，因为目标xml文件不可写
                    * 如果设置-- httpURLConnection.setDoInput(true) --会导致 -- httpURLConnection.getInputStream()出错 无法获取InputStream--*/
//                    httpURLConnection.setDoOutput(true);
                    StringBuilder stringBuilder = new StringBuilder();
                    if (httpURLConnection.getResponseCode() == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String stringLine;
                        while((stringLine = bufferedReader.readLine())!=null) {
                            stringBuilder.append(stringLine);
                        }

                        parseXMLWithPULL(stringBuilder.toString());

                    } else {
                        Log.i("info","cannot getInputStream");
                    }
                    
                    /*测试全局获取Context*/
//                    Looper.prepare();
//                    Toast.makeText(MyApplication.getContext(), "This is MyApplication.getContext() --> Context", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
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
    private static void parseXMLWithPULL(String response) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(response));
            int eventType = xmlPullParser.getEventType();
            String id = "";
            String name = "";
            String version = "";
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch(eventType) {
                    //开始解析某个节点
                    case XmlPullParser.START_TAG:
                        if ("row".equals(nodeName)) {
                            id = xmlPullParser.nextText();
                        } else if ("xml".equals(nodeName)) {
                            name = xmlPullParser.nextText();
                        } else if ("version".equals(nodeName)) {
                            version = xmlPullParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("app".equals(nodeName)) {
                            Log.i("info","id is "+id);
                            Log.i("info","name is "+name);
                            Log.i("info","version is "+version);
                        }
                        break;
                    default:break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            Log.i("info","XmlPullParserException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("info","IOException");
            e.printStackTrace();
        }
    }
}
