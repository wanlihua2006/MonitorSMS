package com.example.wlh.monitorsms;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ReadSMSService extends Service {
    public ReadSMSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Log.d("ReadSMSService", "wanlihua debug onCreate" );

        //为Content://sms 的数据改变注册监听器
        getContentResolver().registerContentObserver(Uri.parse("content://sms"),
                true,new ReadSMSService.SmsObserver(new Handler()));
    }

    private class SmsObserver extends ContentObserver {

        public SmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
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
                }


        }
    }

}
