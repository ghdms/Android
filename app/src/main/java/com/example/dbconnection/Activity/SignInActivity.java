package com.example.dbconnection.Activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.dbconnection.IpAddress;
import com.example.dbconnection.R;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignInActivity extends AppCompatActivity {
    private String IP = IpAddress.getIP(); // "61.255.8.214:27922";
    private Button back;
    private Button signIn;
    private Button regis_pic;
    private Button btnDbChk;
    private RadioButton radioButton1, radioButton2;
    private RadioGroup radioGroup;

    private EditText id, pw, name, intro;
    String ID = null;
    String PW = null;
    String NAME = null;
    String SEX = null;
    String INTRO = null;
    boolean IdCheck;
    boolean IdOk;
    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "ID";

    JSONArray peoples = null;
    Bitmap selPhoto;
    ImageView image;
    String[] REQUIRED_PERMISSIONS  = { Manifest.permission.CAMERA, // 카메라
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};  // 외부 저장소
    private FTPClient client;
    private String PATH;
    boolean image_upload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        IdCheck = false;
        IdOk = false;

        id = (EditText)findViewById(R.id.newID);
        pw = (EditText)findViewById(R.id.newPW);
        name = (EditText)findViewById(R.id.newNAME);
        intro = (EditText)findViewById(R.id.editIntro);

        radioGroup = (RadioGroup)findViewById(R.id.radioG1);
        radioButton1 = (RadioButton)findViewById(R.id.radioB1);
        radioButton2 = (RadioButton)findViewById(R.id.radioB2);
        image = (ImageView)findViewById(R.id.profileView);

        btnDbChk = (Button)findViewById(R.id.btnDoubleCheck);
        btnDbChk = (Button)findViewById(R.id.btnDoubleCheck);
        btnDbChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ID = id.getText().toString();
                Log.d("Dbchk", ID);
                if(ID == null)
                {
                    Toast.makeText(getApplicationContext(), "ID를 먼저 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(ID.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "ID를 먼저 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    IdCheck = true;
                    getData("http://" + IP + "/mp/IdCheck.php?ID=" + ID); //수정 필요
                }
            }
        });
        back = (Button)findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        regis_pic = (Button)findViewById(R.id.btnRegis);
        regis_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID = id.getText().toString();
                if(ID == null)
                {
                    Toast.makeText(getApplicationContext(), "ID를 먼저 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(ID.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "ID를 먼저 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(!IdOk)
                {
                    Toast.makeText(getApplicationContext(), "ID 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    register();
                }
            }
        });

        signIn = (Button)findViewById(R.id.btnSignIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID = id.getText().toString();
                PW = pw.getText().toString();
                NAME = name.getText().toString();
                INTRO = intro.getText().toString();

                if(ID == null || PW == null || NAME == null || SEX == null || INTRO == null)
                {
                    Toast.makeText(getApplicationContext(), "정보를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else if(ID.equals("") || PW.equals("") || NAME.equals("") || SEX.equals("") || INTRO.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "정보를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else if(!IdOk)
                {
                    Toast.makeText(getApplicationContext(), "ID 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    getData("http://" + IP + "/mp/Signin.php?ID=" + ID + "&PW=" + PW + "&NAME=" + NAME + "&SEX=" + SEX + "&INTRO=" + INTRO); //수정 필요
                }
            }
        });

        ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS, 0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean status = false;
                status = ftpConnect();
                if(status == true) {
                    Log.d("Connect", "Connect Success");
                    return ;
                }
                else {
                    Log.d("Connect", "Connect failed");
                }
            }
        }).start();

        id.getText().clear();
    }

    public void register()
    {
        Intent intent=new Intent(getApplicationContext(),SetImageActivity.class);

        startActivityForResult(intent, 1001);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1001)
        {	//if image change
            if(resultCode==RESULT_OK)
            {
                PATH = data.getStringExtra("PATH");
                selPhoto=(Bitmap) data.getParcelableExtra("bitmap");
                image.setImageBitmap(selPhoto);//썸네일
                image_upload = true;
            }
        }
    }

    public void mOnClick(View v)
    {
        switch (v.getId())
        {
            case R.id.radioB1:
                SEX = "M";
                break;
            case R.id.radioB2:
                SEX = "F";
                break;
        }
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

                if(!IdCheck)
                {
                    if (dbid.equals("true"))
                    {
                        if(image_upload)
                        {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean status = false;
                                    status = ftpUpload(PATH);
                                    if(status == true) {
                                        Log.d("Connect", "Upload Success");
                                        return ;
                                    }
                                    else {
                                        Log.d("Connect", "Upload failed");
                                    }
                                }
                            }).start();
                            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "사진을 등록해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    if(dbid != null)
                    {
                        if(!dbid.equals("")) {
                            Toast.makeText(getApplicationContext(), "중복된 ID가 있습니다.", Toast.LENGTH_SHORT).show();
                            IdOk = false;
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "사용 가능한 ID 입니다.", Toast.LENGTH_SHORT).show();
                            IdOk = true;
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "사용 가능한 ID 입니다.", Toast.LENGTH_SHORT).show();
                        IdOk = true;
                    }
                    IdCheck = false;
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

    public boolean ftpConnect()
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Connect", "Connect false");
        return false;
    }

    public boolean ftpUpload(String path)
    {
        FileInputStream fis = null;
        Log.d("Connect", "upload start");
        try {
//            String ex = Environment.getExternalStorageDirectory().getAbsolutePath();
//            Log.d("Connect", ex);
//            String filename = ex + "/DCIM/Camera/abcdefg.jpg";
            Log.d("Connect", path);
            File file = new File(path);
            fis = new FileInputStream(file);
            client.storeFile("/" + ID + ".jpg", fis);
            fis.close();
            Log.d("Connect", "true");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Connect", "false");
        return false;
    }
}