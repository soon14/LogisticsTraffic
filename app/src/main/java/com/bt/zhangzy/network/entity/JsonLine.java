package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-5-18.
 */
public class JsonLine extends BaseEntity {

    //出发城市 目的城市
    String startCity, stopCity;
    //收货人姓名  收货人电话  收货人地址
    String receiverName, receiverPhone, receiverAddress;

    String consignorName;//发货人名字
    String consignorPhone;//电话
}
