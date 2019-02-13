package com.example.dbconnection.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dbconnection.Activity.PackageSelectActivity;
import com.example.dbconnection.IpAddress;
import com.example.dbconnection.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RandomMatchActivity extends Fragment {
    private String IP = IpAddress.getIP(); //"61.255.8.214:27922";

    private ImageView userPortrait;
    private TextView userName;
    private Button selectButton, passButton, btnMailbox, btnRecord, btnDate, btnMyInfo;
    private String cur_ID, cur_SEX, cur_KAKAO;
    private ArrayList<PARTNER> Partner;

    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "ID";
    private static final String TAG_INTRO = "INTRO";
    private int partner_idx = 0;
    JSONArray peoples = null;

    Bitmap bmImg;
    private int partner_num;

    public class PARTNER
    {
        String ID;
        String INTRO;

        String getID() {return ID;}
        String getINTRO() {return INTRO;}

        PARTNER(String id, String intro)
        {
            ID = id;
            INTRO = intro;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.activity_random_match, container, false);

        partner_num = 0;
        Partner = new ArrayList<PARTNER>();

        final Intent get = getActivity().getIntent();
        cur_ID = get.getStringExtra("ID");
        cur_SEX = get.getStringExtra("SEX");
        cur_KAKAO = get.getStringExtra("KAKAO");
        Log.d("test",cur_ID + ":: " + cur_SEX + ":: " + cur_KAKAO);

        userPortrait = (ImageView) v.findViewById(R.id.user_portrait);
        userName = (TextView) v.findViewById(R.id.user_name);
        selectButton = (Button) v.findViewById(R.id.pick_button);
        passButton = (Button) v.findViewById(R.id.pass_button);

        getData("http://" + IP + "/mp/Search.php?SEX=" + cur_SEX);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int random = (int)(Math.random()*partner_num);
                partner_idx = random;

                back change = new back();
                change.execute("http://" + IP + "/mp/image/" + Partner.get(random).getID() + ".jpg");
            }
        });
    return v;
    }

    void show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("데이트 명세 시작");
        builder.setMessage("1. 데이트 날짜를 정하세요!\n2. 데이트 장소를 정하세요!\n3. 보내기~! 및 기다리기");
        builder.setPositiveButton("시작하자",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getContext(),PackageSelectActivity.class);
                        intent.putExtra("myId",cur_ID);
                        intent.putExtra("KAKAO", cur_KAKAO);
                        intent.putExtra("partnerId", Partner.get(partner_idx).getID());
                        intent.putExtra("SEX", cur_SEX);
                        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | intent.FLAG_ACTIVITY_SINGLE_TOP));
                    }
                });
        builder.setNegativeButton("다시생각해볼게요",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
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
                String dbintro = c.getString(TAG_INTRO);

                Partner.add(new PARTNER(dbid, dbintro));
                partner_num++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        int random = (int)(Math.random()*partner_num);
        back init = new back();
        init.execute("http://" + IP + "/mp/image/" + Partner.get(random).getID() + ".jpg");
        partner_idx = random;
        userName.setText(Partner.get(random).getINTRO());
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
                Log.d("debugging","1 : " + result);
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public class back extends AsyncTask<String, Integer,Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try
            {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);

                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            return bmImg;
        }

        protected void onPostExecute(Bitmap img){
            //userPortrait.setImageBitmap(bmImg);
        }
    }

    final Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            Log.d("ptest", partner_idx+"");

            userName.setText(Partner.get(partner_idx).getINTRO());
            userPortrait.setImageBitmap(bmImg);
            Toast.makeText(getContext(), "pass", Toast.LENGTH_SHORT).show();
        }
    };

}