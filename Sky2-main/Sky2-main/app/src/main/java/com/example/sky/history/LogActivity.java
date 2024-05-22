package com.example.sky.history;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sky.R;
import com.example.sky.anony.Anony;

import java.util.List;

public class LogActivity extends AppCompatActivity {
    ImageView deleteBtn,backBtn;
    Dialog dialog;


    SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences_3 = getSharedPreferences("pref2", 0);

        int isNightMode = sharedPreferences_3.getInt("search2", 0);

        Context context = this;
        setContentView(R.layout.activity_log);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        if (isNightMode == 1) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }
        if (Anony.isAnony == false) {
            RecyclerView recyclerView = findViewById(R.id.recycler_view);



            deleteBtn = findViewById(R.id.deleteBtn);
            if (isNightMode == 1) {
                deleteBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
            }
            searchView = findViewById(R.id.searchView);
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.confirm_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_2));
            Button okBtn = dialog.findViewById(R.id.okBtn);
            Button cancelBtn = dialog.findViewById(R.id.cancelBtn);
            if (isNightMode == 1) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_5));
                TextView tv = dialog.findViewById(R.id.messageTextView);
                tv.setTextColor(Color.WHITE);
                okBtn.setTextColor(Color.WHITE);
                cancelBtn.setTextColor(Color.WHITE);
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            LogDatabaseHelper dbHelper = new LogDatabaseHelper(this);
            List<LogItem> logList = dbHelper.getAllLogs(); // Lấy danh sách các mục nhật ký từ cơ sở dữ liệu

            LogAdapter logAdapter = new LogAdapter(this, logList, getIntent());
            recyclerView.setAdapter(logAdapter);

            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    logList.clear();
                    LogAdapter logAdapter = new LogAdapter(context, logList, getIntent());
                    recyclerView.setAdapter(logAdapter);
                    dbHelper.setAllLogs(logList);
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();


                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();

                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Xử lý khi người dùng nhấn nút tìm kiếm (ví dụ: gọi hàm tìm kiếm dữ liệu)
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    List<LogItem> logList = dbHelper.getAllLogs(); // Lấy danh sách các mục nhật ký từ cơ sở dữ liệu

                    LogAdapter logAdapter = new LogAdapter(context, logList, getIntent());
                    logAdapter.setFilter(newText);
                    recyclerView.setAdapter(logAdapter);
                    return false;
                }
            });


        }
    }


}
