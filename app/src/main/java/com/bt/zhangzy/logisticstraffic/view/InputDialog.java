package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.d.R;

/**
 * 输入对话框
 * Created by ZhangZy on 2016-2-2.
 */
public class InputDialog extends BaseDialog implements TextView.OnEditorActionListener {
    TextView suffixTx;
    EditText inputEd;
    Button confirmBt, cancelBt;
    Callback callback;
    boolean isVolumeMode;
    EditText inputLengthEd, inputWidthEd, inputHeightEd;

    public interface Callback {
        void inputCallback(String string);
    }

    public InputDialog(Activity context) {
        this(context, false);
    }

    public InputDialog(Activity context, boolean isVolumeMode) {
        super(context);
        if (isVolumeMode) {
            setVolumeView();
        } else {
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

            //软键盘按键 监听
            inputEd.setOnEditorActionListener(this);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    /**
     * 体积输入页面
     *
     * @return
     */
    public InputDialog setVolumeView() {
        setView(R.layout.order_dialog_input_volume_confirm);
        confirmBt = (Button) findViewById(R.id.dl_confirm_bt);
        cancelBt = (Button) findViewById(R.id.dl_cancel_bt);
        cancelBt.setOnClickListener(this);
        confirmBt.setOnClickListener(this);

        isVolumeMode = true;

        inputLengthEd = (EditText) findViewById(R.id.dl_input_length_ed);
        inputWidthEd = (EditText) findViewById(R.id.dl_input_width_ed);
        inputHeightEd = (EditText) findViewById(R.id.dl_input_height_ed);

        inputLengthEd.requestFocus();

        inputLengthEd.setOnEditorActionListener(this);
        inputWidthEd.setOnEditorActionListener(this);
        inputHeightEd.setOnEditorActionListener(this);

        return this;
    }

    public InputDialog setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //当actionId == XX_SEND 或者 XX_DONE时都触发
        //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
        //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
            callBack();
            cancel();

        }
        return false;
    }

    private void callBack() {
        if (isVolumeMode) {
            if (TextUtils.isEmpty(inputLengthEd.getText()) || TextUtils.isEmpty(inputWidthEd.getText()) || TextUtils.isEmpty(inputHeightEd.getText())) {
                Toast.makeText(getContext(), "信息不完整", Toast.LENGTH_SHORT).show();
                return;
            }
            String string = inputLengthEd.getText() + "*" + inputWidthEd.getText() + "*" + inputHeightEd.getText() + "方";
            if (callback != null) {
                callback.inputCallback(string);
            }
        } else {
            //处理事件
            if (!TextUtils.isEmpty(inputEd.getText())) {
                String string = inputEd.getText().toString();
                if (callback != null) {
                    callback.inputCallback(string);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == confirmBt) {
            callBack();
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

    public InputDialog setInputLength(int length) {
        inputEd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        return this;
    }

    public void setConfirm(String name) {

    }


}
