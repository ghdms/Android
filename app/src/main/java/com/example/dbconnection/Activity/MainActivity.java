package com.example.dbconnection.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dbconnection.Fragment.Station;
import com.example.dbconnection.IpAddress;
import com.example.dbconnection.R;
import com.example.dbconnection.TAG_;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private String IP = IpAddress.getIP(); // "61.255.8.214:27922";
    String myJSON;

    private Button signIn_;
    private Button logIn_;
    private EditText userID, userPD;
    private String id, pd, sex, kakao, intro;
    String autoID, autoPD;

    JSONArray peoples = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signIn_ = (Button) findViewById(R.id.btnSignIn);
        logIn_ = (Button) findViewById(R.id.btnLogIn);
        userID = (EditText) findViewById(R.id.user_ID);
        userPD = (EditText) findViewById(R.id.user_PD);

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        autoID = auto.getString("inputID", null);
        autoPD = auto.getString("inputPD", null);

        signIn_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });

        logIn_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = userID.getText().toString();
                pd = userPD.getText().toString();
                if(id == null || pd == null)
                {
                    Toast.makeText(getApplication(), "로그인 실패", Toast.LENGTH_SHORT).show();
                }
                else if(id.equals("") || pd.equals(""))
                {
                    Toast.makeText(getApplication(), "로그인 실패", Toast.LENGTH_SHORT).show();
                }
                else {
                    getData("http://" + IP + "/mp/Login.php?ID=" + id + "&PW=" + pd); //수정 필요
                }
            }
        });

        if(autoID != null && autoPD != null)
        {
            id = autoID;
            pd = autoPD;
            kakao = auto.getString("inputKA", null);
            sex = auto.getString("inputSEX", null);
            intro = auto.getString("inputINTRO", null);
            IpAddress.setCur_INTRO(intro);

            Toast.makeText(MainActivity.this, autoID +"님 자동 로그인", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Station.class);
            intent.putExtra("ID", id);
            intent.putExtra("KAKAO", kakao);
            intent.putExtra("SEX", sex);
            intent.putExtra("INTRO", intro);
            intent.setAction("MAIN");
            startActivity(intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP));
        }
    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_.getTagResults());

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                String dbid = c.getString(TAG_.getTagId());
                String dbpd = c.getString(TAG_.getTagPd());
                kakao = c.getString(TAG_.getTagKakao());
                sex = c.getString(TAG_.getTagSex());
                intro = c.getString(TAG_.getTagIntro());

                if (id.equals(dbid) && pd.equals(dbpd)) {
                    if(autoID == null && autoPD == null)
                    {
                        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = auto.edit();
                        editor.putString("inputID", id);
                        editor.putString("inputPD", pd);
                        editor.putString("inputKA", kakao);
                        editor.putString("inputSEX", sex);
                        editor.putString("inputINTRO", intro);
                        editor.commit();
                    }

                    IpAddress.setCur_INTRO(intro);
                    Intent intent = new Intent(getApplicationContext(), Station.class);
                    intent.putExtra("ID", id);
                    intent.putExtra("KAKAO", kakao);
                    intent.putExtra("SEX", sex);
                    intent.putExtra("INTRO", intro);
                    intent.setAction("MAIN");
                    startActivity(intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP));
                } else if (dbid.equals("") || dbpd.equals("") || kakao.equals("") || sex.equals("")) {
                    Toast.makeText(getApplication(), "로그인 실패", Toast.LENGTH_SHORT).show();
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