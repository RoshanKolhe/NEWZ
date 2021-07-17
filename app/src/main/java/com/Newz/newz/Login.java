package com.Newz.newz;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

@RequiresApi(api = Build.VERSION_CODES.O)
public class Login extends AppCompatActivity {
    TextView createnewacc, forgetpass;
    Button submit;
    EditText email, password;

    private RequestQueue requestQueue;
    private static final String TAG = Registration.class.getSimpleName();
    int sucess;
    private String TAG_SUCESS = "sucess";
    private String TAG_MESSAGE = "message";
    private String tag_json_obj = "json_obj_req";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    PleaseWaitDbox pleaseWaitDbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createnewacc = findViewById(R.id.createnewacc);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        forgetpass = findViewById(R.id.forgetpass);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        createnewacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });
        submit = findViewById(R.id.submit);
        pleaseWaitDbox = new PleaseWaitDbox(Login.this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pleaseWaitDbox.startLoadingDialog();
                validateCredentials();
                //AuthenticateUser();
                //startActivity(new Intent(Login.this,Home.class));
            }
        });

        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetmail = new EditText(v.getContext());
                AlertDialog.Builder passwordreset = new AlertDialog.Builder(v.getContext());
                passwordreset.setTitle("Reset Password ?");
                passwordreset.setMessage("Enter Your E-mail Address to Reset Password");
                passwordreset.setView(resetmail);
                passwordreset.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pleaseWaitDbox.startLoadingDialog();
                        final String email = resetmail.getText().toString();
                        try{
                            String sendUrl="http://www.datamanagement.ml/forgot.php";
                            StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jobj = new JSONObject(response);
                                        sucess = jobj.getInt(TAG_SUCESS);
                                        if (sucess == 1) {
                                            StyleableToast.makeText(Login.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                                            pleaseWaitDbox.dismissDialog();
                                            Intent intent = new Intent(Login.this, ResetPassword.class);
                                            intent.putExtra("rphone", email);
                                            startActivity(intent);
                                        }
                                        else {
                                            pleaseWaitDbox.dismissDialog();
                                            StyleableToast.makeText(Login.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.errortoast).show();

                                        }
                                    } catch (JSONException e) {
                                        pleaseWaitDbox.dismissDialog();
                                        StyleableToast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    pleaseWaitDbox.dismissDialog();
                                    StyleableToast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG,R.style.successtoast).show();
                                }
                            }) {
                                public Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("email", email);
                                    return params;
                                }
                            };
                            request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
                            requestQueue.add(request);
                        }catch(Exception e){
                            pleaseWaitDbox.dismissDialog();
                            StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
                        }

                        //Toast.makeText(Login.this, resetmail.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                passwordreset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                passwordreset.create().show();
            }
        });
    }

//    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
//    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

//    public static String generateNewToken() {
//        byte[] randomBytes = new byte[24];
//        secureRandom.nextBytes(randomBytes);
//        return base64Encoder.encodeToString(randomBytes);
//    }


    private void validateCredentials() {
        if (email.getText().toString().equals("")) {
            pleaseWaitDbox.dismissDialog();
            email.setError("Please Enter Email/Phone");
        } else if (password.getText().toString().equals("")) {
            pleaseWaitDbox.dismissDialog();
            password.setError("Please Enter Password");
        } else {
            AuthenticateUser();
        }
    }

    private void AuthenticateUser() {
        try {
            String password1 = password.getText().toString();
            String email1 = email.getText().toString();
            String sendUrl = "http://www.datamanagement.ml/login.php";
            StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        sucess = jobj.getInt(TAG_SUCESS);
                        if (sucess == 1) {
                            editor.putString(getString(R.string.uemail), email.getText().toString());
                            editor.commit();
                            if (restorePrefData()) {

                                pleaseWaitDbox.dismissDialog();
                                startActivity(new Intent(Login.this, Home.class));
                                finish();

                            } else {
                                pleaseWaitDbox.dismissDialog();
                                startActivity(new Intent(Login.this, Walkthrough.class));
                                finish();
                            }
                            StyleableToast.makeText(Login.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                        } else {
                            pleaseWaitDbox.dismissDialog();
                            StyleableToast.makeText(Login.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.errortoast).show();
                        }
                    } catch (JSONException e) {
                        StyleableToast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    StyleableToast.makeText(Login.this,error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                }
            }) {
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", email.getText().toString());
                    params.put("password", password.getText().toString());
                    return params;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
            requestQueue.add(request);
        } catch (Exception e) {
            StyleableToast.makeText(Login.this, "Unable To Connect With Internet Please Check Internet Connectivity", Toast.LENGTH_LONG,R.style.errortoast).show();

        }
    }

    private boolean restorePrefData() {


        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}