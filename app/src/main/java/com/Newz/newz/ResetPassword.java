package com.Newz.newz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {
    EditText otp;
    Button verify;
    String verificationCodebySystem;
    FirebaseAuth mAuth;
    int sucess;
    String email;
    private RequestQueue requestQueue;
    private String TAG_SUCESS = "sucess";
    private String TAG_MESSAGE = "message";
    private String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Intent intent=getIntent();
        email=intent.getStringExtra("rphone");
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        otp=findViewById(R.id.otp);
        verify=findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=otp.getText().toString();
                if(code.isEmpty() || code.length()<6){
                    otp.setError("Wrong OTP");
                    otp.requestFocus();
                    return;
                }
                else{
                    verifycode(code);
                }
            }
        });
    }
    private  void verifycode(String codeByUser){
        try{
            String sendUrl="http://www.datamanagement.ml/compareOtp.php";
            StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        sucess = jobj.getInt(TAG_SUCESS);
                        if (sucess == 1) {
                            StyleableToast.makeText(ResetPassword.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                            Intent intent = new Intent(ResetPassword.this, PasswordReset.class);
                            intent.putExtra("rphone", email);
                            startActivity(intent);
                        }
                        else {
                            StyleableToast.makeText(ResetPassword.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();

                        }
                    } catch (JSONException e) {
                        StyleableToast.makeText(ResetPassword.this, e.getMessage(), Toast.LENGTH_LONG,R.style.successtoast).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    StyleableToast.makeText(ResetPassword.this, error.getMessage(), Toast.LENGTH_LONG,R.style.successtoast).show();
                }
            }) {
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", email);
                    params.put("otp",codeByUser);
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
            requestQueue.add(request);
        }catch(Exception e){
            StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
        }

    }


}