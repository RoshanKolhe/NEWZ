package com.Newz.newz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ContactUs extends AppCompatActivity {

    Toolbar toolbar;
    TextView abtapp,contactus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactUs.this,Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        abtapp=findViewById(R.id.abtapp);
        contactus=findViewById(R.id.contact);

        abtapp.setText("1) NEWZ is a News/Articles Providing based application platform downloadable from play store and owned by Us.\n\n" +
                "2) The app does not provide or publish any news or content but it's just a brief summary of news/articles available in public domain. The news content will be copyrighted and will be only available for non - commercial and personal use.\n\n" +
                "3) The app may include links and advertisements to other application or websites.\n");

        contactus.setText("For Any Queries or Feedback Contact us at newz.console@gmail.com");

    }

//    public void helpbox(){
//        AlertDialog.Builder help = new AlertDialog.Builder(this);
//        help.setTitle("Contact Us");
//        help.setMessage("In this Activity Information About Our Application And Our Contact Details Are Provided !");
//        help.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        help.create().show();
//
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater= getMenuInflater();
//        inflater.inflate(R.menu.help_menu,menu);
//        return true;
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.help:
//                helpbox();
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }
//
//    }
}