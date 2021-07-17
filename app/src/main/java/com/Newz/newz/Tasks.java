package com.Newz.newz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Tasks extends AppCompatActivity {

    Toolbar toolbar;
    TextView min5, min10, min15, min20, stask,loading;
    private RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String uniqueid;
    private RewardedAd rewardedAd;
    private String sendUrl = "http://www.datamanagement.ml/task.php";
    int sucess;
    private String TAG_SUCESS = "sucess";
    private String TAG_MESSAGE = "message";
    private String tag_json_obj = "json_obj_req";
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    final String currentDate = formatter.format(date);
    int slag = 10;
    int agl = 10;
    int ag = 10;
    int g = 10;
    int ga= 10;
    int rewardcounter = 0;
    ProgressBar loadstask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        updaterewardcounter();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        getSupportActionBar().setTitle("Tasks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uniqueid=sharedPreferences.getString(getString(R.string.uniqueid), "");
       // Toast.makeText(this, ""+uniqueid, Toast.LENGTH_SHORT).show();
        min5 = findViewById(R.id.min5);
        min10 = findViewById(R.id.min10);
        min15 = findViewById(R.id.min15);
        min20 = findViewById(R.id.min20);
        stask = findViewById(R.id.stask);
        loading=findViewById(R.id.textView20);
        loadstask=findViewById(R.id.progressBart);
        rewardedAd = new RewardedAd(this,
                "ca-app-pub-2184993334191556/8817086846");


        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                loadstask.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                stask.setVisibility(View.VISIBLE);
             // StyleableToast.makeText(Tasks.this,"Ad Loaded Successfully", Toast.LENGTH_LONG,R.style.successtoast).show();
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
             //   StyleableToast.makeText(Tasks.this,"", Toast.LENGTH_LONG,R.style.warningtoast).show();
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        stask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rewardedAd.isLoaded() ) {
                    if (rewardcounter != 5) {
                    final Activity activityContext = Tasks.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                            rewardedAd = createAndLoadRewardedAd();
                        }
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            // User earned reward.
                            //Toast.makeText(activityContext, "You Earned 20 Points", Toast.LENGTH_SHORT).show();
                            try {
                                String sendUrl1 = "http://www.datamanagement.ml/updateReward.php";
                                StringRequest request = new StringRequest(Request.Method.POST, sendUrl1, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jobj = new JSONObject(response);
                                            sucess = jobj.getInt(TAG_SUCESS);
                                            if (sucess == 1) {
                                                updaterewardcounter();
                                                StyleableToast.makeText(Tasks.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG, R.style.successtoast).show();
                                            } else {
                                                StyleableToast.makeText(Tasks.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG, R.style.successtoast).show();
                                            }
                                        } catch (JSONException e) {
                                            StyleableToast.makeText(Tasks.this, "Some Error Occurred Please Try Again ! ", Toast.LENGTH_LONG, R.style.errortoast).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        StyleableToast.makeText(Tasks.this, "Some Error Occurred Please Try Again ! ", Toast.LENGTH_LONG, R.style.errortoast).show();
                                    }
                                }) {
                                    public Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("uniqueid", uniqueid);
                                        params.put("earnPoints", "5");
                                        params.put("type", "Special Task");
                                        params.put("date", currentDate);
                                        return params;
                                    }
                                };

                                request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
                                requestQueue.add(request);
                            } catch (Exception e) {
                                StyleableToast.makeText(activityContext, "Please Check Connectivity", Toast.LENGTH_SHORT, R.style.errortoast).show();
                            }
                        }


                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            // Ad failed to display.
                            // StyleableToast.makeText(Tasks.this, "" + adError.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();

                            // Toast.makeText(activityContext, "" + adError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    };
                    rewardedAd.show(activityContext, adCallback);
                }
                else{
                        StyleableToast.makeText(Tasks.this, "Your Daily Special task are completed please come again tomorrow !", Toast.LENGTH_LONG,R.style.errortoast).show();

                    }
                } else {
                    StyleableToast.makeText(Tasks.this, "The rewarded ad isn't loaded yet !", Toast.LENGTH_LONG,R.style.errortoast).show();

                }
            }
        });

//        min5.setAnimation(AnimationUtils.loadAnimation(this,R.anim.scale_out));
//        min10.setAnimation(AnimationUtils.loadAnimation(this,R.anim.scale_out));
//        min15.setAnimation(AnimationUtils.loadAnimation(this,R.anim.scale_out));
//        min20.setAnimation(AnimationUtils.loadAnimation(this,R.anim.scale_out));
        checkIfAlreadyClaimed();
        timeControl();

    }

    private void updaterewardcounter() {
        String counter1 = sharedPreferences.getString(getString(R.string.rewardCounter), "0");
        rewardcounter = Integer.parseInt(counter1);
        String date =   sharedPreferences.getString(getString(R.string.todaysdate), "");
        if(date.equals(currentDate)){
            rewardcounter ++;
        }
        else{
            rewardcounter = 0;
            editor.putString(getString(R.string.todaysdate),currentDate);
            editor.commit();
        }
    }

    public RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(this,
                "ca-app-pub-2184993334191556/8817086846");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
          //      Toast.makeText(Tasks.this, "Loaded Second Rewarded Add", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
    }


    private void checkIfAlreadyClaimed() {
        try{
        String ExtractDataUrl = "http://www.datamanagement.ml/taskFetch.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ExtractDataUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {

                if (response != null) {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject data = array.getJSONObject(i);
                            String date = data.getString("date");
                            Log.d("date1", date);
                            String taskType = data.getString("taskType");
                            Log.d("taskType", taskType);
                            String isComplete = data.getString("isComplete");
                            Log.d("isComplete", isComplete);
                            Log.d("currentdate", currentDate);
                            if ((currentDate.equals(date) && taskType.equals("5 Minute Task")) && (isComplete.equals("1"))) {
                                slag = 1;
                                //Toast.makeText(Tasks.this, "Inside If Loop", Toast.LENGTH_SHORT).show();
                                Log.d("flag10", "" + slag);

                            }
                            if ((currentDate.equals(date) && taskType.equals("10 Minute Task")) && isComplete.equals("1")) {
                                agl = 1;
                                Log.d("flag11", "" + agl);

                            }
                            if ((currentDate.equals(date) && taskType.equals("15 Minute Task")) && isComplete.equals("1")) {
                                ag = 1;
                                Log.d("flag12", "" + ag);
                            }
                            if ((currentDate.equals(date) && taskType.equals("20 Minute Task")) && isComplete.equals("1")) {
                                g = 1;

                            }
                        }
                        if (slag == 1) {
                            min5.setBackgroundColor(getColor(R.color.gray));
                        }
                        if (agl == 1) {
                            min10.setBackgroundColor(getColor(R.color.gray));
                        }
                        if (ag == 1) {
                            min15.setBackgroundColor(getColor(R.color.gray));
                        }
                        if (g == 1) {
                            min20.setBackgroundColor(getColor(R.color.gray));
                        }

                    } catch (Exception e) {

                        StyleableToast.makeText(Tasks.this, "Some Error Occurred Please Try Again ! ", Toast.LENGTH_LONG,R.style.errortoast).show();
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                StyleableToast.makeText(Tasks.this, error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
            }
        }) {
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uniqueid", uniqueid);
                params.put("date", currentDate);

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }catch(Exception e){
        StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
    }
    }


    public void timeControl() {
        min5.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                try{
                final String taskType = "5 Minute Task";
                StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobj = new JSONObject(response);
                            sucess = jobj.getInt(TAG_SUCESS);
                            if (sucess == 1) {
                                //enable timer code
                                Intent intent = new Intent(Tasks.this, Home.class);
                                editor.putString(getString(R.string.taskflag5min), "true");
                                editor.commit();
                                long starttime = 300000;
                                intent.putExtra("timeleft", starttime);
                                startActivity(intent);
                                //
                                finish();
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();


                            } else if (sucess == 2) {
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.warningtoast).show();
                            } else if (sucess == 3) {
                                Intent intent = new Intent(Tasks.this, Home.class);
                                editor.putString(getString(R.string.taskflag5min), "true");
                                editor.commit();
                                long starttime = 300000;
                                intent.putExtra("timeleft", starttime);
                                startActivity(intent);
                                finish();
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                            } else {
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.errortoast).show();

                            }
                        } catch (JSONException e) {
                            StyleableToast.makeText(Tasks.this,e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        StyleableToast.makeText(Tasks.this,"Some Error Occurred Please Try Again ! ", Toast.LENGTH_LONG,R.style.errortoast).show();

                    }
                }) {
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("uniqueid", uniqueid);
                        params.put("tasktype", taskType);
                        params.put("date", currentDate);
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
                requestQueue.add(request);
            }catch(Exception e){
                StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
            }
            }
        });

        min10.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                try{
                final String taskType = "10 Minute Task";
                StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobj = new JSONObject(response);
                            sucess = jobj.getInt(TAG_SUCESS);
                            if (sucess == 1) {

                                Intent intent = new Intent(Tasks.this, Home.class);
                                editor.putString(getString(R.string.taskflag10min), "true");
                                editor.commit();
                                long starttime = 600000;
                                intent.putExtra("timeleft", starttime);
                                startActivity(intent);

                                finish();
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                            } else if (sucess == 2) {
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.warningtoast).show();
                            }else if(sucess == 3){
                                Intent intent = new Intent(Tasks.this, Home.class);
                                editor.putString(getString(R.string.taskflag10min), "true");
                                editor.commit();
                                long starttime = 600000;
                                intent.putExtra("timeleft", starttime);
                                startActivity(intent);
                                finish();
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();

                            }
                            else {
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.errortoast).show();

                            }
                        } catch (JSONException e) {
                            StyleableToast.makeText(Tasks.this,"Some Error Occurred Please Try Again ! ", Toast.LENGTH_LONG,R.style.errortoast).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        StyleableToast.makeText(Tasks.this,"Some Error Occurred Please Try Again ! ", Toast.LENGTH_LONG,R.style.errortoast).show();
                    }
                }) {
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("uniqueid", uniqueid);
                        params.put("tasktype", taskType);
                        params.put("date", currentDate);
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
                requestQueue.add(request);
            }catch(Exception e){
                StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
            }

            }
        });

        min15.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                try{
                final String taskType = "15 Minute Task";
                StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobj = new JSONObject(response);
                            sucess = jobj.getInt(TAG_SUCESS);
                            if (sucess == 1) {

                                Intent intent = new Intent(Tasks.this, Home.class);
                                editor.putString(getString(R.string.taskflag15min), "true");
                                editor.commit();
                                long starttime = 900000;
                                intent.putExtra("timeleft", starttime);
                                startActivity(intent);
                                finish();
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                            } else if (sucess == 2) {
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.warningtoast).show();
                            }
                            else if (sucess == 3) {
                                Intent intent = new Intent(Tasks.this, Home.class);
                                editor.putString(getString(R.string.taskflag15min), "true");
                                editor.commit();
                                long starttime = 900000;
                                intent.putExtra("timeleft", starttime);
                                startActivity(intent);
                                finish();
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();

                            }else {
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.errortoast).show();

                            }
                        } catch (JSONException e) {
                            StyleableToast.makeText(Tasks.this,"Some Error Occurred Please Try Again ! ", Toast.LENGTH_LONG,R.style.errortoast).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        StyleableToast.makeText(Tasks.this,"Some Error Occurred Please Try Again ! ", Toast.LENGTH_LONG,R.style.errortoast).show();
                    }
                }) {
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("uniqueid", uniqueid);
                        params.put("tasktype", taskType);
                        params.put("date", currentDate);
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
                requestQueue.add(request);

            }catch(Exception e){
                StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
            }
            }
        });

        min20.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                try{
                final String taskType = "20 Minute Task";
                StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobj = new JSONObject(response);
                            sucess = jobj.getInt(TAG_SUCESS);
                            if (sucess == 1) {

                                Intent intent = new Intent(Tasks.this, Home.class);
                                editor.putString(getString(R.string.taskflag20min), "true");
                                editor.commit();
                                long time=600000+600000;
                                long starttime = time;
                                intent.putExtra("timeleft", starttime);
                                startActivity(intent);

                                finish();
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                            } else if (sucess == 2) {
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.warningtoast).show();
                            } else if (sucess == 3) {
                                Intent intent = new Intent(Tasks.this, Home.class);
                                editor.putString(getString(R.string.taskflag20min), "true");
                                editor.commit();
                                long time=600000+600000;
                                long starttime = time;
                                intent.putExtra("timeleft", starttime);
                                startActivity(intent);
                                finish();
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                            }
                            else {
                                StyleableToast.makeText(Tasks.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.errortoast).show();

                            }
                        } catch (JSONException e) {
                            StyleableToast.makeText(Tasks.this,e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        StyleableToast.makeText(Tasks.this,error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                    }
                }) {
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("uniqueid", uniqueid);
                        params.put("tasktype", taskType);
                        params.put("date", currentDate);
                        return params;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
                requestQueue.add(request);
            }catch(Exception e){
                StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
            }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.help_menu,menu);
        return true;

    }

    public void helpbox(){
        AlertDialog.Builder help = new AlertDialog.Builder(this);
        help.setTitle("Task Activity");
        help.setMessage("In this Activity You Can See your Daily & Special Tasks\n\n" +
                "Here You Can Perform Daily Tasks To Earn Points. You Can Only Perform daily task once in a Day \n\n" +
                "You Can Perform 5 Special Task Everyday");
        help.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        help.create().show();

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                helpbox();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}