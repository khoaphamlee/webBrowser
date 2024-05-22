package com.example.sky.tabpreview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sky.MainActivity;
import com.example.sky.R;
import com.example.sky.anony.Anony;
import com.example.sky.history.LogAdapter;

import java.util.List;

public class TabActivity extends AppCompatActivity {
    Context context;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.sky.R.layout.activity_tab_2);
        context = this;
        SharedPreferences sharedPreferences_3 = getSharedPreferences("pref2", 0);

        int isNightMode = sharedPreferences_3.getInt("search2", 0);



        TabItemDbHelper db2 = new TabItemDbHelper(this);
        List<TabItem> a = db2.getAllTabItems();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        ImageButton addBtn = findViewById(R.id.addBtn);
        ImageButton deleteBtn = findViewById(R.id.deleteBtn2);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("item_id_tab",-1 );

                ((AppCompatActivity)context).setResult(Activity.RESULT_OK, intent);
                ((AppCompatActivity)context).finish();

            }
        });
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

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                a.clear();
                TabItemAdapter logAdapter = new TabItemAdapter(context, a);
                recyclerView.setAdapter(logAdapter);
                db2.setAllTabItems(a);
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

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        TabItemAdapter tabAdapter= null;
        if (Anony.isAnony == false) {
            tabAdapter = new TabItemAdapter(this, a);
        }
        else{
            tabAdapter = new TabItemAdapter(this,Anony.a);
        }

        recyclerView.setAdapter(tabAdapter);
        if (isNightMode == 1){
            LinearLayout background = findViewById(R.id.background);
            background.setBackgroundColor(Color.BLACK);
            recyclerView.setBackgroundColor(Color.BLACK);
            recyclerView.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            addBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            addBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
            deleteBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            deleteBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
        }


    }
}

