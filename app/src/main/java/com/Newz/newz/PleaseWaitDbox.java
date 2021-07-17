package com.Newz.newz;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class PleaseWaitDbox {
    Activity activity;
    AlertDialog alertDialog;

    public PleaseWaitDbox(Activity activity) {
        this.activity = activity;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.please_wait_dialog,null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    void dismissDialog(){
        alertDialog.dismiss();
    }
}
