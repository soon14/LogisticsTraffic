package com.bt.zhangzy.logisticstraffic.d.pay;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-3-30.
 */
public class WXRequest extends BaseEntity {
    String title, detail,
    //                orderId,//返回的订单号包含yyyyMMdd前缀
//                totalFee,//价格 单位：分
    from;//发送订单的手机的ip地址
    int userId;
    int amount;
    int carId;

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
