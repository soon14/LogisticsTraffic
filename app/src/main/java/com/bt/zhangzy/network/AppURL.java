package com.bt.zhangzy.network;

/**
 * Created by ZhangZy on 2016-1-5.
 */
public final class AppURL {

    /*服务器端口  例：http://182.92.77.31:8080/freight/users/1  */
    public static final String Host = "http://182.92.77.31:8080";

    public static final String ABOUT_APP = Host + "/html/software.html";//软件
    public static final String REGISTER_LAW = Host + "/html/law.html";//法律
    public static final String DOWNLOAD_APP = Host + "/html/download.html";//下载
    public static final String ABOUT_COMPANY = Host + "/html/company.html";//公司介绍
    public static final String LOCATION_MAP = Host + "/mall/qiantai/ditu.html?longitude=%d1&latitude=%d2";

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
    public static final String UpLoadImage = Host + "/upload";
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
    public static final String GetDriverInfo = Host + "/drivers/user_id/";
    /*根据用户id获取对应的企业信息*/
    public static final String GetEnterprisesInfo = Host + "/enterprises/user_id/";
    /*根据对应用户id获取 物流公司的详细信息*/
    public static final String GetCompaniesInfo = Host + "/companies/user_id/";

    /* 按照id更新企业信息 */
    public static final String PutEnterprisesInfo = Host + "/enterprises/";
    public static final String PutCompaniesInfo = Host + "/companies/";
    public static final String PutDrivers = Host + "/drivers/";

    /*=========== 首页相关 ==============================*/
    //根据id值获取店铺信息
    public static final String GetCompany = Host + "/companies/";
    public static final String GetCompanyList = Host + "/companies/list";

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
    public static final String GetOrder = Host + "/orders/";//按ID获取订单
    public static final String PutOrder = Host + "/orders/";
    public static final String GetMyOrderList = Host + "/orders/show_user_order";//获取用户所属的订单列表
    public static final String GetOrderList = Host + "/orders/list";//获取可抢订单列表
    public static final String CallDriver = Host + "/orders/call_driver";// call车接口
    public static final String CallMotorcade = Host + "/orders/call_motorcade";// call 车队
    public static final String CallPublic = Host + "/orders/call_public";// call 所有车
    public static final String Accept = Host + "/orders/accept";//接单  orderId  role roleId
    public static final String Reject = Host + "/orders/reject";//拒绝接单


    /*=========== 收藏 ============================*/
    public static final String GetFavourite = Host + "/users/favourite";///users/ favour  get方式，传4个参数 fromRole,fromRoleId,toRole,toRoleId
    public static final String DelFavourite = Host + "/users/favourite/";///users/favourite/{id} //取消 和 获取收藏;
    public static final String GetFavouriteList = Host + "/users/list_favourite";//收藏夹

    /*============= 搜索 =================================*/
    public static final String GetSearch = Host + "/companies/search";//在认证公司信息或者修改公司信息时对name、address、area字段做索引，通过关键词对公司名称进行检索


}