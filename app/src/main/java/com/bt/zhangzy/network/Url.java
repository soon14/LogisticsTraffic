package com.bt.zhangzy.network;

/**
 * Created by ZhangZy on 2016-1-5.
 */
public final class Url {

    /*服务器端口  例：http://182.92.77.31:8080/freight/users/1  */
    public static final String Host = "http://182.92.77.31:8080";

    /* 注册   用户列表： http://182.92.77.31:8080/users/list  */
    public static final String Register = Host + "/users/register";

    /*按ID获取用户基本信息  get */
    public static final String GetUserInfo = Host + "/users/";
    //按ID修改用户基本信息
    public static final String PutUserInfo = Host + "/users/";

    /* 获取用户列表*/
    public static final String GetUserList = Host + "/users/list";

    /* 登录 */
    public static final String Login = Host + "/users/login";

   /* 验证司机/企业和信息部的接口是
    /enterprises/authenticate
    /drivers/authenticate
    /companies/authenticate  */

    /* 发送验证码 */
    public static final String SendVerificationCode = Host + "/commons/sendVerificationCode/";

    /* 获取手机验证码 /commons/getVerificationCode/{phoneNumber}*/
    public static final String GetVerificationCode = Host + "/commons/getVerificationCode/";

    /*图片上传接口*/
    public static final String UpLoadImage = Host+"/upload";

    /*企业身份认证*/
    public static final String PostVerifyEnterprises = Host + "/enterprises/authenticate";
    /*信息部/物流公司身份认证*/
    public static final String PostVerifyCompanies = Host + "/companies/authenticate";
    /*司机身份认证*/
    public static final String PostVerifyDrivers = Host + "/drivers/authenticate";

    /* 根据用户id获取对应的司机信息*/
    public static final String GetDriverInfo =Host +  "/drivers/user_id/";
    /*根据用户id获取对应的企业信息*/
    public static final String GetEnterprisesInfo =Host +  "/enterprises/user_id/";
    /*根据对应用户id获取 物流公司的详细信息*/
    public static final String GetCompaniesInfo =Host +  "/companies/user_id/";

    //根据id值获取店铺信息
    public static final String GetCompany = Host + "/companies/";

    /* 按照id更新企业信息 */
    public static final String PutEnterprisesInfo = Host + "/enterprises/";
    public static final String PutCompaniesInfo = Host + "/companies/";
    public static final String PutDrivers = Host + "/drivers/";

    public static final String PostDriversAddCar = Host + "/drivers/add_car";
    //获取车队信息
    public static final String GetMotorcades = Host + "/motorcades/";
//    按角色和id获取车队列表
    public static final String GetMotorcadesList = Host + "/motorcades/list/";
    //获取归属车队列表   /drivers/list_motorcade
    public static final String GetDriversListMotorcade = Host + "/drivers/list_motorcade";
    //添加成员/加入车队   /motorcades/motorcade_driver
    public static final String PostAddMotorcadeDriver = Host + "/motorcades/motorcade_driver";
    //通过账号添加成员  /motorcades/{id}/{phoneNumber}  车队Id	 司机电话
    public static final String PostAddMotorcadeDriverPhone = Host + "/motorcades/";
    //退出车队/踢出车队  /motorcades/motorcade_driver
    public static final String DeleteMotorcadeDriver = Host + "/motorcades/motorcade_driver";


    /* 城市列表     */
    public static final String CityList = Host + "values";
    /* 货物类型     */
    public static final String CargoType = Host + "goodstype";

}
