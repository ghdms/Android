package com.example.dbconnection.Fragment;



import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dbconnection.IpAddress;
import com.example.dbconnection.MailboxAdapter;
import com.example.dbconnection.MailboxMessage;
import com.example.dbconnection.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MailboxActivity extends Fragment {

    private String IP = IpAddress.getIP(); //"61.255.8.214:27922";
    ListView messages;
    MailboxAdapter mailboxAdapter;
    TextView textView;
    private String cur_ID, cur_SEX, cur_MODE;
    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "ASK_ID";
    private static final String TAG_ACK = "ACK_ID";
    private static final String TAG_MSG = "MESSAGE";
    private static final String TAG_ANS = "ANSWER";
    JSONArray peoples = null;

    ArrayList<MailboxMessage> adapter;
    private MailboxMessage selected;
    boolean answer_query;
    String date, answer_result, answer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.activity_mailbox,container,false);

        cur_ID = getArguments().getString("myId");
        cur_SEX = getArguments().getString("SEX");
        cur_MODE = getArguments().getString("MODE");

        messages = (ListView)v.findViewById(R.id.messages);
        textView = (TextView)v.findViewById(R.id.Title);
        adapter = new ArrayList<>();

        long NOW = System.currentTimeMillis();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date mDate = new Date(NOW);
        date = mFormat.format(mDate);

        answer_query = false;

        if(cur_MODE.equals("mail"))
        {
            textView.setText("MESSAGE");
            getData("http://" + IP + "/mp/lovecall.php?ID=" + cur_ID + "&NOW=" + date);

            messages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("메세지");
                    builder.setMessage("당신을 맘에 들어합니다.");
                    selected = (MailboxMessage)(adapterView.getAdapter().getItem(i));
                    builder.setPositiveButton("수락",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String ask_id = selected.getName();
                                    String ack_id = cur_ID;
                                    String msg = selected.getADD2();
                                    answer = "ok";

                                    answer_query = true;
                                    getData("http://" + IP + "/mp/answer.php?ASK_ID=" + ask_id + "&ACK_ID=" + ack_id + "&MESSAGE=" + msg + "&ANSWER=" + answer + "&DT=" + date);
                                }
                            });
                    builder.setNegativeButton("별로에요",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String ask_id = selected.getName();
                                    String ack_id = cur_ID;
                                    String msg = selected.getADD2();
                                    answer = "sorry";

                                    answer_query = true;
                                    getData("http://" + IP + "/mp/answer.php?ASK_ID=" + ask_id + "&ACK_ID=" + ack_id + "&MESSAGE=" + msg + "&ANSWER=" + answer + "&DT=" + date);
                                }
                            });
                    builder.show();
                }
            });
        }

        return v;
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
                if(answer_query)
                {
                    answer_result = dbid;
                    answer_query = false;

                    Toast.makeText(getContext(), answer, Toast.LENGTH_SHORT).show();
                    break;
                }
                MailboxMessage mm;

                String dback, dbmsg, dbans;
                dbmsg = c.getString(TAG_MSG);
                mm = new MailboxMessage(dbid, dbmsg, cur_ID);

                adapter.add(mm);
            }
            mailboxAdapter = new MailboxAdapter(getContext(),adapter);
            messages.setAdapter(mailboxAdapter);
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
