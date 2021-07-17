package com.Newz.newz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.efaso.admob_advanced_native_recyvlerview.AdmobNativeAdAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Bookmarks extends AppCompatActivity implements LifecycleObserver {

    Toolbar toolbar;
    private String ExtractDataUrl = "https://www.datamanagement.ml/showBookmarks.php";
    FloatingActionButton menu;
    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    SharedPreferences sharedPreferences;
    private List<News> listItems;
    private RequestQueue requestQueue;
    private InterstitialAd mInterstitialAd;

    SharedPreferences.Editor editor;
    FloatingActionButton timer;
    float xDown = 0, yDown = 0;
    CountDownTimer countDownTimer;
    long START_TIMER = 0000;
    boolean timerRunning;
    long time_left_in_milis = START_TIMER;

    private String TAG_SUCESS = "sucess";
    private String TAG_MESSAGE = "message";
    int sucess;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date date1 = new Date();
    final String currentDate = formatter.format(date1);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bookmarks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.bookmarkrecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        timer = findViewById(R.id.timer);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        ExtractData();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2184993334191556/3635056707");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        String timeleft = sharedPreferences.getString(getString(R.string.timeLeft), "0");
        long time_left_milis = Long.parseLong(timeleft);
        int min = (int) (time_left_milis / 1000) / 60;
        int sec = (int) (time_left_milis / 1000) % 60;
        String timeleftFormated = String.format(Locale.getDefault(), "%02d:%02d", min, sec);
      //  StyleableToast.makeText(this, "Time Remaining : " + timeleftFormated, Toast.LENGTH_LONG,R.style.successtoast).show();

        START_TIMER = time_left_milis;
        time_left_in_milis = START_TIMER;

        if ( sharedPreferences.getString(getString(R.string.taskflag5min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag10min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag15min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag20min), "").equals("true")
        ) {

            StyleableToast.makeText(this, "Task is on " + timeleftFormated, Toast.LENGTH_LONG,R.style.successtoast).show();

            timer.setVisibility(View.VISIBLE);
            Drawable d = timer.getDrawable();
            if (d instanceof Animatable) {
                ((Animatable) d).start();
            }

            if (timerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }

        }


        timer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //  Toast.makeText(Home.this, "timer", Toast.LENGTH_SHORT).show();
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        xDown = event.getX();
                        yDown = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        // Toast.makeText(Home.this, "timer", Toast.LENGTH_SHORT).show();
                        updateTime();
                        getTimeLeft();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float moveX, moveY;
                        moveX = event.getX();
                        moveY = event.getY();

                        float distanceX = moveX - xDown;
                        float distanceY = moveY - yDown;

                        timer.setX(timer.getX() + distanceX);
                        timer.setY(timer.getY() + distanceY);

                        //Toast.makeText(Home.this, ""+(timer.getX()+distanceX)+"\n"+(timer.getY()+distanceY), Toast.LENGTH_SHORT).show();

//                        if((timer.getX()+distanceX)<500 && (timer.getY()+distanceY)>1000){
//                            timer.setX(550);
//                            timer.setY(641);
//                           // Toast.makeText(Home.this, "do u want to end task", Toast.LENGTH_SHORT).show();
//                        }


                        break;

                }
                return true;
            }
        });


    }

    public void helpbox(){
        AlertDialog.Builder help = new AlertDialog.Builder(this);
        help.setTitle("Bookmarks Activity");
        help.setMessage("In this Activity You Can See your Saved News / Articles \n\nAnd Alongside you can complete your Ongoing Tasks !");
        help.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        help.create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.help_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                updateTime();
                Intent intent=new Intent(Bookmarks.this,Home.class);
                if(sharedPreferences.getString(getString(R.string.taskflag5min),"").equals("true")){
                    editor.putString(getString(R.string.taskflag5min),"true");
                    editor.commit();
                    intent.putExtra("timeleft",time_left_in_milis);
                }else if(sharedPreferences.getString(getString(R.string.taskflag10min),"").equals("true")){
                    editor.putString(getString(R.string.taskflag10min),"true");
                    editor.commit();
                    intent.putExtra("timeleft",time_left_in_milis);
                }else if(sharedPreferences.getString(getString(R.string.taskflag15min),"").equals("true")){
                    editor.putString(getString(R.string.taskflag15min),"true");
                    editor.commit();
                    intent.putExtra("timeleft",time_left_in_milis);
                }else if(sharedPreferences.getString(getString(R.string.taskflag20min),"").equals("true")){
                    editor.putString(getString(R.string.taskflag20min),"true");
                    editor.commit();
                    intent.putExtra("timeleft",time_left_in_milis);
                } else{
                    editor.putString(getString(R.string.taskflag5min),"false");
                    editor.commit();
                    editor.putString(getString(R.string.taskflag10min),"false");
                    editor.commit();
                    editor.putString(getString(R.string.taskflag15min),"false");
                    editor.commit();
                    editor.putString(getString(R.string.taskflag20min),"false");
                    editor.commit();
                }
                startActivity(intent);

                return true;

            case R.id.help:
                helpbox();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    void getTimeLeft() {
        String timeleft = sharedPreferences.getString(getString(R.string.timeLeft), "False");
        long time_left_in_milis = Long.parseLong(timeleft);
        int min = (int) (time_left_in_milis / 1000) / 60;
        int sec = (int) (time_left_in_milis / 1000) % 60;
        String timeleftFormated = String.format(Locale.getDefault(), "%02d:%02d", min, sec);
        StyleableToast.makeText(this, "Time Remaining : " + timeleftFormated, Toast.LENGTH_LONG,R.style.successtoast).show();

    }

    void startTimer() {
        countDownTimer = new CountDownTimer(time_left_in_milis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time_left_in_milis = millisUntilFinished;
                updateTime();
            }

            @Override
            public void onFinish() {
               // Toast.makeText(Bookmarks.this, "finished", Toast.LENGTH_SHORT).show();
                timerRunning = false;
                timer.setVisibility(View.GONE);

                if(sharedPreferences.getString(getString(R.string.taskflag5min),"").equals("true")) {
                    addPoints(getString(R.string.min5points),"5 Minute Task");
                    editor.putString(getString(R.string.taskflag5min), "false");
                    editor.commit();
                    StyleableToast.makeText(Bookmarks.this, "Task Completed Successfully ! \n Points Will Be Added Shortly", Toast.LENGTH_LONG,R.style.successtoast).show();
                }
                if(sharedPreferences.getString(getString(R.string.taskflag10min),"").equals("true")) {
                    addPoints(getString(R.string.min10points),"10 Minute Task");
                    editor.putString(getString(R.string.taskflag10min), "false");
                    editor.commit();
                    StyleableToast.makeText(Bookmarks.this, "Task Completed Successfully ! \n Points Will Be Added Shortly", Toast.LENGTH_LONG,R.style.successtoast).show();
                }
                if(sharedPreferences.getString(getString(R.string.taskflag15min),"").equals("true")) {
                    addPoints(getString(R.string.min15points),"15 Minute Task");
                    editor.putString(getString(R.string.taskflag15min), "false");
                    editor.commit();
                    StyleableToast.makeText(Bookmarks.this, "Task Completed Successfully ! \n Points Will Be Added Shortly", Toast.LENGTH_LONG,R.style.successtoast).show();
                }
                if(sharedPreferences.getString(getString(R.string.taskflag20min),"").equals("true")) {
                    addPoints(getString(R.string.min20points),"20 Minute Task");
                    editor.putString(getString(R.string.taskflag20min), "false");
                    editor.commit();
                    StyleableToast.makeText(Bookmarks.this, "Task Completed Successfully ! \n Points Will Be Added Shortly", Toast.LENGTH_LONG,R.style.successtoast).show();
                }
            }
        }.start();
        timerRunning = true;

    }

    void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
    }

    void resetTimer() {
        time_left_in_milis = START_TIMER;
        updateTime();
    }

    void updateTime() {
        int min = (int) (time_left_in_milis / 1000) / 60;
        int sec = (int) (time_left_in_milis / 1000) % 60;
        String timeleftFormated = String.format(Locale.getDefault(), "%02d:%02d", min, sec);

        String timeleft = "" + time_left_in_milis;
        editor.putString(getString(R.string.timeLeft), timeleft);
        editor.commit();

        //Toast.makeText(this, "Time Remaining : "+timeleftFormated, Toast.LENGTH_SHORT).show();
    }

    private void ExtractData() {
        try{
        final String email = sharedPreferences.getString(getString(R.string.uemail), "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ExtractDataUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        int count = 0;
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject data = array.getJSONObject(i);
                            News item = new News(
                                    data.getString("id"),
                                    data.getString("title"),
                                    data.getString("short_news"),
                                    data.getString("detail_news"),
                                    data.getString("news_link"),
                                    data.getString("original_article"),
                                    data.getString("category"),
                                    data.getString("photo"),
                                    data.getString("date"),
                                    data.getString("tags"),
                                    data.getString("writtenby")
                            );

                            listItems.add(item);
                            count++;
                            Log.d("Data1:", "" + data.get("title"));

                        }
                        adapter = new BookmarkAdapter(listItems, getApplicationContext(), Bookmarks.this);

                        AdmobNativeAdAdapter admobNativeAdAdapter= AdmobNativeAdAdapter.Builder
                                .with(
                                        "ca-app-pub-2184993334191556/3756331859",//Create a native ad id from admob console
                                        adapter,//The adapter you would normally set to your recyClerView
                                        "small"
                                )
                                .adItemIterval(2)//native ad repeating interval in the recyclerview
                                .build();

                        recyclerView.setAdapter(admobNativeAdAdapter);


                    } catch (Exception e) {
                        StyleableToast.makeText(Bookmarks.this, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();

                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                StyleableToast.makeText(Bookmarks.this, error.getMessage(), Toast.LENGTH_LONG,R.style.successtoast).show();
            }
        }) {
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("useremail", email);
                return params;
            }


        };
        requestQueue.add(stringRequest);
    }catch(Exception e){
        StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
    }
    }

    void addPoints(String points,String tasktype){
        try{
        Log.d("points1",points);
        Log.d("tasktype1",tasktype);
        String sendUrl="http://www.datamanagement.ml/simpleTaskReward.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    sucess = jobj.getInt(TAG_SUCESS);
                    if (sucess == 1) {
                        StyleableToast.makeText(Bookmarks.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();

                        }else {
                            Log.d("TAG", "The interstitial ad wasn't loaded yet.");
                        }
                    }
                    else {
                        StyleableToast.makeText(Bookmarks.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();

                    }
                } catch (JSONException e) {
                    StyleableToast.makeText(Bookmarks.this, e.getMessage(), Toast.LENGTH_LONG,R.style.successtoast).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                StyleableToast.makeText(Bookmarks.this, error.getMessage(), Toast.LENGTH_LONG,R.style.successtoast).show();
            }
        }) {
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uniqueid", getString(R.string.uniqueid));
                params.put("tasktype", tasktype);
                params.put("earnPoints", points);
                params.put("date", currentDate);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        requestQueue.add(request);
    }catch(Exception e){
        StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
    }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        Log.d("MyApp", "App in background");
        if(timerRunning){
            countDownTimer.cancel();
            timerRunning = false;
            timer.setVisibility(View.GONE);
            if(sharedPreferences.getString(getString(R.string.taskflag5min),"").equals("true")) {
                editor.putString(getString(R.string.taskflag5min), "false");
                editor.commit();
                // Toast.makeText(Home.this, "5 min task ended", Toast.LENGTH_SHORT).show();
            }
            if(sharedPreferences.getString(getString(R.string.taskflag10min),"").equals("true")) {
                editor.putString(getString(R.string.taskflag10min), "false");
                editor.commit();
                // Toast.makeText(Home.this, "10 min task ended", Toast.LENGTH_SHORT).show();
            }
            if(sharedPreferences.getString(getString(R.string.taskflag15min),"").equals("true")) {
                editor.putString(getString(R.string.taskflag15min), "false");
                editor.commit();
                // Toast.makeText(Home.this, "15 min task ended", Toast.LENGTH_SHORT).show();
            }
            if(sharedPreferences.getString(getString(R.string.taskflag20min),"").equals("true")) {
                editor.putString(getString(R.string.taskflag20min), "false");
                editor.commit();
                // Toast.makeText(Home.this, "20 min task ended", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        Log.d("MyApp", "App in foreground");
    }
}