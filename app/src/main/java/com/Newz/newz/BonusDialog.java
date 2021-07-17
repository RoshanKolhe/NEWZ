package com.Newz.newz;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class BonusDialog {
    Activity activity;
    AlertDialog alertDialog;

    public BonusDialog(Activity activity) {
        this.activity = activity;
    }

    void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.bonus,null));
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }

    void dismissDialog(){
        alertDialog.dismiss();
    }
}
