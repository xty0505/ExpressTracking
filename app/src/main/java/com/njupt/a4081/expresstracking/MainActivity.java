package com.njupt.a4081.expresstracking;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.StaticLayout;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SearchView query_searchView = (SearchView)findViewById(R.id.query_searchView);
        Button search_btn = (Button)findViewById(R.id.search_btn);
        final TextView result_txtView = (TextView)findViewById(R.id.result_textView);

        final Handler UIhandler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Bundle data = msg.getData();
                result_txtView.setText(data.getString("result"));
            }
        };
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String expNo = query_searchView.getQuery().toString();
                OrderDistinguish api = new OrderDistinguish();
                try {
                    String result = api.getOrderTracesByJson(expNo);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("result",result);
                    msg.setData(data);
                    UIhandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(runnable).start();
            }
        });


    }
}
