package com.bt.zhangzy.logisticstraffic.data;

import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.ResponseCompany;

import java.io.Serializable;

/**
 * 展示的信息数据类型封装
 * Created by ZhangZy on 2015/7/9.
 */
public class Product implements Serializable {
    final int ID;
    int userId;
    private Type type = Type.EmptyType;
    private String name;
    private String phoneNumber;
    private String describe;
    private String address;//地址
    private String times;//被浏览的次数
    private String callTimes;//拨打次数
    private float level;//评分等级
    private boolean isVip;// 是否是认证用户
    private Location location;//地理位置
    private String iconImgUrl;//图标
    private String[] photoImgUrl;
    private ResponseCompany company;//数据备份
//    private int[] photoImg = {R.drawable.fake_1,//相册
//            R.drawable.fake_2, R.drawable.fake_3, R.drawable.fake_4, R.drawable.fake_5, R.drawable.fake_6, R.drawable.fake_7, R.drawable.fake_8, R.drawable.fake_9, R.drawable.fake_10};

    public Product(int ID) {
        this.ID = ID;
    }

    public Product(Product p) {
        this.ID = p.ID;
        this.userId = p.userId;
        this.type = p.type;
        this.name = p.name;
        this.phoneNumber = p.phoneNumber;
        this.describe = p.describe;
        this.address = p.address;
        this.times = p.times;
        this.callTimes = p.callTimes;
        this.level = p.level;
        this.isVip = p.isVip;
        this.location = p.location;
        this.iconImgUrl = p.iconImgUrl;
        this.photoImgUrl = p.photoImgUrl;
        this.company = p.company;
    }

    public static Product ParseJson(ResponseCompany response) {
        JsonUser user = response.getUser();
        JsonCompany company = response.getCompany();
        if (user == null || company == null)
            return null;

        Product product = ParseJson(company);
        product.userId = user.getId();
//        product.name = user.getName();
        product.phoneNumber = user.getPhoneNumber();
        product.iconImgUrl = company.getPhotoUrl();
        product.setCompany(response);
        return product;
    }

    public static Product ParseJson(JsonCompany company) {
        if (company == null)
            return null;
        Product product = new Product(company.getId());
        product.type = Type.CompanyInformationType;
        product.name = company.getName();
        product.address = company.getAddress();
        product.photoImgUrl = new String[]{company.getPhotoUrl(), company.getPhotoUrl2(), company.getPhotoUrl3()};
        product.location = new Location();
        product.location.setLongitude(String.valueOf(company.getLongitude()));
        product.location.setLatitude(String.valueOf(company.getLatitude()));
        //设置 认证用户或者 付费用户
        product.setIsVip(company.getStatus() != -1);
        product.setLevel((float) company.getStar());
        product.setTimes(String.valueOf(company.getViewCount()));
        product.setCallTimes(String.valueOf(company.getCallCount()));
        product.setDescribe(company.getOftenRoute());
//        product.setCompany(response);
        return product;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getCallTimes() {
        return callTimes;
    }

    public void setCallTimes(String callTimes) {
        this.callTimes = callTimes;
    }

    public int getID() {
        return ID;
    }

    public float getLevel() {
        return level;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setIsVip(boolean isVip) {
        this.isVip = isVip;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Product) {
            Product p = (Product) o;
            if (ID == p.ID)
                return true;
        }
        return super.equals(o);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setCompany(ResponseCompany company) {
        this.company = company;
    }

    public ResponseCompany getCompany() {
        return company;
    }

    public String[] getPhotoImgUrl() {
        return photoImgUrl;
    }

    public void setPhotoImgUrl(String[] photoImgUrl) {
        this.photoImgUrl = photoImgUrl;
    }

    public String getIconImgUrl() {
        return iconImgUrl;
    }

    public void setIconImgUrl(String iconImgUrl) {
        this.iconImgUrl = iconImgUrl;
    }
}
