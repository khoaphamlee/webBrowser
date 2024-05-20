package com.example.sky.download;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.sky.R;

public class DownloadActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_READ_STORAGE = 1;
    private RecyclerView recyclerView;
    private DownloadAdapter adapter;
    private List<DownloadItem> downloadItemList;
    private List<DownloadItem> filteredList;
    private SearchView searchView;
    private ImageView backBtn, settingBtn;
    private DownloadDao downloadDao;
    private Switch switchDownload;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("pref2", 0);
        int isNightMode = sharedPreferences.getInt("search2", 0);
        if (isNightMode == 1) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setContentView(R.layout.download_setting);
        switchDownload = findViewById(R.id.switch_download);

        setContentView(R.layout.activity_download);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        }

        searchView = findViewById(R.id.searchView);
        backBtn = findViewById(R.id.backBtn);
        settingBtn = findViewById(R.id.settingBtn);

        recyclerView = findViewById(R.id.recycler_view);

        downloadDao = new DownloadDao(this);
        List<DownloadItem> downloadItems = downloadDao.getAllDownloadItems();

        downloadItemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new DownloadAdapter(this, downloadItemList, switchDownload.isChecked());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadDownloadHistory();
        setupSearchView();

        backBtn.setOnClickListener(v -> finish());
        settingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DownloadActivity.this, DownloadSetting.class);
            startActivity(intent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied to write to storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadDownloadHistory() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = downloadManager.query(query);

        if (cursor.moveToFirst()) {
            do {
                String fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                long fileSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                String fileDate = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP));
                String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                DownloadItem item = new DownloadItem(fileName, fileSize, fileDate, fileUri);
                downloadItemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        filteredList.addAll(downloadItemList);
        adapter.notifyDataSetChanged();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList.clear();
                if (newText.isEmpty()) {
                    filteredList.addAll(downloadItemList);
                } else {
                    for (DownloadItem item : downloadItemList) {
                        if (item.getFileName().toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    public void downloadAndSaveFile(String url, String fileName) {
        new Thread(() -> {
            try {
                URL downloadUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("Download", "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                    return;
                }

                InputStream input = connection.getInputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();

                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                input.close();
                byte[] fileData = output.toByteArray();
                output.close();

                runOnUiThread(() -> saveFileToExternalStorage(fileName, fileData));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveFileToExternalStorage(String fileName, byte[] fileData) {
        if (isExternalStorageWritable()) {
            File file = new File(getExternalFilesDir(null), fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileData);
                Toast.makeText(this, "File saved to: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                DownloadItem item = new DownloadItem(fileName, fileData.length, String.valueOf(System.currentTimeMillis()), Uri.fromFile(file).toString());
                downloadItemList.add(item);
                filteredList.add(item);
                adapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("External Storage", "External storage is not writable");
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
