package com.bt.zhangzy.network;

/**
 * Created by ZhangZy on 2016-1-5.
 */
public enum AppURL {

    ABOUT_APP("/html/software.html"),//软件
    REGISTER_LAW("/html/law.html"),//法律
    DOWNLOAD_APP("/html/download.html"),//下载
    ABOUT_COMPANY("/html/company.html"),//公司介绍
    LOCATION_MAP("/mall/qiantai/ditu.html?longitude=%d1&latitude=%d2"),
    //周边服务
    LOCATION_MAP_SERVERS("/freight/html/ditu2.html?longitude=%f1&latitude=%f2&zf=%s"),

    /* 注册   用户列表： http://182.92.77.31:8080/users/list  */
    Register("/users/register"),

    /*按ID获取用户基本信息  get */
    GetUserInfo("/users/"),
    //按ID修改用户基本信息
    PutUserInfo("/users/"),

    /* 获取用户列表*/
    GetUserList("/users/list"),

    /* 登录 */
    Login("/users/login"),

    /* 发送验证码 */
    SendVerificationCode("/commons/sendVerificationCode/"),

    /* 获取手机验证码 /commons/getVerificationCode/{phoneNumber}*/
    GetVerificationCode("/commons/getVerificationCode/"),

    /*图片上传接口*/
    UpLoadImage("/upload"),

    /*========= 验证 司机/企业/信息部 的接口 =====================================================*/
    /*企业身份认证*/
    PostVerifyEnterprises("/enterprises/authenticate"),
    /*信息部/物流公司身份认证*/
    PostVerifyCompanies("/companies/authenticate"),
    /*司机身份认证*/
    PostVerifyDrivers("/drivers/authenticate"),
    /*========== 获取角色信息 ====================================================*/
    GetCarInfo("/drivers/list_car/"),//传driverID
    /* 根据用户id获取对应的司机信息*/
    GetDriverInfo("/drivers/user_id/"),
    /*根据用户id获取对应的企业信息*/
    GetEnterprisesInfo("/enterprises/user_id/"),
    /*根据对应用户id获取 物流公司的详细信息*/
    GetCompaniesInfo("/companies/user_id/"),

    /* 按照id更新企业信息 */
    PutEnterprisesInfo("/enterprises/"),
    PutCompaniesInfo("/companies/"),
    PutDrivers("/drivers/"),

    /*=========== 首页相关 ==============================*/
    GetRecommend("/companies/recommend"),//推荐位 ，/companies/recommend get方法
    //根据id值获取店铺信息
    GetCompany("/companies/"),
    GetCompanyList("/companies/list"),
    GetCommentList("/comments/get_comment_role"),//获取被评论对象的评论列表
    GetCommentListForMe("/comments/get_role"),//获取论对象的评论列表
    PostComment("/comments/post"),//发布评论

    /*============ 车队相关 ==================================================*/
    PostDriversAddCar("/drivers/add_car"),
    PutDrviersCar("/cars/"),// /cars/{id} 修改车辆信息
    PostDriversPublishCar("/drivers/publish_car"),
    //获取车队信息
    GetMotorcades("/motorcades/"),
    //    按角色和id获取车队列表
    GetMotorcadesList("/motorcades/list/"),
    //获取归属车队列表   /drivers/list_motorcade
    GetDriversListMotorcade("/drivers/list_motorcade"),
    //添加成员/加入车队   /motorcades/motorcade_driver
    PostAddMotorcadeDriver("/motorcades/motorcade_driver"),
    //通过账号添加成员  /motorcades/{id}/{phoneNumber}  车队Id	 司机电话
    PostAddMotorcadeDriverPhone("/motorcades/"),
    //退出车队/踢出车队  /motorcades/motorcade_driver
    DeleteMotorcadeDriver("/motorcades/motorcade_driver"),
 /*==============================================================*/

    /* 城市列表  所有城市http://182.92.77.31:8080/citys/list
返回已开通城市的介绍和图片http://182.92.77.31:8080/cityInfo/%E5%8C%85%E5%A4%B4
返回已开通城市的列表
http://182.92.77.31:8080/cityInfo/ktlist   */
    GetCityList("/citys/list"),
    GetCityInfo("/cityInfo/"),//+城市名称
    GetOpenCityList("/cityInfo/ktlist"),

    /*=============== 订单接口 ====================================*/
    PostCreatOrders("/orders/create"),
    PostSendToCompany("/orders/send_to_company"),
    GetOrder("/orders/"),//按ID获取订单
    PutOrder("/orders/"),
    GetOrderDel("/orders/delete"),//删除订单 get方式，传orderId
    GetMyOrderList("/orders/show_user_order"),//获取用户所属的订单列表
    GetOrderList("/orders/list"),//获取可抢订单列表
    PostCallDriver("/orders/call_driver"),// call车接口
    PostCallMotorcade("/orders/call_motorcade"),// call 车队
    PostCallPublic("/orders/call_public"),// call 所有车
    PostAccept("/orders/accept"),//接单  orderId  role roleId
    PostReject("/orders/reject"),//拒绝接单
    PostAllocationDriverList("/orders/list_orderhistory"),//抢单成功的司机列表（订单推送列表）
    PostAllocation("/orders/allocate"),//物流公司选择接受订单的司机并分配订单
    PostSaveOrderHistory("/orders/save_order_history"),//公共货源里，司机点抢单调用这个接口save_order_history    这个接口里会调用accept
    PostFinishOrder("/orders/finish"),//根据订单号将订单修改成完成


    /*=========== 收藏 ============================*/
    GetFavourite("/users/favourite"),///users/ favour  get方式，传4个参数 fromRole,fromRoleId,toRole,toRoleId
    DelFavourite("/users/favourite/"),///users/favourite/{id} //取消 和 获取收藏;
    GetFavouriteList("/users/list_favourite"),//收藏夹

    /*============= 搜索 =================================*/
    GetSearch("/companies/search"),//在认证公司信息或者修改公司信息时对name、address、area字段做索引，通过关键词对公司名称进行检索

    /*============== 车源 ================================*/
    GetPublicCarList("/cars/list"),
    GetCarSearch("/cars/search"),
    GetMotorcadeCarList("/motorcades/list_car"),

    /*=============== 位置上传 ==================================*/
    PostUploadLocation("/drivers/upload_coordinate"),//post方法，传carTrack对象
    GetDriverLocationList("/drivers/list_coordinate"),//get方法，传orderId对象

    Empty("NULL");
    //  枚举类 URL 的自定义
     /*服务器端口  例：http://182.92.77.31:8080/freight/users/1  */
    public static final String Host = "http://182.92.77.31:8080";
    private String url;

    /**
     * @param url
     */
    AppURL(String url) {
        this.url = Host + url;
    }


    @Override
    public String toString() {
        //复写tostring方法  便利操作
        return url;
    }
}