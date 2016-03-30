package com.bt.zhangzy.network.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSON;
import com.bt.zhangzy.logisticstraffic.data.OrderStatus;
import com.bt.zhangzy.tools.Tools;
import com.zhangzy.base.http.BaseEntity;

import java.util.Date;

/**
 * Created by ZhangZy on 2016-1-27.
 */
public class JsonOrder extends BaseEntity implements Parcelable, Comparable<JsonOrder> {
    public JsonOrder() {
    }

    int id;
    String companyName, enterpriseName;
    Date publishDate;//订单发布日期
    int status;//订单状态（已提交/交易中/已完成/取消）
    String describe;//订单描述（作废原因）
    //货物类型  货物重量 货物体积 货物名称
    String goodsType, goodsWeight, goodsVolume, goodsName;
    //需求车型 需求车长
    String needCarType, needCarLength;
    String remark;//备注
    //运输报价
    int price;
    int enterpriseId;//企业ID
    int companyId;//物流公司ID
    int driverId;//司机ID
    Date deliverDate;//发货日期
    Date receiptDate;//收获日期
    //出发城市 目的城市
    String startCity, stopCity;
    //收货人姓名  收货人电话  收货人地址
    String receiverName, receiverPhone, receiverAddress;
    String insurancePolicyNumber;//保险单号
    int orderType;//订单类型（车队货源/公共货源）
    int driverCount;

    /*===============================*/


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(JSON.toJSONString(this));
    }

    //实例化静态内部对象CREATOR实现接口Parcelable.Creator
    public static final Parcelable.Creator<JsonOrder> CREATOR = new Creator<JsonOrder>() {
        @Override
        public JsonOrder createFromParcel(Parcel source) {

            return JSON.parseObject(source.readString(), JsonOrder.class);
        }

        @Override
        public JsonOrder[] newArray(int size) {
            return new JsonOrder[size];
        }
    };

    /*==================================*/

    public int getDriverCount() {
        return driverCount;
    }

    public void setDriverCount(int driverCount) {
        this.driverCount = driverCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status.ordinal();
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(String goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public String getGoodsVolume() {
        return goodsVolume;
    }

    public void setGoodsVolume(String goodsVolume) {
        this.goodsVolume = goodsVolume;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getNeedCarType() {
        return needCarType;
    }

    public void setNeedCarType(String needCarType) {
        this.needCarType = needCarType;
    }

    public String getNeedCarLength() {
        return needCarLength;
    }

    public void setNeedCarLength(String needCarLength) {
        this.needCarLength = needCarLength;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(Date deliverDate) {
        this.deliverDate = deliverDate;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getStartCity() {
        return startCity;
    }

    //地址简写
    public String getStartCitySimple() {
        String s = Tools.splitAddress(startCity, ",")[0];
        return Tools.splitAddress(s,"·")[1];
    }

    //地址详细
    public String getStartCityDetail() {
        return Tools.splitAddress(startCity, ",")[1];
    }

    public void setStartCity(String startCity) {
        this.startCity = startCity;
    }

    public String getStopCity() {
        return stopCity;
    }

    //地址简写
    public String getStopCitySimple() {
        String s = Tools.splitAddress(stopCity, ",")[0];
        return Tools.splitAddress(s,"·")[1];
    }

    //地址详细
    public String getStopCityDetail() {
        return Tools.splitAddress(stopCity, ",")[1];
    }

    public void setStopCity(String stopCity) {
        this.stopCity = stopCity;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }


    @Override
    public int compareTo(JsonOrder another) {
        //倒序排列
        return another.id - this.id;
    }
}
