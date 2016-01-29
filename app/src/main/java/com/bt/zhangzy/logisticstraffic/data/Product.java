package com.bt.zhangzy.logisticstraffic.data;

import com.bt.zhangzy.logisticstraffic.R;

import java.io.Serializable;
import java.util.Random;

/**
 * 展示的信息数据类型封装
 * Created by ZhangZy on 2015/7/9.
 */
public class Product implements Serializable {
    final int ID;

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
    private int iconImg;//图标
    private int[] photoImg = {R.drawable.fake_1,//相册
            R.drawable.fake_2, R.drawable.fake_3, R.drawable.fake_4, R.drawable.fake_5, R.drawable.fake_6, R.drawable.fake_7, R.drawable.fake_8, R.drawable.fake_9, R.drawable.fake_10};

    public Product(int ID) {
        this.ID = ID;
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

    public int[] getPhotoImg() {
        return photoImg;
    }

    private Random random = new Random();

    public int getIconImg() {
        if (iconImg == 0) {
            int index = Math.abs(random.nextInt() % photoImg.length);
            iconImg = photoImg[index];
        }
        return iconImg;
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
}
