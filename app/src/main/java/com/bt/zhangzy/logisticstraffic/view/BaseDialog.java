package com.bt.zhangzy.logisticstraffic.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;

/**
 * Created by ZhangZy on 2015/6/15.
 */
public class BaseDialog extends Dialog implements View.OnClickListener {

    public interface ConfirmListener{
        public void onClick(View view,boolean isConfirm);
    }

    private ConfirmListener listener;

    public void setListener(ConfirmListener listener){
        this.listener = listener;
    }

    public BaseDialog(Context context){
        super(context, R.style.myDialogTheme);
        setContentView(R.layout.base_dialog);
        Button btn = (Button) findViewById(R.id.dialog_btn_no);
        btn.setOnClickListener(this);
        findViewById(R.id.dialog_btn_yes).setOnClickListener(this);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static BaseDialog CreateDialog(Context context){
//        AlertDialog.Builder builder = new Builder(context);
//        builder.setTitle("");

        BaseDialog dialog = new BaseDialog(context);
//        dialog.setContentView(R.layout.base_dialog);

        return dialog;
    }

    public  void setPhoneNum(String num){
        TextView textView = (TextView) findViewById(R.id.dialog_phone_num);
        textView.setText(num);
    }

    @Override
    public void onClick(View v) {
        if(v != null){
            dismiss();
            if(listener != null){
                listener.onClick(v,v.getId() == R.id.dialog_btn_yes);
            }
        }
//            if(v.getId() == R.id.dialog_btn_yes){
//                dismiss();
//            }else if(v.getId() == R.id.dialog_btn_no){
//                dismiss();
//            }
    }
}
