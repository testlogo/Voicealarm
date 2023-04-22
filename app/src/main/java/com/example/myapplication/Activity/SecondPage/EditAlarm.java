package com.example.myapplication.Activity.SecondPage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myapplication.Activity.MainPage.ActivityCollector;
import com.example.myapplication.Activity.MyUtil.file.DateUtil;
import com.example.myapplication.R;
import com.example.myapplication.list.AlarmList.intentclass;
import com.pinery.audioedit.MainActivity;
import com.pinery.audioedit.util.FileUtils;
import com.pinery.audioedit.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.util.Locale;

/***
 * 编辑闹钟界面，根据intent中“data_send”获取itemclass
 */
public class EditAlarm extends AppCompatActivity implements OnClickListener, repeatSelectDialogGroup.Callback, repeatSelectDialogCustom.Callback {

    private TextView editTime;
    private TextView ringText;
    private TextView repeatText;
    private TextView title;
    private Intent intent;
    private intentclass item;
    private ActivityResultLauncher<Intent> luncher;
    private ActivityResultLauncher<Intent> luncher1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.edit_alarm);

        editTime = findViewById(R.id.editTextTime);
        editTime.setOnClickListener(this);
        ringText = findViewById(R.id.ring);
        ConstraintLayout ring_item = findViewById(R.id.ring_item);
        ring_item.setOnClickListener(this);
        TextView cut = findViewById(R.id.ring_cut);
        cut.setOnClickListener(this);
        repeatText = findViewById(R.id.repeat);
        title = findViewById(R.id.title);
        InitContentByData();
        luncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Uri uri;
            if (result.getResultCode()==Activity.RESULT_OK) {
//                if(result.getResultCode()==1)
                assert result.getData() != null;
                uri = result.getData().getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                ringText.setText(uri.getQueryParameter("title"));
                item.setSingUri(uri.toString());
            }
        });

        EventBus.getDefault().register(this);
    }

    private void InitContentByData() {
        /*获取数据item：通过id判断功能，-1表示增加，其他为修改*/
        intent = getIntent();
        item = (intentclass) intent.getSerializableExtra("data_send");
        /*判断为增加,默认不修改*/
        if (item.getId() != -1) {/*判断为修改*/
            title.setText("修改闹钟");
            editTime.setText(item.getTime());
            ringText.setText(item.getSingName());
            repeatText.setText(item.getRepeatInfo());
        } else {
            editTime.setText(DateUtil.Current_time());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ActivityCollector.removeActivity(this);
    }


    public void onBackPressed() {
        //添加"Yes"按钮
        //添加取消

        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("保存？")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("是", (dialogInterface, i) -> {
                    item.setSwitch1(true);
                    intent.putExtra("data_send", item);
                    setResult(RESULT_OK, intent);
                    finish();
                })

                .setNegativeButton("否", (dialogInterface, i) -> finish())
                .create();
        alertDialog2.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.editTextTime) {
            TimePickerDialog time = new TimePickerDialog(EditAlarm.this, (view, hourOfDay, minute) -> {
                editTime.setText(String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute));
                try {
                    item.setTime(DateUtil.parse(editTime.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, Integer.parseInt(editTime.getText().toString().split(":")[0]),
                    Integer.parseInt(editTime.getText().toString().split(":")[1]),
                    true);
            time.show();
        } else if (id == R.id.repeat_item) {
            new repeatSelectDialogGroup().show(getSupportFragmentManager(), "Dialog");
        } else if (id == R.id.ring_item) {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            luncher.launch(intent);
        }else if(id==R.id.ring_cut){
            Intent intent1= new Intent(this, MainActivity.class);
            intent1.putExtra("path",item.getUri());
            luncher.launch(intent1);
        }

    }

    @Override
    public String getSelected(String boxString) {
        if (boxString.length() == 7) {
            item.setBoxString(boxString);
            repeatText.setText(item.getRepeatInfo());
        } else if (boxString.equals("boxString")) {
            return item.getBoxString().toString();
        } else if (boxString.equals("repeatInfo")) {
            return item.repeatInfo;
        }
        return "";
    }

    @Override
    public void getSelectCustom(String boxString) {
        if (boxString.length() == 7) {
            item.setBoxString(boxString);
            repeatText.setText(item.getRepeatInfo());
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN) public void onReceiveAudioMsg(String msg) {
        ToastUtil.showToast(msg);
        ringText.setText(FileUtils.getFileNameWithSuffix(msg));
        item.setSingUri(msg);
    }
}