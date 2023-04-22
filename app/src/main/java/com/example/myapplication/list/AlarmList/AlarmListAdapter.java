package com.example.myapplication.list.AlarmList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.example.myapplication.R;

import java.text.ParseException;
import java.util.List;

public class AlarmListAdapter extends ArrayAdapter<intentclass> {
    private final int resourceId;
    private boolean mCheckable;
    private Callback mCallback;

    public AlarmListAdapter(Context context, int textViewResourceId,
                            List<intentclass> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    public void setSwitchListener(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        intentclass alarm = getItem(position);
        //   Log.e("ee","a"+alarm.toString());
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ampm = view.findViewById(R.id.ampmShow);
            viewHolder.aSwitch = view.findViewById(R.id.switchShow);
            viewHolder.box1 = view.findViewById(R.id.BoxShow);
            viewHolder.date = view.findViewById(R.id.NextRingTimeShow);
            viewHolder.time = view.findViewById(R.id.timeShow);
            viewHolder.Atext = view.findViewById(R.id.repeatShow);
            Log.e("getViewa", Integer.toHexString(viewHolder.Atext.getCurrentTextColor()));
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        //   Log.e("ee","re="+alarm.getDate());
        viewHolder.date.setText(alarm.getRepeatInfo());
        viewHolder.time.setText(alarm.getTime());
        viewHolder.ampm.setText(alarm.getAmpm());

        viewHolder.box1.setChecked(alarm.isCheckbox());
        viewHolder.aSwitch.setChecked(alarm.isSwitch1());
        viewHolder.aSwitch.setOnClickListener(
                v -> {
                    try {
                        mCallback.click(viewHolder.aSwitch, position);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
        );
        viewHolder.aSwitch.setTag(position);
        if (mCheckable) {
            viewHolder.aSwitch.setVisibility(View.INVISIBLE);
            viewHolder.box1.setVisibility(View.VISIBLE);
        } else {
            viewHolder.aSwitch.setVisibility(View.VISIBLE);
            viewHolder.box1.setVisibility(View.INVISIBLE);
        }
        viewHolder.box1.setChecked(((ListView) parent).isItemChecked(position));
        return view;
    }

    public void setmCheckable(boolean checkable) {
        mCheckable = checkable;
    }

    public interface Callback {
        void click(SwitchCompat v, int position) throws ParseException;
    }

    private static class ViewHolder {
        TextView ampm, date, time, Atext;
        SwitchCompat aSwitch;
        CheckBox box1;
    }

}