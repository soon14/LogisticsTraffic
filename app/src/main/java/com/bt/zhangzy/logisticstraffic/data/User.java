package com.bt.zhangzy.logisticstraffic.data;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/30.
 */
public class User {

    private static User instance = new User();
    public static User getInstance() {
        return instance;
    }

    private boolean isLogin = false;
    private Type userType = Type.EmptyType;
    private String userName;
    private String phoneNum;
    private String address;
    private Location location;//保存用户的定位信息
    /**司机列表*/
    private ArrayList<People> driverList;
    /** 浏览历史 */
    private ArrayList<Product> historyList = new ArrayList<Product>();

    private User() {
        //test data
        driverList = new ArrayList<People>();
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));

    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<People> getDriverList() {
        return driverList;
    }

    public ArrayList<Product> getHistoryList() {
        return historyList;
    }

    /*添加历史记录*/
    public void addHistoryList(Product product) {
        historyList.add(product);
    }

    public void setDriverList(ArrayList<People> driverList) {
        this.driverList = driverList;
    }

    public boolean getLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        this.isLogin = login;
    }

    public Type getUserType() {
        return userType;
    }

    public void setUserType(Type userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
