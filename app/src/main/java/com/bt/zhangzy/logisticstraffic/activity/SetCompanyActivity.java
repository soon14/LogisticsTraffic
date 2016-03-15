package com.bt.zhangzy.logisticstraffic.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.Location;
import com.bt.zhangzy.logisticstraffic.data.Type;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.logisticstraffic.view.ChooseItemsDialog;
import com.bt.zhangzy.logisticstraffic.view.LocationView;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.UploadImageHelper;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.tools.Tools;
import com.bt.zhangzy.tools.ViewUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ZhangZy on 2015/6/23.
 */
public class SetCompanyActivity extends BaseActivity {

    JsonCompany company;
    EditText introduceEd;
    final int[] oftenRouteIds = {R.id.set_company_often_route_1, R.id.set_company_often_route_2, R.id.set_company_often_route_3, R.id.set_company_often_route_4, R.id.set_company_often_route_5, R.id.set_company_often_route_6, R.id.set_company_often_route_7, R.id.set_company_often_route_8, R.id.set_company_often_route_9, R.id.set_company_often_route_10};
    boolean inEditMode = false;//编辑模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_company);

        setPageName("店铺设置");

        introduceEd = (EditText) findViewById(R.id.set_introduce_ed);
        introduceEd.setEnabled(false);
//        oftenRouteLy = (LinearLayout) findViewById(R.id.set_company_often_route_ly);
        findViewById(R.id.set_company_introduce_mode_bt).setVisibility(View.INVISIBLE);

        if (User.getInstance().getUserType() == Type.EnterpriseType) {
            findViewById(R.id.setting_lines_ly).setVisibility(View.GONE);
        }

        if (User.getInstance().getJsonTypeEntity() != null) {
            if (User.getInstance().getUserType() == Type.CompanyInformationType) {
                company = User.getInstance().getJsonTypeEntity();
            }
        }

        if (company != null) {
            introduceEd.setText(company.getScaleOfOperation());
            setTextView(R.id.set_often_send_type_ed, company.getOftenSendType());
            String often_route = company.getOftenRoute();
            if (!TextUtils.isEmpty(often_route)) {
                ArrayList<String> oftenRouteList = new ArrayList<>();
                String[] often_route_list = often_route.split(",");
                for (String string : often_route_list) {
                    oftenRouteList.add(string);
                }
                initOftenRoute(oftenRouteList);
            }
            setImageUrl(R.id.set_company_photo_1, company.getPhotoUrl());
            setImageUrl(R.id.set_company_photo_2, company.getPhotoUrl2());
            setImageUrl(R.id.set_company_photo_3, company.getPhotoUrl3());

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (UploadImageHelper.getInstance().onActivityResult(this, requestCode, resultCode, data)) {
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick_Photo(View view) {
        if (!inEditMode) {
            showToast("不是编辑模式");
            return;
        }
        UploadImageHelper.getInstance().onClick_Photo(this, view, new UploadImageHelper.Listener() {
            @Override
            public void handler(ImageView userImage, String uploadImgURL) {
                if (userImage != null)
                    switch (userImage.getId()) {
                        case R.id.set_company_photo_1:
                            company.setPhotoUrl(uploadImgURL);
                            break;
                        case R.id.set_company_photo_2:
                            company.setPhotoUrl2(uploadImgURL);
                            break;
                        case R.id.set_company_photo_3:
                            company.setPhotoUrl3(uploadImgURL);
                            break;

                    }
            }
        });


    }


    @Override
    protected void onPause() {
//        requestSetCompany();
        super.onPause();
    }

    //删除 常发线路
    public void onClick_ChangeLocation(final View view) {
        if (!inEditMode) {
            showToast("不是编辑模式");
            return;
        }
        LocationView.createDialog(this)
                .setTitle("请设置始发地")
                .setListener(new LocationView.ChangingListener() {
                    @Override
                    public void onChanged(Location loc) {
                    }

                    public void onCancel(Location loc) {

                        if (!ViewUtils.setText((TextView) view, loc.getCityName())) {
                            ((TextView) view).setText("");
                        }
                    }
                }).show();
    }

    private void initOftenRoute(ArrayList<String> oftenRouteList) {
        if (oftenRouteList.isEmpty())
            return;
        Iterator<String> iterator = oftenRouteList.iterator();
        String tmp_oftenroute;
        int index = 0;
        String[] split;
        TextView textView;
        View viewById;
        while (iterator.hasNext() && index < oftenRouteIds.length) {
            tmp_oftenroute = iterator.next();
            split = tmp_oftenroute.split("/");
            if (split.length >= 2) {
                viewById = findViewById(oftenRouteIds[index]);
                index++;
                textView = (TextView) viewById.findViewById(R.id.item_start_city_tx);
                ViewUtils.setText(textView, split[0]);
                textView = (TextView) viewById.findViewById(R.id.item_stop_city_tx);
                ViewUtils.setText(textView, split[1]);
            }
        }
    }

    private String getOftenRoute() {
        StringBuffer stringBuffer = new StringBuffer();
        String start_str, stop_str;
        int index = 0;
        TextView textView;
        View viewById;
        while (index < oftenRouteIds.length) {
            viewById = findViewById(oftenRouteIds[index]);
            index++;
            textView = (TextView) viewById.findViewById(R.id.item_start_city_tx);
            start_str = ViewUtils.getStringFromTextView(textView);
            textView = (TextView) viewById.findViewById(R.id.item_stop_city_tx);
            stop_str = ViewUtils.getStringFromTextView(textView);
            if (!TextUtils.isEmpty(start_str) && !TextUtils.isEmpty(stop_str)) {
                stringBuffer.append(start_str).append('/').append(stop_str);
                stringBuffer.append(',');
            }
        }

        //删除最后一个 /
        if (stringBuffer.length() > 1 && stringBuffer.charAt(stringBuffer.length() - 1) == ',')
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    @NonNull
    private View getOftenRouteView(String start, String stop) {
        View tmp_view = getLayoutInflater().inflate(R.layout.often_route_item, null, false);
        TextView start_city = (TextView) tmp_view.findViewById(R.id.item_start_city_tx);
        TextView stop_city = (TextView) tmp_view.findViewById(R.id.item_stop_city_tx);

        ViewUtils.setText(start_city, start);
        ViewUtils.setText(stop_city, stop);
        return tmp_view;
    }

    public void onClick_ModelText(View view) {
        if (!inEditMode) {
            showToast("不是编辑模式");
            return;
        }
        String[] stringArray = getResources().getStringArray(R.array.company_introduce_items);
        String str;
        for (int k = 0; k < stringArray.length; k++) {
            str = stringArray[k];
            str = String.valueOf(k + 1) + "." + str;
            stringArray[k] = str;
        }
        new AlertDialog.Builder(this)
                .setItems(stringArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
//                        EditText editText = (EditText) findViewById(R.id.set_introduce_ed);
                        String[] stringArray = getResources().getStringArray(R.array.company_introduce_items);
                        introduceEd.setText(stringArray[which]);
                    }
                })
                .setTitle("请选择模板")
                .create().show();
    }

    public void onClick_OftenType(View view) {
        if (!inEditMode) {
            showToast("不是编辑模式");
            return;
        }
        final String[] stringArray = getResources().getStringArray(R.array.order_change_type_items);
        new ChooseItemsDialog(this)
                .setTitle("设置常发货物类型")
                .setItems(stringArray, new ChooseItemsDialog.SelectListener() {
                    @Override
                    public void onClickFinish(String[] select_str) {
                        String string = Tools.toString(';', select_str);

                        setTextView(R.id.set_often_send_type_ed, string);

                    }
                })
                .show();

//        new AlertDialog.Builder(this)
//                .setTitle("设置常发货物类型")
//                .setItems(stringArray, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        setTextView(R.id.set_often_send_type_ed, stringArray[which]);
//                    }
//                })
//                .create().show();
    }

    public void onClick_EditMode(View view) {
        if (inEditMode) {
            inEditMode = false;
            introduceEd.setEnabled(false);
            setTextView(R.id.set_company_edit_bt, "编辑");
            requestSetCompany();
        } else {
            inEditMode = true;
            introduceEd.setEnabled(true);
            findViewById(R.id.set_company_introduce_mode_bt).setVisibility(View.VISIBLE);
            setTextView(R.id.set_company_edit_bt, "完成编辑");
        }
    }

    private void requestSetCompany() {
        //// TODO: 2016-1-25  店铺设置接口
        if (company == null) {
            showToast("数据对象为空");
            return;
        }
//        JsonCompany company = new JsonCompany();
        if (!TextUtils.isEmpty(introduceEd.getText())) {
            company.setScaleOfOperation(introduceEd.getText().toString());
        }
        String often_send_type = getStringFromTextView(R.id.set_often_send_type_ed);
        if (!TextUtils.isEmpty(often_send_type))
            company.setOftenSendType(often_send_type);

        String often_route = getOftenRoute();
        company.setOftenRoute(often_route);
        HttpHelper.getInstance().put(AppURL.PutCompaniesInfo + String.valueOf(company.getId()), company, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                showToast("数据上传成功");
            }

            @Override
            public void onFailed(String str) {
                showToast("数据上传失败" + str);
            }
        });
    }
}
