package com.example.dbconnection.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.dbconnection.IpAddress;
import com.example.dbconnection.MailboxAdapter;
import com.example.dbconnection.MailboxMessage;
import com.example.dbconnection.R;
import com.example.dbconnection.TAG_;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class YesOrNo extends Fragment {

    private String IP = IpAddress.getIP(); //"61.255.8.214:27922";
    private String cur_ID, cur_MODE;
    private String myJSON;
    private ListView messages;
    private JSONArray peoples = null;
    MailboxAdapter mailboxAdapter;

    ArrayList<MailboxMessage> adapter;
    private MailboxMessage selected;

    private boolean chk = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.activity_yes_or_no,container,false);

        messages = (ListView)v.findViewById(R.id.messages);

        cur_ID = getArguments().getString("myId");
        cur_MODE = getArguments().getString("MODE");

        adapter = new ArrayList<>();

        if(cur_MODE.equals("record"))
        {
            getData("http://" + IP + "/mp/record.php?ID=" + cur_ID);

            messages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selected = (MailboxMessage)(adapterView.getAdapter().getItem(i));
                    if(selected.getADD3().equals("ok") && selected.getADD().equals(cur_ID))
                    {
                        final View layout = inflater.inflate(R.layout.ratingbar, null);
                        final double[] tmp = {3.0};
                        RatingBar rb = (RatingBar) layout.findViewById(R.id.ratingBar);
                        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                tmp[0] = (double)rating;
                                chk = true;
                                getData("http://" + IP + "/mp/updateScore.php?ASK_ID=" + selected.getName() + "&ACK_ID=" + cur_ID + "&MESSAGE=" + selected.getADD2() + "&RATING=" + tmp[0]);
                                //update couple set rating = tmp[0] where ASK_ID = selected.getName() and ACK_ID = cur_ID and message = selected.getADD2() and answer = 'ok' and dt < date;
                            }
                        });

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(layout);
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getContext(), tmp[0] + "", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.show();
                    }
                }
            });
        }

        return v;
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
                if(!chk) {
                    myJSON = result;
                    showList();
                }
                chk = false;
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    protected void showList()
    {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_.getTagResults());

            for (int i = 0; i < peoples.length(); i++)
            {
                JSONObject c = peoples.getJSONObject(i);
                MailboxMessage mm;

                String dbid = c.getString(TAG_.getTagAsk());
                String dback = c.getString(TAG_.getTagAck());
                String dbans = c.getString(TAG_.getTagAns());
                mm = new MailboxMessage(dbid, dback, dbans, cur_ID);

                String dbmsg = c.getString(TAG_.getTagMsg());
                mm.setADD2(dbmsg);

                double dbscore = c.getDouble(TAG_.getTagScore());
                mm.setScore(dbscore);

                adapter.add(mm);
            }
            mailboxAdapter = new MailboxAdapter(getContext(),adapter);
            messages.setAdapter(mailboxAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
