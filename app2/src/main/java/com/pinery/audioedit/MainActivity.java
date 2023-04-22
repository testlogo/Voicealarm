package com.pinery.audioedit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity {
  private static Context context;
  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = getApplicationContext();
    setContentView(R.layout.activity_main);
    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      System.out.println("成功！");
    }
//    MainFragment.newInstance().navigateTo(this, R.id.fl_content);
    CutFragment.newInstance().navigateTo(this, R.id.fl_content);

  }

  @Override public void onBackPressed() {
    getSupportFragmentManager().popBackStack();
    if(getSupportFragmentManager().getBackStackEntryCount() > 1){
      return;
    }
    super.onBackPressed();
  }
  public static Context getContext(){
    return context;
  }
}

