package com.example.dbconnection;

public class IpAddress {
    public static final String IP = "192.168.56.1";
    public static final String PORT = "27922";
    public static final String FTPIP = "192.168.56.1";
    public static final int FTPPORT = 27925;

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
}