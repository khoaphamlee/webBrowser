package com.example.sky.download;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Context;
import android.widget.ImageView;
import android.graphics.Color;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.graphics.PorterDuff;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Switch;
import com.example.sky.R;



public class DownloadSetting extends AppCompatActivity {
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editorSP;

    private Switch switchSaveFile;
    private Switch switchDownload;
    private TextView textBackTab2;
    private TextView textStatusNewTab;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_setting);
        SharedPreferences sharedPreferences_3 = getSharedPreferences("pref2", 0);

        int isNightMode = sharedPreferences_3.getInt("search2", 0);
        if (isNightMode == 1){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        sharedPrefs = getSharedPreferences("ShowTabs", Context.MODE_PRIVATE);
        editorSP = sharedPrefs.edit();

        switchSaveFile = findViewById(R.id.switch_save_file);
        switchDownload = findViewById(R.id.switch_download);
        textBackTab2 = findViewById(R.id.text_back_tab_2);
        textStatusNewTab = findViewById(R.id.text_status_new_tab);

        boolean switchSaveFileState = sharedPrefs.getBoolean("SwitchSaveFileState", false);
        boolean switchDownloadState = sharedPrefs.getBoolean("SwitchDownloadState", false);

        switchSaveFile.setChecked(switchSaveFileState);
        switchDownload.setChecked(switchDownloadState);

        updateTextView(textBackTab2, switchSaveFileState);

        switchSaveFile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editorSP.putBoolean("SwitchSaveFileState", isChecked);
            editorSP.apply();
            updateTextView(textBackTab2, isChecked);
        });

        switchDownload.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editorSP.putBoolean("SwitchDownloadState", isChecked);
            editorSP.apply();
        });


        backBtn = findViewById(R.id.web_back_11);
        backBtn.setOnClickListener(v -> finish());
    }

    private void updateTextView(TextView textView, boolean isChecked) {
        if (isChecked) {
            textView.setText("Đang bật");
        } else {
            textView.setText("Chưa bật");
        }
    }

    private void updateSwitchColor(Switch switchView, boolean isChecked) {
        if (isChecked) {
            switchView.getThumbDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
            switchView.getTrackDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        } else {
            switchView.getThumbDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
            switchView.getTrackDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }
    }
}