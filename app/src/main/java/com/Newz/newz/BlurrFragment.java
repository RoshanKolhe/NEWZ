package com.Newz.newz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;


public class BlurrFragment extends SupportBlurDialogFragment {
    private RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int sucess;
    private String TAG_SUCESS="sucess";
    private String TAG_MESSAGE="message";
    private String tag_json_obj="json_obj_req";
    public BlurrFragment() {
        // Required empty public constructor
    }
    String uniqueid;
    TextView profile,wallet,tasks,logout,bookmarks;
    Button back;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blurr, container, false);
        profile=view.findViewById(R.id.profile);
        wallet=view.findViewById(R.id.wallet);
        tasks=view.findViewById(R.id.tasks);
        logout=view.findViewById(R.id.logout);
        bookmarks=view.findViewById(R.id.bookmarks);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();
        requestQueue = Volley.newRequestQueue(getContext());
        back=view.findViewById(R.id.back);
        // getUniqueId();

        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),Bookmarks.class));
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFlags()) {
                    androidx.appcompat.app.AlertDialog.Builder passwordreset = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
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
                            startActivity(new Intent(getActivity(), Profile.class));
                        }
                    });
                    passwordreset.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    passwordreset.create().show();
                }else{
                    startActivity(new Intent(getActivity(), Profile.class));
                }
            }
        });
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(checkFlags()) {
                    androidx.appcompat.app.AlertDialog.Builder passwordreset = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
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
                            Intent intent=new Intent(getContext(),Wallet.class);
                            startActivity(intent);
                        }
                    });
                    passwordreset.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    passwordreset.create().show();
                }else{
                    startActivity(new Intent(getContext(),Wallet.class));
                }
            }
        });
        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFlags()) {
                    androidx.appcompat.app.AlertDialog.Builder passwordreset = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
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
                            Intent intent=new Intent(getContext(),Tasks.class);
                            startActivity(intent);
                        }
                    });
                    passwordreset.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    passwordreset.create().show();
                }else{
                    Intent intent=new Intent(getContext(),Tasks.class);
                    startActivity(intent);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), Home.class);
                long time_left_in_milis = Long.parseLong(sharedPreferences.getString(getString(R.string.timeLeft), "0"));
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
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
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
                                                Intent intent =new Intent(getContext(),Login.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);

                                                StyleableToast.makeText(getContext(), jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();

                                            } else {
                                                StyleableToast.makeText(getContext(), jobj.getString(TAG_MESSAGE), Toast.LENGTH_LONG,R.style.successtoast).show();
                                            }
                                        } catch (JSONException e) {
                                            StyleableToast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        StyleableToast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                                    }
                                });

                                requestQueue.add(stringRequest);
                            }catch(Exception e){
                                StyleableToast.makeText(getContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
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

        return view;
    }

    private void getUniqueId() {
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
                                //Toast.makeText(Wallet.this, token, Toast.LENGTH_SHORT).show();
                                if(!token.equals("")){
                                    uniqueid=data.getString("uniqueid");

                                }
                                else{
                                    StyleableToast.makeText(getContext(),"Please Log In", Toast.LENGTH_LONG,R.style.errortoast).show();

                                }
                            }
                        } catch (Exception e) {
                            StyleableToast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();

                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    StyleableToast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG,R.style.errortoast).show();
                }
            });
            requestQueue.add(stringRequest);
        }catch(Exception e){
            StyleableToast.makeText(getContext(), "Please Check internet Connectivity", Toast.LENGTH_SHORT,R.style.errortoast).show();
        }
        }
    }
    public boolean checkFlags(){
        return sharedPreferences.getString(getString(R.string.taskflag5min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag10min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag15min), "").equals("true") ||
                sharedPreferences.getString(getString(R.string.taskflag20min), "").equals("true");
    }
}