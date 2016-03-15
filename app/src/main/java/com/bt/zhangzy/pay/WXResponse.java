package com.bt.zhangzy.pay;

import com.bt.zhangzy.network.entity.BaseEntity;
import com.bt.zhangzy.network.entity.JsonMember;

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
