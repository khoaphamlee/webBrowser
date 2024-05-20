package com.example.sky;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sky.bookmark.BookmarkActivity;
import com.example.sky.bookmark.BookmarkDatabaseHelper;
import com.example.sky.bookmark.BookmarkItem;
import com.example.sky.setting.fourth;
import com.example.sky.tabpreview.CacheTab;
import com.example.sky.tabpreview.EasyViewUtils;
import com.example.sky.tabpreview.MySharedPreferences;
import com.example.sky.tabpreview.TabActivity;
import com.example.sky.tabpreview.TabItem;
import com.example.sky.tabpreview.TabItemDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.sky.download.DownloadDao;
import com.example.sky.download.DownloadItem;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;

    private static final int REQUEST_WRITE_STORAGE = 112;
    String oldUrl ;
    private float offsetX;
    private float offsetY;
    private static final int CLICK_ACTION_THRESHOLD = 200;
    private long pressStartTime;

    EditText urlInput;

    Dialog dialog;
    WebView webView;

    RelativeLayout mainLayout;

    SharedPreferences preferences;

    Bundle a;
    Context context = this;
    List<String> historyL;
    String search1,search2;

    private BookmarkDatabaseHelper dbHelper;
    private DownloadDao downloadDao;


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


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

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                if (data.hasExtra("item_id_tab")) {



                    int myIntData = data.getIntExtra("item_id_tab", 0);
                    if(myIntData == -1){

                        int id = 0;
                        loadMyUrl("");
                        TabItemDbHelper db2 = new TabItemDbHelper(this);
                        List<TabItem> a = db2.getAllTabItems();
                        if(a.size()!= 0){
                            id =  a.get(a.size()-1).getId()+1;
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
                        MySharedPreferences.saveOld(context,id);

                        TabItem tempt = new TabItem(id, webView.getTitle(), webView.getUrl(), stringList, bitmap);
                        historyL = new ArrayList<>();
                        db2.insertTabItem(tempt);

                    }
                    else {
                        MySharedPreferences.saveOld(context, myIntData);
                        ;
                        TabItemDbHelper db2 = new TabItemDbHelper(this);
                        List<TabItem> a = db2.getAllTabItems();

                        if (CacheTab.lruCache.get(myIntData) != null) {
                            webView.restoreState(CacheTab.lruCache.get(myIntData));
                            for (int i = 0; i < a.size(); i++) {
                                if (a.get(i).getId() == myIntData) {
                                    historyL = new ArrayList<>(a.get(i).getHistory());
                                    break;
                                }
                            }
                        }

                        else {
                            for (int i = 0; i < a.size(); i++) {
                                if (a.get(i).getId() == myIntData) {
                                    loadMyUrl(a.get(i).getUrl());
                                    historyL = new ArrayList<>(a.get(i).getHistory());
                                    break;
                                }
                            }
                        }
                    }


                }
                else
                    loadMyUrl(search1);

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
    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new BookmarkDatabaseHelper(this);
        downloadDao = new DownloadDao(this);

        SharedPreferences sharedPreferences_2 = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences_3 = getSharedPreferences("pref2", 0);
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

        if(historyL == null)
        historyL = new ArrayList<>();

        oldUrl = "";
        Context context = this;
        LogDatabaseHelper dbHelper = new LogDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        TabItemDbHelper tabdbHelper = new TabItemDbHelper(this);
        SQLiteDatabase db2 = dbHelper.getWritableDatabase();

        setContentView(R.layout.activity_main);

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
                bookmarkCurrentPage();
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                loadMyUrl(search1);

            }
        });

        ImageButton tabBtn = dialog.findViewById(R.id.tabBtn);
        tabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

                Intent intent = new Intent(context, TabActivity.class);
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
                TabItemDbHelper db2 = new TabItemDbHelper(context);
                List<TabItem> a = db2.getAllTabItems();
                int con = 0;
                for(int i =0;i<a.size();i++){
                    if(a.get(i).getId() == MySharedPreferences.getOld(context)){
                        a.get(i).setBitmap(bitmap);
                        a.get(i).setHistory(historyL);
                        a.get(i).setTitle(webView.getTitle());
                        a.get(i).setUrl(webView.getUrl());
                        tabdbHelper.setAllTabItems(a);

                        if(CacheTab.lruCache.get(a.get(i).getId()) != null){
                            CacheTab.lruCache.remove(a.get(i).getId());
                            Bundle aa = new Bundle() ;
                            webView.saveState(aa);
                            CacheTab.lruCache.put(a.get(i).getId(),aa);
                        }
                        else{
                            Bundle aa = new Bundle() ;
                            webView.saveState(aa);
                            CacheTab.lruCache.put(a.get(i).getId(),aa);

                        }
                        con = 1;
                        startActivityForResult(intent, 2);

                    }
                }





                if(con == 0) {
                    int id = 0;



                    TabItem tempt = new TabItem(id, webView.getTitle(), webView.getUrl(), historyL, bitmap);
                    if (CacheTab.lruCache == null) {
                        CacheTab.lruCache = new LruCache<>(20);
                    }
                    Bundle aa = new Bundle();
                    webView.saveState(aa);
                    if (MySharedPreferences.getOld(context) != -1)
                        CacheTab.lruCache.put(id, aa);
                    tabdbHelper.insertTabItem(tempt);
                    startActivityForResult(intent, 2);
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
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    downloadFile(url, contentDisposition, mimetype);
                }
            }
        });
        if (getIntent().hasExtra("item_id")) {
            loadMyUrl(String.valueOf(getIntent().getStringExtra("item_id")));
            getIntent().removeExtra("item_id");
        }
        else {
            TabItemDbHelper db222 = new TabItemDbHelper(this);
            List<TabItem> a = db222.getAllTabItems();
            if(a.size()!= 0 && MySharedPreferences.getOld(context)!= -1) {


                for(int i = 0;i<a.size();i++) {
                    if(a.get(i).getId() == MySharedPreferences.getOld(context))
                    historyL = new ArrayList<>(a.get(i).getHistory());
                    loadMyUrl(a.get(i).getUrl());
                }
            }
            else
            loadMyUrl(search1);
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
                            dialog.show();
                            movableButton.setVisibility(View.INVISIBLE);
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
                }
            }
        });
        // Lấy chuỗi từ SharedPreferences


    }



    @SuppressLint("SuspiciousIndentation")
    private void loadMyUrl(String url) {

        boolean matchUrl = Patterns.WEB_URL.matcher(url).matches();
        if(matchUrl){
            if(url.contains("https")){
                webView.loadUrl(url);
            }
            else
            webView.loadUrl("https://www."+url);
        }else{

            webView.loadUrl(search2+url);
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


    public void bookmarkCurrentPage() {
        String currentUrl = webView.getUrl();
        String currentTitle = webView.getTitle();

        if (currentUrl != null && !currentUrl.isEmpty() && currentTitle != null && !currentTitle.isEmpty()) {
            BookmarkItem bookmarkItem = new BookmarkItem(currentUrl, currentTitle);
            dbHelper.addBookmark(bookmarkItem);
            Toast.makeText(this, "Page bookmarked!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to bookmark page.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        if(!historyL.isEmpty()){
            int x = historyL.size();
            for(int i = x-1;i >= 0 ;i--){
                if(historyL.get(i).equals(webView.getUrl()))
                    historyL.remove(i);
                else break;
            }
            if(!historyL.isEmpty()) {
                loadMyUrl(historyL.get(historyL.size() - 1));
                historyL.remove(historyL.size() - 1);
            }


        }else{
            super.onBackPressed();
        }
    }
    /*
    public void changeLog(){
        Intent intent = new Intent(this,LogActivity.class);
        startActivity(intent);
    }

     */
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

            if(!Objects.equals(oldUrl, url)) {
                ContentValues values = new ContentValues();
                values.put("url", url);

                values.put("event", webView.getTitle());
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                values.put("date",sdf.format(new Date()));


                historyL.add(url);

                database.insert("browser_logs", null, values);
                oldUrl = url;
            }
        }


    }
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
                    String fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    long fileSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    String fileDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                    DownloadItem downloadItem = new DownloadItem(fileName, fileSize, fileDate, fileUri);
                    downloadDao.insertDownloadItem(downloadItem);

                    Toast.makeText(context, "Download Completed: " + fileName, Toast.LENGTH_LONG).show();
                }
                cursor.close();
            }
        }
    }



}