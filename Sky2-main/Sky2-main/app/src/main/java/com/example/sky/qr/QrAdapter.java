package com.example.sky.qr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sky.history.LogAdapter;
import com.example.sky.history.LogDatabaseHelper;
import com.example.sky.history.LogItem;
import com.example.sky.MainActivity;
import com.example.sky.R;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QrAdapter extends RecyclerView.Adapter<QrAdapter.QrViewHolder> {
    private List<QrItem> qrList;
    private static final int REQUEST_CODE = 1;
    private List<QrItem> filteredQrList = new ArrayList<>();
    private Context context;
    String filter = "";
    private Intent myIntent;
    private List<Integer> a = new ArrayList<>();
    public QrAdapter(Context context, List<QrItem> qrList, Intent myIntent) {
        this.context = context;
        this.qrList = qrList;
        a = new ArrayList<>();
        for (int i = 0; i < qrList.size(); i++) {
            QrItem bookmarkItem = qrList.get(i);
            a.add(i);
        }
        this.filteredQrList.addAll(qrList);
        this.myIntent = myIntent;
    }

    public void setFilter(String filter) {
        this.filter = filter;
        filteredQrList.clear();
        a.clear();
        for (int i = 0; i < qrList.size(); i++) {
            QrItem qrItem = qrList.get(i);
            if (qrItem.getUrl().contains(filter)) {
                filteredQrList.add(qrItem);
                a.add(i);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.qr_item, parent, false);
        return new QrViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QrViewHolder holder,  @SuppressLint("RecyclerView") int position) {
        if (position >= filteredQrList.size()) {
            return; // Bỏ qua nếu vị trí không hợp lệ
        }
        QrItem qrItem = filteredQrList.get(position);
        holder.textUrl.setText(qrItem.getUrl());

        TextView textUrl;
        ImageView imageViewIcon, imageViewCancel;
        textUrl = holder.itemView.findViewById(R.id.text_url);
        imageViewIcon = holder.itemView.findViewById(R.id.imageViewIcon);
        imageViewCancel = holder.itemView.findViewById(R.id.imageViewCancel);

        if(qrItem.getUrl().isEmpty()){
            textUrl.setVisibility(View.GONE);
            imageViewIcon.setVisibility(View.GONE);
            imageViewCancel.setVisibility(View.GONE);
        } else {
            textUrl.setVisibility(View.VISIBLE);
            imageViewIcon.setVisibility(View.VISIBLE);
            imageViewCancel.setVisibility(View.VISIBLE);
        }
        new QrAdapter.DownloadImageTask(holder.imageViewIcon).execute(qrItem.getUrl());
        textUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("item_id", holder.textUrl.getText().toString());
                ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
            }
        });

        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("item_id", holder.textUrl.getText());
                ((AppCompatActivity)context).setResult(Activity.RESULT_OK, intent);
                ((AppCompatActivity)context).finish();
            }
        });
        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position >= filteredQrList.size()) {
                    return; // Bỏ qua nếu vị trí không hợp lệ
                }
                filteredQrList.remove(position);
                int tempp = a.get(position);
                a.remove(position);
                qrList.remove(tempp);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                QrDatabaseHelper dbHelper = new QrDatabaseHelper(context);
                dbHelper.setAllQrs(qrList);


            }
        });
    }

    // ĐIỀU CHỈNH LẠI SIZE CỦA filteredBookmarkList để không duyệt qua các "Page Title"
    @Override
    public int getItemCount() {
        return filteredQrList.size();
    }

    public static class QrViewHolder extends RecyclerView.ViewHolder {
        TextView textUrl;
        ImageView imageViewCancel, imageViewIcon;

        public QrViewHolder(@NonNull View itemView) {
            super(itemView);
            textUrl = itemView.findViewById(R.id.text_url);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            imageViewCancel = itemView.findViewById(R.id.imageViewCancel);
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
