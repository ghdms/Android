package com.example.dbconnection.Fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.dbconnection.MyService;
import com.example.dbconnection.R;

public class Station extends AppCompatActivity {

    private ScheduleManagement scheduleManagement;
    private YesOrNo yesOrNo;
    private RandomMatchActivity randomMatch;
    private MailboxActivity mailboxActivity;
    private MyInformation myInformation;
    private String cur_ID, cur_SEX, cur_KAKAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        final Intent get = getIntent();
        cur_ID = get.getStringExtra("ID");
        cur_SEX = get.getStringExtra("SEX");
        cur_KAKAO = get.getStringExtra("KAKAO");

        //fragment를 위한 생성자 생성
        scheduleManagement = new ScheduleManagement();
        yesOrNo = new YesOrNo();
        randomMatch = new RandomMatchActivity();
        mailboxActivity = new MailboxActivity();
        myInformation = new MyInformation();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().add(R.id.container,randomMatch).commit();

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("매칭"));
        tabs.addTab(tabs.newTab().setText("쪽지함"));
        tabs.addTab(tabs.newTab().setText("전적"));
        tabs.addTab(tabs.newTab().setText("일정"));
        tabs.addTab(tabs.newTab().setText("내정보"));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment selected = null;
                String mode="";
                if(position ==0)
                {
                    selected = randomMatch;
                }
                else if(position==1)
                {
                    selected = mailboxActivity;
                    mode = "mail";
                }
                else if(position==2)
                {
                    selected = yesOrNo;
                    mode = "record";
                }
                else if(position ==3)
                {
                    selected = scheduleManagement;
                    mode = "schedule";
                }
                else if(position==4)
                {
                    selected = myInformation;
                    mode = "myinfo";
                }

                Bundle bundle = new Bundle();
                bundle.putString("myId",cur_ID);
                bundle.putString("KAKAO", cur_KAKAO);
                bundle.putString("SEX",cur_SEX);
                bundle.putString("MODE",mode);
                selected.setArguments(bundle);
                Log.d("test","mode : " + mode);

                getSupportFragmentManager().beginTransaction().replace(R.id.container,selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            if(keyCode == KeyEvent.KEYCODE_BACK){
                logOutQuestion();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void logOutQuestion(){
        boolean tmpQ = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("종료");
        builder.setMessage("로그인을 유지하시겠습니까?");
        builder.setPositiveButton("네",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = auto.edit();
                        editor.clear();
                        editor.commit();

                        if(isServiceRunningCheck())
                        {
                            Intent intent_service_start = new Intent(getApplicationContext(), MyService.class);
                            intent_service_start.putExtra("thID", cur_ID);
                            stopService(intent_service_start.setFlags(intent_service_start.FLAG_ACTIVITY_CLEAR_TOP | intent_service_start.FLAG_ACTIVITY_SINGLE_TOP));
                        }
                        finishAffinity();
                    }
                });
        builder.show();
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.dbconnection.MyService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
