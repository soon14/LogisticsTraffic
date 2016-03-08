package com.bt.zhangzy.logisticstraffic.view;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.tools.ViewUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by ZhangZy on 2016-2-24.
 */
public class ChooseItemsDialog extends BaseDialog {
    public interface SelectListener {
        void onClickFinish(String[] select_str);
    }

    private GridLayout itemsLy;
    private boolean isSelectModel;//选择模式
    private SelectListener listener;
    private ArrayList<String> selectArray = new ArrayList<>();

    public ChooseItemsDialog(Activity context) {
        super(context);
        setView(R.layout.dialog_choose_items);
        itemsLy = (GridLayout) findViewById(R.id.dialog_choose_item_ly);
        findViewById(R.id.dialog_choose_select_finish_bt).setVisibility(View.GONE);
    }

    public ChooseItemsDialog setTitle(String title) {
        setTextView(R.id.dialog_choose_title, title);
        return this;
    }

    public ChooseItemsDialog addItem(String title, View.OnClickListener listener) {
        if (itemsLy == null || TextUtils.isEmpty(title))
            return this;
        Button item = addButton(title);
        setOnClickObj(item, listener);
        return this;
    }

    @NonNull
    private Button addButton(String title) {
        Button item = (Button) getLayoutInflater().inflate(R.layout.choose_item_button, null);
        item.setText(title);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        params.setGravity(Gravity.CENTER);
        params.setMargins(10, 0, 10, 0);
        item.setPadding(10, 10, 10, 10);
//        params.columnSpec.
        itemsLy.addView(item, params);
        return item;
    }

    public ChooseItemsDialog addItems(View.OnClickListener listener, String... items) {
        if (items != null) {
            for (String item : items) {
                addItem(item, listener);
            }
        }
        return this;
    }

    /**
     * 设置 选择模式
     *
     * @param items
     * @param listener
     * @return
     */
    public ChooseItemsDialog setItems(String[] items, SelectListener listener) {
        isSelectModel = true;
        this.listener = listener;
        if (items != null) {
            Button button;
            for (String item : items) {
                button = addButton(item);
                button.setOnClickListener(this);
            }
        }
        View finish_btn = findViewById(R.id.dialog_choose_select_finish_bt);
        finish_btn.setVisibility(View.VISIBLE);
        finish_btn.setOnClickListener(this);
        return this;
    }

    @Override
    public void onClick(View v) {
        if (isSelectModel) {
            if (v != null) {
                if (v.getId() == R.id.dialog_choose_select_finish_bt) {
                    dismiss();
                    if (listener != null) {
                        listener.onClickFinish(selectArray.toArray(new String[selectArray.size()]));
                    }
                } else {
                    v.setSelected(!v.isSelected());
                    if (v instanceof TextView) {
                        String string = ViewUtils.getStringFromTextView((TextView) v);
                        if (!TextUtils.isEmpty(string)) {
                            if (v.isSelected()) {
                                selectArray.add(string);
                            } else {
                                selectArray.remove(string);
                            }
                        }

                    }
                }
            }
        } else
            super.onClick(v);
    }

    /**
     * 项目选择dialog封装
     *
     * @param act      上下文
     * @param title    标题
     * @param listener 监听事件
     * @param items    选择的项目 最多16项
     */
    public static void showChooseItemsDialog(Activity act, String title, View.OnClickListener listener, String... items) {
        new ChooseItemsDialog(act)
                .setTitle(title)
                .addItems(listener, items)
                .show();

       /*
        BaseDialog dialog = new BaseDialog(act);
        dialog.setView(R.layout.dialog_choose_items);
        dialog.setTextView(R.id.dialog_choose_title, title);
//        GridLayout layout = (GridLayout) dialog.findViewById(R.id.dialog_choose_item_ly);
        final int[] ids = {R.id.dialog_choose_item_1, R.id.dialog_choose_item_2, R.id.dialog_choose_item_3, R.id.dialog_choose_item_4, R.id.dialog_choose_item_5,
                R.id.dialog_choose_item_6, R.id.dialog_choose_item_7, R.id.dialog_choose_item_8, R.id.dialog_choose_item_9, R.id.dialog_choose_item_10,
                R.id.dialog_choose_item_11, R.id.dialog_choose_item_12, R.id.dialog_choose_item_13, R.id.dialog_choose_item_14, R.id.dialog_choose_item_15,
                R.id.dialog_choose_item_16};
        for (int k = 0, id = -1; k < ids.length; k++) {
            id = ids[k];
            if (k < items.length) {
                dialog.setOnClickListener(id, listener);
                dialog.setTextView(id, items[k]);
            } else {
                dialog.findViewById(id).setVisibility(View.GONE);
            }
        }

        dialog.show();
        */
    }
}
