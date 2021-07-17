package com.Newz.newz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity implements LifecycleObserver {
    Toolbar toolbar;
    ImageView newsimg;
    TextView title,desc,date,tags,credits,link;
    private AdView mAdView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private InterstitialAd mInterstitialAd;
    FloatingActionButton timer;
    float xDown = 0, yDown = 0;
    Button share,bookmark;
    CountDownTimer countDownTimer;
    long START_TIMER = 0000;
    File imagePath;
    boolean timerRunning;
    //View mcontainer;
    String id;
    long time_left_in_milis = START_TIMER;

    private String TAG_SUCESS = "sucess";
    private String TAG_MESSAGE = "message";
    int sucess;
    private RequestQueue requestQueue;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date date1 = new Date();
    final String currentDate = formatter.format(date1);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        requestQueue= Volley.newRequestQueue(getApplicationContext());

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2184993334191556/3635056707");

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DetailsActivity.this);
        editor = sharedPreferences.edit();
        String email=sharedPreferences.getString(getString(R.string.uemail),"");
        timer = findViewById(R.id.timer);
       // mcontainer=findViewById(R.id.detailLinear);
        mAdView = findViewById(R.id.adView);
        share=findViewById(R.id.share);
        bookmark=findViewById(R.id.bookmark);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
              //  StyleableToast.makeText(DetailsActivity.this,"Some Error Occured While Loading the Ad", Toast.LENGTH_LONG,R.style.errortoast).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        Intent intent=getIntent();
        String image=intent.getStringExtra("image");
        //Toast.makeText(this, ""+image, Toast.LENGTH_SHORT).show();
        newsimg=findViewById(R.id.newsimg);
        title=findViewById(R.id.text_view_title);
        desc=findViewById(R.id.text_view_description);
        date=findViewById(R.id.text_view_date);
        tags=findViewById(R.id.tags);
        credits=findViewById(R.id.credits);
        link=findViewById(R.id.oglink);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            String img=bundle.getString("image");
            id=bundle.getString("id");
           // newsimg.setImageResource(img);
          //  Toast.makeText(this, ""+img, Toast.LENGTH_SHORT).show();
            Picasso.get().load(img).into(newsimg);
            title.setText(bundle.getString("title"));
            desc.setText(bundle.getString("details"));
            date.setText(bundle.getString("date"));
            tags.setText("Tags : "+bundle.getString("tags"));
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Uri uri = Uri.parse(bundle.getString("link")); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }catch (Exception e){
                        StyleableToast.makeText(DetailsActivity.this, "Link Not Available !", Toast.LENGTH_SHORT,R.style.warningtoast).show();
                    }
                }
            });
           // link.setText("Link : "+bundle.getString("link"));
            credits.setText("Credits : "+bundle.getString("credits"));

        }
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    final String url="https://www.datamanagement.ml/bookmarkNews.php";

                    final String email=sharedPreferences.getString(getApplicationContext().getResources().getString(R.string.uemail),"");
                    StringRequest request=new StringRequest(Request.Method.POST,url , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jobj = new JSONObject(response);
                                sucess = jobj.getInt(TAG_SUCESS);
                                if (sucess == 1) {
                                    StyleableToast.makeText(DetailsActivity.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();

                                } else {
                                    StyleableToast.makeText(DetailsActivity.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                                }
                            } catch (JSONException e) {
                                StyleableToast.makeText(DetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            StyleableToast.makeText(DetailsActivity.this, error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                        }
                    }){
                        public Map<String,String> getParams(){
                            Map<String, String> params=new HashMap<String, String>();
                            params.put("useremail",email);
                            params.put("newsid",id);
                            return params;
                        }
                    };
                    request.setRetryPolicy(new DefaultRetryPolicy(10000,1,1.0f));
                    requestQueue.add(request);
                }catch(Exception e){
                    StyleableToast.makeText(getApplicationContext(), "Please Check Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bundle!=null) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "*" + bundle.getString("title").toString() + "*" + "\n"
                            + bundle.getString("short_news") + "\n\n" + "Check out This application To Earn While Reading : "
                            + "https://play.google.com/store/apps/details?id=com.Newz.newz");
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, "Share Via");
                    startActivity(shareIntent);
//                LinearLayout LL = (LinearLayout) findViewById(R.id.linearDetails);
//                Bitmap bitmap = takeScreenshot(LL);
//                saveBitmap(bitmap);
//                shareIt();
                }
            }


        });

        String timeleft = sharedPreferences.getString(getString(R.string.timeLeft), "0");
        long time_left_milis = Long.parseLong(timeleft);
        int min = (int) (time_left_milis / 1000) / 60;
        int sec = (int) (time_left_milis / 1000) % 60;
        String timeleftFormated = String.format(Locale.getDefault(), "%02d:%02d", min, sec);
        //StyleableToast.makeText(this, "Time Remaining : " + timeleftFormated, Toast.LENGTH_LONG,R.style.successtoast).show();

        START_TIMER = time_left_milis;
        time_left_in_milis = START_TIMER;



        if ( sharedPreferences.getString(getString(R.string.taskflag5min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag10min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag15min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag20min), "").equals("true")
        )
        {

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

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .icon(ResourcesCompat.getDrawable(getResources(),R.mipmap.newz,null))
                .session(3)
                .threshold(7)
                .title("How was your experience with us?")
                .titleTextColor(R.color.flat_blue)
                .positiveButtonText("Not Now")
                .negativeButtonText("Never")
                .positiveButtonTextColor(R.color.grey_500)
                .negativeButtonTextColor(R.color.grey_500)
                .formTitle("Submit Feedback")
                .formHint("Tell us where we can improve")
                .formSubmitText("Submit")
                .formCancelText("Cancel")
                .ratingBarColor(R.color.orange)
                .playstoreUrl("https://play.google.com/store/apps/details?id=com.Newz.newz")
                .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                    @Override
                    public void onThresholdCleared(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        //do something
                        Intent intent;
                        intent = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.Newz.newz"));
                        startActivity(intent);
                        ratingDialog.dismiss();
                    }
                })
                .onThresholdFailed(new RatingDialog.Builder.RatingThresholdFailedListener() {
                    @Override
                    public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        //do something
                        ratingDialog.dismiss();
                    }
                })
                .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {

                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        ratingDialog.show();

    }

//    private Bitmap takeScreenshot(View view) {
//        View rootView = findViewById(android.R.id.content).getRootView();
//        rootView.setDrawingCacheEnabled(true);
//        return rootView.getDrawingCache();
//
//    }
//    public void saveBitmap(Bitmap bitmap) {
//         imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
//        FileOutputStream fos;
//        try {
//            fos = new FileOutputStream(imagePath);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            Log.e("GREC", e.getMessage(), e);
//        } catch (IOException e) {
//            Log.e("GREC", e.getMessage(), e);
//        }
//    }
//    private void shareIt() {
//        Uri uri = Uri.fromFile(imagePath);
//        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//        sharingIntent.setType("image/*");
//        String shareBody = "In Tweecher, My highest score with screen shot";
//        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Tweecher score");
//        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
//
//        startActivity(Intent.createChooser(sharingIntent, "Share via"));
//    }

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
                Intent intent=new Intent(DetailsActivity.this,Home.class);
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

    public void helpbox(){
        AlertDialog.Builder help = new AlertDialog.Builder(this);
        help.setTitle("Details Activity");
        help.setMessage("In this Activity You Can See your News / Articles in Brief \n\nAnd Alongside you can complete your Ongoing Tasks ! \n\nCredits & links To the original News / Article is also given in this Activity !");
        help.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        help.create().show();

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
              //  Toast.makeText(DetailsActivity.this, "finished", Toast.LENGTH_SHORT).show();
                timerRunning = false;
                timer.setVisibility(View.GONE);

                if(sharedPreferences.getString(getString(R.string.taskflag5min),"").equals("true")) {
                    addPoints(getString(R.string.min5points),"5 Minute Task");
                    editor.putString(getString(R.string.taskflag5min), "false");
                    editor.commit();
                    StyleableToast.makeText(DetailsActivity.this, "Task Completed Successfully ! \n Points Will Be Added Shortly", Toast.LENGTH_LONG,R.style.successtoast).show();
                }
                if(sharedPreferences.getString(getString(R.string.taskflag10min),"").equals("true")) {
                    addPoints(getString(R.string.min10points),"10 Minute Task");
                    editor.putString(getString(R.string.taskflag10min), "false");
                    editor.commit();
                    StyleableToast.makeText(DetailsActivity.this, "Task Completed Successfully ! \n Points Will Be Added Shortly", Toast.LENGTH_LONG,R.style.successtoast).show();
                }
                if(sharedPreferences.getString(getString(R.string.taskflag15min),"").equals("true")) {
                    addPoints(getString(R.string.min15points),"15 Minute Task");
                    editor.putString(getString(R.string.taskflag15min), "false");
                    editor.commit();
                    StyleableToast.makeText(DetailsActivity.this, "Task Completed Successfully ! \n Points Will Be Added Shortly", Toast.LENGTH_LONG,R.style.successtoast).show();
                }
                if(sharedPreferences.getString(getString(R.string.taskflag20min),"").equals("true")) {
                    addPoints(getString(R.string.min20points),"20 Minute Task");
                    editor.putString(getString(R.string.taskflag20min), "false");
                    editor.commit();
                    StyleableToast.makeText(DetailsActivity.this, "Task Completed Successfully ! \n Points Will Be Added Shortly", Toast.LENGTH_LONG,R.style.successtoast).show();
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

    void addPoints(String points,String tasktype){
        try {
            Log.d("points1", points);
            Log.d("tasktype1", tasktype);
            String sendUrl = "http://www.datamanagement.ml/simpleTaskReward.php";
            StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        sucess = jobj.getInt(TAG_SUCESS);
                        if (sucess == 1) {
                            StyleableToast.makeText(DetailsActivity.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG, R.style.successtoast).show();
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();

                            }else {
                                Log.d("TAG", "The interstitial ad wasn't loaded yet.");
                            }
                        } else {
                            StyleableToast.makeText(DetailsActivity.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG, R.style.successtoast).show();
                        }
                    } catch (JSONException e) {
                        StyleableToast.makeText(DetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG, R.style.errortoast).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    StyleableToast.makeText(DetailsActivity.this, error.getMessage(), Toast.LENGTH_LONG, R.style.errortoast).show();
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
        }catch (Exception e){
            StyleableToast.makeText(DetailsActivity.this,"PLease Check Internet Connectivity", Toast.LENGTH_LONG,R.style.errortoast).show();
        }
    }





}