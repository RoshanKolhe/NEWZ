package com.Newz.newz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;

public class TaskDialogBox extends Fragment {

    Activity activity;
    Context context;
    AlertDialog alertDialog;
    Button min5,min10,min15;
    ImageButton back;
    TextView username;
    SharedPreferences sharedPreferences;
    String uniqueid;
    RequestQueue requestQueue;
    public TaskDialogBox(Activity activity,Context context) {
        this.activity = activity;
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_dialog_box, container, false);
//        min5=view.findViewById(R.id.min5);
//        min10=view.findViewById(R.id.min10);
//        min15=view.findViewById(R.id.min15);
//        min5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "5min", Toast.LENGTH_SHORT).show();
//            }
//        });
//        min10.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "10min", Toast.LENGTH_SHORT).show();
//            }
//        });
//        min15.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "15min", Toast.LENGTH_SHORT).show();
//            }
//        });

        startLoadingDialog(container);
        return view;

    }

    void startLoadingDialog( ViewGroup container){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.task_dialog_box, container, false);
        back=view.findViewById(R.id.back);
        min5=view.findViewById(R.id.min5);
        min10=view.findViewById(R.id.min10);
        min15=view.findViewById(R.id.min15);
        username=view.findViewById(R.id.username);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        uniqueid=sharedPreferences.getString("username","Username");
        username.setText(uniqueid);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
//        min5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toast.makeText(context, "5min", Toast.LENGTH_SHORT).show();
//               // startActivity(new Intent(activity, Home.class));
//                flag5=1;
//                dismissDialog();
//            }
//        });
//        min10.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "10min", Toast.LENGTH_SHORT).show();
//            }
//        });
//        min15.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "15min", Toast.LENGTH_SHORT).show();
//            }
//        });
        builder.setView(view);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }



    void dismissDialog(){
        alertDialog.dismiss();
    }

}

