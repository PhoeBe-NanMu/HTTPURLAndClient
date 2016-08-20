package com.example.leiyang.httpurlclient;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int SHOW_INFORMATION = 0;
    private Button getInformationButton;
    private TextView showInformationTextView;

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
                sendRequestWithHttpURLConnection();
            }
        });
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpurlconnection = null;
                try {
                    URL url = new URL("http://www.baidu.com");
                    httpurlconnection = (HttpURLConnection) url.openConnection();
                    httpurlconnection.setRequestMethod("GET");
                    httpurlconnection.setConnectTimeout(8000);
                    httpurlconnection.setReadTimeout(8000);
                    InputStream in = httpurlconnection.getInputStream();
                    InputStreamReader inReader = new InputStreamReader(in);
                    BufferedReader bufferReader = new BufferedReader(inReader);
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferReader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.i("info", response.toString());
                    Message message = new Message();
                    message.what = SHOW_INFORMATION;
                    message.obj = response.toString();

                    handler.sendMessage(message);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("info", "--------error");
                    e.printStackTrace();
                } finally {
                    if (httpurlconnection != null) {
                        httpurlconnection.disconnect();
                    }
                }
            }
        }).start();
    }
}
