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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.efaso.admob_advanced_native_recyvlerview.AdmobNativeAdAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Home extends AppCompatActivity implements LifecycleObserver {
    public static final int ITEM_PER_AD=4;
    private static final String BANNER_AD_ID="ca-app-pub-2184993334191556/2560852023";
    Toolbar toolbar;
    int sucess;
    private int totalItemCOunt;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int page=1;
    private InterstitialAd mInterstitialAd;
    private  int previosTotal;
    private boolean load=true;
    ProgressBar progressBar;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    final String currentDate = formatter.format(date);
    private LinearLayoutManager linearLayoutManager;
    private RequestQueue requestQueue;
    private String TAG_SUCESS = "sucess";
    private String TAG_MESSAGE = "message";
    private String tag_json_obj = "json_obj_req";
    private RecyclerView recyclerView,tagsview;
    private Newsadapter adapter;
    private List<News> listItems;
    private List<Tags> tagItems;
    String email2;
    String uniqueid;

    FloatingActionButton menu,timer;
    SwipeRefreshLayout swipeRefreshLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    String[]  tags;
    float xDown = 0, yDown = 0;
    long START_TIMER = 0000;
    CountDownTimer countDownTimer;
    boolean timerRunning;
    long time_left_in_milis = START_TIMER;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String sendUrl = "http://www.datamanagement.ml/task.php";
   // NotificationHelper notificationHelper;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        Log.d("currentdate",currentDate);
        swipeRefreshLayout=findViewById(R.id.swiperefreshlayout);
        shimmerFrameLayout=findViewById(R.id.shimmerlayout);
        tagsview=findViewById(R.id.tagsview);
        timer = findViewById(R.id.timer);
        tagsview.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        ImageButton profic=findViewById(R.id.profic);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2184993334191556/3635056707");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if(!restorePrefData()) {
            BonusDialog bonusDialog = new BonusDialog(Home.this);
            bonusDialog.startLoadingDialog();
            savePrefsData();
        }

        if (sharedPreferences.getString(getString(R.string.taskflag5min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag10min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag15min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag20min), "").equals("true")
        ) {
            timer.setVisibility(View.VISIBLE);
            Intent i = getIntent();
            long timeleftmil = i.getLongExtra("timeleft", 0000);
            START_TIMER = timeleftmil;
            time_left_in_milis = START_TIMER;
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
        createNotificationChannel1();
        createNotificationChannel2();
       // notificationHelper=new NotificationHelper(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               Intent intent=new Intent(Home.this,RemindNotification.class);
                PendingIntent pendingIntent=PendingIntent.getBroadcast(Home.this,0,intent,0);
                AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
                long currtime=System.currentTimeMillis();
                long timer=10800000;

                alarmManager.set(AlarmManager.RTC_WAKEUP,currtime+timer,pendingIntent);
//                NotificationCompat.Builder nb=notificationHelper.getChannel1Notification("New Task are Available Checkout","Complete New Tasks !");
//                notificationHelper.getManager().notify(1,nb.build());

            }
        }, 2000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent=new Intent(Home.this,MorningNotification.class);
                PendingIntent pendingIntent=PendingIntent.getBroadcast(Home.this,0,intent,0);
                AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
                long currtime=System.currentTimeMillis();
                long timer=86400000;

                alarmManager.set(AlarmManager.RTC_WAKEUP,currtime+timer,pendingIntent);
//                Calendar calendar=Calendar.getInstance();
//
//                calendar.set(Calendar.HOUR_OF_DAY,9);
//                calendar.set(Calendar.MINUTE,10);
//
//                if(calendar.before(Calendar.getInstance())){
//                    calendar.add(Calendar.DATE,1);
//                }
//
//                Intent intent=new Intent(Home.this,MorningNotification.class);
//                PendingIntent pendingIntent=PendingIntent.getBroadcast(Home.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//                AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
//                long remindtime=System.currentTimeMillis();
//                long timer=1000*10;
//                alarmManager.set(AlarmManager.RTC_WAKEUP,remindtime+timer,pendingIntent);
//                NotificationCompat.Builder nb=notificationHelper.getChannel1Notification("New Task are Available Checkout","Complete New Tasks !");
//                notificationHelper.getManager().notify(1,nb.build());

            }
        }, 2000);
        try {
            profic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedPreferences.getString(getString(R.string.taskflag5min), "").equals("true") ||
                            sharedPreferences.getString(getString(R.string.taskflag10min), "").equals("true") ||
                            sharedPreferences.getString(getString(R.string.taskflag15min), "").equals("true") ||
                            sharedPreferences.getString(getString(R.string.taskflag20min), "").equals("true")
                    ) {

                        AlertDialog.Builder passwordreset = new AlertDialog.Builder(v.getContext());
                        passwordreset.setTitle("Cancel Task ?");
                        passwordreset.setMessage("If you switched to another activity your ongoing task will be finished !");
                        passwordreset.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putString(getString(R.string.taskflag5min), "false");
                                editor.commit();
                                editor.putString(getString(R.string.taskflag10min), "false");
                                editor.commit();
                                editor.putString(getString(R.string.taskflag15min), "false");
                                editor.commit();
                                editor.putString(getString(R.string.taskflag20min), "false");
                                editor.commit();
                                timer.setVisibility(View.GONE);
                                startActivity(new Intent(Home.this, Profile.class));
                            }
                        });
                        passwordreset.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        passwordreset.create().show();

                    }else{
                        startActivity(new Intent(Home.this, Profile.class));
                    }
                }
            });
        }catch (Exception e){
            StyleableToast.makeText(Home.this,e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();

        }

        menu=findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlurrFragment blurrFragment =new BlurrFragment();
                blurrFragment.show(getSupportFragmentManager(),blurrFragment.getClass().getSimpleName());
            }
        });
        progressBar=findViewById(R.id.progress_bar);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView=(RecyclerView)findViewById(R.id.newsrecycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        listItems=new ArrayList<>();
        tagItems=new ArrayList<>();
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        adapter=new Newsadapter(listItems,getApplicationContext(),Home.this);

        AdmobNativeAdAdapter admobNativeAdAdapter= AdmobNativeAdAdapter.Builder
                .with(
                        "ca-app-pub-2184993334191556/3756331859",//Create a native ad id from admob console
                        adapter,//The adapter you would normally set to your recyClerView
                        "small"
                )
                .adItemIterval(2)//native ad repeating interval in the recyclerview
                .build();

        recyclerView.setAdapter(admobNativeAdAdapter);

        extracTags();

        ExtractData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ExtractData();
                adapter=new Newsadapter(listItems,getApplicationContext(),Home.this);

                AdmobNativeAdAdapter admobNativeAdAdapter= AdmobNativeAdAdapter.Builder
                        .with(
                                "ca-app-pub-2184993334191556/3756331859",//Create a native ad id from admob console
                                adapter,//The adapter you would normally set to your recyClerView
                                "small"
                        )
                        .adItemInterval(2)//native ad repeating interval in the recyclerview
                        .build();

                recyclerView.setAdapter(admobNativeAdAdapter);
                swipeRefreshLayout.setRefreshing(false);


            }
        });
        shimmerFrameLayout.startShimmer();


        getUniqueid();
        final TaskDialogBox taskDialogBox = new TaskDialogBox(Home.this, Home.this);
        final ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
//        if (sharedPreferences.getString(getString(R.string.taskflag5min), "").equals("false") &&
//                sharedPreferences.getString(getString(R.string.taskflag10min), "").equals("false") &&
//                sharedPreferences.getString(getString(R.string.taskflag15min), "").equals("false") &&
//                sharedPreferences.getString(getString(R.string.taskflag20min), "").equals("false")
//
//        ) {
//            timer.setVisibility(View.GONE);
//            new Handler().postDelayed(new Runnable() {
//                @RequiresApi(api = Build.VERSION_CODES.M)
//                @Override
//                public void run() {
//                    taskDialogBox.startLoadingDialog(view);
//                    taskDialogBox.min5.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try{
//                            final String taskType = "5 Minute Task";
//                            StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    try {
//                                        JSONObject jobj = new JSONObject(response);
//                                        sucess = jobj.getInt(TAG_SUCESS);
//                                        if (sucess == 1) {
//                                            StyleableToast.makeText(Home.this,"5 Min Challenge Started !", Toast.LENGTH_LONG,R.style.successtoast).show();
//                                            editor.putString(getString(R.string.taskflag5min), "true");
//                                            editor.commit();
//                                            taskDialogBox.dismissDialog();
//                                            timer.setVisibility(View.VISIBLE);
//                                            Drawable d = timer.getDrawable();
//                                            if (d instanceof Animatable) {
//                                                ((Animatable) d).start();
//                                            }
//                                            START_TIMER = 300000;
//                                            time_left_in_milis = START_TIMER;
//                                            if (timerRunning) {
//                                                pauseTimer();
//                                            } else {
//                                                startTimer();
//                                            }
//                                        } else if (sucess == 2) {
//
//                                            StyleableToast.makeText(Home.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.warningtoast).show();
//
//                                        } else if (sucess == 3) {
//                                            StyleableToast.makeText(Home.this,"5 Min Challenge Started !", Toast.LENGTH_LONG,R.style.successtoast).show();
//                                            editor.putString(getString(R.string.taskflag5min), "true");
//                                            editor.commit();
//                                            taskDialogBox.dismissDialog();
//                                            timer.setVisibility(View.VISIBLE);
//                                            Drawable d = timer.getDrawable();
//                                            if (d instanceof Animatable) {
//                                                ((Animatable) d).start();
//                                            }
//                                            START_TIMER = 300000;
//                                            time_left_in_milis = START_TIMER;
//                                            if (timerRunning) {
//                                                pauseTimer();
//                                            } else {
//                                                startTimer();
//                                            }
//                                        }
//                                        else {
//
//                                            StyleableToast.makeText(Home.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
//
//                                        }
//                                    } catch (JSONException e) {
//                                        StyleableToast.makeText(Home.this,e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
//                                    }
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    StyleableToast.makeText(Home.this,error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
//                                }
//                            }) {
//                                public Map<String, String> getParams() {
//                                    Map<String, String> params = new HashMap<String, String>();
//                                    params.put("uniqueid", uniqueid);
//                                    params.put("tasktype", taskType);
//                                    params.put("date", currentDate);
//                                    return params;
//                                }
//                            };
//                            request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//                            requestQueue.add(request);
//                        }catch(Exception e){
//                            StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
//                        }
//                        }
//                    });
//                    taskDialogBox.min10.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try {
//                                final String taskType = "10 Minute Task";
//                                StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        try {
//                                            JSONObject jobj = new JSONObject(response);
//                                            sucess = jobj.getInt(TAG_SUCESS);
//                                            if (sucess == 1) {
//                                                StyleableToast.makeText(Home.this,"10 Min Challenge Started !", Toast.LENGTH_LONG,R.style.successtoast).show();
//                                                editor.putString(getString(R.string.taskflag10min), "true");
//                                                editor.commit();
//                                                taskDialogBox.dismissDialog();
//                                                timer.setVisibility(View.VISIBLE);
//                                                Drawable d = timer.getDrawable();
//                                                if (d instanceof Animatable) {
//                                                    ((Animatable) d).start();
//                                                }
//                                                START_TIMER = 600000;
//                                                time_left_in_milis = START_TIMER;
//                                                if (timerRunning) {
//                                                    pauseTimer();
//                                                } else {
//                                                    startTimer();
//                                                }
//                                            } else if (sucess == 2) {
//
//                                                StyleableToast.makeText(Home.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.warningtoast).show();
//                                            } else if (sucess == 3) {
//                                                StyleableToast.makeText(Home.this,"10 Min Challenge Started !", Toast.LENGTH_LONG,R.style.successtoast).show();
//                                                editor.putString(getString(R.string.taskflag10min), "true");
//                                                editor.commit();
//                                                taskDialogBox.dismissDialog();
//                                                timer.setVisibility(View.VISIBLE);
//                                                Drawable d = timer.getDrawable();
//                                                if (d instanceof Animatable) {
//                                                    ((Animatable) d).start();
//                                                }
//                                                START_TIMER = 600000;
//                                                time_left_in_milis = START_TIMER;
//                                                if (timerRunning) {
//                                                    pauseTimer();
//                                                } else {
//                                                    startTimer();
//                                                }
//
//                                            } else {
//                                                StyleableToast.makeText(Home.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
//
//                                            }
//                                        } catch (JSONException e) {
//
//                                            StyleableToast.makeText(Home.this,e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
//                                        }
//                                    }
//                                }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        StyleableToast.makeText(Home.this,error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
//                                    }
//                                }) {
//                                    public Map<String, String> getParams() {
//                                        Map<String, String> params = new HashMap<String, String>();
//                                        params.put("uniqueid", uniqueid);
//                                        params.put("tasktype", taskType);
//                                        params.put("date", currentDate);
//                                        return params;
//                                    }
//                                };
//                                request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//                                requestQueue.add(request);
//
//
//                            } catch (Exception e) {
//                                StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT, R.style.errortoast).show();
//                            }
//                        }
//                    });
//                    taskDialogBox.min15.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try{
//                            final String taskType = "15 Minute Task";
//                            StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    try {
//                                        JSONObject jobj = new JSONObject(response);
//                                        sucess = jobj.getInt(TAG_SUCESS);
//                                        if (sucess == 1) {
//                                            StyleableToast.makeText(Home.this,"15 Min Challenge Started !", Toast.LENGTH_LONG,R.style.successtoast).show();
//                                            editor.putString(getString(R.string.taskflag15min), "true");
//                                            editor.commit();
//                                            taskDialogBox.dismissDialog();
//                                            timer.setVisibility(View.VISIBLE);
//                                            Drawable d = timer.getDrawable();
//                                            if (d instanceof Animatable) {
//                                                ((Animatable) d).start();
//                                            }
//                                            START_TIMER = 900000;
//                                            time_left_in_milis = START_TIMER;
//                                            if (timerRunning) {
//                                                pauseTimer();
//                                            } else {
//                                                startTimer();
//                                            }
//                                        } else if (sucess == 2) {
//
//                                            StyleableToast.makeText(Home.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.warningtoast).show();
//                                        }
//                                        else if (sucess == 3) {
//                                            StyleableToast.makeText(Home.this,"15 Min Challenge Started !", Toast.LENGTH_LONG,R.style.successtoast).show();
//                                            editor.putString(getString(R.string.taskflag15min), "true");
//                                            editor.commit();
//                                            taskDialogBox.dismissDialog();
//                                            timer.setVisibility(View.VISIBLE);
//                                            Drawable d = timer.getDrawable();
//                                            if (d instanceof Animatable) {
//                                                ((Animatable) d).start();
//                                            }
//                                            START_TIMER = 900000;
//                                            time_left_in_milis = START_TIMER;
//                                            if (timerRunning) {
//                                                pauseTimer();
//                                            } else {
//                                                startTimer();
//                                            }
//                                        }else {
//
//                                            StyleableToast.makeText(Home.this,jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
//                                        }
//                                    } catch (JSONException e) {
//                                        StyleableToast.makeText(Home.this,e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
//                                    }
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    StyleableToast.makeText(Home.this,error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
//                                }
//                            }) {
//                                public Map<String, String> getParams() {
//                                    Map<String, String> params = new HashMap<String, String>();
//                                    params.put("uniqueid", uniqueid);
//                                    params.put("tasktype", taskType);
//                                    params.put("date", currentDate);
//                                    return params;
//                                }
//                            };
//                            request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
//                            requestQueue.add(request);
//                        }catch(Exception e){
//                            StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
//                        }
//
//                        }
//                    });
//                }
//            }, 5000);
//        }


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
                        float x = timer.getX() + distanceX;
                        float y = timer.getY() + distanceY;


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

    private void extracTags() {
        try{
            String ExtractDataUrl="https://www.datamanagement.ml/fetchTags.php";
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
                                Tags item=new Tags(
                                        data.getString("id"),
                                        data.getString("tagname")
                                );

                                tagItems.add(item);
                                count++;

                            }
                            tagsview.setAdapter(new TagsAdapter(tagItems,Home.this,Home.this));
                        }catch (Exception e){
                            StyleableToast.makeText(Home.this, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    StyleableToast.makeText(Home.this, error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                }
            });
            requestQueue.add(stringRequest);
        }catch(Exception e){
            StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
        }
    }

    private void getUniqueid() {
        try{
        String email2 = sharedPreferences.getString(getString(R.string.uemail), "");
        String ExtractDataUrl = "https://www.datamanagement.ml/getUdata.php?email="+email2;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ExtractDataUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null) {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject data = array.getJSONObject(i);
                            uniqueid=data.getString("uniqueid");
                            String username=data.getString("username");
                          //  Toast.makeText(Home.this, username, Toast.LENGTH_SHORT).show();
                            String phoneno=data.getString("phoneno");
                            editor.putString(getString(R.string.uniqueid),uniqueid);
                            editor.putString("username",username);
                            editor.commit();
                            editor.putString("phoneno",phoneno);
                            editor.commit();
                           // Toast.makeText(Home.this, ""+uniqueid, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        StyleableToast.makeText(Home.this,e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                StyleableToast.makeText(Home.this,error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
            }
        });
        requestQueue.add(stringRequest);
    }catch(Exception e){
        StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
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
                        StyleableToast.makeText(Home.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();

                        }else {
                            Log.d("TAG", "The interstitial ad wasn't loaded yet.");
                        }
                    }
                    else {
                        StyleableToast.makeText(Home.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.errortoast).show();

                    }
                } catch (JSONException e) {
                    StyleableToast.makeText(Home.this,e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                StyleableToast.makeText(Home.this,error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
            }
        }) {
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uniqueid", uniqueid);
                params.put("tasktype", tasktype);
                params.put("earnPoints", points);
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
               // Toast.makeText(Home.this, "finished", Toast.LENGTH_SHORT).show();
                timerRunning = false;
                timer.setVisibility(View.GONE);

                if(sharedPreferences.getString(getString(R.string.taskflag5min),"").equals("true")) {
                    addPoints(getString(R.string.min5points),"5 Minute Task");
                    editor.putString(getString(R.string.taskflag5min), "false");
                    editor.commit();
                }
                if(sharedPreferences.getString(getString(R.string.taskflag10min),"").equals("true")) {
                    addPoints(getString(R.string.min10points),"10 Minute Task");
                    editor.putString(getString(R.string.taskflag10min), "false");
                    editor.commit();
                }
                if(sharedPreferences.getString(getString(R.string.taskflag15min),"").equals("true")) {
                    addPoints(getString(R.string.min15points),"15 Minute Task");
                    editor.putString(getString(R.string.taskflag15min), "false");
                    editor.commit();
                }
                if(sharedPreferences.getString(getString(R.string.taskflag20min),"").equals("true")) {
                    addPoints(getString(R.string.min20points),"20 Minute Task");
                    editor.putString(getString(R.string.taskflag20min), "false");
                    editor.commit();
                }
            }
        }.start();
        timerRunning = true;

    }

    void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.home_menu,menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.contactus:
                startActivity(new Intent(Home.this,ContactUs.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void ExtractData() {
        try{
    String ExtractDataUrl="https://www.datamanagement.ml/fetch.php?page="+page;
        StringRequest stringRequest=new StringRequest(Request.Method.GET, ExtractDataUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {

                if(response!=null){
                    recyclerView.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    try{
                        int count=0;
                        JSONArray array=new JSONArray(response);
                        for (int i=0;i<array.length();i++){
                            JSONObject data=array.getJSONObject(i);
                            News item=new News(
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
                            Log.d("Data1:",""+data.get("title"));

                        }
                       adapter.notifyDataSetChanged();
                    }catch (Exception e){
                        StyleableToast.makeText(Home.this, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                    }

                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                StyleableToast.makeText(Home.this, error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
            }
        });

        requestQueue.add(stringRequest);
        pagination();
    }catch(Exception e){
        StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
    }
    }

    private void pagination() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCOunt = linearLayoutManager.getItemCount();
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                    if(load){
                        if (totalItemCOunt > previosTotal) {
                            previosTotal = totalItemCOunt;
                            page++;
                            load=false;
                        }
                    }

                    if(!load && (firstVisibleItem + visibleItemCount) >= totalItemCOunt){
                        //Toast.makeText(Home.this, "Calling get next", Toast.LENGTH_SHORT).show();
                        getNext();
                        load = true;
                        Log.d("Page",""+page);
                    }


            }
        });
    }

    private void getNext() {
        try{
        progressBar.setVisibility(View.VISIBLE);
        String ExtractDataUrl="https://www.datamanagement.ml/fetch.php?page="+page;
        StringRequest stringRequest=new StringRequest(Request.Method.GET, ExtractDataUrl, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {

                if(response!=null){
                    //recyclerView.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    try{
                        int count=0;
                        JSONArray array=new JSONArray(response);
                        for (int i=0;i<array.length();i++){
                            JSONObject data=array.getJSONObject(i);
                            News item=new News(
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
                            Log.d("Data1:",""+data.get("title"));

                        }
                        adapter.notifyDataSetChanged();

                        //tagsview.setAdapter(new TagsAdapter(tags,Home.this,Home.this));

                    }catch (Exception e){
                        StyleableToast.makeText(Home.this, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                    }

                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                StyleableToast.makeText(Home.this, error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
            }
        });

        requestQueue.add(stringRequest);
    }catch(Exception e){
        StyleableToast.makeText(getApplicationContext(), "Error While Loading", Toast.LENGTH_SHORT,R.style.errortoast).show();
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

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("bonuspref",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isOpnend",false);
        return  isIntroActivityOpnendBefore;
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("bonuspref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isOpnend",true);
        editor.commit();
    }

    private void createNotificationChannel1(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name="ReminderChannel";
            String desc="Channel For Reminder";
            int imp= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel("comeback",name,imp);
            channel.setDescription(desc);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotificationChannel2(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name="Reminder morning Channel";
            String desc="Channel For morning Reminder";
            int imp= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel("morning",name,imp);
            channel.setDescription(desc);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        Log.d("MyApp", "App in foreground");
    }
}