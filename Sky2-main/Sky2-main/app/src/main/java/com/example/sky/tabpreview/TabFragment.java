package com.example.sky.tabpreview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sky.MainActivity;
import com.example.sky.R;
import com.example.sky.anony.Anony;

import java.util.List;

public class TabFragment extends Fragment {
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_tab_2, container, false);
        context = getContext();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("pref2", 0);
        int isNightMode = sharedPreferences.getInt("search2", 0);

        TabItemDbHelper db = new TabItemDbHelper(context);
        List<TabItem> tabItems = db.getAllTabItems();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        ImageButton addBtn = view.findViewById(R.id.addBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn2);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("item_id_tab", -1);
                getActivity().setResult(getActivity().RESULT_OK, intent);
                getActivity().finish();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        TabItemAdapter tabAdapter;
        if (Anony.isAnony == false) {
            tabAdapter = new TabItemAdapter(context, tabItems);
        } else {
            tabAdapter = new TabItemAdapter(context, Anony.a);
        }
        recyclerView.setAdapter(tabAdapter);

        if (isNightMode == 1) {
            LinearLayout background = view.findViewById(R.id.background);
            background.setBackgroundColor(Color.BLACK);
            recyclerView.setBackgroundColor(Color.BLACK);
            recyclerView.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            addBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            addBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
            deleteBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            deleteBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
        }

        return view;
    }
}
