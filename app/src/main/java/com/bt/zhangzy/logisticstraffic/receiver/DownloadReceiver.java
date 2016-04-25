package com.bt.zhangzy.logisticstraffic.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * 主要用于apk更新
 * Created by ZhangZy on 2016-4-22.
 */
public class DownloadReceiver extends BroadcastReceiver {
    private static final String TAG = DownloadReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
//            Toast.makeText(context, "下载完成！", Toast.LENGTH_LONG).show();
            Log.i(TAG, "下载完成！");

            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);//TO DO 判断这个id与之前的id是否相等，如果相等说明是之前的那个要下载的文件
            if (id == Long.parseLong(getApkId(context))) {//TO DO 判断这个id与之前的id是否相等，如果相等说明是之前的那个要下载的文件
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uriForDownloadedFile = downloadManager.getUriForDownloadedFile(id);
                Log.d(TAG, "download manager = " + uriForDownloadedFile);
                if (uriForDownloadedFile != null) {
                    String uriString = getFilePathFromUri(context, uriForDownloadedFile);
                    Log.d(TAG, "download uriString = " + uriString);
                    Uri uri = Uri.fromFile(new File(uriString));
                    installAPK(context, uri);
                    showToast(context, "开始安装");
                } else {
                    showToast(context, "下载失败-文件为空");
                }

//                ortherInstallAPK(context, id, downloadManager);
            }
        } else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
//            Toast.makeText(context, "点击通知了....", Toast.LENGTH_LONG).show();
            Log.i(TAG, "点击通知了....");

        }
    }

    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Log.w(TAG, msg);
    }

    private void ortherInstallAPK(Context context, long id, DownloadManager downloadManager) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        int columnCount = cursor.getColumnCount();
        String path = null;//TO DO 这里把所有的列都打印一下，有什么需求，就怎么处理,文件的本地路径就是path
        while (cursor.moveToNext()) {
            for (int j = 0; j < columnCount; j++) {
                String columnName = cursor.getColumnName(j);
                String string = cursor.getString(j);
                if (columnName.equals("local_uri")) {
                    path = string;
                }
                if (string != null) {
                    System.out.println(columnName + ": " + string);
                } else {
                    System.out.println(columnName + ": null");
                }
            }
        }
        cursor.close();

        //如果sdcard不可用时下载下来的文件，那么这里将是一个内容提供者的路径，这里打印出来，有什么需求就怎么样处理  if(path.startsWith("content:")) {
        if (path.startsWith("content:")) {
            System.out.println("-----------------------CompleteReceiver 下载完了----路径path = " + path.toString());
        }

        String uriString = getFilePathFromUri(context, Uri.parse(path));//TODO 转换path路径 否则报解析包错误
        System.out.println("-----------------------CompleteReceiver 转换后----路径uriString = " + uriString);
        if (TextUtils.isEmpty(uriString)) {
            Toast.makeText(context, "下载失败-文件为空", Toast.LENGTH_LONG).show();
            return;
        }
        Uri uri = Uri.fromFile(new File(uriString));
        installAPK(context, uri);
        showToast(context, "开始安装");
    }

    private void installAPK(Context context, Uri uri) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    private String getApkId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("APK_ID", null);
    }

    /**
     * 转换 path路径
     */
    public static String getFilePathFromUri(Context c, Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            ContentResolver contentResolver = c.getContentResolver();

            Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                    null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if ("file".equals(uri.getScheme())) {
            filePath = new File(uri.getPath()).getAbsolutePath();
        }
        return filePath;
    }
}
