package com.example.httputil;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {


    private static final int SHOW_INFORMATION = 0;
    private Button getInformationButton;
    private TextView showInformationTextView;

    /*新建Handler对象handler，重写handMessage(message)方法，对sendMessage(message)传递的参数进行处理*/
private Handler handler = new Handler() {
    public void handleMessage(Message message) {
        super.handleMessage(message);
        switch (message.what) {
            case SHOW_INFORMATION:
                String response = (String) message.obj;
                showInformationTextView.setText(response);
                break;
            default:
                break;
        }
    }
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getInformationButton = (Button) findViewById(R.id.getInformation);
        showInformationTextView = (TextView) findViewById(R.id.showInformation);
        getInformationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

//                String address = "http://www.baidu.com";
                String address = "http://trade.500.com/static/public/ssc/xml/newlyopenlist.xml";
//                String address = "http://111.8.49.214:8080/";
                Log.i("info",address.toString());
                HttpUtil.sendHttpRequest(address.toString(), new HttpCallbackListener() {
                    @Override
                    public void onFinish(final String s) {
                        Log.i("info",s);

                        Log.i("info","---->onFinish()--1");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = SHOW_INFORMATION;

                                /*是message.obj = s.toString(),而不是message.obj = s*/
                                message.obj = s.toString();

                                /*一定要注意是sendMessage(),而不是handMessage()*/
                                handler.sendMessage(message);
                                Log.i("info","---->onFinish()");
                            }
                        }).start();

                    }

                    @Override
                    public void onError() {
                        Log.i("info","---->onError()");
                    }
                });
            }
        });
    }
}
