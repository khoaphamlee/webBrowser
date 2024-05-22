package com.example.sky.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.sky.MainActivity;
import com.example.sky.R;
import com.example.sky.bookmark.BookmarkActivity;
import com.example.sky.download.DownloadActivity;
import com.example.sky.qr.QrActivity;

import java.util.ArrayList;

public class fourth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences_3 = getSharedPreferences("pref2", 0);

        int isNightMode = sharedPreferences_3.getInt("search2", 0);
        if (isNightMode == 1){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        setContentView(com.example.sky.R.layout.activity_fourth);

        ArrayList<String> lvDataModels = new ArrayList<>();
        lvDataModels.add("Công cụ tìm kiếm");
        lvDataModels.add("Chủ đề");
        lvDataModels.add("Qr");
        lvDataModels.add("Tải xuống");
        lvDataModels.add("Dấu trang");

        // Khởi tạo Adapter cho lv
        ArrayAdapter<String> lvAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lvDataModels);
        ListView lv = findViewById(R.id.lv);
        lv.setAdapter(lvAdapter);


        // Thêm Listener cho lv
        lv.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            switch (selectedItem) {
                case "Công cụ tìm kiếm":
                    Intent intent = new Intent(this, sub_fourth.class);
                    startActivity(intent);
                    break;
                case "Chủ đề":
                    intent = new Intent(this, sub_fourth_2.class);
                    startActivity(intent);
                    break;

                case "Dấu trang":
                    intent = new Intent(this, BookmarkActivity.class);
                    startActivity(intent);
                    break;
                case "Tải xuống":
                    intent = new Intent(this, DownloadActivity.class);
                    startActivity(intent);
                    break;
                case "Qr":
                    intent = new Intent(this, QrActivity.class);
                    startActivity(intent);
                    break;

            }
        });


    }
    public void imageClick_fourth(View view){
        int id = view.getId();
        if(id == R.id.web_back_5){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void onBackPressed() {
        if(true) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
            super.onBackPressed();
    }
}