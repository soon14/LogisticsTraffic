package com.bt.zhangzy.logisticstraffic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.bt.zhangzy.jpush.ExampleUtil;


/**
 * Created by ZhangZy on 2016-1-23.
 */
public class MessageReceiver extends BroadcastReceiver {
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private static final String TAG = MessageReceiver.class.getSimpleName();
    private static MessageReceiver mMessageReceiver;
    public static boolean isForeground = false;

    public static void registerMessageReceiver(Context ctx) {
        if (mMessageReceiver == null)
            mMessageReceiver = new MessageReceiver();

        isForeground = true;
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        ctx.registerReceiver(mMessageReceiver, filter);
    }

    public static void unregisterReceiver(Context ctx) {
        if (mMessageReceiver != null)
            ctx.unregisterReceiver(mMessageReceiver);

        isForeground = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
            Log.d(TAG, "[MessageReceiver]收到Receive:" + intent.toString());
            String messge = intent.getStringExtra(KEY_MESSAGE);
            String extras = intent.getStringExtra(KEY_EXTRAS);
            StringBuilder showMsg = new StringBuilder();
            showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
            if (!ExampleUtil.isEmpty(extras)) {
                showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
            }
            Toast.makeText(context, showMsg.toString(), Toast.LENGTH_LONG);
        }
    }
}
