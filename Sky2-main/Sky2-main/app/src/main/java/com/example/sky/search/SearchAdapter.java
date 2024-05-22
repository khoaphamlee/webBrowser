package com.example.sky.search;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sky.MainActivity;
import com.example.sky.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private List<SearchItem> searchList;
    private Context context;

    public SearchAdapter(Context context, List<SearchItem> searchList) {
        this.context = context;
        this.searchList = searchList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SearchItem searchItem = searchList.get(position);
        holder.textKeyword.setText(searchItem.getKeyword());
        ImageView upImg = holder.itemView.findViewById(R.id.imageViewCancel);
        LinearLayout searchLayout = holder.itemView.findViewById(R.id.searchLayout);

        SharedPreferences sharedPreferences_3 = context.getSharedPreferences("pref2", 0);
        int isNightMode = sharedPreferences_3.getInt("search2", 0);

        ColorStateList colorStateList2 = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black_blue_lighter));

        if(isNightMode == 1){
            holder.itemView.setBackgroundTintList(colorStateList2);
            ImageView icon  = holder.itemView.findViewById(R.id.imageViewIcon);
            icon.setBackgroundTintList(colorStateList2);
            holder.textKeyword.setTextColor(Color.WHITE);
            icon.setBackgroundTintList(colorStateList2);
            icon.setImageTintList(context.getResources().getColorStateList(android.R.color.white));
            upImg.setImageTintList(context.getResources().getColorStateList(android.R.color.white));
        }

        // Xử lý sự kiện khi người dùng nhấn vào một mục tìm kiếm
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý logic khi nhấn vào một mục (ví dụ: chuyển sang màn hình MainActivity)
                MainActivity mainA = null;
                if (context instanceof MainActivity) {
                    mainA = (MainActivity) context;
                }
                if(mainA != null){
                    mainA.loadMyUrl(searchItem.getKeyword());
                    mainA.rc.setVisibility(View.INVISIBLE);
                    mainA.urlInput.clearFocus();

                    SearchDatabaseHelper dp5 =new SearchDatabaseHelper(context);
                    List<SearchItem> searchList = dp5.getAllSearches();

                    for(int i = 0;i<searchList.size();i++){
                        if(searchList.get(i).getKeyword().equals(searchItem.getKeyword())){
                            searchList.remove(i);
                            searchList.add(0,new SearchItem(searchItem.getKeyword()));
                        }
                    }
                    dp5.setAllSearches(searchList);
                    SearchAdapter searchA = new SearchAdapter(context,searchList);
                    mainA.rc.setAdapter(searchA);




                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog  = new Dialog(context);
                dialog.setContentView(R.layout.delete_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.rounded_bg_2));


                Button cancleBtn2 = dialog.findViewById(R.id.cancelBtn);
                Button okBtn2 = dialog.findViewById(R.id.okBtn);


                okBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        SearchDatabaseHelper db6 = new SearchDatabaseHelper(context);

                        searchList.remove(position);
                        db6.setAllSearches(searchList);
                        SearchAdapter beginAdapter = new SearchAdapter(context,searchList);
                        RecyclerView searchRecycle = ((MainActivity)context).findViewById(R.id.searchRecycle);
                        searchRecycle.setLayoutManager(new LinearLayoutManager(context));
                        searchRecycle.setAdapter(beginAdapter);

                    }
                });

                cancleBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();



                    }
                });
                dialog.show();
                return true;

            }
        });




        upImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainA = null;
                if (context instanceof MainActivity) {
                    mainA = (MainActivity) context;
                }
                if(mainA != null) {
                    mainA.urlInput.setText(searchItem.getKeyword());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView textKeyword;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            textKeyword = itemView.findViewById(R.id.text_event);
        }
    }
}
