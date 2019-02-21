package com.example.dbconnection.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dbconnection.Activity.SetImageActivity;
import com.example.dbconnection.IpAddress;
import com.example.dbconnection.MyService;
import com.example.dbconnection.R;
import com.example.dbconnection.TAG_;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyInformation extends Fragment {

    private String IP = IpAddress.getIP(); //"61.255.8.214:27922";

    private Button btnUpdateUserImage, btnUpdateUserInfo, btnUpdateService;
    private TextView ID, KAKAO, SEX, TEXT, SERVICE, AVG;
    private ImageView imageView;

    String cur_ID, cur_SEX, cur_KAKAO, cur_INTRO;
    Bitmap bmImg;
    String PATH;

    String myJSON;
    JSONArray peoples = null;

    boolean changing_intro;

    FTPClient client;
    String[] REQUIRED_PERMISSIONS  = { Manifest.permission.CAMERA, // 카메라
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};  // 외부 저장소

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_my_information,container,false);

        ActivityCompat.requestPermissions( getActivity(), REQUIRED_PERMISSIONS, 0);

        changing_intro = false;
        cur_ID = getArguments().getString("myId");
        cur_SEX = getArguments().getString("SEX");
        cur_KAKAO = getArguments().getString("KAKAO");
        cur_INTRO = IpAddress.getCur_INTRO();

        btnUpdateUserImage = (Button) rootView.findViewById(R.id.change_image);
        btnUpdateUserInfo = (Button) rootView.findViewById(R.id.change_intro);
        btnUpdateService = (Button) rootView.findViewById(R.id.change_service);

        ID = (TextView) rootView.findViewById(R.id.my_ID);
        SEX = (TextView) rootView.findViewById(R.id.my_SEX);
        KAKAO = (TextView) rootView.findViewById(R.id.my_KAKAO);
        TEXT = (TextView) rootView.findViewById(R.id.my_INTRO);
        SERVICE = (TextView) rootView.findViewById(R.id.my_SERVICE);
        AVG = (TextView) rootView.findViewById(R.id.my_AVG);
        getData("http://" + IP + "/mp/rate.php?ID=" + cur_ID);

        ID.setText("ID : " + cur_ID);
        SEX.setText("성별 : " + cur_SEX);
        KAKAO.setText("카카오톡 ID : " + cur_KAKAO);
        TEXT.setText("한줄 소개 : " + cur_INTRO);

        imageView = (ImageView)rootView.findViewById(R.id.imageView);

        back imageload = new back();
        imageload.execute("http://" + IP + "/mp/image/" + cur_ID + ".jpg");

        btnUpdateUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),SetImageActivity.class);
                startActivityForResult(intent, 1001);
            }
        });

        btnUpdateUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                View layout = inflater.inflate(R.layout.updateinfo,null);
                builder.setView(layout);
                final EditText mEdit = (EditText)layout.findViewById(R.id.updateInfo);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cur_INTRO = mEdit.getText().toString();
                        IpAddress.setCur_INTRO(cur_INTRO);
                        TEXT.setText("한 줄 소개 : " + cur_INTRO);
                        changing_intro = true;
                        getData("http://" + IP + "/mp/ChangeIntro.php?ID=" + cur_ID + "&INTRO=" + cur_INTRO);
                    }
                });
                builder.show();
            }
        });

        btnUpdateService.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("쪽지 알람 설정");
                builder.setMessage("쪽지 알람 수신 여부를 선택합니다.");
                builder.setPositiveButton("수신",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "쪽지 알람 수신을 받습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent_service_start = new Intent(getContext(), MyService.class);
                                intent_service_start.putExtra("thID", cur_ID);
                                rootView.getContext().startService(intent_service_start.setFlags(intent_service_start.FLAG_ACTIVITY_CLEAR_TOP | intent_service_start.FLAG_ACTIVITY_SINGLE_TOP));
                                isServiceRunningCheck(rootView);
                            }
                        });
                builder.setNegativeButton("거부",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "쪽지 알람 수신을 중지합니다.", Toast.LENGTH_LONG).show();
                                rootView.getContext().stopService(new Intent(getContext(), MyService.class));
                                isServiceRunningCheck(rootView);
                            }
                        });
                builder.show();
            }
        });

        isServiceRunningCheck(rootView);
        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1001)
        {	//if image change
            if(resultCode == -1)
            {
                PATH = data.getStringExtra("PATH");
                bmImg=(Bitmap) data.getParcelableExtra("bitmap");
                imageView.setImageBitmap(bmImg);
                Log.d("Connect", PATH);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean status = false;
                        status = ftpConnectAndUpload(PATH);
                        if(status == true) {
                            Log.d("Connect", "Upload Success");
                            return ;
                        }
                        else {
                            Log.d("Connect", "Upload failed");
                        }
                    }
                }).start();
            }
        }
    }

    public class back extends AsyncTask<String, Integer,Bitmap> {
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
    }

    final Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            imageView.setImageBitmap(bmImg);
        }
    };

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
                if(changing_intro)
                {
                    changing_intro = false;
                }
                else
                {
                    myJSON = result;
                    showList();
                }
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
                int dball = c.getInt(TAG_.getTagAll());
                int dbmy = c.getInt(TAG_.getTagMy());
                double dbavg = c.getDouble(TAG_.getTagAvg());
                AVG.setText("전적 : " + dball + "전 " + dbmy + "승, 평점 : " + dbavg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean ftpConnectAndUpload(String path)
    {
        client = new FTPClient();
        FileInputStream fis = null;
        Log.d("Connect", "Connect Call");
        try {
            //client.connect(IpAddress.getIP());
            client.connect(IpAddress.getFTPIP(), IpAddress.getFTPPORT());
            client.login("ghdms", "ghdms789");
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.setBufferSize(5 * 1024 * 1024);
            Log.d("Connect", "Connect true");

            File file = new File(path);
            fis = new FileInputStream(file);
            client.storeFile("/" + cur_ID + ".jpg", fis);
            fis.close();
            Log.d("Connect", "Upload true");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Connect", "Connect false");
        return false;
    }

    public void isServiceRunningCheck(View view) {
        boolean chk = false;
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.dbconnection.MyService".equals(service.service.getClassName())) {
                SERVICE.setText("쪽지 알람 서비스 : 수신");
                chk = true;
            }
        }
        if(!chk)
        {
            SERVICE.setText("쪽지 알람 서비스 : 거부");
        }
        view.invalidate();
    }
}