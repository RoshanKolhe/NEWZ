package com.Newz.newz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {

    Animation circleIN, fadein, logoRotatefull;
    ImageView circle, logo;
    TextView txt;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private RequestQueue requestQueue;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circle = findViewById(R.id.circle);
        logo = findViewById(R.id.logo);
        progressBar=findViewById(R.id.progress);
        circleIN = AnimationUtils.loadAnimation(this, R.anim.circle_scale_in);
        fadein = AnimationUtils.loadAnimation(this, R.anim.logo_fade_in);
        logoRotatefull = AnimationUtils.loadAnimation(this, R.anim.logo_rotate_full);
        circle.setAnimation(circleIN);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        logo.setVisibility(View.INVISIBLE);
        txt = findViewById(R.id.title);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logo.setVisibility(View.VISIBLE);
                logo.setAnimation(fadein);
                logo.setAnimation(logoRotatefull);
            }
        }, 2000);
        progressBar.setVisibility(View.VISIBLE);
        manageUserToken();
    }
        private void manageUserToken () {

            String email = sharedPreferences.getString(getString(R.string.uemail), "");
            //Toast.makeText(MainActivity.this, email, Toast.LENGTH_SHORT).show();

            if (!email.equals("")) {
                try{
                String ExtractDataUrl = "https://www.datamanagement.ml/getUdata.php?email="+email;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ExtractDataUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null) {
                            try {
                                JSONArray array = new JSONArray(response);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject data = array.getJSONObject(i);
                                    String token=data.getString("token");
                                   // Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                                    if(!token.equals("")){
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                Intent Rhomeintent = new Intent(MainActivity.this, Home.class);
                                                startActivity(Rhomeintent);
                                                finish();
                                            }
                                        }, 4000);
                                    }
                                    else{
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                editor.putString(getString(R.string.uemail),"");
                                                editor.commit();

                                            }
                                        }, 4000);
                                    }
                                }
                            } catch (Exception e) {

                                StyleableToast.makeText(MainActivity.this, "Something Went Wrong Please Try Again..", Toast.LENGTH_LONG,R.style.errortoast).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        StyleableToast.makeText(MainActivity.this, "Something Went Wrong Please Try Again..", Toast.LENGTH_LONG,R.style.errortoast).show();
                    }
                });
                requestQueue.add(stringRequest);
                 }catch(Exception e){

                      StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
                }
            }
            else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (restorePrefData()) {

                            startActivity(new Intent(MainActivity.this, Login.class));
                            finish();

                        } else {

                            startActivity(new Intent(MainActivity.this, Walkthrough.class));
                            finish();
                        }
                    }
                }, 4000);
            }
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    private boolean restorePrefData() {


        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;


    }
}