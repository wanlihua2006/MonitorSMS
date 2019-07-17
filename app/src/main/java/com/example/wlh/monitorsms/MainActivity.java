package com.example.wlh.monitorsms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_READ_SMS = 20190717;
    private Button btn_readSmsService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (hasPermission()) {
            btn_readSmsService = findViewById(R.id.btn_readSmsService);
            btn_readSmsService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,ReadSMSService.class);
                    startService(intent);
                }
            });

        } else {
            requestPermission();
        }

    }

    private class SmsObserver extends ContentObserver {

        public SmsObserver(Handler handler) {
         super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {

            if (hasPermission()) {
                //Log.d(TAG,"loadPlugin");

                //this.loadPlugin(this);
                //查询发送箱中的短信内容
                Cursor cursor = getContentResolver().query(Uri.parse("content://sms/outbox"),
                        null, null, null, null);
                //遍历查询得到的结果集，即可获取用户正在发送的短信
                while (cursor.moveToNext()){
                    StringBuilder sb = new StringBuilder();
                    //获取短信的发送地址
                    sb.append("address=").append(cursor.getString(cursor.getColumnIndex("address")));
                    //获取短信的标题
                    sb.append(";subject=").append(cursor.getString(cursor.getColumnIndex("subject")));
                    //获取短信内容
                    sb.append(";body=").append(cursor.getString(cursor.getColumnIndex("body")));
                    //获取短信的发送时间
                    sb.append(";time=").append(cursor.getLong(cursor.getColumnIndex("date")));
                    if (BuildConfig.DEBUG) Log.d("SmsObserver", "wanlihua debug Has Sent SMS:::" + sb.toString());
                    Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                requestPermission();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PERMISSION_REQUEST_CODE_READ_SMS == requestCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                //this.loadPlugin(this);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean hasPermission() {

        //Log.d(TAG,"hasPermission");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {

        //Log.d(TAG,"requestPermission");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE_READ_SMS);
        }
    }

}
