package com.example.sky.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.sky.R;

public class sub_fourth_2 extends AppCompatActivity {
    RadioGroup idgroupthemes;
    RadioButton radioButton, radioButton2, radioButton8;
    SharedPreferences sharedPreferences, getSharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_fourth2);

        sharedPreferences = getSharedPreferences("pref2", 0);
        int searchSP2 = sharedPreferences.getInt("searchSP2", 4);

        editor = sharedPreferences.edit();
        idgroupthemes = findViewById(R.id.idgroupthemes);
        radioButton = findViewById(R.id.radioButton);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton8 = findViewById(R.id.radioButton8);


        boolean isNightMode = sharedPreferences.getBoolean("nightMode", false);
        if (isNightMode) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        radioButton2.setChecked(isNightMode);
        switch (searchSP2) {
            case 2:
                radioButton.setChecked(true);
                break;
            case 1:
                radioButton2.setChecked(true);
                break;
            case 0:
                radioButton8.setChecked(true);
                break;
        }
        idgroupthemes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton) {
                    editor.putInt("search2", 2);

                } else if (checkedId == R.id.radioButton2) {
                    editor.putInt("search2", 1);

                } else if (checkedId == R.id.radioButton8) {
                    editor.putInt("search2", 0);
                }
                editor.apply();
            }
        });

        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Lưu trạng thái của RadioButton vào SharedPreferences khi thay đổi
                editor.putBoolean("nightMode", isChecked);
                editor.apply();

                setNightMode(isChecked);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Lưu trạng thái của RadioButton được chọn vào SharedPreferences khi Activity bị tạm dừng
        editor.putInt("searchSP2", sharedPreferences.getInt("search2", 4));
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Khôi phục trạng thái của RadioButton khi Activity tiếp tục hoạt động
        int searchSP2 = sharedPreferences.getInt("searchSP2", 4);
        switch (searchSP2) {
            case 2:
                radioButton.setChecked(true);
                break;
            case 1:
                radioButton2.setChecked(true);
                break;
            case 0:
                radioButton8.setChecked(true);
                break;

            default:
                break;
        }
    }
    private void setNightMode(boolean isNightMode) {
        if (isNightMode) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        recreate();
    }
    public void imageClick_sub_fourth_2(View view){
        int id = view.getId();
        if(id == R.id.web_back_6){
            Intent intent = new Intent(this, fourth.class);
            startActivity(intent);
        }
    }

    public void onBackPressed() {
        if(true) {
            Intent intent = new Intent(this, fourth.class);
            startActivity(intent);
        }
        else
            super.onBackPressed();
    }
}