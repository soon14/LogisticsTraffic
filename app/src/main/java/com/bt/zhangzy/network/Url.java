package com.bt.zhangzy.network;

/**
 * Created by ZhangZy on 2016-1-5.
 */
public final class Url {

    /*服务器端口  例：http://182.92.77.31:8080/freight/users/1  */
    private static final String Host = "http://182.92.77.31:8080";

    /* 注册   用户列表： http://182.92.77.31:8080/users/list  */
    public static final String Register = Host + "/users/register";

    /*按ID获取用户基本信息  get */
    public static final String GetUserInfo = Host + "/users/";

    /* 获取用户列表*/
    public static final String GetUserList = Host + "/users/list";

    /* 登录 */
    public static final String Login = Host + "/users/login";

    /* 城市列表     */
    public static final String CityList = Host + "values";
    /* 货物类型     */
    public static final String CargoType = Host + "goodstype";

}
