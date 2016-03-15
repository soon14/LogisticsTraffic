package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;

/**
 * Created by ZhangZy on 2016-2-2.
 */
public class InputDialog extends BaseDialog {
    TextView suffixTx;
    EditText inputEd;
    Button confirmBt, cancelBt;
    Callback callback;

    public interface Callback {
        void inputCallback(String string);
    }

    public InputDialog(Activity context) {
        super(context);
        setView(R.layout.order_dialog_input_confirm);
        inputEd = (EditText) findViewById(R.id.dl_input_ed);
        suffixTx = (TextView) findViewById(R.id.dl_suffix_ex);
        suffixTx.setVisibility(View.GONE);
        confirmBt = (Button) findViewById(R.id.dl_confirm_bt);
        cancelBt = (Button) findViewById(R.id.dl_cancel_bt);
        cancelBt.setOnClickListener(this);
        confirmBt.setOnClickListener(this);

        //弹出软键盘
        inputEd.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //软键盘按键 监听
        inputEd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    if (!TextUtils.isEmpty(inputEd.getText())) {
                        String string = inputEd.getText().toString();
                        if (callback != null) {
                            callback.inputCallback(string);
                        }
                    }
                    cancel();

                }
                return false;
            }
        });
    }

    public InputDialog setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == confirmBt) {
            if (TextUtils.isEmpty(inputEd.getText())) {
                return;
            }
            String string = inputEd.getText().toString();
            if (callback != null) {
                callback.inputCallback(string);
            }
        }
    }

    //    protected InputDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
//        super(context, cancelable, cancelListener);
//    }

    /**
     * 设置后缀名
     *
     * @param suffix
     */
    public InputDialog setSuffixString(String suffix) {
        if (TextUtils.isEmpty(suffix))
            return this;

        suffixTx.setText(suffix);
        suffixTx.setVisibility(View.VISIBLE);
        return this;
    }

    public InputDialog setEditHintString(String hintString) {
        if (TextUtils.isEmpty(hintString)) {
            return this;
        }

        inputEd.setHint(hintString);

        return this;
    }

    public InputDialog setInputType(int type) {
        inputEd.setInputType(type);
        return this;
    }

    public void setConfirm(String name) {

    }


}
