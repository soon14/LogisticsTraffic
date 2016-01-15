package com.bt.zhangzy.logisticstraffic.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.User;

/**
 * Created by ZhangZy on 2016-1-15.
 */
public class WebViewActivity extends BaseActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);
        setPageName("web页");
        Location location = User.getInstance().getLocation();
        if (location == null) {
            return;
        }
//            String url = "http://192.168.1.115:8080/mall/qiantai/ditu.html?a=" + location.getLangitude() + "&b=" + location.getLatitude();
//
//            Uri uri = Uri.parse(url);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        //启用数据库 搜索
        webView.getSettings().setDatabaseEnabled(true);
        //启用地理定位
        webView.getSettings().setGeolocationEnabled(true);
        //设置定位的数据库路径
//        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
//        webSettings.setGeolocationDatabasePath(dir);
        //最重要的方法，一定要设置，这就是出不来的主要原因
        webView.getSettings().setDomStorageEnabled(true);

        //http://192.168.1.115:8080/mall/qiantai/ditu.html?a=116.39&b=39.116
        String url = "http://192.168.1.115:8080/mall/qiantai/ditu.html?longitude=" + location.getLangitude() + "&latitude=" + location.getLatitude();
//        url = "http://map.baidu.com/";
        webView.loadUrl(url);
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
}
