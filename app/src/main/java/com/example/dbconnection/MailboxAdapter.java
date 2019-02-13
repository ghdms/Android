package com.example.dbconnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MailboxAdapter extends BaseAdapter {
    private String IP = IpAddress.getIP(); // "61.255.8.214:27922";
    private Context ctx;
    private ArrayList<MailboxMessage> data;
    private Bitmap bmImg;

    public MailboxAdapter(Context ctx, ArrayList<MailboxMessage> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(ctx);
            convertView = inflater.inflate(R.layout.activity_mailbox_message,parent,false);
        }

        ImageView imageView = (ImageView)convertView.findViewById(R.id.imageId);

        TextView name = (TextView)convertView.findViewById(R.id.sender_name);
        name.setText(data.get(position).getName() + " -> " + data.get(position).getADD() + "\n" + data.get(position).getADD2() + "\n" + data.get(position).getADD3());

        String ID;
        if(data.get(position).getMY().equals(data.get(position).getName()))
        {
            ID = data.get(position).getADD();
        }
        else
        {
            ID = data.get(position).getName();
        }
        back task = new back();
        task.execute("http://" + IP + "/mp/image/" + ID + ".jpg");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bmImg);

        return convertView;
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
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            return bmImg;
        }

        protected void onPostExecute(Bitmap img){
            //imageView.setImageBitmap(img);
        }
    }
}


