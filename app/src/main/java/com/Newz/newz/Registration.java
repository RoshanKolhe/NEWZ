package com.Newz.newz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    TextView tv,tv1,tv2,phoneNo,terms1;
    Button register;
    Animation fadein;
    RelativeLayout back;
    CheckBox terms,showpassword,showpassword1;
    int visible=10;
    int visible1=10;
    EditText username,email,password,cPassword,refferal;
    private String sendUrl="http://www.datamanagement.ml/register.php";
    private RequestQueue requestQueue;
    private  static  final  String TAG=Registration.class.getSimpleName();
    int sucess;
    private String TAG_SUCESS="sucess";
    private String TAG_MESSAGE="message";
    private String tag_json_obj="json_obj_req";
    private static final int CREDENTIAL_PICKER_REQUEST =120 ;
    TextView backbtn,egtext;
    PleaseWaitDbox pleaseWaitDbox;
    RadioButton refyes,refno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        fadein= AnimationUtils.loadAnimation(this,R.anim.logo_fade_in);
        back=findViewById(R.id.back);
        backbtn=findViewById(R.id.backbtn);
        username=(EditText)findViewById(R.id.username);
        phoneNo=(TextView) findViewById(R.id.phnNo);
        refyes=findViewById(R.id.refyes);
        egtext=findViewById(R.id.egtext);
        refno=findViewById(R.id.refno);
        terms1=findViewById(R.id.terms1);
        terms1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpbox();
            }
        });
        pleaseWaitDbox=new PleaseWaitDbox(Registration.this);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this,Login.class));
            }
        });
        phoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HintRequest hintRequest = new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
                PendingIntent intent = Credentials.getClient(Registration.this).getHintPickerIntent(hintRequest);
                try
                {
                    startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0,new Bundle());
                }
                catch (IntentSender.SendIntentException e)
                {
                    e.printStackTrace();
                }

            }
        });
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        cPassword=(EditText)findViewById(R.id.cpassword);
        refferal=(EditText)findViewById(R.id.userRefferal);
        terms=(CheckBox)findViewById(R.id.checkBox);
        register=(Button)findViewById(R.id.register);
        refyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refferal.setVisibility(View.VISIBLE);
                egtext.setVisibility(View.VISIBLE);
            }
        });
        refno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refferal.setVisibility(View.GONE);
                egtext.setVisibility(View.GONE);
            }
        });
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pleaseWaitDbox.startLoadingDialog();
                validateData();
            }
        });
        tv=findViewById(R.id.textView);
        tv1=findViewById(R.id.textView1);
        tv2=findViewById(R.id.textView2);
        back.setAnimation(fadein);
        tv.setAnimation(fadein);
        tv1.setAnimation(fadein);
        tv2.setAnimation(fadein);
        showpassword=(CheckBox)findViewById(R.id.checkBox2);
        showpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(visible==10){
                    password.setTransformationMethod(null);
                    visible=9;
                }
                //Toast.makeText(MainActivity.this, ""+visible, Toast.LENGTH_SHORT).show();
                else if(visible==9){
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    visible=10;
                }
            }
        });
        showpassword1=(CheckBox)findViewById(R.id.checkBox3);
        showpassword1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(visible1==10){
                    cPassword.setTransformationMethod(null);
                    visible1=9;
                }
                //Toast.makeText(MainActivity.this, ""+visible, Toast.LENGTH_SHORT).show();
                else if(visible1==9){
                    cPassword.setTransformationMethod(new PasswordTransformationMethod());
                    visible1=10;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            // Obtain the phone number from the result
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);
            /* EditText.setText(credentials.getId().substring(3));*/ //get the selected phone number
//Do what ever you want to do with your selected phone number here
            phoneNo.setText(credentials.getId().substring(3));
        //   Toast.makeText(this, "MOB"+credentials.getId().substring(3), Toast.LENGTH_SHORT).show();


        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
        {
            // *** No phone numbers available ***
            StyleableToast.makeText(Registration.this, "No phone numbers found", Toast.LENGTH_LONG,R.style.errortoast).show();

        }


    }
    private void validateData() {

        if(username.getText().toString().equals("")){
            pleaseWaitDbox.dismissDialog();
            username.setError("Please Enter Username");
        }
        else if(email.getText().toString().equals("")){
            pleaseWaitDbox.dismissDialog();
            email.setError("Please Enter Confirm Password");
        }
        else if(phoneNo.getText().toString().equals("")){
            pleaseWaitDbox.dismissDialog();
            phoneNo.setError("Please Enter Phone Number");
        }
        else if(password.getText().toString().equals("")){
            pleaseWaitDbox.dismissDialog();
            password.setError("Please Enter Password");
        }
        else if(cPassword.getText().toString().equals("")){
            pleaseWaitDbox.dismissDialog();
            cPassword.setError("Please Enter Confirm Password");
        }
        else if(!terms.isChecked()){
            pleaseWaitDbox.dismissDialog();
            StyleableToast.makeText(Registration.this, "Please Accept the Terms And Condition", Toast.LENGTH_LONG,R.style.errortoast).show();

        }
        else{
            createUser();
        }

    }
    private void createUser() {
        String username1=username.getText().toString();
        String email1=email.getText().toString();
        String phoneNo1=phoneNo.getText().toString();
        String password1=password.getText().toString();
        String cPassword1=cPassword.getText().toString();
        String reffer1=refferal.getText().toString();
        if(password1.equals(cPassword1)){
            try {
            StringRequest request=new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        sucess = jobj.getInt(TAG_SUCESS);
                        if (sucess == 1) {
                            pleaseWaitDbox.dismissDialog();
                            Intent intent=new Intent(Registration.this,Login.class);
                            intent.putExtra("email",email.getText().toString());
                            intent.putExtra("pass",password.getText().toString());
                            startActivity(intent);
                            finish();
                            StyleableToast.makeText(Registration.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();

                        } else {
                            pleaseWaitDbox.dismissDialog();
                            StyleableToast.makeText(Registration.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.errortoast).show();
                        }
                    } catch (JSONException e) {
                        pleaseWaitDbox.dismissDialog();
                        StyleableToast.makeText(Registration.this, "Something Went Wrong Please Try Again..", Toast.LENGTH_LONG,R.style.errortoast).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    StyleableToast.makeText(Registration.this,"Something Went Wrong Please Try Again..", Toast.LENGTH_LONG,R.style.errortoast).show();
                }
            }){
                public Map<String,String> getParams(){
                    Map<String, String> params=new HashMap<String, String>();
                    params.put("username",username.getText().toString());
                    params.put("email",email.getText().toString());
                    params.put("phoneno",phoneNo.getText().toString());
                    params.put("password",password.getText().toString());
                    params.put("uniqueidreffer",refferal.getText().toString());
                    return params;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(10000,1,1.0f));
            requestQueue.add(request);
        }catch(Exception e){
            StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
        }

        }
        else{
            pleaseWaitDbox.dismissDialog();
            password.setText("");
            cPassword.setText("");
            StyleableToast.makeText(Registration.this, "Password And Confirm Password Should Be same", Toast.LENGTH_LONG,R.style.errortoast).show();

        }
    }
    public void helpbox(){
        AlertDialog.Builder help = new AlertDialog.Builder(this);
        help.setTitle("Terms and Conditions");

        help.setMessage("\nProvision of the application : \n" +
                "1) Newz is a News/Articles Providing based application platform downloadable from play store and owned by us.\n" +
                "2) The app does not provide any news or content but just a brief summary available in public domain. The news content will be copyrighted and will be only available for non - commercial and personal use.\n" +
                "3) The app may include links and/or advertisements to other application or websites.\n" +
                "4) If You find any content on the App which falls under any of the prohibited categories of content you can inform us of such a violation and content takedown.\n"+
                "\nRestriction on use of content : \n"+
                "a) User are not allowed to copy, alter, modify ,reproduce or create derivative work\n"+
                "b) We may stop the provision of the application or any part of it or the content to the users at our sole discretion.\n"+
                "c) All the trademarks, brands and services of the app are property right of Newz only. Newz owns all the copyright and database in relation to the app.\n" +
                "\nYour agreement with the application : \n" +
                "I) Violation of any part of the use of application and/or the Agreement may result in a legal liability upon the user. User are  responsible for their conduct and activities while using the App, and for any consequences thereof.\n" +
                "II) If any provision of the Agreement is found to be unenforceable under the applicable law, it will not affect the enforceability of the other provisions of this Agreement. lf any provision of this Agreement is held to be invalid or unenforceable, such provision shall be deemed superseded by a valid enforceable provision that most closely matches the intent of the original provision and the remaining provisions shall be enforced.\n" +
                "III) We own the right to terminate or suspend any user access to the Service immediately, without prior notice or liability, under our sole discretion, for any reason whatsoever and without limitation, including but not limited to a breach of the Terms.\n"+
                "\nConstraints on use : \n" +
                "a) User should not make use of the app or any of its content thereof for any purpose that is illegal, unlawful or prohibited by this Agreement.\n" +
                "b) User should not access (or attempt to access) the content provided through the App by any means.\n" +
                "c) User should not redistribute, sublicense, rent, publish, sell, assign, lease, market, transfer, or otherwise make the App or any component or content thereof, available to third parties.\n" +
                "\nPrivacy : \n" +
                "a) You are responsible for maintaining the confidentiality of passwords associated with any device You use to access the App. Accordingly, You are solely responsible for all activities that occur with Your device. If You become aware of any unauthorized use of Your device, You will notify the relevant authorities as soon as.\n" +
                "\nRewards\n" +
                "1) Users can earn Points while using our application\n" +
                "2) Claiming  Points every Task Perform \n" +
                "3) Users CANNOT be rewarded with Points when they click on in-feed ads.\n" +
                "4) We may constantly monitor abusive user behaviors to protect the integrity of the community. When detect abusive behaviors, appropriate actions including but not limited to: freezing payouts, terminating accounts, blocking the usage of the app, etc.\n" +
                "\nUser can accept this Agreement only if :\n" +
                "You are a natural person of the legal age, eligibility and has the mental capacity to form a binding contract to the use of the App.\n");
        help.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                terms.setChecked(true);
            }
        });
        help.create().show();

    }
}