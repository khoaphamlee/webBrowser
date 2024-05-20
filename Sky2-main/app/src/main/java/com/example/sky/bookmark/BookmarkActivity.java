package com.example.sky.bookmark;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sky.LogAdapter;
import com.example.sky.R;

import java.util.List;

public class BookmarkActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_bookmark);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        backBtn = findViewById(R.id.backBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        searchView = findViewById(R.id.searchView);

        if (isNightMode == 1) {
            deleteBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
        }

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirm_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_2));
        Button okBtn = dialog.findViewById(R.id.okBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BookmarkDatabaseHelper dbHelper = new BookmarkDatabaseHelper(this);
        List<BookmarkItem> bookmarkList = dbHelper.getAllBookmarks();

        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(this, bookmarkList, getIntent());
        recyclerView.setAdapter(bookmarkAdapter);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                bookmarkList.clear();
                BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(context, bookmarkList,getIntent());
                recyclerView.setAdapter(bookmarkAdapter);
                dbHelper.setAllBookmarks(bookmarkList);
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
                List<BookmarkItem> bookmarkList = dbHelper.getAllBookmarks();
                BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(context, bookmarkList, getIntent());
                bookmarkAdapter.setFilter(newText);
                recyclerView.setAdapter(bookmarkAdapter);
                return false;
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }
}
