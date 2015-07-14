package com.bt.zhangzy.logisticstraffic.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 联系人数据封装
 * Created by ZhangZy on 2015/7/10.
 */
public class People implements Parcelable {
    private String name;
    private String phoneNumber;


    public String getName() {
        return name;
    }

    public People setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public People setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNumber);
    }

    //实例化静态内部对象CREATOR实现接口Parcelable.Creator
    public static final Parcelable.Creator<People> CREATOR = new Creator<People>() {

        @Override
        public People[] newArray(int size) {
            return new People[size];
        }

        //将Parcel对象反序列化为ParcelableDate
        @Override
        public People createFromParcel(Parcel source) {
            People people = new People();
            people.setName(source.readString());
            people.setPhoneNumber(source.readString());
            return people;
        }
    };
}
