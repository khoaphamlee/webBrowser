package com.example.sky;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.LruCache;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sky.anony.Anony;
import com.example.sky.begin.BeginAdapter;
import com.example.sky.begin.BeginDataHelper;
import com.example.sky.begin.BeginItem;
import com.example.sky.bookmark.BookmarkActivity;
import com.example.sky.bookmark.BookmarkDatabaseHelper;
import com.example.sky.bookmark.BookmarkItem;
import com.example.sky.download.DownloadDatabaseHelper;
import com.example.sky.download.DownloadItem;
import com.example.sky.history.LogActivity;
import com.example.sky.history.LogDatabaseHelper;
import com.example.sky.qr.QrActivity;
import com.example.sky.qr.QrDatabaseHelper;
import com.example.sky.qr.QrItem;
import com.example.sky.search.SearchAdapter;
import com.example.sky.search.SearchDatabaseHelper;
import com.example.sky.search.SearchItem;
import com.example.sky.setting.fourth;
import com.example.sky.tabpreview.CacheTab;
import com.example.sky.tabpreview.EasyViewUtils;
import com.example.sky.tabpreview.MySharedPreferences;
import com.example.sky.tabpreview.TabActivity;
import com.example.sky.tabpreview.TabItem;
import com.example.sky.tabpreview.TabItemDbHelper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    String oldUrl ;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private float offsetX;
    private float offsetY;
    private static final int CLICK_ACTION_THRESHOLD = 200;
    private long pressStartTime;

    public EditText urlInput;

    Dialog dialog, dialog2;
    public Dialog dialogB;
    WebView webView;

    RelativeLayout mainLayout;

    SharedPreferences preferences;

    Bundle a;
    Context context = this;

    String search1,search2;
    SearchAdapter searchAdapter;
    public RecyclerView rc;

    public Boolean flag = false;

    public WebView testWeb;

    public RelativeLayout homePage;

    Boolean backk ;
    int countBack = 0;

    private QrDatabaseHelper qrDatabaseHelper;
    private DownloadDatabaseHelper downloadDao;



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                String qrContent = result.getContents();
                dialog2 = new Dialog(this);
                dialog2.setContentView(R.layout.confirm_dialog_qr);
                dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_2));
                TextView message = dialog2.findViewById(R.id.messageTextView);
                Button okBtn = dialog2.findViewById(R.id.okBtn);
                Button cancelBtn = dialog2.findViewById(R.id.cancelBtn);
                QrItem qrItem = new QrItem(qrContent);
                qrDatabaseHelper = new QrDatabaseHelper(context);
                qrDatabaseHelper.addQr(qrItem);
                message.setText("Bạn có muốn truy cập:" + qrContent);
                dialog2.show();
                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.cancel();
                        loadMyUrl(qrContent);
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.cancel();
                    }
                });
                Toast.makeText(this, "Scanned: " + qrContent, Toast.LENGTH_LONG).show();

                //handleQRCodeResult(qrContent);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                if (data.hasExtra("item_id")) {
                    loadMyUrl(String.valueOf(data.getStringExtra("item_id")));
                    getIntent().removeExtra("item_id");
                }
                else
                    loadMyUrl(search1);

            }
        }

        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                if (data.hasExtra("item_id")) {
                    loadMyUrl(String.valueOf(data.getStringExtra("item_id")));
                    getIntent().removeExtra("item_id");
                }
                else
                    loadMyUrl(search1);

            }
        }

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if(Anony.isAnony == false) {
                    if (data.hasExtra("item_id_tab")) {


                        int myIntData = data.getIntExtra("item_id_tab", 0);

                        if (myIntData == -1) {
                            homePage.setVisibility(View.VISIBLE);
                            backk =true;
                            webView.setVisibility(webView.INVISIBLE);
                            int id = 0;

                            TabItemDbHelper db2 = new TabItemDbHelper(this);
                            List<TabItem> a = db2.getAllTabItems();
                            if (a.size() != 0) {
                                id = a.get(a.size() - 1).getId() + 1;

                            }
                            int width = (int) EasyViewUtils.dp2px(context, 90);
                            int height = (int) EasyViewUtils.dp2px(context, 160);
                            Bitmap bitmap = null;
                            Canvas canvas = null;
                            if (width > 0 && height > 0) {
                                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                canvas = new Canvas(bitmap);

                                int left = webView.getScrollX();
                                int top = webView.getScrollY();
                                canvas.translate(-left, -top);

                                float scaleX = (float) width / webView.getWidth();
                                float scaleY = (float) height / webView.getHeight();
                                canvas.scale(scaleX, scaleY, left, top);

                                webView.draw(canvas);

                                canvas.setBitmap(null);
                            }

                            List<String> stringList = new ArrayList<>();
                            stringList.add("");
                            stringList.add("");
                            MySharedPreferences.saveOld(context, id);

                            TabItem tempt = new TabItem(id, "Thẻ mới", "", stringList, bitmap);

                            db2.insertTabItem(tempt);

                        } else {
                            MySharedPreferences.saveOld(context, myIntData);
                            ;
                            TabItemDbHelper db2 = new TabItemDbHelper(this);
                            List<TabItem> a = db2.getAllTabItems();

                            if (CacheTab.lruCache.get(myIntData) != null) {
                                webView.restoreState(CacheTab.lruCache.get(myIntData));

                            } else {
                                for (int i = 0; i < a.size(); i++) {
                                    if (a.get(i).getId() == myIntData) {
                                        loadMyUrl(a.get(i).getUrl());

                                        break;
                                    }
                                }
                            }
                        }


                    } else
                        loadMyUrl(search1);
                }
                else{
                    if (data.hasExtra("item_id_tab")) {


                        int myIntData = data.getIntExtra("item_id_tab", 0);
                        if (myIntData == -1) {

                            int id = 0;
                            loadMyUrl("");


                            if (Anony.a.size() != 0) {
                                id = Anony.a.get(Anony.a.size() - 1).getId() + 1;
                            }

                            int width = (int) EasyViewUtils.dp2px(context, 90);
                            int height = (int) EasyViewUtils.dp2px(context, 160);
                            Bitmap bitmap = null;
                            Canvas canvas = null;
                            if (width > 0 && height > 0) {
                                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                canvas = new Canvas(bitmap);

                                int left = webView.getScrollX();
                                int top = webView.getScrollY();
                                canvas.translate(-left, -top);

                                float scaleX = (float) width / webView.getWidth();
                                float scaleY = (float) height / webView.getHeight();
                                canvas.scale(scaleX, scaleY, left, top);

                                webView.draw(canvas);

                                canvas.setBitmap(null);
                            }
                            List<String> stringList = new ArrayList<>();
                            stringList.add("");
                            stringList.add("");
                            MySharedPreferences.saveAOld(context, id);

                            TabItem tempt = new TabItem(id, webView.getTitle(), webView.getUrl(), stringList, bitmap);

                            Anony.a.add(tempt);

                        } else {
                            MySharedPreferences.saveAOld(context, myIntData);
                            ;



                            if (CacheTab.AlruCache.get(myIntData) != null) {
                                webView.restoreState(CacheTab.AlruCache.get(myIntData));
                                if(CacheTab.AlruCache.get(MySharedPreferences.getAOld(context)) != null) {

                                }
                            } else {
                                for (int i = 0; i < Anony.a.size(); i++) {
                                    if (Anony.a.get(i).getId() == myIntData) {
                                        loadMyUrl(Anony.a.get(i).getUrl());

                                        break;
                                    }
                                }
                            }

                        }


                    } else
                        loadMyUrl(search1);




                }

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
    private void changeColors(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            // Duyệt qua tất cả các view con trong viewGroup
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                changeColors(child); // Đệ quy để xử lý các view con

                // Kiểm tra nếu view là ImageView hoặc RelativeLayout

                    changeBackgroundColor(child);

            }
        }
    }

    private void changeBackgroundColor(View view) {
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            // Lấy màu sắc của background
            int color = ((ColorDrawable) background).getColor();

            // Kiểm tra nếu màu sắc là đen thì thay đổi thành trắng và ngược lại
            if (color == Color.BLACK) {
                view.setBackgroundColor(Color.WHITE);
            } else {
                view.setBackgroundColor(Color.BLACK);
            }
        }
    }
    @SuppressLint({"SuspiciousIndentation", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences_2 = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences_3 = getSharedPreferences("pref2", 0);
        downloadDao = new DownloadDatabaseHelper(this);
        int isNightMode = sharedPreferences_3.getInt("search2", 0);
        String searchProvider = sharedPreferences_2.getString("imageToShow", "google");



        switch (searchProvider) {
            case "yahoo":
                search1 = "yahoo.com";
                search2 = "https://search.yahoo.com/search?p=" ;

                break;
            case "bing":
                search1 = "bing.com";
                search2 = "https://www.bing.com/search?q=";

                break;
            case "coccoc":
                search1 = "coccoc.com";
                search2 = "https://coccoc.com/search#query=";

                break;
            case "duckduckgo":
                search1 = "duckduckgo.com";
                search2 = "https://duckduckgo.com/?q=";

                break;
            case "google":
            default:
                search1 = "google.com";
                search2 = "https://www.google.com/search?q=";

                break;
        }



        oldUrl = "";
        Context context = this;
        LogDatabaseHelper dbHelper = new LogDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        TabItemDbHelper tabdbHelper = new TabItemDbHelper(this);
        SQLiteDatabase db2 = dbHelper.getWritableDatabase();


        setContentView(R.layout.activity_main);
        SearchDatabaseHelper dp5 =new SearchDatabaseHelper(this);
        List<SearchItem> searchList = dp5.getAllSearches();
         searchAdapter = new SearchAdapter(this,searchList);
        homePage = findViewById(R.id.homePage);
        rc = findViewById(R.id.searchRecycle);
        rc.setLayoutManager(new LinearLayoutManager(this));
        rc.setAdapter(searchAdapter);










        FloatingActionButton movableButton = findViewById(R.id.movableButton);


        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.main_dialog_content);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        if(isNightMode ==1)  dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_3));
        else
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_2));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                movableButton.setVisibility(View.VISIBLE);
            }
        });

        ImageButton qrBtn = dialog.findViewById(R.id.qrBtn);
        ImageButton searchBtn = dialog.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                urlInput.requestFocus();
            }
        });
        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });
        qrBtn.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, QrActivity.class);
                startActivity(intent);
                return false;
            }
        });

        ImageButton history = dialog.findViewById(R.id.historyBtn);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(context, LogActivity.class);
                intent.putExtra("oldUrl", webView.getUrl());
                startActivityForResult(intent,1);

            }
        });


        ImageButton settingBtn = dialog.findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(context, fourth.class);
                saveState();
                startActivityForResult(intent,3);

            }
        });

        ImageButton anonyBtn = dialog.findViewById(R.id.anonyBtn);
        anonyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(context, MainActivity.class);

                saveState();


                if(Anony.isAnony == false){
                    Anony.isAnony = true;


                }
                else{
                    Anony.isAnony = false;
                }

                startActivity(intent);
            }







        });

        ImageButton forwardBtn = dialog.findViewById(R.id.forwardBtn);
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(webView.canGoForward()) {
                    webView.goForward();
                }

            }
        });

        ImageButton homeBtn = dialog.findViewById(R.id.homeBtn);
        ImageButton bookmarkBtn = dialog.findViewById(R.id.bookmarkBtn);
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Anony.isAnony == false && homePage.getVisibility() != View.VISIBLE) {
                    BookmarkDatabaseHelper dpHelper = new BookmarkDatabaseHelper(context);
                    List<BookmarkItem> a = dpHelper.getAllBookmarks();
                    for (int i = 0; i < a.size(); i++) {
                        if (a.get(i).getUrl().equals(webView.getUrl())) {
                            ImageButton bookmarkBtn = dialog.findViewById(R.id.bookmarkBtn);
                            if (isNightMode == 1)
                                bookmarkBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
                            else {
                                bookmarkBtn.setImageTintList(getResources().getColorStateList(android.R.color.black));
                            }
                            Toast.makeText(context, "Xóa dấu trang!", Toast.LENGTH_SHORT).show();
                            a.remove(i);
                            dpHelper.setAllBookmarks(a);
                            return;
                        }
                    }


                    BookmarkDatabaseHelper dbHelper = new BookmarkDatabaseHelper(context);

                    String currentUrl = webView.getUrl();
                    String currentTitle = webView.getTitle();

                    if (currentUrl != null && !currentUrl.isEmpty() && currentTitle != null && !currentTitle.isEmpty()) {
                        BookmarkItem bookmarkItem = new BookmarkItem(currentUrl, currentTitle);
                        dbHelper.addBookmark(bookmarkItem);
                        bookmarkBtn.setImageTintList(getResources().getColorStateList(android.R.color.holo_blue_dark));
                        Toast.makeText(context, "Thêm dấu trang!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Thêm lỗi.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        bookmarkBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Anony.isAnony == false && homePage.getVisibility() != View.VISIBLE) {
                    saveState();
                    Intent intent = new Intent(context, BookmarkActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                homePage.setVisibility(View.VISIBLE);
                backk =true;
                webView.setVisibility(webView.INVISIBLE);

            }
        });

        ImageButton tabBtn = dialog.findViewById(R.id.tabBtn);
        tabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

                if (Anony.isAnony == false) {

                    Intent intent = new Intent(context, TabActivity.class);


                    int width = (int) EasyViewUtils.dp2px(context, 90);
                    int height = (int) EasyViewUtils.dp2px(context, 160);
                    Bitmap bitmap = null;
                    Canvas canvas = null;
                    if(homePage.getVisibility() == View.INVISIBLE) {
                        if (width > 0 && height > 0) {
                            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                            canvas = new Canvas(bitmap);

                            int left = webView.getScrollX();
                            int top = webView.getScrollY();
                            canvas.translate(-left, -top);

                            float scaleX = (float) width / webView.getWidth();
                            float scaleY = (float) height / webView.getHeight();
                            canvas.scale(scaleX, scaleY, left, top);

                            webView.draw(canvas);

                            canvas.setBitmap(null);
                        }
                    }
                    else{
                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        canvas = new Canvas(bitmap);

                        int left = webView.getScrollX();
                        int top = webView.getScrollY();
                        canvas.translate(-left, -top);

                        float scaleX = (float) width / webView.getWidth();
                        float scaleY = (float) height / webView.getHeight();
                        canvas.scale(scaleX, scaleY, left, top);

                        homePage.draw(canvas);

                        canvas.setBitmap(null);
                    }

                    List<String> stringList = new ArrayList<>();
                    stringList.add("");
                    stringList.add("");


                    TabItemDbHelper db2 = new TabItemDbHelper(context);
                    List<TabItem> a = db2.getAllTabItems();
                    int con = 0;


                    for (int i = 0; i < a.size(); i++) {
                        if (a.get(i).getId() == MySharedPreferences.getOld(context)) {
                            a.get(i).setBitmap(bitmap);
                            a.get(i).setHistory(stringList);
                            if(homePage.getVisibility() == View.INVISIBLE)
                                a.get(i).setTitle(webView.getTitle());
                            else{
                                a.get(i).setTitle("Thẻ mới");
                            }
                            a.get(i).setUrl(webView.getUrl());
                            tabdbHelper.setAllTabItems(a);

                            if (CacheTab.lruCache.get(a.get(i).getId()) != null) {
                                CacheTab.lruCache.remove(a.get(i).getId());
                                Bundle aa = new Bundle();
                                webView.saveState(aa);
                                CacheTab.lruCache.put(a.get(i).getId(), aa);
                            } else {
                                Bundle aa = new Bundle();
                                webView.saveState(aa);
                                CacheTab.lruCache.put(a.get(i).getId(), aa);

                            }
                            con = 1;
                            startActivityForResult(intent, 2);

                        }
                    }


                    if (con == 0) {
                        int id = 0;

                            MySharedPreferences.saveOld(context,0);


                        TabItem tempt = null;
                        if(homePage.getVisibility() == View.INVISIBLE) {
                            tempt = new TabItem(id, webView.getTitle(), webView.getUrl(), new ArrayList<String>(), bitmap);
                        }
                        else{
                            tempt = new TabItem(id, "Thẻ mới", "", new ArrayList<String>(), bitmap);
                        }

                        if (CacheTab.lruCache == null) {
                            CacheTab.lruCache = new LruCache<>(20);
                        }
                        Bundle aa = new Bundle();
                        webView.saveState(aa);

                        CacheTab.lruCache.put(id, aa);
                        tabdbHelper.insertTabItem(tempt);
                        startActivityForResult(intent, 2);
                    }
                }
                else{
                    Intent intent = new Intent(context, TabActivity.class);


                    int width = (int) EasyViewUtils.dp2px(context, 90);
                    int height = (int) EasyViewUtils.dp2px(context, 160);
                    Bitmap bitmap = null;
                    Canvas canvas = null;
                    if(homePage.getVisibility() == View.INVISIBLE) {
                        if (width > 0 && height > 0) {
                            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                            canvas = new Canvas(bitmap);

                            int left = webView.getScrollX();
                            int top = webView.getScrollY();
                            canvas.translate(-left, -top);

                            float scaleX = (float) width / webView.getWidth();
                            float scaleY = (float) height / webView.getHeight();
                            canvas.scale(scaleX, scaleY, left, top);

                            webView.draw(canvas);

                            canvas.setBitmap(null);
                        }
                    }
                    else{
                        if (width > 0 && height > 0) {
                            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                            canvas = new Canvas(bitmap);

                            int left = webView.getScrollX();
                            int top = webView.getScrollY();
                            canvas.translate(-left, -top);

                            float scaleX = (float) width / webView.getWidth();
                            float scaleY = (float) height / webView.getHeight();
                            canvas.scale(scaleX, scaleY, left, top);

                            homePage.draw(canvas);

                            canvas.setBitmap(null);
                        }
                    }
                    List<String> stringList = new ArrayList<>();
                    stringList.add("");
                    stringList.add("");


                    if(Anony.a == null) Anony.a= new ArrayList<>();
                    int con = 0;
                    for (int i = 0; i < Anony.a.size(); i++) {
                        if (Anony.a.get(i).getId() == MySharedPreferences.getAOld(context)) {
                            Anony.a.get(i).setBitmap(bitmap);
                            if(homePage.getVisibility() == View.INVISIBLE)
                            Anony.a.get(i).setTitle(webView.getTitle());
                            else{
                                Anony.a.get(i).setTitle("Thẻ mới");
                            }
                            Anony.a.get(i).setUrl(webView.getUrl());


                            if (CacheTab.AlruCache.get(Anony.a.get(i).getId()) != null) {
                                CacheTab.AlruCache.remove(Anony.a.get(i).getId());
                                Bundle aa = new Bundle();
                                webView.saveState(aa);
                                CacheTab.AlruCache.put(Anony.a.get(i).getId(), aa);
                            } else {
                                Bundle aa = new Bundle();
                                webView.saveState(aa);
                                CacheTab.AlruCache.put(Anony.a.get(i).getId(), aa);

                            }
                            con = 1;
                            startActivityForResult(intent, 2);

                        }
                    }


                    if (con == 0) {
                        int id = 0;
                        if(MySharedPreferences.getAOld(context) == 0){
                            MySharedPreferences.saveAOld(context,0);
                        }
                        TabItem tempt = new TabItem(id, webView.getTitle(), webView.getUrl(), new ArrayList<String>(), bitmap);
                        if(homePage.getVisibility() == View.VISIBLE)
                            tempt.setTitle("");


                        if (CacheTab.AlruCache == null) {
                            CacheTab.AlruCache = new LruCache<>(20);
                        }
                        Bundle aa = new Bundle();
                        webView.saveState(aa);

                            CacheTab.AlruCache.put(id, aa);
                        Anony.a.add(tempt);
                        startActivityForResult(intent, 2);
                    }
                }

            }


        });


        webView = findViewById(R.id.webView);


        urlInput = findViewById(R.id.urlInput);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true  );
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


        webView.setWebViewClient(new MyWebViewClient(db));
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    downloadFile(url, contentDisposition, mimetype);
                }


            }
        });




        testWeb = new WebView(context);

        testWeb.setWebViewClient(new MyWebViewClient(db));
        testWeb.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });


        AppBarLayout appBar = findViewById(R.id.appbar);


        ImageView iconSearch = findViewById(R.id.google);
        switch (searchProvider) {
            case "yahoo":
                iconSearch.setBackground(getDrawable(R.drawable.yahoo));

                break;
            case "bing":
                iconSearch.setBackground(getDrawable(R.drawable.bing));

                break;
            case "coccoc":
                iconSearch.setBackground(getDrawable(R.drawable.coccoc));

                break;
            case "duckduckgo":
                iconSearch.setBackground(getDrawable(R.drawable.duckduckgo));

                break;
            case "google":
            default:


                break;
        }
        appBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePage.setVisibility(View.INVISIBLE);
                webView.setVisibility(webView.INVISIBLE);
                urlInput.setText("");
                urlInput.requestFocus();

            }
        });


        //beginList.add(new BeginItem(testWeb.getUrl(),testWeb.getTitle()));
        //db6.setAllLogs(beginList);
        BeginDataHelper db6 = new BeginDataHelper(context);
        List<BeginItem> beginList = db6.getAllLogs();
        BeginAdapter beginAdapter = new BeginAdapter(context,beginList);
        RecyclerView beginRecycle = findViewById(R.id.beginRecycle);
        beginRecycle.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        beginRecycle.setAdapter(beginAdapter);



        dialogB  = new Dialog(this);
        dialogB.setContentView(R.layout.add_begin_dialog);
        dialogB.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogB.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_2));
        Button cancleBtn2 = dialogB.findViewById(R.id.cancelBtn);
        Button okBtn2 = dialogB.findViewById(R.id.okBtn);
        EditText urlInput2 = dialogB.findViewById(R.id.urlInput);

        okBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogB.cancel();

                String url = urlInput2.getText().toString();
                boolean matchUrl = Patterns.WEB_URL.matcher(url).matches();
                if(matchUrl){
                    if(url.contains("https")){
                        testWeb.loadUrl(url);
                    }
                    else
                        testWeb.loadUrl("https://www."+url);
                }else{

                    testWeb.loadUrl(search2+url);


                }

                flag = true;

            }
        });

        cancleBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogB.cancel();



            }
        });




        if(Anony.isAnony == true){

            if (Anony.a != null  && MySharedPreferences.getAOld(context) != -1) {

                for (int i = 0; i < Anony.a.size(); i++) {
                    if (Anony.a.get(i).getId() == MySharedPreferences.getAOld(context)) {
                        if (Anony.a.get(i).getTitle().equals("Thẻ mới")) {
                            homePage.setVisibility(View.VISIBLE);
                            backk =true;
                            webView.setVisibility(webView.INVISIBLE);
                            ImageView mainImg = homePage.findViewById(R.id.imageView2);
                            mainImg.setImageDrawable(context.getResources().getDrawable(R.drawable.incognito));
                            homePage.setBackgroundColor(Color.DKGRAY);
                            beginRecycle.setVisibility(View.INVISIBLE);
                            appBar.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));

                        } else {

                            if (CacheTab.AlruCache.get(Anony.a.get(i).getId()) != null) {
                                webView.restoreState(CacheTab.AlruCache.get(Anony.a.get(i).getId()));
                            } else
                                loadMyUrl(Anony.a.get(i).getUrl());
                        }
                    }
                }


            } else {
                homePage.setVisibility(View.VISIBLE);
                backk =true;
                webView.setVisibility(webView.INVISIBLE);
                beginRecycle.setVisibility(View.INVISIBLE);
                appBar.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
                ImageView mainImg = homePage.findViewById(R.id.imageView2);
                mainImg.setImageDrawable(context.getResources().getDrawable(R.drawable.incognito));

                homePage.setBackgroundColor(Color.DKGRAY);
            }

        }
        else {


            if (getIntent().hasExtra("item_id")) {
                loadMyUrl(String.valueOf(getIntent().getStringExtra("item_id")));
                getIntent().removeExtra("item_id");
            } else {

                TabItemDbHelper db222 = new TabItemDbHelper(this);
                List<TabItem> a = db222.getAllTabItems();


                if (a.size() != 0 ) {


                    for (int i = 0; i < a.size(); i++) {
                        if (a.get(i).getId() == MySharedPreferences.getOld(context)) {
                            if (a.get(i).getTitle().equals("Thẻ mới")) {
                                homePage.setVisibility(View.VISIBLE);
                                backk =true;
                                webView.setVisibility(webView.INVISIBLE);
                            } else {

                                if(CacheTab.lruCache.get(a.get(i).getId()) != null){
                                    webView.restoreState(CacheTab.lruCache.get(a.get(i).getId()));
                                }
                                else
                                loadMyUrl(a.get(i).getUrl());
                            }
                        }

                    }
                } else {
                    homePage.setVisibility(View.VISIBLE);
                    backk =true;
                    webView.setVisibility(webView.INVISIBLE);
                }

            }
        }


        urlInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(urlInput.getWindowToken(),0);
                    loadMyUrl(urlInput.getText().toString());
                    return true;
                }
                return false;
            }
        });



        urlInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if(Anony.isAnony == false) {
                        urlInput.selectAll();
                        RecyclerView rc = findViewById(R.id.searchRecycle);
                        rc.setVisibility(View.VISIBLE);
                    }
                    else{
                        LinearLayout linearLayout = findViewById(R.id.linearLayout);
                        if(linearLayout.getVisibility() == View.VISIBLE) {
                            RecyclerView rc = findViewById(R.id.searchRecycle);
                            rc.setAdapter(new SearchAdapter(context, new ArrayList<SearchItem>()));
                            rc.setVisibility(View.VISIBLE);
                        }
                    }

                } else {

                    RecyclerView rc = findViewById(R.id.searchRecycle);
                    rc.setVisibility(View.INVISIBLE);
                }
            }
        });
        movableButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressStartTime = System.currentTimeMillis();
                        offsetX = motionEvent.getRawX() - view.getX();
                        offsetY = motionEvent.getRawY() - view.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float newX = motionEvent.getRawX() - offsetX;
                        float newY = motionEvent.getRawY() - offsetY;

                        // Lấy kích thước của màn hình


                        // Kiểm tra xem nút có rời khỏi biên màn hình không và điều chỉnh vị trí nếu cần
                        if (newX >= 0 && newX <= ((View)view.getParent()).getWidth() - view.getWidth() &&
                                newY >= 0 && newY <= ((View)view.getParent()).getHeight() - view.getHeight()) {
                            view.animate()
                                    .x(newX)
                                    .y(newY)
                                    .setDuration(0)
                                    .start();

                            // Kiểm tra xem nút gần biên trái hay phải của màn hình hơn và điều chỉnh vị trí


                        }


                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - pressStartTime < CLICK_ACTION_THRESHOLD) {

                            movableButton.setVisibility(View.INVISIBLE);
                            BookmarkDatabaseHelper dbHelper = new BookmarkDatabaseHelper(context);
                            List<BookmarkItem> a = dbHelper.getAllBookmarks();
                            for(int i = 0;i<a.size();i++){
                                if(a.get(i).getUrl().equals( webView.getUrl())){
                                    ImageButton bookmarkBtn = dialog.findViewById(R.id.bookmarkBtn);
                                    bookmarkBtn.setImageTintList(getResources().getColorStateList(android.R.color.holo_blue_dark));
                                }
                            }
                            dialog.show();
                        }
                        break;
                }
                if (view.getX() < ((View)view.getParent()).getWidth() / 2) {
                    view.setX(0); // Đặt nút gần biên trái
                } else {
                    view.setX(((View)view.getParent()).getWidth() - view.getWidth()); // Đặt nút gần biên phải
                }
                return true;
            }
        });

        mainLayout = findViewById(R.id.mainLayout);




        if (isNightMode == 1){

            changeColors(mainLayout);
            movableButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            movableButton.setImageTintList(getResources().getColorStateList(android.R.color.white));
            history.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            history.setImageTintList(getResources().getColorStateList(android.R.color.white));

            forwardBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            forwardBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
            settingBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            settingBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
            homeBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            homeBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
            tabBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            tabBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
            bookmarkBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            bookmarkBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));
            anonyBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            anonyBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));

            searchBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            searchBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));

            qrBtn.setBackgroundTintList(getResources().getColorStateList(android.R.color.black));
            qrBtn.setImageTintList(getResources().getColorStateList(android.R.color.white));

            ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_blue));
            homePage.setBackgroundTintList(colorStateList);

            ColorStateList colorStateList2 = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_blue_lighter));
            appBar.setBackgroundTintList(colorStateList2);

            beginRecycle.setBackgroundTintList(colorStateList2);
            rc.setBackgroundTintList(colorStateList2);



        }
        if(Anony.isAnony== true){
LinearLayout linearLayout = findViewById(R.id.linearLayout);
            linearLayout.setBackgroundColor(Color.DKGRAY);
            anonyBtn.setImageTintList(getResources().getColorStateList(android.R.color.holo_blue_dark));
        }



        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    // Cuộn xuống, ẩn thanh URL

                    if(linearLayout.getVisibility() != View.INVISIBLE  ) {

                        TranslateAnimation slideDown = new TranslateAnimation(0, 0,0 , -linearLayout.getHeight());
                        slideDown.setDuration(500); // Đặt thời gian hoàn thành là 500ms
                        linearLayout.startAnimation(slideDown);
                        linearLayout.setVisibility(View.INVISIBLE);



                    }
                    if( movableButton.getVisibility()!= View.INVISIBLE){
                        if(movableButton.getX() == 0){
                            TranslateAnimation slideDown2 = new TranslateAnimation(0, -movableButton.getWidth(),0 , 0);
                            slideDown2.setDuration(500); // Đặt thời gian hoàn thành là 500ms
                            movableButton.startAnimation(slideDown2);
                            movableButton.setVisibility(View.INVISIBLE);
                        }
                        else{
                            TranslateAnimation slideDown2 = new TranslateAnimation(0, movableButton.getWidth(),0 , 0);
                            slideDown2.setDuration(500); // Đặt thời gian hoàn thành là 500ms
                            movableButton.startAnimation(slideDown2);
                            movableButton.setVisibility(View.INVISIBLE);
                        }
                    }

                } else if (scrollY < oldScrollY) {

                    if(linearLayout.getVisibility() != View.VISIBLE ) {
                        TranslateAnimation slideUp = new TranslateAnimation(0, 0, -linearLayout.getHeight(), 0);
                        slideUp.setDuration(500); // Đặt thời gian hoàn thành là 500ms
                        linearLayout.startAnimation(slideUp);
                        linearLayout.setVisibility(View.VISIBLE);

                    }

                    if( movableButton.getVisibility() != View.VISIBLE){
                        if(movableButton.getX() == 0){
                            TranslateAnimation slideDown2 = new TranslateAnimation(- movableButton.getWidth(),0 ,0 , 0);
                            slideDown2.setDuration(500); // Đặt thời gian hoàn thành là 500ms
                            movableButton.startAnimation(slideDown2);
                            movableButton.setVisibility(View.VISIBLE);
                        }
                        else{
                            TranslateAnimation slideDown2 = new TranslateAnimation(movableButton.getWidth(),0,0 , 0);
                            slideDown2.setDuration(500); // Đặt thời gian hoàn thành là 500ms
                            movableButton.startAnimation(slideDown2);
                            movableButton.setVisibility(View.VISIBLE);
                        }
                    }

                    if(scrollY == 0){
                        TranslateAnimation slideDown = new TranslateAnimation(0, 0,0 , -linearLayout.getHeight());
                        slideDown.setDuration(500); // Đặt thời gian hoàn thành là 500ms
                        linearLayout.startAnimation(slideDown);
                        linearLayout.setVisibility(View.INVISIBLE);


                    }
                }

            }
        });
        // Lấy chuỗi từ SharedPreferences
        if(homePage.getVisibility() == View.VISIBLE){
            backk = true;

        }
        else{
            backk = false;

        }

    }


    private void downloadFile(String url, String contentDisposition, String mimetype) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType(mimetype);
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url));
        request.addRequestHeader("User-Agent", webView.getSettings().getUserAgentString());
        request.setDescription("Downloading file...");
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        // Register a BroadcastReceiver to listen for the download completion event
        registerReceiver(new DownloadBroadcastReceiver(downloadId, downloadManager), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    @SuppressLint("SuspiciousIndentation")
    public void loadMyUrl(String url) {

        boolean matchUrl = Patterns.WEB_URL.matcher(url).matches();
        if(matchUrl){
            if(url.contains("https")){
                webView.loadUrl(url);
            }
            else
            webView.loadUrl("https://www."+url);
        }else{

            webView.loadUrl(search2+url);
            SearchDatabaseHelper dp = new SearchDatabaseHelper(context);
            List<SearchItem> a = dp.getAllSearches();

            for(int i = 0;i<a.size();i++){
                if(a.get(i).getKeyword().equals(url)) return;
            }
            a.add(0,new SearchItem((url)));
            dp.setAllSearches(a);
            SearchDatabaseHelper dp5 =new SearchDatabaseHelper(this);
            List<SearchItem> searchList = dp5.getAllSearches();
            searchAdapter = new SearchAdapter(this,searchList);

            RecyclerView rc = findViewById(R.id.searchRecycle);
            rc.setLayoutManager(new LinearLayoutManager(this));
            rc.setAdapter(searchAdapter);

        }
    }






    @Override
    public void onBackPressed() {
        if(backk == true){
            homePage.setVisibility(View.VISIBLE) ;

            webView.setVisibility(View.INVISIBLE);


        }
        else if(homePage.getVisibility() == View.VISIBLE){

        }
        else if(urlInput.hasFocus() ){

            urlInput.clearFocus();
        }
        else if(webView.canGoBack()){
            webView.goBack();


        }else{
            super.onBackPressed();
        }
    }
    void saveState(){
        if (Anony.isAnony == false) {




            int width = (int) EasyViewUtils.dp2px(context, 90);
            int height = (int) EasyViewUtils.dp2px(context, 160);
            Bitmap bitmap = null;
            Canvas canvas = null;
            if(homePage.getVisibility() == View.INVISIBLE) {
                if (width > 0 && height > 0) {
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    canvas = new Canvas(bitmap);

                    int left = webView.getScrollX();
                    int top = webView.getScrollY();
                    canvas.translate(-left, -top);

                    float scaleX = (float) width / webView.getWidth();
                    float scaleY = (float) height / webView.getHeight();
                    canvas.scale(scaleX, scaleY, left, top);

                    webView.draw(canvas);

                    canvas.setBitmap(null);
                }
            }
            else{
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                canvas = new Canvas(bitmap);

                int left = webView.getScrollX();
                int top = webView.getScrollY();
                canvas.translate(-left, -top);

                float scaleX = (float) width / webView.getWidth();
                float scaleY = (float) height / webView.getHeight();
                canvas.scale(scaleX, scaleY, left, top);

                homePage.draw(canvas);

                canvas.setBitmap(null);
            }
            List<String> stringList = new ArrayList<>();
            stringList.add("");
            stringList.add("");
            TabItemDbHelper db2 = new TabItemDbHelper(context);
            List<TabItem> a = db2.getAllTabItems();
            int con = 0;
            for (int i = 0; i < a.size(); i++) {
                if (a.get(i).getId() == MySharedPreferences.getOld(context)) {
                    a.get(i).setBitmap(bitmap);

                    if(homePage.getVisibility() == View.INVISIBLE)
                        a.get(i).setTitle(webView.getTitle());
                    else{
                        a.get(i).setTitle("Thẻ mới");
                    }
                    a.get(i).setUrl(webView.getUrl());
                    db2.setAllTabItems(a);

                    if (CacheTab.lruCache.get(a.get(i).getId()) != null) {
                        CacheTab.lruCache.remove(a.get(i).getId());
                        Bundle aa = new Bundle();
                        webView.saveState(aa);
                        CacheTab.lruCache.put(a.get(i).getId(), aa);
                    } else {
                        Bundle aa = new Bundle();
                        webView.saveState(aa);
                        CacheTab.lruCache.put(a.get(i).getId(), aa);

                    }
                    con = 1;


                }
            }


            if (con == 0) {
                int id = 0;


                TabItem tempt = new TabItem(id, webView.getTitle(), webView.getUrl(), new ArrayList<String>(), bitmap);
                if(homePage.getVisibility() == View.VISIBLE)
                    tempt.setTitle("");
                if (CacheTab.lruCache == null) {
                    CacheTab.lruCache = new LruCache<>(20);
                }
                Bundle aa = new Bundle();
                webView.saveState(aa);
                if (MySharedPreferences.getOld(context) != -1)
                    CacheTab.lruCache.put(id, aa);
                db2.insertTabItem(tempt);

            }
        }
        else{

            int width = (int) EasyViewUtils.dp2px(context, 90);
            int height = (int) EasyViewUtils.dp2px(context, 160);
            Bitmap bitmap = null;
            Canvas canvas = null;
            if(homePage.getVisibility() == View.INVISIBLE) {
                if (width > 0 && height > 0) {
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    canvas = new Canvas(bitmap);

                    int left = webView.getScrollX();
                    int top = webView.getScrollY();
                    canvas.translate(-left, -top);

                    float scaleX = (float) width / webView.getWidth();
                    float scaleY = (float) height / webView.getHeight();
                    canvas.scale(scaleX, scaleY, left, top);

                    webView.draw(canvas);

                    canvas.setBitmap(null);
                }
            }
            else{
                if (width > 0 && height > 0) {
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    canvas = new Canvas(bitmap);

                    int left = webView.getScrollX();
                    int top = webView.getScrollY();
                    canvas.translate(-left, -top);

                    float scaleX = (float) width / webView.getWidth();
                    float scaleY = (float) height / webView.getHeight();
                    canvas.scale(scaleX, scaleY, left, top);

                    homePage.draw(canvas);

                    canvas.setBitmap(null);
                }
            }
            List<String> stringList = new ArrayList<>();
            stringList.add("");
            stringList.add("");


            if(Anony.a == null) Anony.a= new ArrayList<>();
            int con = 0;
            for (int i = 0; i < Anony.a.size(); i++) {
                if (Anony.a.get(i).getId() == MySharedPreferences.getAOld(context)) {
                    Anony.a.get(i).setBitmap(bitmap);
                    if(homePage.getVisibility() == View.INVISIBLE)
                        Anony.a.get(i).setTitle(webView.getTitle());
                    else{
                        Anony.a.get(i).setTitle("Thẻ mới");
                    }
                    Anony.a.get(i).setUrl(webView.getUrl());


                    if (CacheTab.AlruCache.get(Anony.a.get(i).getId()) != null) {
                        CacheTab.AlruCache.remove(Anony.a.get(i).getId());
                        Bundle aa = new Bundle();
                        webView.saveState(aa);
                        CacheTab.AlruCache.put(Anony.a.get(i).getId(), aa);
                    } else {
                        Bundle aa = new Bundle();
                        webView.saveState(aa);
                        CacheTab.AlruCache.put(Anony.a.get(i).getId(), aa);

                    }
                    con = 1;


                }
            }


            if (con == 0) {
                int id = 0;
                if(MySharedPreferences.getAOld(context) == 0){
                    MySharedPreferences.saveAOld(context,0);
                }
                TabItem tempt = new TabItem(id, webView.getTitle(), webView.getUrl(), new ArrayList<String>(), bitmap);
                if(homePage.getVisibility() == View.VISIBLE)
                    tempt.setTitle("");


                if (CacheTab.AlruCache == null) {
                    CacheTab.AlruCache = new LruCache<>(20);
                }
                Bundle aa = new Bundle();
                webView.saveState(aa);

                CacheTab.AlruCache.put(id, aa);
                Anony.a.add(tempt);

            }
        }
    }
    /*
    public void changeLog(){
        Intent intent = new Intent(this,LogActivity.class);
        startActivity(intent);
    }

     */

    private class DownloadBroadcastReceiver extends BroadcastReceiver {
        private long downloadId;
        private DownloadManager downloadManager;

        public DownloadBroadcastReceiver(long downloadId, DownloadManager downloadManager) {
            this.downloadId = downloadId;
            this.downloadManager = downloadManager;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == downloadId) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(query);

                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") String fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    @SuppressLint("Range") long fileSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    @SuppressLint("Range") String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    String fileDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                    DownloadItem downloadItem = new DownloadItem(fileName, fileSize, fileDate, fileUri);
                    downloadDao.insertDownloadItem(downloadItem);

                    Toast.makeText(context, "Download Completed: " + fileName, Toast.LENGTH_LONG).show();
                }
                cursor.close();
            }
        }
    }
    class MyWebViewClient extends WebViewClient {
        private SQLiteDatabase database;

        public MyWebViewClient(SQLiteDatabase db) {
            this.database = db;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null) {
                        startActivity(intent);
                        return true; // Đã xử lý URL, không cần WebView xử lý nữa
                    }
                } catch (URISyntaxException | ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return false; // Để WebView xử lý URL bình thường
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            urlInput.setText(webView.getUrl());


        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            urlInput.setText(webView.getUrl());
            if(rc.getVisibility() == View.VISIBLE){
                rc.setVisibility(View.INVISIBLE);
            }
            if(webView.getVisibility() == View.INVISIBLE){
                webView.setVisibility(View.VISIBLE);
            }
            if(flag == true){

                BeginDataHelper db6 = new BeginDataHelper(context);


                List<BeginItem> beginList = db6.getAllLogs();
                for(int i = 0;i<beginList.size();i++){
                    if(beginList.get(i).getUrl().equals(testWeb.getUrl())) return ;
                }
                beginList.add(new BeginItem(testWeb.getUrl(),testWeb.getTitle()));
                db6.setAllLogs(beginList);
                BeginAdapter beginAdapter = new BeginAdapter(context,beginList);
                RecyclerView beginRecycle = findViewById(R.id.beginRecycle);
                beginRecycle.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
                beginRecycle.setAdapter(beginAdapter);




                flag = false;
                return;
            }
            if(Anony.isAnony == false) {
                if (!Objects.equals(oldUrl, url)) {
                    ContentValues values = new ContentValues();
                    values.put("url", url);

                    values.put("event", webView.getTitle());
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    values.put("date", sdf.format(new Date()));


                    database.insert("browser_logs", null, values);
                    oldUrl = url;


                }
            }
            if(backk == true){
                countBack ++;
                if(countBack >= 2) {
                    backk = false;
                    countBack= 0;
                }
            }
        }




    }



}