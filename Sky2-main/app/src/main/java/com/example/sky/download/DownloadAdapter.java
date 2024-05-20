package com.example.sky.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import com.example.sky.R;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {

    private List<DownloadItem> downloadItemList;
    private Switch switchDownload;
    private Context context;

    private boolean isSwitchChecked;

    public DownloadAdapter(Context context, List<DownloadItem> downloadItemList, boolean isSwitchChecked) {
        this.context = context;
        this.downloadItemList = downloadItemList;
        this.isSwitchChecked = isSwitchChecked;
    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.download_item, parent, false);
        return new DownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
        DownloadItem item = downloadItemList.get(position);
        holder.fileNameTextView.setText(item.getFileName());
        holder.fileSizeTextView.setText(formatFileSize(item.getFileSize()));
        holder.fileDateTextView.setText(item.getFileDate());

        ImageView imageViewIcon, imageViewCancel;
        TextView fileName, fileSize, fileDate;

        fileName = holder.itemView.findViewById(R.id.fileNameTextView);
        fileSize = holder.itemView.findViewById(R.id.fileSizeTextView);
        fileDate = holder.itemView.findViewById(R.id.fileDateTextView);
        imageViewIcon = holder.itemView.findViewById(R.id.imageViewIcon);
        imageViewCancel = holder.itemView.findViewById(R.id.imageViewCancel);

        holder.fileNameTextView.setOnClickListener(v -> {

                openFile(item);

        });



        holder.imageViewCancel.setOnClickListener(v -> {
            // Lấy vị trí của item được click
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                // Xóa mục tải xuống khỏi danh sách
                downloadItemList.remove(currentPosition);
                // Thông báo cho Adapter biết rằng mục đã bị xóa
                notifyItemRemoved(currentPosition);
                // Cập nhật lại số lượng item sau khi xóa
                notifyItemRangeChanged(currentPosition, downloadItemList.size());
                DownloadDatabaseHelper dbHelper = new DownloadDatabaseHelper(context);
                //dbHelper.deleteDownload(item.getId());
            }
        });
    }

    private void openFile(DownloadItem item) {
        File file = new File(Uri.parse(item.getFileUri()).getPath());
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, getMimeType(fileUri.toString()));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Không thể mở tệp này", Toast.LENGTH_SHORT).show();
            Log.e("File Open Error", "Error opening file", e);
        }
    }
    @Override
    public int getItemCount() {
        return downloadItemList.size();
    }

    private String formatFileSize(long sizeInBytes) {
        if (sizeInBytes >= 1024 * 1024) {
            double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
            return String.format("%.2f MB", sizeInMB);
        } else {
            double sizeInKB = sizeInBytes / 1024.0;
            return String.format("%.2f KB", sizeInKB);
        }
    }

    public static class DownloadViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;
        TextView fileSizeTextView;
        TextView fileDateTextView;
        ImageView imageViewIcon;
        ImageView imageViewCancel;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
            fileSizeTextView = itemView.findViewById(R.id.fileSizeTextView);
            fileDateTextView = itemView.findViewById(R.id.fileDateTextView);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            imageViewCancel = itemView.findViewById(R.id.imageViewCancel);
        }
    }
    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type != null ? type : "*/*";
    }




    public boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
