package com.bt.zhangzy.logisticstraffic.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.zhangzy.logisticstraffic.R;

/**
 * 虚拟键盘
 * Created by ZhangZy on 2015/7/24.
 */
public class LicenceKeyboardPopupWindow extends PopupWindow implements View.OnClickListener {

    public interface ConfirmListener{
        void confirm(String string);
    }

    ConfirmListener listener;
    private View provinceBtn;
    private View letterBtn;
    private View numBtn;

    enum KeyboardStatus {
        Empty, Province, Letter, Num
    }

    KeyboardStatus currentStatus = KeyboardStatus.Empty;
    TextView showText;

    GridLayout provinceLy;
    GridLayout letterLy;
    GridLayout numLy;

    private LicenceKeyboardPopupWindow(View contentView) {
        super(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
    }

    public static LicenceKeyboardPopupWindow create(Context context) {
        //虚拟键盘
        View licenceView = LayoutInflater.from(context).inflate(R.layout.dialog_select_licence, null);
        LicenceKeyboardPopupWindow popup = new LicenceKeyboardPopupWindow(licenceView);
        popup.init(licenceView);
        return popup;
    }

    public LicenceKeyboardPopupWindow setListener(ConfirmListener ls){
        listener = ls;
        return this;
    }

    private void init(View view) {
        if (view == null)
            return;
        provinceBtn = view.findViewById(R.id.licence_province_btn);
        provinceBtn.setOnClickListener(this);
        letterBtn = view.findViewById(R.id.licence_letter_btn);
        letterBtn.setOnClickListener(this);
        numBtn = view.findViewById(R.id.licence_num_btn);
        numBtn.setOnClickListener(this);
        view.findViewById(R.id.licence_back_btn).setOnClickListener(this);
        view.findViewById(R.id.licence_confirm_btn).setOnClickListener(this);
        view.findViewById(R.id.licence_del_btn).setOnClickListener(this);
        showText = (TextView) view.findViewById(R.id.licence_show_tx);
        provinceLy = (GridLayout) view.findViewById(R.id.select_licence_province);
        letterLy = (GridLayout) view.findViewById(R.id.select_licence_letter);
        numLy = (GridLayout) view.findViewById(R.id.select_licence_num);
        setOnClickGridLayout(provinceLy);
        setOnClickGridLayout(letterLy);
        setOnClickGridLayout(numLy);
        showText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    setStatus(KeyboardStatus.Province);
                } else {
                    if (currentStatus == KeyboardStatus.Province) {
                        setStatus(KeyboardStatus.Letter);
                    }
                }
            }
        });
        //设置初始状态
        setStatus(KeyboardStatus.Province);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.licence_province_btn:
                setStatus(KeyboardStatus.Province);
                break;
            case R.id.licence_letter_btn:
                setStatus(KeyboardStatus.Letter);
                break;
            case R.id.licence_num_btn:
                setStatus(KeyboardStatus.Num);
                break;
            case R.id.licence_back_btn:
                dismiss();
                break;
            case R.id.licence_confirm_btn:
                if(showText.length()<8){
                    Toast.makeText(getContentView().getContext(),"填写不完整",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(listener != null){
                    listener.confirm(showText.getText().toString());
                }
                dismiss();
                break;
            case R.id.licence_del_btn:
                delText();
                break;
            default:
                if (v instanceof Button) {
                    addText(((Button) v).getText().toString());

                }
                break;
        }
    }

    private void addText(String string) {
        if (showText == null || TextUtils.isEmpty(string))
            return;
//        showText.setText(showText.getText() + string);
        showText.append(string);
        if (showText.length() == 2) {
            showText.append("·");
        }

    }

    private void delText() {
        if (showText == null || showText.length() == 0)
            return;
        showText.setText(showText.getText().subSequence(0, showText.length() - 1));
    }

    private void setStatus(KeyboardStatus status) {
        if (currentStatus == status)
            return;
        switch (status) {
            case Province:
                if (showText.getText().length() > 1)
                    return;
                setShowView(provinceLy);
                setSelectView(provinceBtn);
                break;
            case Letter:
                setShowView(letterLy);
                setSelectView(letterBtn);
                break;
            case Num:
                setShowView(numLy);
                setSelectView(numBtn);
                break;
        }
        currentStatus = status;
    }

    View lastShowView;

    private void setShowView(View view) {
        if (lastShowView == view)
            return;
        if (lastShowView != null) {
            lastShowView.setVisibility(View.GONE);
        }
        view.setVisibility(View.VISIBLE);
        lastShowView = view;
    }

    View lastSelectView;

    private void setSelectView(View view) {
        if (lastSelectView == view)
            return;
        if (lastSelectView != null) {
            lastSelectView.setSelected(false);
        }
        view.setSelected(true);
        lastSelectView = view;
    }

    private void setOnClickGridLayout(GridLayout gridLayout) {
        if (gridLayout == null)
            return;
        View v = null;
        for (int k = 0; k < gridLayout.getChildCount(); k++) {
            v = gridLayout.getChildAt(k);
            if (v != null/* && v instanceof Button*/) {
                v.setOnClickListener(this);
            }
        }
    }
}
