package me.sugarkawhi.recentnewimage;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    String TAG = "TAG";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        getImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getImage();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "handleMessage: ");
            String path = (String) msg.obj;
            if (!TextUtils.isEmpty(path)) {
                Bitmap b = BitmapFactory.decodeFile(path);
                imageView.setImageBitmap(b);
            }
        }
    };

    public void getImage() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = MainActivity.this.getContentResolver();

        //只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " DESC");

        if (mCursor == null) {
            return;
        }

        //只取一个
        if (mCursor.moveToNext()) {
            //获取最新图片的路径
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            //获取该图片的父路径名
//        String parentName = new File(path).getParentFile().getName();
            File file = new File(path);
            long updatetime = file.lastModified();

            Log.e(TAG, "getImage: path " + path);
            Log.e(TAG, "getImage: updatetime " + updatetime);
            Log.e(TAG, "getImage: currenttime " + System.currentTimeMillis());
            //这里可以比较一下时间差 来判断是否提示有最新的照片

            //通知Handler扫描图片完成
            Message msg = Message.obtain();
            msg.obj = path;
            handler.sendMessage(msg);
            mCursor.close();
        }
    }

}
