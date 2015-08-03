package com.bt.zhangzy.logisticstraffic.data;

import android.text.TextUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ZhangZy on 2015/6/30.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static User instance = new User();

    public static User getInstance() {
        return instance;
    }

    public void loadUser(User user) {
        if (user == null)
            return;
        instance = user;
    }

    private boolean isLogin = false;
    private Type userType = Type.EmptyType;
    private String userName;
    private String phoneNum;
    private String address;
    private Location location;//保存用户的定位信息
    /**
     * 司机列表
     */
    private ArrayList<People> driverList;
    /**
     * 浏览历史
     */
    private ArrayList<Product> historyList = new ArrayList<Product>();

    /**
     * 收藏列表
     */
    private ArrayList<Product> collectionList = new ArrayList<Product>();

    /**
     * 搜索关键字历史
     */
    private ArrayList<String> searchKeyWordList = new ArrayList<String>();

    private User() {
        //test data
        driverList = new ArrayList<People>();
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));
        driverList.add(new People().setName("王鹏").setPhoneNumber("13511233658"));

        searchKeyWordList.add("测试测试");
        searchKeyWordList.add("测试1");

        Product p = new Product(12343);
        p.setName("到底多大多大的");
        collectionList.add(p);

    }

    @Override
    public String toString() {
        return super.toString();
//        StringBuffer stringBuffer = new StringBuffer();
//        driverList.toString();
//        return stringBuffer.toString();
    }

    public ArrayList<String> getSearchKeyWordList() {
        return searchKeyWordList;
    }

    /**
     * 添加搜索记录
     *
     * @param keyStr
     */
    public void addSearchKeyWord(String keyStr) {
        if (TextUtils.isEmpty(keyStr))
            return;
        searchKeyWordList.add(keyStr);
        if (searchKeyWordList.size() > 5) {
            searchKeyWordList.remove(0);
        }
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

    public ArrayList<Product> getCollectionList() {
        return collectionList;
    }

    /**
     * 添加到收藏列表
     *
     * @param product
     */
    public void addCollectionProduct(Product product) {
        collectionList.add(product);
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
