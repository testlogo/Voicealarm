package com.example.myapplication.Activity.SecondPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.MotionButton;

import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class repeatSelectDialogGroup extends BottomSheetDialogFragment implements View.OnClickListener {
    Callback mcallback;
    private MotionButton only_one, workday, everyday, custom_text;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mcallback = (Callback) context;
        }
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repeat_rank, container, false);
        InitBind(view);
        InitListener();
        return view;
    }

    private void InitListener() {
        only_one.setOnClickListener(this);
        workday.setOnClickListener(this);
        everyday.setOnClickListener(this);
        custom_text.setOnClickListener(this);
    }

    private void InitBind(View view) {
        only_one = view.findViewById(R.id.only_one);
        workday = view.findViewById(R.id.workday);
        everyday = view.findViewById(R.id.everyday);
        custom_text = view.findViewById(R.id.custom);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        only_one.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        workday.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        everyday.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        custom_text.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        switch (v.getId()) {
            case R.id.only_one:
                only_one.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                mcallback.getSelected("0000000");
                break;
            case R.id.workday:
                workday.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                mcallback.getSelected("1111100");
                break;
            case R.id.everyday:
                everyday.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                mcallback.getSelected("1111111");
                break;
            case R.id.custom:
                custom_text.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                new repeatSelectDialogCustom().show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "Dialog");
                custom_text.setText(mcallback.getSelected("repeatInfo"));
                break;
        }
    }

    public interface Callback {
        String getSelected(String boxString);
    }
}
