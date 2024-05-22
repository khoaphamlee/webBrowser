package com.example.sky.tabpreview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sky.MainActivity;
import com.example.sky.R;
import com.example.sky.anony.Anony;

import java.util.ArrayList;
import java.util.List;

public class TabItemAdapter extends RecyclerView.Adapter<TabItemAdapter.ViewHolder> {
    private List<TabItem> tabItemList;

    private List<TabItem> newTabItemList;
    private Context context;
    ArrayList<Integer> a;
    public TabItemAdapter(Context context, List<TabItem> tabItemList) {
        this.context = context;
        this.tabItemList = tabItemList;
        a = new ArrayList<>();
        for(int i = 0;i< tabItemList.size();i++){
            a.add(i);
        }
        newTabItemList = new ArrayList<>(tabItemList);




    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_tab_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TabItem tabItem = tabItemList.get(position);
        holder.titleTextView.setText(tabItem.getTitle());


        holder.imageView.setImageBitmap(tabItem.getBitmap());
        ImageView imageViewCancel = holder.itemView.findViewById(R.id.deleteTab);
        ImageView imageView = holder.itemView.findViewById(R.id.imageView);
         imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                if(Anony.isAnony == false) {
                    newTabItemList.remove(position);
                    int tempp = a.get(position);
                    CacheTab.lruCache.remove(tabItemList.get(tempp).getId());
                    tabItemList.remove(tempp);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    TabItemDbHelper dbHelper = new TabItemDbHelper(context);
                    dbHelper.setAllTabItems(tabItemList);
                }
                else{
                    newTabItemList.remove(position);
                    int tempp = a.get(position);
                    CacheTab.AlruCache.remove(tabItemList.get(tempp).getId());
                    tabItemList.remove(tempp);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());

                }


            }
        });

         imageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 TabItem tabItem = tabItemList.get(position);
                 Intent intent = new Intent(context, MainActivity.class);
                 intent.putExtra("item_id_tab",tabItem.getId() );
                 ((AppCompatActivity)context).setResult(Activity.RESULT_OK, intent);
                 ((AppCompatActivity)context).finish();
             }
         });

        SharedPreferences sharedPreferences_3 = context.getSharedPreferences("pref2", 0);

        int isNightMode = sharedPreferences_3.getInt("search2", 0);
        if(isNightMode ==1){
            holder.itemView.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.darker_gray));
            holder.titleTextView.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.darker_gray));
        }
        if(Anony.isAnony == false){
            int tempp = a.get(position);
            if(tabItemList.get(tempp).getId() == MySharedPreferences.getOld(context)){
                holder.itemView.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_blue_bright));
                holder.titleTextView.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_blue_bright));
            }
        }
        else{
            int tempp = a.get(position);
            if(tabItemList.get(tempp).getId() == MySharedPreferences.getAOld(context)){
                holder.itemView.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_blue_bright));
                holder.titleTextView.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.holo_blue_bright));
            }
        }
    }

    @Override
    public int getItemCount() {
        return newTabItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public ImageView imageView;

        public ImageView cancelImg;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);

            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}

