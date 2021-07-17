package com.Newz.newz;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Profile extends AppCompatActivity {

    Button back,logout,help,refer;
    private RequestQueue requestQueue;
    TextView email1,phno,contact;
    TextView points,username;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int sucess;
    private int STORAGE_PERMISSION_CODE=1;
    private static final int STORAGE_CODE=1000;
    private String TAG_SUCESS="sucess";
    private String TAG_MESSAGE="message";
    private String tag_json_obj="json_obj_req";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        back=findViewById(R.id.back);
        logout=findViewById(R.id.logout);

        points=findViewById(R.id.points);
        email1=(TextView) findViewById(R.id.email12);
        help=findViewById(R.id.helpic);
        phno=(TextView) findViewById(R.id.phno);
        username=findViewById(R.id.username);
        refer=findViewById(R.id.refer);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        contact=findViewById(R.id.contacts);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,ContactUs.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this,Home.class));
            }
        });

        setProfileData();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(Profile.this)
                        .setTitle("Logout")
                        .setMessage("Are You Sure Want To Logout ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                String email = sharedPreferences.getString(getString(R.string.uemail), "");
                                String ExtractDataUrl="https://www.datamanagement.ml/logout.php?email="+email;
                                StringRequest stringRequest=new StringRequest(Request.Method.POST, ExtractDataUrl, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jobj = new JSONObject(response);
                                            sucess = jobj.getInt(TAG_SUCESS);
//                                            Toast.makeText(getContext(), sucess, Toast.LENGTH_SHORT).show();
                                            if (sucess == 1) {
                                                editor.putString(getString(R.string.taskflag5min), "false");
                                                editor.commit();
                                                editor.putString(getString(R.string.taskflag10min), "false");
                                                editor.commit();
                                                editor.putString(getString(R.string.taskflag15min), "false");
                                                editor.commit();
                                                editor.putString(getString(R.string.taskflag20min), "false");
                                                editor.commit();
                                                editor.putString(getString(R.string.uemail),"");
                                                editor.commit();
                                                editor.putString(getString(R.string.upwd),"");
                                                editor.commit();
                                                editor.putString(getString(R.string.uniqueid),"");
                                                editor.commit();
                                                startActivity(new Intent(Profile.this,Login.class));
                                                StyleableToast.makeText(Profile.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                                            } else {
                                                StyleableToast.makeText(Profile.this, jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                                            }
                                        } catch (JSONException e) {
                                            StyleableToast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        StyleableToast.makeText(Profile.this, error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                                    }
                                });

                                requestQueue.add(stringRequest);
                            }catch(Exception e){
                                StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
                            }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpbox();
            }
        });

        refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent shareIntent;
//                Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
//                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/share.jpg";
//                OutputStream out = null;
//                File file=new File(path);
//                try {
//                    out = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                    out.flush();
//                    out.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                path=file.getPath();
//                Uri bmpUri = Uri.parse("file://"+path);
//                shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//                shareIntent.putExtra(Intent.EXTRA_TEXT,"Hey please check this application " + "https://play.google.com/store/apps/details?id=" +getPackageName());
//                shareIntent.setType("image/jpg");
//                startActivity(Intent.createChooser(shareIntent,"Share with"));
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permimssion = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permimssion, STORAGE_CODE);
                    } else {

                        shareapp();
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "*Download This Fantastic Newz Application That lets You Earn while Reading !*\n\n" +
//                        "*Download Now To Get 50 Points as Joining Bonus*"+
//                        "\nUse My Code While Registration : "+username.getText().toString()+"@"+phno.getText().toString()+
//                        "\n\n"+"https://play.google.com/store/apps/details?id=com.example.newz");
//                sendIntent.setType("text/plain");
//
//                Intent shareIntent = Intent.createChooser(sendIntent, null);
//                startActivity(shareIntent);
                    }
                }
            }
        });

    }

    private void shareapp() {
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.share);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), b, "Title", null);
        Uri imageUri = Uri.parse(path);
//        Picasso.get().load("http://datamanagement.ml/images/download.jpg").into(new Target() {
//            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("image/*");
//                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
//                startActivity(Intent.createChooser(i, "Share Image"));
//            }
//            @Override
//            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//            }
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//    });
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        share.putExtra(Intent.EXTRA_TEXT, "*Download This Fantastic Newz Application That lets You Earn while Reading !*\n\n" +
                "*Download Now To Get 50 Points as Joining Bonus*" +
                "\nUse My Code While Registration : " + username.getText().toString() + "@" + phno.getText().toString() +
                "\n\n" + "https://play.google.com/store/apps/details?id=com.Newz.newz");
        startActivity(Intent.createChooser(share, "Share"));

    }

        public Uri getLocalBitmapUri(Bitmap bmp) {
            Uri bmpUri = null;
            try {
                File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }

    public void helpbox(){
        androidx.appcompat.app.AlertDialog.Builder help = new androidx.appcompat.app.AlertDialog.Builder(this);
        help.setTitle("Profile Activity");
        help.setMessage("In this Activity You Can See your Information or You Can Logout From Profile\n\n" +
                "You Can Share Your Unique ID from Referral option To Earn 50 Points \n\n" );
        help.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        help.create().show();

    }

    private void setProfileData() {
         String email15 = sharedPreferences.getString(getString(R.string.uemail), "");
        if (!email15.equals("")) {
            try{
            String ExtractDataUrl = "https://www.datamanagement.ml/getUdata.php?email="+email15;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ExtractDataUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.getJSONObject(i);
                                String token=data.getString("token");
                                //Toast.makeText(Profile.this, token, Toast.LENGTH_SHORT).show();
                                if(!token.equals("")){
                                    email1.setText(data.getString("email"));
                                    phno.setText(data.getString("phoneno"));
                                    points.setText(data.getString("points"));
                                    username.setText(data.getString("username"));
                                }
                                else{
                                    StyleableToast.makeText(Profile.this, "Please Log In", Toast.LENGTH_LONG,R.style.errortoast).show();

                                }
                            }
                        } catch (Exception e) {
                            StyleableToast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();

                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    StyleableToast.makeText(Profile.this, error.getMessage(), Toast.LENGTH_LONG,R.style.successtoast).show();

                }
            });
            requestQueue.add(stringRequest);
        }catch(Exception e){
            StyleableToast.makeText(getApplicationContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
        }
        }
    }
}