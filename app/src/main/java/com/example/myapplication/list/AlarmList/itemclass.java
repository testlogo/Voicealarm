package com.example.myapplication.list.AlarmList;

import com.example.myapplication.Activity.MyUtil.file.DateUtil;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Date;

/*
存储闹钟结构体
time:时间字符串
date:重复描述，例如->重复一次，周一到周五
ampm:上午下午描述
BoxString:同date,共7位,用字节描述某一天重复，1为重复，0为不重复,顺序为从周一到周日;全0为仅一次
checkbox:复选框，用来选中
switch1:开关
 */
public class itemclass extends intentclass {

    private itemclass(Builder builder) {
        super();
        this.time = builder.time;
        this.ampm = builder.ampm;
        this.id = builder.id;
        this.repeatInfo = builder.repeatInfo;
        this.BoxString = builder.BoxString;
        this.SingUri = builder.SingUri;
        this.checkbox = builder.checkbox;
        this.switch1 = builder.switch1;
    }

    //输出
    @NotNull
    @Override
    public String toString() {
        return "itemclass{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", date='" + repeatInfo + '\'' +
                ", ampm='" + ampm + '\'' +
                ", BoxString=" + BoxString +
                ", singName=" + getSingName() +
                ", SingUri=" + SingUri +
                ", checkbox=" + checkbox +
                ", switch1=" + switch1 +
                '}';
    }

    public static class Builder {
        //必选参数
        private final Date time;
        private final String ampm;
        //可选参数
        private int id;
        private String repeatInfo;
        private StringBuilder BoxString;
        private String SingUri;
        private Boolean checkbox, switch1;

        /**
         * @param time "HH:mm:ss"
         */
        public Builder(String time) throws ParseException {
            this.time = DateUtil.parse(time);
            this.ampm = DateUtil.AM_PM(this.time);
            this.id = -2;
            this.repeatInfo = "仅此一次";
            this.BoxString = new StringBuilder(7);
            this.BoxString.replace(0, 7, "0000000");
            this.SingUri = "";
            this.checkbox = false;
            this.switch1 = false;
        }

        public Builder(intentclass in) {
            this.time = in.time;
            this.ampm = in.ampm;
            this.id = in.id;
            this.repeatInfo = in.repeatInfo;
            this.BoxString = in.BoxString;
            this.SingUri = in.SingUri;
            this.checkbox = in.checkbox;
            this.switch1 = in.switch1;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder BoxString(String BoxString) {

            this.BoxString.replace(0, 7, BoxString);

            return this;
        }

        public Builder SingUri(String SingUri) {
            this.SingUri = SingUri;
            return this;
        }

        public Builder checkbox(Boolean checkbox) {
            this.checkbox = checkbox;
            return this;
        }

        public Builder switch1(Boolean switch1) {
            this.switch1 = switch1;
            return this;
        }

        public itemclass build() {
            itemclass item = new itemclass(this);
            try {
                for (int i = 0; i < 7; i++)
                    if (item.BoxString.charAt(i) != '0' && item.BoxString.charAt(i) != '1')
                        throw new ArithmeticException("repeatTime Set error");
            } catch (ArithmeticException e) {
                e.printStackTrace();
            }
            return item;
        }

        public intentclass father_build() {
            return new intentclass(time, ampm, id, repeatInfo, BoxString, SingUri, checkbox, switch1);
        }

    }
}
