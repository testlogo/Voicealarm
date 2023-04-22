package com.example.myapplication.Activity.MainPage;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.Activity.MyUtil.ASR.JsonParser;
import com.example.myapplication.Activity.SecondPage.EditAlarm;
import com.example.myapplication.Database.MyDao;
import com.example.myapplication.R;
import com.example.myapplication.ServiceOrBroadcast.BroadcastAlarm;
import com.example.myapplication.list.AlarmList.AlarmListAdapter;
import com.example.myapplication.list.AlarmList.intentclass;
import com.example.myapplication.list.AlarmList.itemclass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.pinery.audioedit.util.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.example.myapplication.Activity.MyUtil.ASR.TextAnalysis.GetTime;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();
    FloatingActionButton bt1;
    EditText d;
    private int record_index=1;
    private ListView mListView;
    private List<intentclass> lists;
    private AlarmListAdapter mAdapter;
    private MyDao myDao;
    private ActivityResultLauncher<Intent> luncher;
    private Toast mToast;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private SpeechRecognizer mAsr;
    private InitListener mInitListener;
    private RecognizerListener mRecognizerListener;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        DataBasePrepare();
        initBind();
        AsrPrepare();
        initListener();
        /* 进入编辑闹钟后返回的处理 */
        luncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                intentclass item = (intentclass) result.getData().getSerializableExtra("data_send");
                if (item.getId() == -1) {//增加闹钟
                    int j = 1;
                    for (int i = 0; i < lists.size(); ) {//寻找未被占用的id
                        if (lists.get(i).getId() == j) j++;
                        else i++;
                    }
                    item.setId(j);
                    item.setSwitch1(Boolean.TRUE);
                    lists.add(item);
                    myDao.insert(item);
                    try {
                        AddAlarm(lists.size() - 1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {//修改闹钟
                    CancelAlarm(item.getId());
                    item.setSwitch1(false);
                    lists.set(GetIndexbyId(item.getId()), item);
                    myDao.update(item);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /*
     * 数据库准备
     * */
    private void DataBasePrepare() {
        myDao = new MyDao(this);
        try {
            lists = myDao.queryAll();//读取数据库
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /*
     * 绑定组件
     * */
    private void initBind() {
        setContentView(R.layout.list);
        mListView = findViewById(R.id.list_view);
        mAdapter = new AlarmListAdapter(MainActivity.this, R.layout.item, lists);
        mListView.setAdapter(mAdapter);
        mListView.setMultiChoiceModeListener(new MultiChoiceModeListener(mListView));
        d = findViewById(R.id.isr_text);
        bt1 = findViewById(R.id.setting);
    }

    /*
     * 初始化语音识别
     * */
    private void AsrPrepare() {
        requestPermissions();
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=88027a64");
        mToast  = Toast.makeText(this, "message", Toast.LENGTH_SHORT);

        mInitListener = new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    showTip("初始化失败,错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                }
            }
        };
        mAsr = SpeechRecognizer.createRecognizer(this, mInitListener);
        mRecognizerListener = new RecognizerListener() {
            @Override
            public void onVolumeChanged(int volume, byte[] data) {
                showTip("当前正在说话，音量大小：" + volume);
            }

            @Override
            public void onResult(final RecognizerResult result, boolean isLast) {
                if (null != result) {
                    String text;
                    if ("cloud".equalsIgnoreCase(mEngineType)) {
                        text = JsonParser.parseGrammarResult(result.getResultString());
                    } else {
                        text = JsonParser.parseLocalGrammarResult(result.getResultString());
                    }

                    // 显示
                    try {
                        printResult(result);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }

            @Override
            public void onEndOfSpeech() {
                // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
                mIatResults.clear();
                Handler handler = new Handler();
                handler.postDelayed(() -> d.setVisibility(View.GONE), 1000);//3秒后执行Runnable中的run方法

                showTip("结束说话");
            }

            @Override
            public void onBeginOfSpeech() {
                // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
                d.setVisibility(View.VISIBLE);
                showTip("开始说话");
            }

            @Override
            public void onError(SpeechError error) {
                showTip("onError Code：" + error.getErrorCode());
            }

            @Override
            public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
                // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
                // 若使用本地能力，会话id为null
                //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
                //	}
            }

        };
        // 清空参数
        mAsr.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        //此处用于设置dialog中不显示错误码信息
        //mAsr.setParameter("view_tips_plain","false");
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mAsr.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mAsr.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mAsr.setParameter(SpeechConstant.VAD_EOS, "2000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mAsr.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                getExternalFilesDir(null).getAbsolutePath() +File.separator+"0asr.wav");
    }

    /*
     * 初始化监听
     * */
    private void initListener() {

        bt1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                } else {

                    int ret = mAsr.startListening(mRecognizerListener);
                    if (ret != ErrorCode.SUCCESS) {
                        showTip("识别失败,错误码: " + ret + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                    }
                }
                return true;
            }
        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditAlarm.class);
                intent.putExtra("data_send", new intentclass(-1));
                luncher.launch(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditAlarm.class);
                intent.putExtra("data_send", lists.get(position));
                luncher.launch(intent);
            }
        });

        mAdapter.setSwitchListener(new AlarmListAdapter.Callback() {
            @Override
            public void click(SwitchCompat v, int position) throws ParseException {
                lists.get(position).setSwitch1(v.isChecked());
                myDao.updateUseful(lists.get(position).getId(), v.isChecked());
                if (v.isChecked()) {
                    AddAlarm(position);
                } else {
                    CancelAlarm(lists.get(position).getId());
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /*
     * 获取列表的索引，通过闹钟编号
     * */
    private int GetIndexbyId(int id) {
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getId() == id) return i;
        }
        return -1;
    }

    private void CancelAlarm(int id) {//class里的id
        //Intent intent = new Intent(this, BroadcastAlarm.class);
        Intent intent = new Intent(this, BroadcastAlarm.class);
        intent.setAction("com.gcc.alarm");
        PendingIntent sender = PendingIntent.getBroadcast(this,
                id, intent, PendingIntent.FLAG_MUTABLE);

        //lists.get(GetIndexbyId(id)).set("");
        showTip("取消闹钟");
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    private void AddAlarm(int index) throws ParseException {//lists的index
        intentclass item = lists.get(index);
        //       Intent intent = new Intent(this, BroadcastAlarm.class);
        Intent intent = new Intent(this, BroadcastAlarm.class);
        intent.setAction("com.gcc.alarm");
        intent.putExtra("RepeatArray", item.getBoxString().toString());
        intent.putExtra("id", item.getId());
        PendingIntent sender = PendingIntent.getBroadcast(this, item.getId(), intent, PendingIntent.FLAG_MUTABLE);
        //以上数据准备完毕，下面开始设置闹钟
        //计算时间差
        String[] r = item.getTime().split(":");
        int h = Integer.parseInt(r[0]);
        int m = Integer.parseInt(r[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        if (calendar.getTimeInMillis() + 10000 < System.currentTimeMillis())
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 3600 * 1000);
        long diff = calendar.getTimeInMillis() - System.currentTimeMillis();
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        //提示时间差
        String str = String.format("%d小时%d分钟后闹铃", hours, minutes);
        showTip(str);
        //设置闹钟（版本控制）
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        //am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
        int ti = intent.getIntExtra(Intent.EXTRA_ALARM_COUNT, -1);
    }

    /*
     * 语音识别的提示信息
     * */
    private void showTip(final String str) {
        mToast.setText(str);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    /*
     * 请求权限
     * */
    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]
                            {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.LOCATION_HARDWARE, android.Manifest.permission.READ_PHONE_STATE,
                                    android.Manifest.permission.WRITE_SETTINGS, android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_CONTACTS}, 0x0010);
                }

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 语音识别后的字段拼接，识别并添加闹钟
     * */
    private void printResult(RecognizerResult results) throws ParseException {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        ArrayList<String> dd = GetTime(resultBuffer.toString());
        /*
        * 当存在时间字符串，
        *   因为每次修改录音文件名必须在识别成功后，也就是此时的录音名在之前要设置，而识别失败的录音可能会覆盖已有录音
        *   我选择，每次成功后，修改默认录音名，就算失败也没法覆盖旧录音成功则加一
        * 后续，每次录音序列record_index加一
        * */{
            for (int k = 0; k < dd.size(); k++) {
                int j = 1;
                for (int i = 0; i < lists.size(); ) {//寻找未被占用的id
                    if (lists.get(i).getId() == j) j++;
                    else i++;
                }

                intentclass it = new itemclass.Builder(dd.get(k)).id(j).BoxString("0000000")
                        .switch1(Boolean.TRUE)
                        .SingUri(mAsr.getParameter(SpeechConstant.ASR_AUDIO_PATH))
                        .build();
                myDao.insert(it);
                lists.add(it);
                AddAlarm(lists.size() - 1);
            }
            if (dd.size() > 0) {
                while (FileUtils.checkFileExist(getApplicationContext().getExternalFilesDir(null) + File.separator + record_index + "asr.wav")) {
                    record_index++;
                }
                mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, getApplicationContext().getExternalFilesDir(null) + File.separator + record_index + "asr.wav");
            }
        }
        d.setText(resultBuffer.toString());
        mAdapter.notifyDataSetChanged();
    }

    /*
     * 多选模式的设计
     * */
    private class MultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {
        private ListView mListView;
        private TextView mTitleTextView;
        private List<Integer> mSelectedItems = new ArrayList<>();

        private MultiChoiceModeListener(ListView listView) {
            mListView = listView;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            mSelectedItems.add(position);
            mTitleTextView.setText("已选择 " + mListView.getCheckedItemCount() + " 项");
//            mAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu, menu);


            View multiSelectActionBarView = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.action_mode_bar, null);
            mode.setCustomView(multiSelectActionBarView);
            mTitleTextView = (TextView) multiSelectActionBarView.findViewById(R.id.title);
            mTitleTextView.setText("已选择 0 项");

            mAdapter.setmCheckable(true);
            mAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    for (int i = 0, j = 0; i < mListView.getCount(); i++, j++) {
                        if (mListView.isItemChecked(i)) {
                            if (lists.get(j).isSwitch1())
                                CancelAlarm(lists.get(j).getId());
                            myDao.delete(lists.get(j).getId());
                            lists.remove(j);
                            j--;
                        }
                    }
                    showTip("删除成功");
                    mode.finish();
                    break;
                case R.id.up:
                    boolean allselect = true;
                    int i = 0;
                    for (; mListView.isItemChecked(i) && i < lists.size(); i++) ;
                    if (i == lists.size()) {
                        allselect = false;
                    }
                    for (int selectedItem = 0; selectedItem < lists.size(); selectedItem++) {
                        mListView.setItemChecked(selectedItem, allselect);
                    }
                    break;
                default:
                    mode.finish();
                    break;
            }
            mAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mSelectedItems.clear();
            mAdapter.setmCheckable(false);
            mAdapter.notifyDataSetChanged();
        }
    }
}