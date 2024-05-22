package com.example.sky.qr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.example.sky.history.LogAdapter;
import com.example.sky.R;

import java.util.List;

public class QrActivity extends AppCompatActivity {
    ImageView deleteBtn,backBtn;
    Dialog dialog;
    SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("pref2", 0);
        int isNightMode = sharedPreferences.getInt("search2", 0);
        if (isNightMode == 1) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        Context context = this;
        setContentView(R.layout.activity_qr);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        backBtn = findViewById(R.id.backBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        searchView = findViewById(R.id.searchView);

        if (isNightMode == 1) {
            deleteBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
        }

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirm_dialog_del_qr);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_2));
        Button okBtn = dialog.findViewById(R.id.okBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        QrDatabaseHelper dbHelper = new QrDatabaseHelper(this);
        List<QrItem> qrList = dbHelper.getAllQrs();

        QrAdapter qrAdapter = new QrAdapter(this, qrList, getIntent());
        recyclerView.setAdapter(qrAdapter);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                qrList.clear();
                QrAdapter bookmarkAdapter = new QrAdapter(context, qrList,getIntent());
                recyclerView.setAdapter(bookmarkAdapter);
                dbHelper.setAllQrs(qrList);
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<QrItem> qrList = dbHelper.getAllQrs();
                QrAdapter qrAdapter = new QrAdapter(context, qrList, getIntent());
                qrAdapter.setFilter(newText);
                recyclerView.setAdapter(qrAdapter);
                return false;
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        if (isNightMode == 1) {

            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_5));
            TextView tv = dialog.findViewById(R.id.messageTextView);
            tv.setTextColor(Color.WHITE);
            okBtn.setTextColor(Color.WHITE);
            cancelBtn.setTextColor(Color.WHITE);
        }
    }
}
