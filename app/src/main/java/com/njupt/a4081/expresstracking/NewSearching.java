package com.njupt.a4081.expresstracking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Hsu on 2017/11/2.
 */

public class NewSearching extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_searching);

        final SearchView query_searchView = (SearchView) findViewById(R.id.new_searching_query_searchView);
        Button search_btn = (Button) findViewById(R.id.new_searching_search_btn);
        final LinearLayout search_layout = (LinearLayout) findViewById(R.id.new_searching_linearLayout);
        final LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);


        // 设置SearchView 默认展开
        SearchView searchView = (SearchView) findViewById(R.id.new_searching_query_searchView);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        final Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                TextView textView = new TextView(NewSearching.this);
                search_layout.addView(textView,layoutParams);
                textView.setText("您输入的单号不存在哟~");
            }
        };

        final Runnable nt = new Runnable() {
            @Override
            public void run() {
                // 获取查询
                String expNo = query_searchView.getQuery().toString();
                OrderDistinguish api = new OrderDistinguish();
                try {
                    String result = api.getOrderTracesByJson(expNo);

                    // 获取companyCode和companyName
                    try {
                        JSONObject jso = new JSONObject(result);
                        Iterator it = jso.keys();
                        Bundle bd = new Bundle();

                        while (it.hasNext()) {
                            String key = (String) it.next();
                            Object value = jso.get(key);
                            if (key.equalsIgnoreCase("Success")) {
                                if (value.toString().equalsIgnoreCase("false")) {
                                    // Print error log
                                    Message msg = new Message();
                                    bd.putString("Success","false");
                                    msg.setData(bd);
                                    handler.sendMessage(msg);
                                    break;
                                }
                            }
                            else if (key.equalsIgnoreCase("LogisticCode")){
                                bd.putString(key, value.toString());
                            }
                            else if (key.equalsIgnoreCase("Shippers")) {
                                try {
                                    Intent iDisplay = new Intent(NewSearching.this, DisplayResult
                                            .class);

                                    JSONArray jsonArray = jso.getJSONArray("Shippers");
                                    //单号不存在但Success=true
                                    if(jsonArray.length() == 0){
                                        Message msg = new Message();
                                        bd.putString("Success","false");
                                        msg.setData(bd);
                                        handler.sendMessage(msg);
                                        break;
                                    }
                                    for(int i =0;i < jsonArray.length();i++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        Iterator It = jsonObject.keys();
                                        while (It.hasNext()) {
                                            String Key = (String) It.next();
                                            Object Value = jsonObject.get(Key);
                                            bd.putString(Key, Value.toString());
                                        }
                                    }
                                    iDisplay.putExtras(bd);
                                    startActivity(iDisplay);

                                    // Save into sql
                                    SearchingHistoryDataHelper dh = new
                                            SearchingHistoryDataHelper(NewSearching.this);
                                    dh.InsertHistory(expNo, bd.getString("ShipperCode"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(nt).start();
            }
        });

    }
}
