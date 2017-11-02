package com.njupt.a4081.expresstracking;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hsu on 2017/11/2.
 */

public class HistorySearching extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_display);
        Map<String, String> c2nMap = new HashMap<>();

        // CompanyCode to Name
        try{
            Resources comp_code = getResources();
            InputStream comp_code_file = comp_code.openRawResource(R.raw.comp_code);
            byte[] reader = new byte[comp_code_file.available()];
            if (comp_code_file.read(reader)!=-1){
                List<String> c2nData = Arrays.asList(new String(reader).split("\r\n"));
                for (String c2nItem: c2nData){
                    List<String> c2n = Arrays.asList(c2nItem.split(";"));
                    c2nMap.put(c2n.get(1), c2n.get(0));
                }

            }
            comp_code_file.close();
        }
        catch (IOException e){
            Log.e("test", e.toString());
        }

        // 绑定ListView
        SearchingHistoryDataHelper dh = new SearchingHistoryDataHelper(HistorySearching.this);
        ListView lv = (ListView)findViewById(R.id.history_display_list_view);
        List<String> HistoryData = new ArrayList<>();
        HistoryData.addAll(dh.DisplayHistory());
        final List<Map<String, String>> HistoryMap = new ArrayList<>();
        for (int i = 0; i < HistoryData.size(); i++){
            String HistoryItem = HistoryData.get(i);
            for (String j: c2nMap.keySet()){
                if (HistoryItem.indexOf(j) != -1){
                    Map<String, String> HistoryMapItem = new HashMap<>();
                    HistoryMapItem.put("ShippersCode", j);
                    HistoryMapItem.put("ShippersName", c2nMap.get(j));
                    HistoryMapItem.put("LogisticCode", HistoryItem.substring(0, HistoryItem
                            .indexOf(';')));
                    HistoryMapItem.put("Time", HistoryItem.substring(HistoryItem.lastIndexOf(';'
                    )));
                    HistoryMap.add(HistoryMapItem);
                }
            }
        }

        MyAdapter myAdapter = new MyAdapter(HistorySearching.this, HistoryMap);
        lv.setAdapter(myAdapter);


        // 点击intenet
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent iDisplayResult = new Intent(HistorySearching.this, DisplayResult.class);

                Bundle mBundle = new Bundle();
                mBundle.putString("ShippersCode", HistoryMap.get((int)id).get("ShippersCode"));
                mBundle.putString("ShippersName", HistoryMap.get((int)id).get("ShippersName"));
                mBundle.putString("LogisticCode", HistoryMap.get((int)id).get("LogisticCode"));
                iDisplayResult.putExtras(mBundle);
                startActivity(iDisplayResult);
            }
        });
    }
}
