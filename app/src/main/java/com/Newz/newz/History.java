package com.Newz.newz;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ViewStubCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Toolbar toolbar;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<HistoryHelper> listItems;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        swipeRefreshLayout=findViewById(R.id.swiperefreshlayout);
        recyclerView=(RecyclerView)findViewById(R.id.historyRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems=new ArrayList<>();
        ExtractData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter=new HistoryAdapter(listItems,getApplicationContext(),History.this);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    private void ExtractData() {
        String uniqueid=sharedPreferences.getString(getString(R.string.uniqueid), "");
        String ExtractDataUrl="https://www.datamanagement.ml/fetchHistory.php?uniqueid="+uniqueid;
        StringRequest stringRequest=new StringRequest(Request.Method.GET, ExtractDataUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {

                if(response!=null){

                    try{
                        int count=0;
                        JSONArray array=new JSONArray(response);
                        for (int i=0;i<array.length();i++){
                            JSONObject data=array.getJSONObject(i);
                            HistoryHelper item=new HistoryHelper(
                                    data.getString("id"),
                                    data.getString("uniqueid"),
                                    data.getString("date"),
                                    data.getString("pointsEarned"),
                                    data.getString("type")

                            );

                            listItems.add(item);
                            count++;
                            Log.d("Data1:",""+data.get("uniqueid"));
                        }
                        adapter=new HistoryAdapter(listItems,getApplicationContext(),History.this);
                        recyclerView.setAdapter(adapter);


                    }catch (Exception e){
                        StyleableToast.makeText(History.this,e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                    }

                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                StyleableToast.makeText(History.this,error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
            }
        });

        requestQueue.add(stringRequest);
    }
}