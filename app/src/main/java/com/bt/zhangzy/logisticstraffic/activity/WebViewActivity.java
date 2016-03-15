package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;

/**
 * Created by ZhangZy on 2016-1-15.
 */
public class WebViewActivity extends BaseActivity {

    WebView webView;
    String url;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setPageName(bundle.getString(AppParams.WEB_PAGE_NAME));

            if (bundle.containsKey(AppParams.WEB_PAGE_URL))
                url = bundle.getString(AppParams.WEB_PAGE_URL);
            if (bundle.containsKey(AppParams.WEB_PAGE_HTML_DATA))
                data = bundle.getString(AppParams.WEB_PAGE_HTML_DATA);
        }
//        Location location = User.getInstance().getLocation();
//        if (location == null) {
//            return;
//        }
//            String url = "http://192.168.1.115:8080/mall/qiantai/ditu.html?a=" + location.getLongitude() + "&b=" + location.getLatitude();
//
//            Uri uri = Uri.parse(url);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
        webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //启用数据库 搜索
        webSettings.setDatabaseEnabled(true);
        //启用地理定位
        webSettings.setGeolocationEnabled(true);
        //设置定位的数据库路径
//        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
//        webSettings.setGeolocationDatabasePath(dir);
        //最重要的方法，一定要设置，这就是出不来的主要原因
        webSettings.setDomStorageEnabled(true);
        //设置加载进来的页面自适应手机屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        //http://192.168.1.115:8080/mall/qiantai/ditu.html?a=116.39&b=39.116
//        String url = "http://192.168.1.115:8080/mall/qiantai/ditu.html?longitude=" + location.getLongitude() + "&latitude=" + location.getLatitude();
//        url = "http://map.baidu.com/";
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
            Log.d(TAG, "URL=" + url);
        } else if (!TextUtils.isEmpty(data)) {
            final String mimeType = "text/html";
            final String encoding = "utf-8";
            webView.loadData(data, mimeType, encoding);
//            webView.addJavascriptInterface(new Object(){
//
//            },"one");
            Log.d(TAG, "DATA=" + data);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //get the newProgress and refresh progress bar
                Log.d(TAG, "progress=" + newProgress);
            }

            //配置权限（同样在WebChromeClient中实现）
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });
        webView.requestFocus();
    }

    @Override
    public void onClick_Back(View view) {
        super.onClick_Back(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
