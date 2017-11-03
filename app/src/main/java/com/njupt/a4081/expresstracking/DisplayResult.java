package com.njupt.a4081.expresstracking;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.TransformerException;

/**
 * Created by Hsu on 2017/11/2.
 */

public class DisplayResult extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.express_info);

        //初始化控件
        TextView expNo_textView = (TextView)findViewById(R.id.express_info_num_display);
        TextView expName_textView = (TextView)findViewById(R.id.express_info_comp_display);
        final LinearLayout expTraces_layout = (LinearLayout)findViewById(R.id.express_info_tracking_info_layout);
        final LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final ScrollView expTraces_scrollView = (ScrollView)findViewById(R.id.express_info_tracking_info_display);
        final Button expTracking_btn = (Button)findViewById(R.id.express_info_tracking_btn);

        final Intent iReceive = getIntent();
        final Bundle bd = iReceive.getExtras();
        final String expNo = bd.getString("LogisticCode");
        String expName = bd.getString("ShipperName");
        final String expCode = bd.getString("ShipperCode");

        expNo_textView.setText(expNo);
        expName_textView.setText(expName);

        //物流信息显示
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();

                //更改UI
                if(bundle.getString("Traces").equalsIgnoreCase("no")){
                    Resources pic = getResources();
                    Drawable drawable = pic.getDrawable(R.drawable.notrace);
                    expTraces_scrollView.setBackground(drawable);
                    expTracking_btn.setVisibility(View.INVISIBLE);
                }
                else{
                    for(int i =0;i < bundle.size()/2;i++){
                        StringBuffer trace = new StringBuffer();
                        TextView txtView = new TextView(DisplayResult.this);
                        expTraces_layout.addView(txtView,layoutParams);
                        trace.append(bundle.getString("AcceptTime" + i));
                        trace.append(":");
                        trace.append(bundle.getString("AcceptStation" + i));
                        txtView.setText(trace);
                    }
                }

            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                OrderDistinguish api = new OrderDistinguish();

                //传消息给UI线程更新UI
                Message msg = new Message();
                Bundle bundle = new Bundle();

                try {
                    //获取物流轨迹,根据时间划分
                    String traces = api.getOrderTracesByJson(expCode,expNo);
                    StringBuffer result = new StringBuffer();
                    result.append(traces);
                    result.append("}");

                    Log.e("trace",result.toString());
                    try{
                        JSONObject jso = new JSONObject(result.toString());
                        Iterator iterator = jso.keys();
                        Map<String, String> map = new HashMap<String, String>();


                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Object value = jso.get(key);

                            //判断是否有轨迹
                            if (key.equalsIgnoreCase("State")) {
                                if (value.toString().equalsIgnoreCase("0")) {
                                    //无轨迹处理
                                    bundle.putString("Traces","no");
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                    break;
                                }
                                else {
                                    //有轨迹传值
                                    bundle.putString("Traces","yes");
                                    //获取AcceptTime和AcceptStation
                                    try {
                                        JSONArray jsonArray = jso.getJSONArray("Traces");
                                        Log.e("1", "1");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            Iterator iterator1 = jsonObject.keys();
                                            while (iterator1.hasNext()) {
                                                String Key = (String) iterator1.next();
                                                Object Value = jsonObject.get(Key);
                                                map.put(Key + i, Value.toString());
                                                Log.e("map", Key + i + ":" + Value.toString());
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //Map键值对转换到bundle
                                    for (Map.Entry<String, String> entry : map.entrySet()) {
                                        bundle.putString(entry.getKey(), entry.getValue());
                                        Log.e("bundle", entry.getKey() + ":" + bundle.getString(entry.getKey()));
                                    }
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
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
        new Thread(runnable).start();
    }

}
