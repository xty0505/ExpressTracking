package com.njupt.a4081.expresstracking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * Created by Hsu on 2017/11/2.
 */

public class NewSearching extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_searching);

        final SearchView query_searchView = (SearchView)findViewById(R.id.new_searching_query_searchView);
        Button search_btn = (Button)findViewById(R.id.new_searching_search_btn);
        final TextView result_txtView = (TextView)findViewById(R.id.new_searching_result_textView);


        // 设置SearchView 默认展开
        SearchView searchView = (SearchView) findViewById(R.id.new_searching_query_searchView);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

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
