package com.example.sky.begin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sky.MainActivity;
import com.example.sky.R;
import com.example.sky.bookmark.BookmarkActivity;
import com.example.sky.history.LogActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;




public class BeginAdapter extends RecyclerView.Adapter<BeginAdapter.LogViewHolder> {
    private List<BeginItem> beginList;

    private Context context;



    ArrayList<Integer> a;

    public BeginAdapter(Context context, List<BeginItem> logList) {
        this.context = context;
        this.beginList = logList;

        this.beginList.add(0,new BeginItem("",""));

    }



    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.begin_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, @SuppressLint("RecyclerView") int position) {


        BeginItem beginItem = beginList.get(position);

        holder.textEvent.setText(beginItem.getEvent());



        TextView textEvent = holder.itemView.findViewById(R.id.title);
        ImageView imageViewIcon = holder.itemView.findViewById(R.id.webIcon);

        SharedPreferences sharedPreferences_3 = context.getSharedPreferences("pref2", 0);
        int isNightMode = sharedPreferences_3.getInt("search2", 0);

        ColorStateList colorStateList2 = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black_blue_lighter));

        if(isNightMode == 1){
            textEvent.setTextColor(Color.WHITE);
        }
        if(position == 0){
            holder.textEvent.setText("Thêm mới");
            holder.webIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add));

            textEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)context).dialogB.show();


                }
            });

            imageViewIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((MainActivity)context).dialogB.show();
                }
            });
        }
        else{
            textEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)context).loadMyUrl(beginItem.getUrl());
                    ((MainActivity)context).homePage.setVisibility(View.INVISIBLE);
                }
            });

            imageViewIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)context).loadMyUrl(beginItem.getUrl());
                    ((MainActivity)context).homePage.setVisibility(View.INVISIBLE);
                }
            });

            textEvent.setOnLongClickListener(new View.OnLongClickListener() {
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
                            BeginDataHelper db6 = new BeginDataHelper(context);
                            beginList.remove(0);
                            beginList.remove(beginItem);
                            db6.setAllLogs(beginList);


                            BeginAdapter beginAdapter = new BeginAdapter(context,beginList);
                            RecyclerView beginRecycle = ((MainActivity)context).findViewById(R.id.beginRecycle);
                            beginRecycle.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
                            beginRecycle.setAdapter(beginAdapter);

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


            imageViewIcon.setOnLongClickListener(new View.OnLongClickListener() {
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
                            BeginDataHelper db6 = new BeginDataHelper(context);
                            beginList.remove(position);
                            beginList.remove(0);
                            db6.setAllLogs(beginList);
                            BeginAdapter beginAdapter = new BeginAdapter(context,beginList);
                            RecyclerView beginRecycle = ((MainActivity)context).findViewById(R.id.beginRecycle);
                            beginRecycle.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
                            beginRecycle.setAdapter(beginAdapter);

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
        }









        new DownloadImageTask(holder.webIcon).execute(beginItem.getUrl());





    }

    @Override
    public int getItemCount() {
        return beginList.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView textEvent;
        ImageView webIcon;




        public LogViewHolder(@NonNull View itemView) {
            super(itemView);


            textEvent = itemView.findViewById(R.id.title);
            webIcon= itemView.findViewById(R.id.webIcon);


        }


    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap icon = null;
            try {
                InputStream in = new URL("https://www.google.com/s2/favicons?domain=" + url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);

            }
        }
    }


}

