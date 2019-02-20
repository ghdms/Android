package com.example.dbconnection;

public class IpAddress {
    public static final String IP = "10.231.4.6";
    public static final String PORT = "27922";
    public static final String FTPIP = "10.231.4.6";
    public static final int FTPPORT = 27925;
    public static String cur_INTRO = "";

    public static String getIP()
    {
        return IP;
    }

    public static String getPORT()
    {
        return PORT;
    }

    public static String getFTPIP()
    {
        return FTPIP;
    }

    public static int getFTPPORT()
    {
        return FTPPORT;
    }

    public static void setCur_INTRO(String intro)
    {
        cur_INTRO = intro;
    }

    public static String getCur_INTRO()
    {
        return cur_INTRO;
    }
}
