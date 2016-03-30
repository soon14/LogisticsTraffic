package com.bt.zhangzy.logisticstraffic.d.pay;


import com.zhangzy.base.http.BaseEntity;

public class WXResponse extends BaseEntity {
    JsonMember member;
    WXResponsePay weixinPay;

    public JsonMember getMember() {
        return member;
    }

    public void setMember(JsonMember member) {
        this.member = member;
    }

    public WXResponsePay getWeixinPay() {
        return weixinPay;
    }

    public void setWeixinPay(WXResponsePay weixinPay) {
        this.weixinPay = weixinPay;
    }
}
