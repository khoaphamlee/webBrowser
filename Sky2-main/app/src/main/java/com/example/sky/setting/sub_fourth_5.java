package com.example.sky.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.sky.R;

public class sub_fourth_5 extends AppCompatActivity {

    private TextView textTab;
    private SharedPreferences sharedPrefs, sharedPreferences_22;
    private SharedPreferences.Editor editorSP, editorSP_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences_3 = getSharedPreferences("pref2", 0);

        int isNightMode = sharedPreferences_3.getInt("search2", 0);
        if (isNightMode == 1){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        setContentView(com.example.sky.R.layout.activity_sub_fourth5);

        sharedPrefs = getSharedPreferences("Show tabs", Context.MODE_PRIVATE);
        editorSP = sharedPrefs.edit();
        Switch switch4 = findViewById(R.id.switch4);
        boolean switchState = sharedPrefs.getBoolean("Switch4State", true);
        switch4.setChecked(switchState);
        switch4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editorSP.putBoolean("Switch4State", isChecked);
            editorSP.apply();
        });
        Switch switch5 = findViewById(R.id.switch5);
        boolean switchState_2 = sharedPrefs.getBoolean("Switch5State", true);
        switch5.setChecked(switchState_2);
        switch5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editorSP.putBoolean("Switch5State", isChecked);
            editorSP.apply();
        });

        textTab = findViewById(R.id.pos);
        sharedPreferences_22 = getSharedPreferences("visible", Context.MODE_PRIVATE);
        editorSP_2 = sharedPreferences_22.edit(); // Khởi tạo `editorSP_2`
        Boolean isVisible = sharedPreferences_22.getBoolean("visible", true);
        textTab.setText(isVisible ? "Hiển thị" : "Ẩn"); // Đặt văn bản cho TextView

        RelativeLayout tabView3 = findViewById(R.id.tab_view_3);
        if (tabView3 != null) {
            tabView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRadioButtonDialog();
                }
            });
        }
    }

    private void showRadioButtonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn vị trí");

        // Các tùy chọn Radio button
        String[] options = {"Hiển thị", "Ẩn"};
        int checkedItem = -1; // Không có mục nào được chọn trước

        builder.setSingleChoiceItems(options, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean isVisible = (which == 0); // Hiển thị nếu chọn 0, ẩn nếu chọn 1
                textTab.setText(isVisible ? "Hiển thị" : "Ẩn"); // Cập nhật TextView
                editorSP_2.putBoolean("visible", isVisible); // Lưu vào SharedPreferences
                editorSP_2.apply(); // Áp dụng thay đổi
                dialog.dismiss(); // Đóng hộp thoại sau khi chọn
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Đóng hộp thoại
            }
        });

        builder.show(); // Hiển thị AlertDialog
    }

    public void imageClick_sub_fourth_5(View view) {
        int id = view.getId();
        if (id == R.id.web_back_11) {
            Intent intent = new Intent(this, fourth.class);
            startActivity(intent);
        }
    }
}
