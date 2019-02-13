package com.example.dbconnection.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;

import com.example.dbconnection.R;

public class DatePickActivity extends AppCompatActivity {

    CalendarView datePicker;
    private int tmp;
    private String myId, partnerId, cur_SEX, cur_KAKAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick);

        Intent intent = getIntent();

        tmp = intent.getIntExtra("id",-1);
        datePicker = (CalendarView)findViewById(R.id.date_picker);

        myId = intent.getStringExtra("myId");
        partnerId = intent.getStringExtra("partnerId");
        cur_SEX = intent.getStringExtra("SEX");
        cur_KAKAO = intent.getStringExtra("KAKAO");

        Log.d("ptest", partnerId);
        datePicker.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                Intent intent = new Intent(getApplicationContext(), PackageDetailsActivity.class);
                intent.putExtra("MODE", "DATE");
                intent.putExtra("year", year);
                intent.putExtra("month", month+1);
                intent.putExtra("day", day);
                intent.putExtra("id",tmp);
                intent.putExtra("date", String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day));
                intent.putExtra("myId",myId);
                intent.putExtra("partnerId",partnerId);
                intent.putExtra("SEX", cur_SEX);
                intent.putExtra("KAKAO", cur_KAKAO);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();

        tmp = intent.getIntExtra("id",-1);
        datePicker = (CalendarView)findViewById(R.id.date_picker);

        myId = intent.getStringExtra("myId");
        partnerId = intent.getStringExtra("partnerId");
        cur_SEX = intent.getStringExtra("SEX");
        Log.d("ptest", partnerId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();

        tmp = intent.getIntExtra("id",-1);
        datePicker = (CalendarView)findViewById(R.id.date_picker);

        myId = intent.getStringExtra("myId");
        partnerId = intent.getStringExtra("partnerId");
        cur_SEX = intent.getStringExtra("SEX");
        Log.d("ptest", partnerId);
    }
}