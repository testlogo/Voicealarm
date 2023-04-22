package com.example.myapplication.list.AlarmList;

import android.net.Uri;
import android.util.Log;

import com.example.myapplication.Activity.MyUtil.file.DateUtil;
import com.example.myapplication.Activity.MyUtil.file.fileUtil;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

public class intentclass implements Serializable {
    //必选参数
    public Date time;
    public String ampm;
    //可选参数
    public int id;
    public String repeatInfo;
    public StringBuilder BoxString;
    public String SingUri;
    public Boolean checkbox, switch1;

    public intentclass() {
    }

    public intentclass(Date time, String ampm, int id, String repeatInfo, StringBuilder BoxString, String SingUri, Boolean checkbox, Boolean switch1) {
        this.time = time;
        this.ampm = ampm;
        this.id = id;
        this.repeatInfo = repeatInfo;
        this.BoxString = BoxString;
        this.SingUri = SingUri;
        this.checkbox = checkbox;
        this.switch1 = switch1;
    }

    public intentclass(int id) {
        this.time = new Date();
        this.ampm = DateUtil.AM_PM(this.time);
        this.id = id;
        this.repeatInfo = "仅此一次";
        this.BoxString = new StringBuilder(7);
        this.BoxString.replace(0, 7, "0000000");
        this.SingUri = "";
        this.checkbox = false;
        this.switch1 = false;
    }//读函数

    public String getTime() {
        return DateUtil.formatDate(time);
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAmpm() {
        return ampm;
    }

    public int getId() {
        return id;
    }

    //写函数
    public void setId(int id) {
        this.id = id;
    }

    public String getRepeatInfo() {
        return repeatInfo;
    }

    //辅助函数
    private void setRepeatInfo(String repeatInfo) {
        this.repeatInfo = repeatInfo;
    }

    public StringBuilder getBoxString() {
        return BoxString;
    }

    public void setBoxString(String BoxString) {
        try {
            for (int i = 0; i < 7; i++) {
                if (BoxString.charAt(i) != '0' && BoxString.charAt(i) != '1') {
                    throw new ArithmeticException("repeatTime Set error");
                }
            }
            this.BoxString.replace(0, 7, BoxString);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        BoxString2date(BoxString);
    }

    public String getUri() {
        return SingUri;
    }

    public String getSingName() {
        return SingUri.equals("") ? "默认铃声" : fileUtil.getFileName(Uri.parse(SingUri));
    }

    public Boolean isCheckbox() {
        return checkbox;
    }

    public Boolean isSwitch1() {
        return switch1;
    }

    public void setSingUri(String singUri) {
        this.SingUri = singUri;
    }

    public void setCheckbox(Boolean checkbox) {
        this.checkbox = checkbox;
    }

    public void setSwitch1(Boolean switch1) {
        this.switch1 = switch1;
    }

    private void date2BoxString() {
        switch (repeatInfo) {
            case "周一到周五":
                BoxString.replace(0, 7, "1111100");
                break;
            case "每天":
                BoxString.replace(0, 7, "1111111");
                break;
            case "只响一次":
                BoxString.replace(0, 7, "0000000");
                break;
            default:
                String[] daydata = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
                String[] temp = repeatInfo.split(",");
                for (int i = 0, j = 0; i < daydata.length && j < temp.length; i++) {
                    if (temp[j].equals(daydata[i])) {
                        BoxString.setCharAt(i, '1');
                        j++;
                    }
                }
        }
    }

    private void BoxString2date(String BoxString) {

        switch (BoxString) {
            case "1111100":
                setRepeatInfo("周一到周五");
                break;
            case "1111111":
                setRepeatInfo("每天");
                break;
            case "0000000":
                setRepeatInfo("只响一次");
                break;
            default:
                String[] daydata = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
                StringBuilder temp = new StringBuilder();
                for (int i = 0; i < daydata.length; i++) {
                    if (BoxString.charAt(i) == '1') {
                        temp.append(daydata[i]).append(',');
                    }
                }
                Log.e("string", "before:" + temp.toString());
                temp.deleteCharAt(temp.length() - 1);
                Log.e("string", "new:" + temp.toString());
                setRepeatInfo(temp.toString());
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "intentclass{" +
                "time=" + time +
                ", ampm='" + ampm + '\'' +
                ", id=" + id +
                ", repeatInfo='" + repeatInfo + '\'' +
                ", BoxString=" + BoxString +
                ", SingUri='" + SingUri + '\'' +
                ", checkbox=" + checkbox +
                ", switch1=" + switch1 +
                '}';
    }
}
