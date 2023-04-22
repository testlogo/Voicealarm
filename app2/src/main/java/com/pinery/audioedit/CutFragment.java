package com.pinery.audioedit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.pinery.audioedit.bean.AudioMsg;
import com.pinery.audioedit.service.AudioTaskCreator;
import com.pinery.audioedit.util.FileUtils;
import com.pinery.audioedit.util.ToastUtil;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author hesong
 * @time 2018/1/9
 * @desc
 */

public class CutFragment extends Fragment {

  private static final int REQUEST_AUDIO_CODE = 1;
  private Intent intent;
  private TextView tvAudioPath1,tvMsgInfo,etStartTime,etEndTime;
  private MediaPlayer mediaPlayer;
  private ImageView btnPlayAudio;
  private boolean isPlaying = false;
  private Handler handler;
  private int mCurPickBtnId;
  private String mCurPath;
  private RangeSeekBar<Integer> seekBar;
  public static CutFragment newInstance() {
    CutFragment fragment = new CutFragment();
    fragment.setArguments(new Bundle());

    return fragment;
  }

  public void navigateTo(FragmentActivity activity, int parentViewId){
    activity.getSupportFragmentManager().beginTransaction()
        .add(parentViewId, this, getClass().getSimpleName())
        .addToBackStack(getClass().getSimpleName())
        .commit();
  }

  @SuppressLint("InflateParams")
  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

    EventBus.getDefault().register(this);

    return LayoutInflater.from(getContext()).inflate(R.layout.testcut, null);
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    initViews(view);
    Init_Component();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    handler.removeCallbacks(this::playSong);
    mediaPlayer.release();
    EventBus.getDefault().unregister(this);
    EventBus.getDefault().post(mCurPath);
  }

  @SuppressLint("ClickableViewAccessibility")
  private void initViews(View view) {

    view.setOnTouchListener(new View.OnTouchListener() {
      @SuppressLint("ClickableViewAccessibility")
      @Override public boolean onTouch(View v, MotionEvent event) {
        return true;
      }
    });
    tvAudioPath1 = (TextView) view.findViewById(R.id.tv_audio_path_1);
    LinearLayout btnPickAudioPath1 =  view.findViewById(R.id.btn_pick_audio_1);
    ImageView btnCutAudio = view.findViewById(R.id.btn_cut_audio);
     btnPlayAudio =  view.findViewById(R.id.btn_play_audio);
    tvMsgInfo = (TextView) view.findViewById(R.id.tv_msg_info);
    etStartTime =view.findViewById(R.id.et_start_time);
    etEndTime = view.findViewById(R.id.tv_audio_length_1);
    // Setup the new range seek bar
//    RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<Integer>(requireContext());
    seekBar= view.findViewById(R.id.rangeSeekBar);
    // Set the range



// 将MediaPlayer的当前位置移动到10秒处
//    mediaPlayer.seekTo(10000);


    seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
      @Override
      public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
       if(mediaPlayer!=null ){ mediaPlayer.seekTo(minValue);etStartTime.setText(String.valueOf(minValue/1000));etEndTime.setText(String.valueOf(maxValue/1000));}
      }
    });

    handler = new Handler();





    btnPickAudioPath1.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        pickAudioFile(view.getId());
      }
    });

    btnCutAudio.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        cutAudio();return true;
      };
    });
    btnPlayAudio.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP && mediaPlayer !=null) {
          if (isPlaying) {
            pauseSong();
          } else {
            playSong();
          }
        }
      return true;
      }
    });



  }
  private void Init_Component(){
    intent = requireActivity().getIntent();
    mCurPath = (String) intent.getSerializableExtra("path");

    try {
      tvAudioPath1.setText(mCurPath);
      if(mediaPlayer != null){pauseSong(); mediaPlayer.release();}
      mediaPlayer =new  MediaPlayer();
      mediaPlayer.setDataSource(mCurPath);mediaPlayer.prepare();
      // 在SeekBar上显示MediaPlayer的当前进度
      seekBar.setRangeValues(0,mediaPlayer.getDuration());
      seekBar.setSelectedMinValue(0);
      seekBar.setSelectedMaxValue(mediaPlayer.getDuration());
      // 每秒更新一次SeekBar的进度
      etStartTime.setText("0");
      etEndTime.setText(String.valueOf(mediaPlayer.getDuration()/1000));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  private void playSong() {
    if (!mediaPlayer.isPlaying()) {
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          if (mediaPlayer!=null)
            if( mediaPlayer.isPlaying()) {
            System.out.println("seekBar:"+seekBar.getSelectedMaxValue()+"media:"+mediaPlayer.getCurrentPosition());

              seekBar.setSelectedMinValue(mediaPlayer.getCurrentPosition());
              etStartTime.setText(String.valueOf(mediaPlayer.getCurrentPosition()/1000));
              handler.postDelayed(this,10);
          }else{
            System.out.println("FFFFFF---seekBar:"+seekBar.getSelectedMaxValue()+"media:"+mediaPlayer.getCurrentPosition());
            handler.removeCallbacks(this);pauseSong();
          }

        }
      },100);
      mediaPlayer.start();animateButton(true);
      ToastUtil.showToast("播放：" + mCurPath);
    }
    isPlaying = true;

  }

  private void pauseSong() {
    handler.removeCallbacks(this::playSong);mediaPlayer.pause();animateButton(false);ToastUtil.showToast("已暂停");
    isPlaying = false;
  }

  private void animateButton(boolean isPlaying) {
    if (isPlaying) {
      btnPlayAudio.setImageResource(R.drawable.ic_pluse);
      Animation anim = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate);
      btnPlayAudio.startAnimation(anim);
    } else {
      btnPlayAudio.setImageResource(R.drawable.ic_play);
      btnPlayAudio.clearAnimation();
    }
  }
  /**
   * 选取音频文件
   */
  private void pickAudioFile(int viewId) {
    mCurPickBtnId = viewId;

    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("audio/*");
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    startActivityForResult(intent, REQUEST_AUDIO_CODE);
  }

  /**
   * 裁剪音频
   */
  private void cutAudio() {

    String path1 = mCurPath;
//    System.out.println(Arrays.toString(path1.split("/")));
    if(TextUtils.isEmpty(path1)){
      ToastUtil.showToast("音频路径为空");
      return;
    }
    if(TextUtils.isEmpty(etStartTime.getText().toString()) || TextUtils.isEmpty(etEndTime.getText().toString())){
      ToastUtil.showToast("开始时间和结束时间不能为空");
      return;
    }

    float startTime = seekBar.getSelectedMinValue();
    float endTime = seekBar.getSelectedMaxValue();

    if(startTime <= 0){
      ToastUtil.showToast("时间不对");
      return;
    }
    if(endTime <= 0){
      ToastUtil.showToast("时间不对");
      return;
    }
    if(startTime >= endTime){
      ToastUtil.showToast("时间不对");
      return;
    }

    AudioTaskCreator.createCutAudioTask(getContext(), path1, startTime/1000, endTime/1000);
  }

  @SuppressLint("SetTextI18n")
  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == REQUEST_AUDIO_CODE) {
        String path = FileUtils.queryFilePath(getContext(), data.getData());

        if (mCurPickBtnId == R.id.btn_pick_audio_1) {
          tvAudioPath1.setText(path);          assert path != null;

          String temp= FileUtils.getFileNameWithSuffix(path);
          FileUtils.copyFileToPrivateDir(requireContext(),data.getData(),temp);
          mCurPath=requireContext().getExternalFilesDir(null)+File.separator+temp;

          try {
            if(mediaPlayer != null){pauseSong(); mediaPlayer.release();}
            mediaPlayer =new  MediaPlayer();
            mediaPlayer.setDataSource(mCurPath);mediaPlayer.prepare();
            // 在SeekBar上显示MediaPlayer的当前进度
            seekBar.setRangeValues(0,mediaPlayer.getDuration());
            seekBar.setSelectedMinValue(0);
            seekBar.setSelectedMaxValue(mediaPlayer.getDuration());
            // 每秒更新一次SeekBar的进度
            etStartTime.setText("0");
            etEndTime.setText(String.valueOf(mediaPlayer.getDuration()/1000));
          } catch (IOException e) {
            e.printStackTrace();
          }
//          updateAudioTime(data.getData());
        }
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND) public void onReceiveAudioMsg(AudioMsg msg) {
    if(msg != null && !TextUtils.isEmpty(msg.msg)){
      tvMsgInfo.setText(msg.msg);
      mCurPath =msg.path;
    }
  }


}
