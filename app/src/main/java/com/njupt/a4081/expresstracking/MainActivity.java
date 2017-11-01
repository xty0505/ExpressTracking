package com.njupt.a4081.expresstracking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SearchView query_searchView = (SearchView)findViewById(R.id.query_searchView);
        Button search_btn = (Button)findViewById(R.id.search_btn);
        final TextView result_txtView = (TextView)findViewById(R.id.result_textView);


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expNo = query_searchView.getQuery().toString();
                OrderDistinguish api = new OrderDistinguish();
                try {
                    String result = api.getOrderTracesByJson(expNo);
                    result_txtView.setText(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
