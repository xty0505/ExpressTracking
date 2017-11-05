package com.njupt.a4081.expresstracking;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.ls.LSException;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hsu on 2017/11/2.
 */

public class HistorySearching extends AppCompatActivity {

    private Map<String, String> c2nMap = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_display);

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
        ListView lv = (ListView)findViewById(R.id.history_display_list_view);
        final List<Map<String, String>> HistoryMap = RegisterListView(HistorySearching.this,c2nMap);
        Log.e("test", HistoryMap.toString());
        MyAdapter myAdapter = new MyAdapter(HistorySearching.this, HistoryMap);
        lv.setAdapter(myAdapter);
        //绑定Menu
        registerForContextMenu(lv);


        // 点击intenet
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent iDisplayResult = new Intent(HistorySearching.this, DisplayResult.class);

                Bundle mBundle = new Bundle();
                mBundle.putString("ShipperCode", HistoryMap.get((int)id).get("ShipperCode"));
                mBundle.putString("ShipperName", HistoryMap.get((int)id).get("ShipperName"));
                mBundle.putString("LogisticCode", HistoryMap.get((int)id).get("LogisticCode"));
                iDisplayResult.putExtras(mBundle);
                startActivity(iDisplayResult);
            }
        });
    }

    //绑定ListView
    public List<Map<String, String>> RegisterListView(Context context, Map<String, String> c2nMap){
        SearchingHistoryDataHelper dh = new SearchingHistoryDataHelper(HistorySearching.this);
        List<String> HistoryData = new ArrayList<>();
        HistoryData.addAll(dh.DisplayHistory());
        final List<Map<String, String>> HistoryMap = new ArrayList<>();
        for (int i = 0; i < HistoryData.size(); i++){
            String HistoryItem = HistoryData.get(i);
            for (String j: c2nMap.keySet()){
                if (HistoryItem.indexOf(j) != -1){
                    Map<String, String> HistoryMapItem = new HashMap<>();
                    HistoryMapItem.put("ShipperCode", j);
                    HistoryMapItem.put("ShipperName", c2nMap.get(j));
                    HistoryMapItem.put("LogisticCode", HistoryItem.substring(0, HistoryItem
                            .indexOf(';')));
                    HistoryMapItem.put("Time", HistoryItem.substring(HistoryItem.lastIndexOf(';'
                    )));
                    HistoryMap.add(HistoryMapItem);
                }
            }
        }
        return HistoryMap;
    }

    //设置Menu选项
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int groupId = 0;
        //显示轨迹
        int displayCommand_id = 0;
        int displayCommand_order = 0;
        String displayCommand = "显示快递轨迹";
        menu.add(groupId,displayCommand_id,displayCommand_order,displayCommand);
        //删除
        int deleteCommand_id = 1;
        int deleteCommand_order = 1;
        String deleteCommand = "删除该记录";
        menu.add(groupId,deleteCommand_id,deleteCommand_order,deleteCommand);
    }

    //选项事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adaptInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int selectedPosition = adaptInfo.position;
        final ListView lv = (ListView)findViewById(R.id.history_display_list_view);
        final HashMap<String,String > map= (HashMap<String, String>) lv.getItemAtPosition(selectedPosition);
        switch (item.getItemId()){
            case 0:
                Intent iDisplayResult = new Intent(HistorySearching.this,DisplayResult.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("ShipperCode", map.get("ShipperCode"));
                mBundle.putString("ShipperName", map.get("ShipperName"));
                mBundle.putString("LogisticCode", map.get("LogisticCode"));
                iDisplayResult.putExtras(mBundle);
                startActivity(iDisplayResult);
                return true;
            case 1:
                //确认对话框
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setDialogClickListener(new ConfirmDialog.onDialogClickListener() {
                    @Override
                    public void onConfirmClick() {
                        try{
                            SearchingHistoryDataHelper dh =
                                    new SearchingHistoryDataHelper(HistorySearching.this);
                            dh.DeleteHistory(map.get("LogisticCode"));
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        lv.setAdapter(new MyAdapter(HistorySearching.this,
                                RegisterListView(HistorySearching.this,c2nMap)));
                        Toast.makeText(HistorySearching.this,
                                "你已经成功删除~",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelClick() {
                        //取消操作
                    }
                });
                confirmDialog.show(getFragmentManager(),"");
                Log.e("dialog",getFragmentManager().toString());
        }
        return false;
    }
}
