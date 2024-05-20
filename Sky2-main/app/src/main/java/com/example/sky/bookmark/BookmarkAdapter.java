package com.example.sky.bookmark;

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

import com.example.sky.LogAdapter;
import com.example.sky.LogDatabaseHelper;
import com.example.sky.LogItem;
import com.example.sky.MainActivity;
import com.example.sky.R;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {
    private List<BookmarkItem> bookmarkList;
    private static final int REQUEST_CODE = 1;
    private List<BookmarkItem> filteredBookmarkList = new ArrayList<>();
    private Context context;
    String filter = "";
    private Intent myIntent;
    private List<Integer> a = new ArrayList<>();
    public BookmarkAdapter(Context context, List<BookmarkItem> bookmarkList, Intent myIntent) {
        this.context = context;
        this.bookmarkList = bookmarkList;
        a = new ArrayList<>();
        for (int i = 0; i < bookmarkList.size(); i++)
            a.add(i);
        this.filteredBookmarkList.addAll(bookmarkList);
        this.myIntent = myIntent;
    }

    public void setFilter(String filter) {
        this.filter = filter;
        filteredBookmarkList.clear();
        a.clear();
        for (int i = 0; i < bookmarkList.size(); i++) {
            BookmarkItem bookmarkItem = bookmarkList.get(i);
            if (bookmarkItem.getUrl().contains(filter) || bookmarkItem.getEvent().contains(filter)) {
                filteredBookmarkList.add(bookmarkItem);
                a.add(i);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookmark_item, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder,  @SuppressLint("RecyclerView") int position) {
        if (position >= filteredBookmarkList.size()) {
            return; // Bỏ qua nếu vị trí không hợp lệ
        }
        BookmarkItem bookmarkItem = filteredBookmarkList.get(position);

        holder.textEvent.setText(bookmarkItem.getEvent());
        holder.textUrl.setText(bookmarkItem.getUrl());

        TextView textUrl, textEvent;
        ImageView imageViewIcon, imageViewCancel;
        textUrl = holder.itemView.findViewById(R.id.text_url);
        textEvent = holder.itemView.findViewById(R.id.text_event);
        imageViewIcon = holder.itemView.findViewById(R.id.imageViewIcon);
        imageViewCancel = holder.itemView.findViewById(R.id.imageViewCancel);

        if(bookmarkItem.getUrl().isEmpty()){
            textEvent.setEnabled(false);
            textUrl.setVisibility(View.GONE);
            imageViewIcon.setVisibility(View.GONE);
            imageViewCancel.setVisibility(View.GONE);
        } else {
            textEvent.setEnabled(true);
            textUrl.setVisibility(View.VISIBLE);
            imageViewIcon.setVisibility(View.VISIBLE);
            imageViewCancel.setVisibility(View.VISIBLE);
        }
        new BookmarkAdapter.DownloadImageTask(holder.imageViewIcon).execute(bookmarkItem.getUrl());
        textUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("item_id", holder.textUrl.getText().toString());
                ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
            }
        });

        textEvent.setOnClickListener(new View.OnClickListener() {
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
                if (position >= filteredBookmarkList.size()) {
                    return; // Bỏ qua nếu vị trí không hợp lệ
                }
                filteredBookmarkList.remove(position);
                int tempp = a.get(position);
                a.remove(position);
                bookmarkList.remove(tempp);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                BookmarkDatabaseHelper dbHelper = new BookmarkDatabaseHelper(context);
                dbHelper.setAllBookmarks(bookmarkList);


            }
        });
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    /*public void removeBookmark(int position) {
        BookmarkItem removedItem = bookmarkList.remove(position);
        notifyDataSetChanged();

        // Thực hiện các hành động cần thiết khi xóa bookmark, chẳng hạn như cập nhật cơ sở dữ liệu
        BookmarkDatabaseHelper dbHelper = new BookmarkDatabaseHelper(context);
        dbHelper.removeBookmark(removedItem.getUrl());
    }*/

    public static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        TextView textUrl, textEvent;
        ImageView imageViewCancel, imageViewIcon;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            textUrl = itemView.findViewById(R.id.text_url);
            textEvent = itemView.findViewById(R.id.text_event);
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
