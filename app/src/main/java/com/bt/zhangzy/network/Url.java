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

    /* 发送验证码 */
    public static final String SendVerificationCode = Host + "/commons/sendVerificationCode/";

    /* 获取手机验证码 /commons/getVerificationCode/{phoneNumber}*/
    public static final String GetVerificationCode = Host + "/commons/getVerificationCode/";

    /*图片上传接口*/
    public static final String UpLoadImage = Host+"/upload";
    /*========= 验证 司机/企业/信息部 的接口 =====================================================*/
    /*企业身份认证*/
    public static final String PostVerifyEnterprises = Host + "/enterprises/authenticate";
    /*信息部/物流公司身份认证*/
    public static final String PostVerifyCompanies = Host + "/companies/authenticate";
    /*司机身份认证*/
    public static final String PostVerifyDrivers = Host + "/drivers/authenticate";
    /*========== 获取角色信息 ====================================================*/
    public static final String GetCarInfo = Host + "/drivers/list_car/";//传driverID
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

    /*============ 车队相关 ==================================================*/
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
 /*==============================================================*/

    /* 城市列表  所有城市http://182.92.77.31:8080/citys/list
返回已开通城市的介绍和图片http://182.92.77.31:8080/cityInfo/%E5%8C%85%E5%A4%B4
返回已开通城市的列表
http://182.92.77.31:8080/cityInfo/ktlist   */
    public static final String GetCityList = Host + "/citys/list";
    public static final String GetCityInfo = Host + "/cityInfo/";//+城市名称
    public static final String GetOpenCityList = Host + "/cityInfo/ktlist";

    /*=============== 订单接口 ====================================*/
    public static final String PostCreatOrders = Host + "/orders/create";
    public static final String PostSendToCompany = Host + "/orders/send_to_company";
}
