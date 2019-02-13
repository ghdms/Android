package com.example.dbconnection.Activity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dbconnection.IpAddress;
import com.example.dbconnection.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PackageDetailsActivity extends AppCompatActivity {
    private String IP = IpAddress.getIP(); // "61.255.8.214:27922";
    Button cafeButton, mealButton, movieButton, pubButton, nextButton,btnBack;
    TextView cafeSelection, mealSelection, movieSelection, pubSelction;
    int increment = 0;
    TimePickerDialog timePicker;
    private String myId, partnerId, cur_SEX, cur_KAKAO;

    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_NAME = "NAME";
    JSONArray peoples = null;
    ArrayList<String> store_name;

    boolean chkc = false, chkf = false, chkt = false, chkb = false;
    Intent send;
    String date;

    @Override
    protected void onStart() {
        super.onStart();
        increment = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        increment = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);
        store_name = new ArrayList<String>();

        cafeButton = (Button)findViewById(R.id.cafe_button);
        mealButton = (Button)findViewById(R.id.meal_button);
        movieButton = (Button)findViewById(R.id.movie_button);
        pubButton = (Button)findViewById(R.id.pub_button);
        nextButton = (Button)findViewById(R.id.next_button);
        btnBack = (Button)findViewById(R.id.btnBack);

        cafeSelection = (TextView)findViewById(R.id.cafe_selection);
        mealSelection = (TextView)findViewById(R.id.meal_selection);
        movieSelection = (TextView)findViewById(R.id.movie_selection);
        pubSelction = (TextView)findViewById(R.id.pub_selection);

        Intent get = getIntent();
        myId = get.getStringExtra("myId");
        partnerId = get.getStringExtra("partnerId");
        cur_SEX = get.getStringExtra("SEX");
        int tmp = get.getIntExtra("id",-1);
        date = get.getStringExtra("date");
        cur_KAKAO = get.getStringExtra("KAKAO");

        send = new Intent(getApplicationContext(), FinalMessageActivity.class);
        increment = 0;
        send.putExtra("date", date);
        send.putExtra("SEX", cur_SEX);
        send.putExtra("KAKAO", cur_KAKAO);

        timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                send.putExtra("time", hour + ":" + minute);
            }
        }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true);

        switch (tmp){
            case 1:
                mealButton.setVisibility(View.INVISIBLE);
                movieButton.setVisibility(View.INVISIBLE);
                pubButton.setVisibility(View.INVISIBLE);
                break;
            case 2:
                movieButton.setVisibility(View.INVISIBLE);
                pubButton.setVisibility(View.INVISIBLE);
                break;
            case 3:
                pubButton.setVisibility(View.INVISIBLE);
                break;
            case 4:
                break;
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cafeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkc = true;
                getData("http://" + IP + "/mp/ALLstore.php?FIELD=CAFE"); //수정 필요
            }
        });
        mealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkf = true;
                getData("http://" + IP + "/mp/ALLstore.php?FIELD=f_store"); //수정 필요
            }
        });
        movieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkt = true;
                getData("http://" + IP + "/mp/ALLstore.php?FIELD=theater"); //수정 필요
            }
        });
        pubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkb = true;
                getData("http://" + IP + "/mp/ALLstore.php?FIELD=bar"); //수정 필요
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send.putExtra("increment", increment);
                send.putExtra("myId", myId);
                send.putExtra("partnerId",partnerId);
                startActivity(send.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | send.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });
    }

    void show_cafe()
    {
        final ArrayList<String> ListItems = new ArrayList<>();
        for(int i=0;i<store_name.size();i++)
        {
            ListItems.add(store_name.get(i));
        }

        CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        final List SelectedItems = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("카페 선택");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });

        builder.setPositiveButton("select",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String msg = "";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);
                            cafeSelection.setText(msg);

                            if (increment == 0){
                                timePicker.show();
                                send.putExtra("first", "cafe");
                            }

                            send.putExtra("cafe", msg);
                            increment++;
                        }
                        Toast.makeText(getApplicationContext(),
                                "Cafe selected\n" + msg, Toast.LENGTH_LONG).show();

                    }
                });

        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }
                });
        builder.show();
    }

    void show_meal()
    {
        final ArrayList<String> ListItems = new ArrayList<>();
        for(int i=0;i<store_name.size();i++)
        {
            ListItems.add(store_name.get(i));
        }

        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        final List SelectedItems = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("음식점 선택");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });

        builder.setPositiveButton("select",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String msg = "";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);
                            mealSelection.setText(msg);

                            if (increment == 0){
                                timePicker.show();
                                send.putExtra("first", "meal");
                            }

                            send.putExtra("meal", msg);
                            increment++;
                        }
                        Toast.makeText(getApplicationContext(),
                                "Meal selected\n" + msg, Toast.LENGTH_LONG).show();

                    }
                });

        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }
                });
        builder.show();
    }

    void show_movie()
    {
        final ArrayList<String> ListItems = new ArrayList<>();
        for(int i=0;i<store_name.size();i++)
        {
            ListItems.add(store_name.get(i));
        }

        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        final List SelectedItems = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("영화관 선택");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });

        builder.setPositiveButton("select",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String msg = "";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);
                            movieSelection.setText(msg);

                            if (increment == 0){
                                timePicker.show();
                                send.putExtra("first", "movie");
                            }

                            send.putExtra("movie", msg);
                            increment++;
                        }
                        Toast.makeText(getApplicationContext(),
                                "Theater selected\n" + msg, Toast.LENGTH_LONG).show();

                    }
                });

        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }
                });
        builder.show();
    }

    void show_pub()
    {
        final ArrayList<String> ListItems = new ArrayList<>();
        for(int i=0;i<store_name.size();i++)
        {
            ListItems.add(store_name.get(i));
        }

        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        final List SelectedItems = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("술집 선택");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });

        builder.setPositiveButton("select",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String msg = "";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);
                            pubSelction.setText(msg);

                            if (increment == 0){
                                timePicker.show();
                                send.putExtra("first", "pub");
                            }

                            send.putExtra("pub", msg);
                            increment++;
                        }
                        Toast.makeText(getApplicationContext(),
                                "Pub selected\n" + msg, Toast.LENGTH_LONG).show();

                    }
                });

        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }
                });
        builder.show();
    }

    protected void showList()
    {
        store_name.clear();
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++)
            {
                JSONObject c = peoples.getJSONObject(i);
                String name = c.getString(TAG_NAME);

                store_name.add(name);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(chkc) {
            show_cafe();
            chkc = false;
        }
        else if(chkf) {
            show_meal();
            chkf = false;
        }
        else if(chkt) {
            show_movie();
            chkt = false;
        }
        else if(chkb) {
            show_pub();
            chkb = false;
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