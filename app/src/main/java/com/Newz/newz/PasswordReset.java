package com.Newz.newz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PasswordReset extends AppCompatActivity {
    EditText rpassword,crpassword;
    Button vsubmit;
    private RequestQueue requestQueue;
    private  static  final  String TAG=Registration.class.getSimpleName();
    int sucess;
    private String TAG_SUCESS="sucess";
    SharedPreferences sharedPreferences;
    private String TAG_MESSAGE="message";
    private String tag_json_obj="json_obj_req";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        rpassword=findViewById(R.id.rpassword);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        crpassword=findViewById(R.id.crpassword);
        vsubmit=(Button) findViewById(R.id.rsubmit);
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        vsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

    }

    private void updatePassword() {
        Intent intent=getIntent();
        final String phoneno1=intent.getStringExtra("rphone");
        //Toast.makeText(this, phoneno1, Toast.LENGTH_SHORT).show();
         String ExtractDataUrl="https://www.datamanagement.ml/passwordReset.php";
         if(rpassword.getText().toString().equals(crpassword.getText().toString())){
             StringRequest request=new StringRequest(Request.Method.POST, ExtractDataUrl, new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                     try {
                         JSONObject jobj = new JSONObject(response);
                         sucess = jobj.getInt(TAG_SUCESS);
                         if (sucess == 1) {
                             startActivity(new Intent(PasswordReset.this,Login.class));
                             finish();
                             StyleableToast.makeText(PasswordReset.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                         } else if(sucess == 2){
                             startActivity(new Intent(PasswordReset.this,Login.class));
                             finish();
                             StyleableToast.makeText(PasswordReset.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                         }else{
                             StyleableToast.makeText(PasswordReset.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                         }
                     } catch (JSONException e) {
                         StyleableToast.makeText(PasswordReset.this, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                     }
                 }
             }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                     StyleableToast.makeText(PasswordReset.this, error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                 }
             }){
                 public Map<String,String> getParams(){
                     Map<String, String> params=new HashMap<String, String>();
                     params.put("email",phoneno1);
                     params.put("password",crpassword.getText().toString());
                     return params;
                 }
             };

             request.setRetryPolicy(new DefaultRetryPolicy(10000,1,1.0f));
             requestQueue.add(request);
         }
    }



}