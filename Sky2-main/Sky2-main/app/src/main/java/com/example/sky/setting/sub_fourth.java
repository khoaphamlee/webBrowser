package com.example.sky.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.sky.R;

public class sub_fourth extends AppCompatActivity {
    RadioGroup idgroup;
    RadioButton radioButton3, radioButton4, radioButton5, radioButton6, radioButton7;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferences_2;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences_3 = getSharedPreferences("pref2", 0);

        int isNightMode = sharedPreferences_3.getInt("search2", 0);
        if (isNightMode == 1){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setContentView(com.example.sky.R.layout.activity_sub_fourth);

        sharedPreferences = getSharedPreferences("pref", 0);
        int searchSP = sharedPreferences.getInt("searchSP", 6);
        editor = sharedPreferences.edit();
        idgroup = findViewById(R.id.idgroup);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        radioButton5 = findViewById(R.id.radioButton5);
        radioButton6 = findViewById(R.id.radioButton6);
        radioButton7 = findViewById(R.id.radioButton7);

        sharedPreferences_2 = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences_2.edit();

        idgroup.setOnCheckedChangeListener((group, checkedId) -> {
            String imageToShow = "google";
            if (checkedId == R.id.radioButton4) {
                imageToShow = "yahoo";
            } else if (checkedId == R.id.radioButton5) {
                imageToShow = "bing";
            } else if (checkedId == R.id.radioButton6) {
                imageToShow = "coccoc";
            } else if (checkedId == R.id.radioButton7) {
                imageToShow = "duckduckgo";
            }

            editor1.putInt("selectedRadioButtonId", checkedId);
            editor1.putString("imageToShow", imageToShow);
            editor1.apply();
        });


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        int selectedRadioButtonId = sharedPreferences_2.getInt("selectedRadioButtonId", -1);
        if (selectedRadioButtonId != -1) {
            idgroup.check(selectedRadioButtonId);
        }
    }

    public void imageOnclick_sub_fourth(View view){
        int id = view.getId();
        if(id == R.id.web_back_4){
            Intent intent = new Intent(this, fourth.class);
            startActivity(intent);
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onBackPressed() {
        if(true) {
            Intent intent = new Intent(this, fourth.class);
            startActivity(intent);
        }
        else
        super.onBackPressed();
    }
}