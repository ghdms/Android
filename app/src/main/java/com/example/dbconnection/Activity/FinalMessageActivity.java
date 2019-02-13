package com.example.dbconnection.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dbconnection.Fragment.Station;
import com.example.dbconnection.IpAddress;
import com.example.dbconnection.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FinalMessageActivity extends AppCompatActivity {

    private String IP = IpAddress.getIP(); // "61.255.8.214:27922";
    String myJSON;
    JSONArray peoples = null;

    private Button submit, back;
    private TextView final_msg;
    private String myId, partnerId, date, time, cur_SEX, cur_KAKAO, first, cafe, meal, movie, pub, dbmsg;
    String message = "";

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "ID";
    int temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_message);
        submit = (Button)findViewById(R.id.btnSubmit);
        back = (Button)findViewById(R.id.btnBack);
        final_msg = (TextView) findViewById(R.id.textFinal);

        Intent intent = getIntent();

        first = "";
        cafe = "";
        meal = "";
        movie = "";
        pub = "";
        dbmsg = "";

        temp = intent.getIntExtra("increment", 0);
        myId = intent.getStringExtra("myId");
        partnerId = intent.getStringExtra("partnerId");
        cur_SEX = intent.getStringExtra("SEX");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        cur_KAKAO = intent.getStringExtra("KAKAO");

        message += "날짜 : " + date + "\n";
        message += "시간 : " + time + "\n";
        first = intent.getStringExtra(intent.getStringExtra("first"));
        message += "첫만남 : " + first + "\n\n";
        dbmsg = date + " " + time + " <" + first + ">";

        if (temp > 0){
            cafe = intent.getStringExtra("cafe");
            message += "카페 : " + cafe +"\n";
            dbmsg += " 카페 : " + cafe;
        }
        if (temp > 1){
            meal = intent.getStringExtra("meal");
            message += "식사 : " + meal + "\n";
            dbmsg += " 식사 : " + meal;
        }
        if (temp > 2){
            movie = intent.getStringExtra("movie");
            message += "영화 : " + movie + "\n";
            dbmsg += " 영화 : " + movie;
        }
        if (temp > 3){
            pub = intent.getStringExtra("pub");
            message += "주점 : " + pub + "\n";
            dbmsg += " 주점 : " + pub;
        }
        message += "카카오톡 ID : " + cur_KAKAO;
        dbmsg += " 카카오톡 ID : " + cur_KAKAO;

        final_msg.setText(message);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData("http://" + IP + "/mp/Match.php?ASK_ID=" + myId + "&ACK_ID=" + partnerId + "&MESSAGE=" + dbmsg + "&DT=" + date + " " + time); //수정 필요
                Intent intent = new Intent(getApplicationContext(), Station.class);
                intent.setAction("FINAL");
                intent.putExtra("ID", myId);
                intent.putExtra("SEX", cur_SEX);
                intent.putExtra("KAKAO", cur_KAKAO);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    protected void showList()
    {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++)
            {
                JSONObject c = peoples.getJSONObject(i);
                String dbid = c.getString(TAG_ID);

                if(dbid.equals("true"))
                {
                    Toast.makeText(getApplicationContext(), "요청 성공", Toast.LENGTH_SHORT).show();
                    break;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "요청 실패", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

        } catch (JSONException e) {
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
