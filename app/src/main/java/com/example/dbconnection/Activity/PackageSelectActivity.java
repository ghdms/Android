package com.example.dbconnection.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dbconnection.R;

public class PackageSelectActivity extends AppCompatActivity {

    private String myId, partnerId, cur_SEX, cur_KAKAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_select);
    }

    public void mOnClick(View v)
    {
        Intent intent = getIntent();
        myId = intent.getStringExtra("myId");
        partnerId = intent.getStringExtra("partnerId");
        cur_KAKAO = intent.getStringExtra("KAKAO");
        cur_SEX = intent.getStringExtra("SEX");

        switch (v.getId())
        {
            case R.id.package1:
                show1();
                break;
            case R.id.package2:
                show2();
                break;
            case R.id.package3:
                show3();
                break;
            case R.id.package4:
                show4();
                break;
        }
    }

    void show4()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Package 4");
        builder.setMessage("Package 4는 커피 + 밥 + 영화 + 술 입니다.");
        builder.setPositiveButton("진행",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),DatePickActivity.class);
                        intent.putExtra("id",4);
                        intent.putExtra("myId",myId);
                        intent.putExtra("partnerId",partnerId);
                        intent.putExtra("KAKAO", cur_KAKAO);
                        intent.putExtra("SEX", cur_SEX);
                        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP));
                    }
                });
        builder.setNegativeButton("다른 패키지 선택",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
    void show3()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Package 3");
        builder.setMessage("Package 3은 커피 + 밥 + 영화 데이트 입니다.");
        builder.setPositiveButton("진행",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),DatePickActivity.class);
                        intent.putExtra("id",3);
                        intent.putExtra("myId",myId);
                        intent.putExtra("partnerId",partnerId);
                        intent.putExtra("KAKAO", cur_KAKAO);
                        intent.putExtra("SEX", cur_SEX);
                        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP));                    }
                });
        builder.setNegativeButton("다른 패키지 선택",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
    void show2()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Package 2");
        builder.setMessage("Package 2는 커피 + 밥 입니다.");
        builder.setPositiveButton("진행",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),DatePickActivity.class);
                        intent.putExtra("id",2);
                        intent.putExtra("myId",myId);
                        intent.putExtra("partnerId",partnerId);
                        intent.putExtra("KAKAO", cur_KAKAO);
                        intent.putExtra("SEX", cur_SEX);
                        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP));                    }
                });
        builder.setNegativeButton("다른 패키지 선택",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
    void show1()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Package 1");
        builder.setMessage("Package 1은 커피 입니다.");
        builder.setPositiveButton("진행",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),DatePickActivity.class);
                        intent.putExtra("id",1);
                        intent.putExtra("myId",myId);
                        intent.putExtra("partnerId",partnerId);
                        intent.putExtra("KAKAO", cur_KAKAO);
                        intent.putExtra("SEX", cur_SEX);
                        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP));                    }
                });
        builder.setNegativeButton("다른 패키지 선택",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
}
