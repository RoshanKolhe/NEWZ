package com.Newz.newz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Wallet extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    TextView amounttobepaid;
    private RequestQueue requestQueue;
    TextView walletMoney, history;
    Button redeem;
    SimpleDateFormat formatter = new SimpleDateFormat("dd");
    Date date = new Date();
    int sucess;
    private String TAG_SUCESS = "sucess";
    private String TAG_MESSAGE = "message";
    private String tag_json_obj = "json_obj_req";
    final String currentDate = formatter.format(date);
    String uniqueid, phoneno;
    String redeemoption = "";
    ImageView paytm, phnpay, gpay;
    double amount = 0;
    RadioButton redeem30,redeem60;
    int redempoints=0;
    double points;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wallet");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        walletMoney = findViewById(R.id.textView6);
        redeem30=findViewById(R.id.redeem30);
        redeem60=findViewById(R.id.redeem60);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        history = findViewById(R.id.history);
        amounttobepaid = (TextView) findViewById(R.id.textView8);
        // Toast.makeText(Wallet.this, ""+currentDate, Toast.LENGTH_SHORT).show();
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Wallet.this, History.class));
            }
        });
        redeem = findViewById(R.id.redeem);
        redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(redempoints==0){
                    StyleableToast.makeText(Wallet.this, "Please Select Redeem Points", Toast.LENGTH_SHORT,R.style.errortoast).show();
                }else {
                    redeemMoney();
                }
            }
        });
        setWalletData();
        phnpay = findViewById(R.id.phnpay);
        gpay = findViewById(R.id.gpay);
        paytm = findViewById(R.id.paytm);

        redeem30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(points>=300) {
                    redempoints = 300;
                    amount = 30;
                    //Toast.makeText(Wallet.this, "" + redempoints, Toast.LENGTH_SHORT).show();
                }else{
                    StyleableToast.makeText(Wallet.this, "Not Enough Points !", Toast.LENGTH_SHORT,R.style.errortoast).show();
                }
            }
        });
        redeem60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(points>=600) {
                    redempoints = 600;
                    amount = 60;
                    //Toast.makeText(Wallet.this, "" + redempoints, Toast.LENGTH_SHORT).show();
                }else {
                    StyleableToast.makeText(Wallet.this, "Not Enough Points !", Toast.LENGTH_SHORT,R.style.errortoast).show();
                }
            }
        });



        getRedeemoption();


    }

    public void getRedeemoption() {
        redeemoption = "";
        phnpay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                redeemoption = "PhonePay";
                //  Toast.makeText(Wallet.this, redeemoption, Toast.LENGTH_SHORT).show();
                phnpay.setBackground(getDrawable(R.drawable.imgborder));
                gpay.setBackground(getDrawable(R.drawable.imgborder_def));
                paytm.setBackground(getDrawable(R.drawable.imgborder_def));

            }
        });
        paytm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                redeemoption = "Paytm";
                //Toast.makeText(Wallet.this, redeemoption, Toast.LENGTH_SHORT).show();
                phnpay.setBackground(getDrawable(R.drawable.imgborder_def));
                gpay.setBackground(getDrawable(R.drawable.imgborder_def));
                paytm.setBackground(getDrawable(R.drawable.imgborder));

            }
        });
        gpay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                redeemoption = "GooglePay";
                // Toast.makeText(Wallet.this, redeemoption, Toast.LENGTH_SHORT).show();
                phnpay.setBackground(getDrawable(R.drawable.imgborder_def));
                gpay.setBackground(getDrawable(R.drawable.imgborder));
                paytm.setBackground(getDrawable(R.drawable.imgborder_def));

            }
        });

    }

    private void redeemMoney() {
        try {
            int a = Integer.parseInt(walletMoney.getText().toString());

            if (currentDate.equals("14") || currentDate.equals("28")) {
                Toast.makeText(this, redeemoption, Toast.LENGTH_SHORT).show();
                if (!redeemoption.equals("")) {
                    if (a >= 300 || a <=600) {
                        String url = "https://www.datamanagement.ml/redeem.php";
                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jobj = new JSONObject(response);
                                    sucess = jobj.getInt(TAG_SUCESS);
                                    if (sucess == 1) {
                                        setWalletData();
                                        StyleableToast.makeText(Wallet.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG, R.style.successtoast).show();

                                    } else {

                                        StyleableToast.makeText(Wallet.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG, R.style.successtoast).show();

                                    }
                                } catch (JSONException e) {
                                    StyleableToast.makeText(Wallet.this, e.getMessage(), Toast.LENGTH_LONG, R.style.errortoast).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                StyleableToast.makeText(Wallet.this, error.getMessage(), Toast.LENGTH_LONG, R.style.errortoast).show();
                            }
                        }) {
                            public Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("uniqueid", uniqueid);
                                params.put("phoneno", phoneno);
                                params.put("amounttobepaid", "" + amount);
                                params.put("getPoints", ""+redempoints);
                                params.put("reddemOption", redeemoption);
                                return params;
                            }
                        };
                        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
                        requestQueue.add(request);
                    } else {
                        StyleableToast.makeText(Wallet.this, "You Don't Have Enough Points in your Wallet", Toast.LENGTH_LONG, R.style.errortoast).show();
                    }

                } else {

                    StyleableToast.makeText(Wallet.this, "select redeem option", Toast.LENGTH_LONG, R.style.errortoast).show();

                }
            } else {
                StyleableToast.makeText(Wallet.this, "You Can Only Redeem Points Twice in a Month", Toast.LENGTH_LONG, R.style.errortoast).show();

            }
        } catch (Exception e) {
            StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT, R.style.errortoast).show();
        }
    }

    private void setWalletData() {
        String email15 = sharedPreferences.getString(getString(R.string.uemail), "");
        if (!email15.equals("")) {
            try {
                String ExtractDataUrl = "https://www.datamanagement.ml/getUdata.php?email=" + email15;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ExtractDataUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response != null) {
                            try {
                                JSONArray array = new JSONArray(response);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject data = array.getJSONObject(i);
                                    String token = data.getString("token");
                                    //Toast.makeText(Wallet.this, token, Toast.LENGTH_SHORT).show();
                                    if (!token.equals("")) {

                                        walletMoney.setText(data.getString("points"));
                                        points = Double.parseDouble(walletMoney.getText().toString());
                                        amount = points / 10;
                                        //  Toast.makeText(Wallet.this, ""+amount, Toast.LENGTH_SHORT).show();
                                        if (amount <= 0) {
                                            amounttobepaid.setText("₹ " + 0 + "/-");
                                        } else {
                                            amounttobepaid.setText("₹ " + amount + "/-");
                                        }

                                        phoneno = data.getString("phoneno");
                                        uniqueid = data.getString("uniqueid");
                                    } else {
                                        StyleableToast.makeText(getApplicationContext(), "Please Log in", Toast.LENGTH_SHORT, R.style.errortoast).show();


                                    }
                                }
                            } catch (Exception e) {
                                StyleableToast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT, R.style.errortoast).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        StyleableToast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT, R.style.errortoast).show();
                    }
                });
                requestQueue.add(stringRequest);
            } catch (Exception e) {
                StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT, R.style.errortoast).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.help_menu, menu);
        return true;

    }

    public void helpbox() {
        AlertDialog.Builder help = new AlertDialog.Builder(this);
        help.setTitle("Wallet Activity");
        help.setMessage("In this Activity You Can See your Points\n\n" +
                "You Can Redeem Your Points Only Twice in a Month i.e. on every 14th & 28th Date. You Should Have Minimum of 300 Points To Redeem\n\n" +
                "Transaction May Take 2-3 Working Days after Redeem ! \nYou Can Check Points History below Redeem Button ");
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