package com.example.dbconnection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.dbconnection.Activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service
{
    private String IP = IpAddress.getIP(); // "61.255.8.214:27922";

    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification Notifi ;
    String cur_ID = "";

    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "ID";
    private static final String TAG_PD = "PASSWORD";
    private static final String TAG_SEX = "SEX";

    JSONArray peoples = null;
    String date = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cur_ID = intent.getStringExtra("thID");
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_REDELIVER_INTENT; //START_STICKY; //
    }

    //서비스가 종료될 때 할 작업
    @Override
    public void onDestroy() {
        thread.setTime(2500); //25200000
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(MyService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            //데이터베이스에서 캐치가 되면
            long NOW = System.currentTimeMillis();
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date mDate = new Date(NOW);
            date = mFormat.format(mDate);
            getData("http://" + IP + "/mp/lovecall.php?ID=" + cur_ID + "&NOW=" + date);
        }
    };

    protected void showList()
    {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            if(peoples.length() > 0)
            {
                Toast.makeText(MyService.this, "쪽지가 도착했습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}