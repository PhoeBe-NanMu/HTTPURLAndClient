package com.example.leiyang.httpurlclient;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                    URL url = new URL("http://10.0.2.2/test2.xml");
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

                    parseXMLWithPULL(response.toString());

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

    private void parseXMLWithPULL(String response) {
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
                        if ("id".equals(nodeName)) {
                            id = xmlPullParser.nextText();

                        }
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
