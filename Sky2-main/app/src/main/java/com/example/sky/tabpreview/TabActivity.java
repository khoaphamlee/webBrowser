package com.example.sky.tabpreview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.sky.LogAdapter;
import com.example.sky.MainActivity;
import com.example.sky.R;
import com.example.sky.setting.fourth;

import java.util.List;

public class TabActivity extends AppCompatActivity {
    Context context;
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

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        TabItemAdapter tabAdapter = new TabItemAdapter(this, a);
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

