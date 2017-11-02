package com.njupt.a4081.expresstracking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

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


        // 设置SearchView 默认展开
        SearchView searchView = (SearchView) findViewById(R.id.new_searching_query_searchView);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取查询
                String expNo = query_searchView.getQuery().toString();
                OrderDistinguish api = new OrderDistinguish();
                try {
                    String result = api.getOrderTracesByJson(expNo);

                    // 获取companyCode和companyName
                    try {
                        JSONObject jso = new JSONObject(result);
                        Iterator it = jso.keys();
                        while (it.hasNext()) {
                            String key = (String) it.next();
                            Object value = jso.get(key);
                            if (key.equalsIgnoreCase("Success")){
                                if (value.toString().equalsIgnoreCase("false")){
                                    // Print error log
                                    break;
                                }
                            }
                            if (key.equalsIgnoreCase("Shippers")) {
                                try {
                                    Bundle bd = new Bundle();
                                    Intent iDisplay = new Intent(NewSearching.this, DisplayResult
                                            .class);

                                    JSONObject Jso = new JSONObject(value.toString().substring(1,
                                            value.toString().length() - 1));
                                    Iterator It = Jso.keys();
                                    while (It.hasNext()) {
                                        String Key = (String) It.next();
                                        Object Value = Jso.get(Key);
                                        bd.putString(Key, Value.toString());
                                    }
                                    iDisplay.putExtra("result", bd);
                                    startActivity(iDisplay);

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
        });

    }
}
