package com.example.dbconnection.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import com.example.dbconnection.R;

public class SetImageActivity extends Activity {

    static final int gallery = 2002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_image);

        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/-");
        startActivityForResult(intent, gallery);
    }

    @SuppressLint("NewApi")
    private Bitmap resize(Bitmap bm) { //이미지 크기로 인해 용량초과

        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 1200)
            bm = Bitmap.createScaledBitmap(bm, 600, 360, true);
        else if (config.smallestScreenWidthDp >= 1000)
            bm = Bitmap.createScaledBitmap(bm, 500, 300, true);
        else if (config.smallestScreenWidthDp >= 800)
            bm = Bitmap.createScaledBitmap(bm, 400, 240, true);
        else if (config.smallestScreenWidthDp >= 600)
            bm = Bitmap.createScaledBitmap(bm, 300, 180, true);
        else if (config.smallestScreenWidthDp >= 400)
            bm = Bitmap.createScaledBitmap(bm, 200, 120, true);
        else if (config.smallestScreenWidthDp >= 360)
            bm = Bitmap.createScaledBitmap(bm, 180, 108, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);

        return bm;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent();
        Bitmap bm;
        if (resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                String []proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(selectedImage, proj, null, null, null);
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                String path = cursor.getString(index);
                intent.putExtra("PATH", path);

                bm = Images.Media.getBitmap(getContentResolver(), data.getData());
                bm = resize(bm);
                intent.putExtra("bitmap", bm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setResult(RESULT_OK, intent);
            finish();
        } else {
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }
}