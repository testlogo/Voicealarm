package com.example.myapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.list.AlarmList.intentclass;
import com.example.myapplication.list.AlarmList.itemclass;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MyDao {
    private MyHelper myHelper;
    private Context context;

    public MyDao(Context context) {
        this.context = context;
    }

    public void insert(intentclass item) {
        myHelper = new MyHelper(context, "Alarm.db", null, 1);
        SQLiteDatabase database = myHelper.getWritableDatabase();
        database.execSQL("insert into Alarm values(?,?,?,?,?)", new Object[]{item.getId(), item.getTime(), item.getBoxString().toString(), item.isSwitch1(), item.getUri()});
        database.close();
    }

    public void delete(int id) {
        myHelper = new MyHelper(context, "Alarm.db", null, 1);
        SQLiteDatabase database = myHelper.getWritableDatabase();
        database.execSQL("delete from Alarm where id= ? ", new Object[]{id});
        database.close();
    }

    public void update(intentclass item) {
        myHelper = new MyHelper(context, "Alarm.db", null, 1);
        SQLiteDatabase database = myHelper.getWritableDatabase();
        database.execSQL("update Alarm set time= ?,boxString= ?,useful=?,uri=? where id=?", new Object[]{item.getTime(), item.getBoxString(), item.isSwitch1(), item.getUri(), item.getId()});
        database.close();
    }

    public void updateUseful(int id, boolean ta) {
        myHelper = new MyHelper(context, "Alarm.db", null, 1);
        SQLiteDatabase database = myHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("useful", ta);
        database.update("Alarm", contentValues, "id=?", new String[]{"" + id});
        database.close();
    }

    public void updateRing(int id, String ring) {
        myHelper = new MyHelper(context, "Alarm.db", null, 1);
        SQLiteDatabase database = myHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uri", ring);
        database.update("Alarm", contentValues, "id=?", new String[]{"" + id});
        database.close();
    }

    public List<intentclass> queryAll() throws ParseException {
        List<intentclass> list = new ArrayList<>();
        myHelper = new MyHelper(context, "Alarm.db", null, 1);
        SQLiteDatabase database = myHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from Alarm", null);
        if (cursor == null) return null;
        while (cursor.moveToNext()) {
            itemclass item = new itemclass
                    .Builder(cursor.getString(1))
                    .id(cursor.getInt(0))
                    .BoxString(cursor.getString(2))
                    .SingUri(cursor.getString(4))
                    .switch1(cursor.getInt(3) == 1)
                    .build();
            list.add(item);
        }
        cursor.close();
        database.close();
        return list;
    }

    public String queryById(int id) {
        myHelper = new MyHelper(context, "Alarm.db", null, 1);
        SQLiteDatabase database = myHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select uri from Alarm where id=?", new String[]{"" + id});
        if (cursor == null) return null;
        cursor.moveToNext();
        String s = cursor.getString(0);
        cursor.close();
        database.close();
        return s;
    }
}
