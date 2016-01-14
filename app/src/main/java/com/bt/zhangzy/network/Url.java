package com.bt.zhangzy.network;

/**
 * Created by ZhangZy on 2016-1-5.
 */
public final class Url {

    /*服务器端口  例：http://182.92.77.31:8080/freight/users/1  */
    private static final String Host = "http://182.92.77.31:8080/freight/";

    /* 注册 */
    public static final String Register = Host + "register01";

    /* 登录 */
    public static final String Login = Host + "login";

    /* 城市列表     */
    public static final String CityList = Host + "values";
    /* 货物类型     */
    public static final String CargoType = Host + "goodstype";

}
