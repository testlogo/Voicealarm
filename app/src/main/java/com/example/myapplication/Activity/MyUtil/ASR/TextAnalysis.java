package com.example.myapplication.Activity.MyUtil.ASR;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextAnalysis {
    public static String[] GetData(String str) {
        Pattern pattern = Pattern.compile("(\\d{1,2}点过?(半|(\\d{1,2})?))");
        String[] stringBuilders = pattern.split(str);
        pattern = Pattern.compile("\\d{1,2}");
        String[] re = new String[stringBuilders.length];
        for (int i = 0; i < stringBuilders.length; i++) {
            String[] t = pattern.split(stringBuilders[i]);
            if (t.length == 1) {
                if (stringBuilders[i].contains("半"))
                    re[i] = t[0].length() == 1 ? "0" : "" + t[0] + ":30";
                else
                    re[i] = t[0].length() == 1 ? "0" : "" + t[0] + ":00";
            } else {
                re[i] = t[0].length() == 1 ? "0" : "" + t[0] + ":" + (t[1].length() == 1 ? "0" : "") + t[1];
            }
        }
        return re;
    }
    public static String ChineseToArabic(String text){

        HashMap<String, String> chineseToArabic = new HashMap<>();
        chineseToArabic.put("零", "0");
        chineseToArabic.put("一", "1");
        chineseToArabic.put("二", "2");
        chineseToArabic.put("三", "3");
        chineseToArabic.put("四", "4");
        chineseToArabic.put("五", "5");
        chineseToArabic.put("六", "6");
        chineseToArabic.put("七", "7");
        chineseToArabic.put("八", "8");
        chineseToArabic.put("九", "9");
        chineseToArabic.put("十", "10");
        chineseToArabic.put("十一", "11");
        chineseToArabic.put("十二", "12");
        chineseToArabic.put("十三", "13");
        chineseToArabic.put("十四", "14");
        chineseToArabic.put("十五", "15");
        chineseToArabic.put("十六", "16");
        chineseToArabic.put("十七", "17");
        chineseToArabic.put("十八", "18");
        chineseToArabic.put("十九", "19");
        chineseToArabic.put("二十", "20");
        chineseToArabic.put("三十", "30");
        chineseToArabic.put("四十", "40");
        chineseToArabic.put("五十", "50");
        chineseToArabic.put("六十", "60");

        String pattern = "(?<![一二三四五六七八九])" + // 前面不能有数字
                "[一二三四五六七八九]?十[一二三四五六七八九]?|" + // 匹配十以内的数字
                "[一二三四五六七八九][十百千万亿]*"; // 匹配十以上的数字

        // 将中文数字替换为阿拉伯数字
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String s = m.group();
            if (s.contains("十")) {
                if (s.length() == 1) {
                    m.appendReplacement(sb, "10");
                } else if (s.startsWith("十")) {
                    m.appendReplacement(sb, chineseToArabic.get("10") + chineseToArabic.get(s.substring(1)));
                } else if (s.endsWith("十")) {
                    m.appendReplacement(sb, chineseToArabic.get(s.substring(0, 1)) + "0");
                }  else {
                    m.appendReplacement(sb, chineseToArabic.get(s.substring(0, 1)) + chineseToArabic.get(s.substring(1)));
                }
            } else {
                m.appendReplacement(sb, Objects.requireNonNull(chineseToArabic.get(s)));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }
    public static ArrayList<String> GetTime(String str) {
        System.out.println("Before: "+str);
        str=ChineseToArabic(str);
        System.out.println("After: "+str);
        Pattern pattern = Pattern.compile("(([0-1]?[0-9])|([2][0-3]))(:([0-5]?[0-9])(:([0-5]?[0-9]))?)?");
        Matcher matcher = pattern.matcher(str);
        ArrayList<String> stringArrayList = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group().length() == 1)
                stringArrayList.add(matcher.group() + ":00");
            else
                stringArrayList.add(matcher.group());
        }
        Log.e("GetTime: ", stringArrayList.toString());
        return stringArrayList;
    }

}
