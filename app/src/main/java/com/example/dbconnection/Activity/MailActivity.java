package com.example.dbconnection.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dbconnection.R;

public class MailActivity extends AppCompatActivity {

    ImageView senderPortrait;
    TextView senderName, senderPlan;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        senderPortrait = (ImageView)findViewById(R.id.sender_portrait);
        senderName = (TextView)findViewById(R.id.sender_name);
        senderPlan = (TextView)findViewById(R.id.sender_plan);

        intent = getIntent();
        int imgsrc = intent.getIntExtra("image", 0);
        String usrname = intent.getStringExtra("name");

        senderPortrait.setImageResource(imgsrc);
        senderName.setText(usrname);


    }
}
