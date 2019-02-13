package com.example.dbconnection;

import android.support.v7.app.AppCompatActivity;

public class MailboxMessage extends AppCompatActivity {
    private String user_name = "";
    private String ADD = "";
    private String ADD2 = "";
    private String ADD3 = "";
    private String MY = "";

    public String getName() {return user_name;}
    public String getADD() {return ADD;}
    public String getADD2() {return ADD2;}
    public String getADD3() {return ADD3;}
    public String getMY() {return MY;}

    public MailboxMessage(String un, String add2, String my){
        this.user_name = un;
        this.ADD2 = add2;
        this.MY = my;
    }

    public MailboxMessage(String user_name, String add, String add3, String my){
        this.user_name = user_name;
        this.ADD = add;
        this.ADD3 = add3;
        this.MY = my;
    }

    public MailboxMessage(String user_name, String add, String add2, String add3, String my){
        this.user_name = user_name;
        this.ADD = add;
        this.ADD2 = add2;
        this.ADD3 = add3;
        this.MY = my;
    }
}