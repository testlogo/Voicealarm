package com.example.myapplication.Activity.SecondPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.MotionButton;

import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class repeatSelectDialogCustom extends BottomSheetDialogFragment implements View.OnClickListener {
    private RadioButton Mon, Tue, Wed, Thu, Fri, Sar, Sun;
    private MotionButton confirm, cancel;
    private StringBuffer repeatinfo = new StringBuffer("0000000");
    private BottomSheetBehavior<FrameLayout> behavior;
    private Callback mcallback;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mcallback = (Callback) context;
        }
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.oneweek, container, false);
        InitBind(view);
        InitListener();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FrameLayout view1 = Objects.requireNonNull(getDialog()).getWindow().findViewById(R.id.design_bottom_sheet);
        ViewGroup.LayoutParams params = view1.getLayoutParams();
        params.height = getResources().getDisplayMetrics().heightPixels;
        view1.setLayoutParams(params);
        behavior = BottomSheetBehavior.from(view1);
    }

    private void InitListener() {
        Mon.setOnClickListener(this);
        Tue.setOnClickListener(this);
        Wed.setOnClickListener(this);
        Thu.setOnClickListener(this);
        Fri.setOnClickListener(this);
        Sar.setOnClickListener(this);
        Sun.setOnClickListener(this);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void InitBind(View view) {
        Mon = view.findViewById(R.id.monday);
        Tue = view.findViewById(R.id.tuesday);
        Wed = view.findViewById(R.id.wednesday);
        Thu = view.findViewById(R.id.thursday);
        Fri = view.findViewById(R.id.friday);
        Sar = view.findViewById(R.id.saturday);
        Sun = view.findViewById(R.id.sunday);
        confirm = view.findViewById(R.id.confirm_button);
        cancel = view.findViewById(R.id.cancel_button);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.monday) {
            repeatinfo.setCharAt(0, '1');
        } else if (id == R.id.tuesday) {
            repeatinfo.setCharAt(1, '1');
        } else if (id == R.id.wednesday) {
            repeatinfo.setCharAt(2, '1');
        } else if (id == R.id.thursday) {
            repeatinfo.setCharAt(3, '1');
        } else if (id == R.id.friday) {
            repeatinfo.setCharAt(4, '1');
        } else if (id == R.id.saturday) {
            repeatinfo.setCharAt(5, '1');
        } else if (id == R.id.sunday) {
            repeatinfo.setCharAt(6, '1');
        } else if (id == R.id.confirm_button) {
            mcallback.getSelectCustom(repeatinfo.toString());
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (id == R.id.cancel_button) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public interface Callback {
        void getSelectCustom(String boxstring);
    }
}
