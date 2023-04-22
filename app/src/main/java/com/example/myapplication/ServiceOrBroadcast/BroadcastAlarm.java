package com.example.myapplication.ServiceOrBroadcast;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.myapplication.Activity.Third.AlarmActivity;
import com.example.myapplication.Database.MyDao;

import java.util.Calendar;

public class BroadcastAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("com.gcc.alarm")) {//自定义的action

            StringBuilder repeat = new StringBuilder();
            repeat.append(intent.getStringExtra("RepeatArray"));
            Log.e("OnReceive", "repeat==" + repeat.toString());
            if (!repeat.toString().equals("null") && repeat.toString().equals("0000000")) {
                Calendar calendar = Calendar.getInstance();//1234567->1234560->2345671,j下一天的星期几
                for (int i = 0, j = (calendar.get(Calendar.DAY_OF_WEEK)) % 7 ; i < 7; i++, j = j % 7 ) {
                    Log.e("OnReceive", "i:"+i+"j:"+j);

                    if (repeat.charAt(j) == '1') {
                        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        PendingIntent sender;//c为设置闹钟的时间的Calendar对象

                        sender = PendingIntent.getBroadcast(context, intent.getIntExtra("id", 0),
                                    intent, PendingIntent.FLAG_MUTABLE);
//                        am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);//c为设置闹钟的时间的Calendar对象
                        am.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), i*24*3600*1000, sender);
                        break;
                    }
                }
            } else {
                MyDao myDao = new MyDao(context);
                myDao.updateUseful(intent.getIntExtra("id", 0), false);
            }
            Intent clockIntent = new Intent(context, AlarmActivity.class);
            clockIntent.putExtra("Id", intent.getIntExtra("id", 0));
            clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(clockIntent);
        }
    }
}