package com.example.myapplication.Activity.Third;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.Activity.MainPage.ActivityCollector;
import com.example.myapplication.Database.MyDao;
import com.example.myapplication.R;
import com.example.myapplication.ServiceOrBroadcast.BroadcastAlarm;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class AlarmActivity extends Activity {

    MediaPlayer mp;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.aty_alarm);
        mp = new MediaPlayer();
        intent = getIntent();
        MyDao dao = new MyDao(this);
        String uri_text = dao.queryById(intent.getIntExtra("Id", 0));

        try {
            Uri uri1 = Uri.parse(uri_text);
            if (uri1.toString().equals(""))
                uri1 = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.girl_1);
            mp.setDataSource(this, uri1);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setVolume(0.5f, 0.5f);
        mp.setLooping(true);
        mp.start();
        alarmDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
        }
    }

    public void alarmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("你有未处理的事件");
        builder.setPositiveButton("稍后提醒",
                (dialogInterface, i) -> {
                    alarm();
                    finish();
                });

        builder.setNegativeButton("停止", (dialogInterface, i) -> {
            cancelAlarm();
            //intent.setAction("Alarm");
            //Intent intent1 = new Intent(AlarmActivity.this, RepeatingAlarm.class);
            //intent1.setAction("com.gcc.alarm");
            //intent.putExtra("id",intent1.getIntExtra("id",0));
            //startActivity(intent);
            ActivityCollector.finishAll();
//                finish();// 关闭窗口
        });
        builder.show().setCanceledOnTouchOutside(false);
    }

    /**
     * 取消闹钟
     */
    private void cancelAlarm() {
        // Create the same intent, and thus a matching IntentSender, for
        // the one that was scheduled.

        //Intent intent = new Intent(AlarmActivity.this, BroadcastAlarm.class);
        Intent intent = new Intent(AlarmActivity.this, BroadcastAlarm.class);
        intent.setAction("com.gcc.alarm");
        PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity.this,
                intent.getIntExtra("id", 0), intent, PendingIntent.FLAG_MUTABLE);
        Log.d(TAG, "cancelAlarm() returned: " + intent.getIntExtra("id", 0));
        // And cancel the alarm.
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
//        Intent intent1=new Intent(AlarmActivity.this,MainActivity.class);

    }

    private void alarm() {
        // 获取系统的闹钟服务
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // 触发闹钟的时间（毫秒）
        long triggerTime = System.currentTimeMillis() + 10000;
        //Intent intent = new Intent(this, BroadcastAlarm.class);
        Intent intent = new Intent(this, BroadcastAlarm.class);
        intent.setAction("com.gcc.alarm");
        PendingIntent op = PendingIntent.getBroadcast(this, intent.getIntExtra("id", 0), intent, 0);
        // 启动一次只会执行一次的闹钟
        // am.set(AlarmManager.RTC, triggerTime, op);
        // 指定时间重复执行闹钟
        am.setExact(AlarmManager.RTC_WAKEUP, triggerTime, op);

    }

}
