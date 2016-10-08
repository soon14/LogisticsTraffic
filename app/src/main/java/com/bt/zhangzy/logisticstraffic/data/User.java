package com.bt.zhangzy.logisticstraffic.data;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.view.ConfirmDialog;
import com.bt.zhangzy.network.AppURL;
import com.bt.zhangzy.network.HttpHelper;
import com.bt.zhangzy.network.JsonCallback;
import com.bt.zhangzy.network.entity.JsonCar;
import com.bt.zhangzy.network.entity.JsonCompany;
import com.bt.zhangzy.network.entity.JsonDriver;
import com.bt.zhangzy.network.entity.JsonFavorite;
import com.bt.zhangzy.network.entity.JsonLine;
import com.bt.zhangzy.network.entity.JsonMember;
import com.bt.zhangzy.network.entity.JsonMotorcades;
import com.bt.zhangzy.network.entity.JsonOrder;
import com.bt.zhangzy.network.entity.JsonUser;
import com.bt.zhangzy.network.entity.RequestFindByDriver;
import com.bt.zhangzy.network.entity.ResponseFavorites;
import com.bt.zhangzy.network.entity.ResponseFindByDriver;
import com.bt.zhangzy.network.entity.ResponseLogin;
import com.bt.zhangzy.network.entity.ResponseUserInfo;
import com.zhangzy.base.http.BaseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用户基本信息
 * Created by ZhangZy on 2015/6/30.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String TAG = User.class.getSimpleName();

    private static User instance = new User();
    private String registrationID;//推送标识符
    private List<JsonFavorite> jsonFavorites = new ArrayList<JsonFavorite>();//收藏标识 // TO DO: 2016-1-29 拿到收藏列表后要根据列表内容判断 是否已收藏

    private boolean isLogin = false;
    private Type userType = Type.EmptyType;
    private UserStatus userStatus = UserStatus.UN_CHECKED;
    private long id;
    private int companyID, enterpriseID, driverID;
    private String userName, nickName;
    private String phoneNum, password;
    private String headUrl;
    private Location location;//保存用户的定位信息
    private boolean isFirstOpen = true;
    private boolean isVIP = false;//标记用户是否付费
    private boolean isSave = true;//标记用户是否记住用户名
    private BaseEntity jsonTypeEntity;
    private JsonUser jsonUser;
    private List<JsonMotorcades> motorcades;//车队列表
    private List<JsonCar> jsonCarList;//司机 所属的 车辆；
    private ArrayList<Integer> orderIdList;//作废 2016年9月29日
    private List<JsonDriver> jsonDriverList;//司机列表 车主功能
    //支付状态
    private PayStatus payStatus;
    private JsonMember payJson;//支付结果缓存;

    //常用发线路
    private ArrayList<JsonLine> linesList = new ArrayList<JsonLine>();
    private JsonLine lastUseLine;

    //运输中的订单
    JsonOrder runOrder;
    JsonCar runCar;

    public static User getInstance() {
        return instance;
    }


    public JsonOrder getRunOrder() {
        return runOrder;
    }

    public JsonCar getRunCar() {
        return runCar;
    }

    /**
     * 获取司机列表
     *
     * @return
     */
    public List<JsonDriver> getJsonDriverList() {
        return jsonDriverList;
    }

    /**
     * 设置司机列表
     *
     * @param jsonDriverList
     */
    public void setJsonDriverList(List<JsonDriver> jsonDriverList) {
        this.jsonDriverList = jsonDriverList;
    }

    public ArrayList<JsonLine> getLinesList() {
        return linesList;
    }

    private boolean addLines(JsonLine jsonLine) {
        if (jsonLine == null)
            return false;
        linesList.add(jsonLine);
        return true;
    }

    private boolean changeLines(JsonLine jsonLine) {
        if (linesList != null && !linesList.isEmpty()) {
            if (jsonLine != null) {
                JsonLine line;
                for (int k = 0; k < linesList.size(); k++) {
                    line = linesList.get(k);
                    if (line.getId() == jsonLine.getId()) {
                        linesList.set(k, jsonLine);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean removeLines(int lineId) {
        if (linesList != null && !linesList.isEmpty()) {
            if (lineId > -1) {
                JsonLine line;
                for (int k = 0; k < linesList.size(); k++) {
                    line = linesList.get(k);
                    if (line.getId() == lineId) {
                        linesList.remove(k);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public JsonLine getLastUseLine() {
        return lastUseLine;
    }

    public void setLastUseLine(JsonLine lastUseLine) {
        this.lastUseLine = lastUseLine;
    }

    /**
     * 获取线路列表
     */
    public void requestFreightLineList(final Handler.Callback callback) {

        HttpHelper.getInstance().get(AppURL.GetFreightLineList, "?userId=" + id, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                Log.i(TAG, "线路列表获取：" + result);
                List<JsonLine> list = ParseJson_Array(result, JsonLine.class);
                if (!list.isEmpty()) {
                    linesList.clear();
                    linesList.addAll(list);
                    if (callback != null) {
                        callback.handleMessage(null);
                    }
                }
            }

            @Override
            public void onFailed(String str) {
                Log.w(TAG, "线路列表获取失败：" + str);
            }
        });
    }

    /**
     * 添加线路
     *
     * @param jsonLine
     */
    public void requestFreightLineAdd(final JsonLine jsonLine) {

        HttpHelper.getInstance().post(AppURL.PostFreightLineAdd, jsonLine, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                Log.i(TAG, "线路添加成功");
                addLines(jsonLine);
            }

            @Override
            public void onFailed(String str) {
                Log.i(TAG, "线路添加失败");
            }
        });
    }

    /**
     * 修改线路
     *
     * @param jsonLine
     */
    public void requestFreightLineModify(final JsonLine jsonLine) {

        HttpHelper.getInstance().post(AppURL.PostFreightLineModify, jsonLine, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                Log.i(TAG, "线路修改成功");
                changeLines(jsonLine);
            }

            @Override
            public void onFailed(String str) {
                Log.i(TAG, "线路修改失败");
            }
        });
    }

    public void requestFreightLineRemove(final int lineId) {

        HttpHelper.getInstance().get(AppURL.GetFreightLineRemove, "?id=" + lineId, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                Log.i(TAG, "线路删除成功");
                removeLines(lineId);
            }

            @Override
            public void onFailed(String str) {
                Log.i(TAG, "线路删除失败");
            }
        });
    }

    /**
     * 检查用户的审核状态
     *
     * @param activity
     * @return
     */
    public boolean checkUserStatus(BaseActivity activity) {
        if (userStatus != UserStatus.CHECKED && userStatus != UserStatus.PAID) {
            switch (userStatus) {
                case UN_CHECKED:
                    activity.showToast("用户未审核");
                    break;
                case LOCK:
                    activity.showToast("用户已冻结");
                    break;
                case DELETE:
                    activity.showToast("用户已删除");
                    break;
                case COMMITED:
                    activity.showToast("用户已提交资料，请等待系统审核");
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * 加载用回对象，用于数据存储；
     *
     * @param user
     */
    public void loadUser(User user) {
        if (user == null)
            return;
        instance = user;
    }

    /**
     * 重置用户 但是保留必要信息
     */
    public void resetUser() {
        String userName = getUserName();
        String phone = getPhoneNum();
        String password = isSave ? getPassword() : null;
        boolean is_save = isSave;
        instance = new User();
        instance.setUserName(userName);
        instance.setPhoneNum(phone);
        instance.setPassword(password);
        instance.setIsSave(is_save);
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public PayStatus getPayStatus() {
        return payStatus;
    }

    public JsonMember getPayJson() {
        return payJson;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public ArrayList<Integer> getOrderIdList() {
        return orderIdList;
    }

    public void setOrderIdList(ArrayList<Integer> orderIdList) {
        this.orderIdList = orderIdList;
    }

    public List<JsonCar> getJsonCar() {
        return jsonCarList;
    }

    public void setJsonCar(List<JsonCar> jsonCar) {
        this.jsonCarList = jsonCar;
    }

    public List<JsonMotorcades> getMotorcades() {
        return motorcades;
    }


    public void setMotorcades(List<JsonMotorcades> motorcades) {
        this.motorcades = motorcades;
    }

    public JsonUser getJsonUser() {
        return jsonUser;
    }

    public void setJsonUser(JsonUser jsonUser) {
        this.jsonUser = jsonUser;
    }

    public void setJsonTypeEntity(BaseEntity jsonTypeEntity) {
        this.jsonTypeEntity = jsonTypeEntity;
    }

    public <T extends BaseEntity> T getJsonTypeEntity() {
        return (T) jsonTypeEntity;
    }

    /**
     * 获取对应的角色id
     *
     * @return
     */
    public int getRoleId() {
        int roleId = getUserType() == Type.CompanyInformationType ? getCompanyID()
                : getUserType() == Type.EnterpriseType ? getEnterpriseID()
                : getUserType() == Type.DriverType ? getDriverID()
                : (int) getId();
        return roleId;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public int getEnterpriseID() {
        return enterpriseID;
    }

    public void setEnterpriseID(int enterpriseID) {
        this.enterpriseID = enterpriseID;
    }

    public int getDriverID() {
        return driverID;
    }

    public void setDriverID(int driverID) {
        this.driverID = driverID;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    public boolean isVIP() {
        return isVIP;
    }


    public boolean isFirstOpen() {
        return isFirstOpen;
    }

    public void setIsFirstOpen(boolean isFirstOpen) {
        this.isFirstOpen = isFirstOpen;
    }

    /**
     * 司机列表
     */
//    private ArrayList<People> driverList;
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

    }

    @Override
    public String toString() {
        return "User{" +
                "isLogin=" + isLogin +
                ", id=" + id +
                ", userType=" + userType +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", password='" + password + '\'' +
                ", isSave=" + isSave +
                ", jsonUser=" + jsonUser +
                '}';
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        Log.i(TAG, "set location " + location);
        this.location = location;
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
//        historyList.add(new Product(product));
    }


    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        this.isLogin = login;
    }

    public boolean isDevicesType() {
        return getUserType() == Type.DriverType;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setRegistrationID(String registrationID) {
        this.registrationID = registrationID;
    }

    public String getRegistrationID() {
        return registrationID;
    }

    public void setJsonFavorites(ResponseFavorites response) {
        //// TO DO: 2016-1-30 返回收藏列表中 带有角色信息
//        if (jsonFavorites != null)
//            this.jsonFavorites = jsonFavorites.getFavorites();
//        else
//            Log.w(TAG, "返回收藏列表中 没有角色信息");
        if (response == null)
            return;
        //更新收藏信息
        if (response.getFavorites() != null && !response.getFavorites().isEmpty()) {
//            User.getInstance().setJsonFavorites(response);
            this.jsonFavorites = response.getFavorites();
        }
        if (response.getCompanies() != null && !response.getCompanies().isEmpty()) {
            ArrayList<Product> list = new ArrayList<Product>();
            Product p;
            for (JsonCompany company : response.getCompanies()) {
                p = Product.ParseJson(company);
                if (p != null) {
                    list.add(p);
                }
            }
            collectionList = list;
        }
    }

    public List<JsonFavorite> getJsonFavorites() {
        return jsonFavorites;
    }

    //检查 是否在收藏列表中
    public int checkFavoritesId(int id) {
        if (jsonFavorites == null || jsonFavorites.isEmpty())
            return -1;
        for (JsonFavorite json : jsonFavorites) {
            if (id == json.getFavoritedRoleId()) {
                return json.getId();
            }
        }

        return -1;
    }


    public void setLoginResponse(ResponseLogin json) {

        updatePayStatus(json.getMember());
        JsonUser jsonUser = json.getUser();
        User user = User.getInstance();
        user.setLogin(true);
//                showToast(JSON.toJSONString(jsonUser));
        user.setId(jsonUser.getId());
        user.setUserName(jsonUser.getName());
        user.setPhoneNum(jsonUser.getPhoneNumber());
        user.setNickName(jsonUser.getNickname());
        user.userStatus = UserStatus.parse(jsonUser.getStatus());
        user.isVIP = user.userStatus == UserStatus.PAID;
        user.setJsonUser(jsonUser);
        switch (jsonUser.getRole()) {
            case 1:
                user.setUserType(Type.DriverType);
                if (json.getDriver() != null) {
                    user.setJsonTypeEntity(json.getDriver());
                    user.setDriverID(json.getDriver().getId());
                    List<JsonCar> cars = json.getCars();
                    if (cars != null && !cars.isEmpty()) {
                        setJsonCar(cars);
                    }
                }
                requestMyRunOrder();
//                user.setMotorcades(BaseEntity.ParseArray(json.getMotorcades(), JsonMotorcades.class));
                break;
            case 2:
                user.setUserType(Type.EnterpriseType);
                if (json.getEnterprise() != null) {
                    user.setJsonTypeEntity(json.getEnterprise());
                    user.setEnterpriseID(json.getEnterprise().getId());
                }
                break;
            case 3:
                user.setUserType(Type.CompanyInformationType);
                if (json.getCompany() != null) {
                    user.setJsonTypeEntity(json.getCompany());
                    user.setCompanyID(json.getCompany().getId());
                }

                break;
        }
//        if(jsonUser.getRole() == 2 || jsonUser.getRole() == 3){
//            if(TextUtils.isEmpty(json.getMotorcades())){
//                Log.w(TAG,"车队返回错误：数据为空");
//                return;
//            }
//            JsonMotorcades motorcades = BaseEntity.ParseEntity(json.getMotorcades(), JsonMotorcades.class);
//            ArrayList<JsonMotorcades> jsonMotorcades = new ArrayList<>();
//            jsonMotorcades.add(motorcades);
//            user.setMotorcades(jsonMotorcades);
//        }
        user.setMotorcades(json.getMotorcades());
        user.setJsonFavorites(json.getFavorites());
        //车主功能出来后  这里的交易中订单 就没用了
//        if (json.getOrders() != null && !json.getOrders().isEmpty()) {
//            //保存交易中的订单id 便于位置上传；
//            ArrayList<Integer> order_id_list = new ArrayList<Integer>();
//            for (JsonOrder order : json.getOrders()) {
//                Log.i(TAG, "running order id=" + order.getId());
//                order_id_list.add(order.getId());
//            }
//            setOrderIdList(order_id_list);
//        }

        // 获取线路列表
        requestFreightLineList(null);
    }

    /**
     * 跟新用户的支付状态
     */
    public void requestPayStatus(final Handler.Callback callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(getId()));
        HttpHelper.getInstance().get(AppURL.GetPayStatus, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                JsonMember jsonMember = ParseJson_Object(result, JsonMember.class);
                updatePayStatus(jsonMember);
                if (callback != null)
                    callback.handleMessage(null);
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

    private void updatePayStatus(JsonMember jsonMember) {
        if (jsonMember != null) {
            payStatus = PayStatus.Parse(jsonMember.getIsExpired());
            payJson = jsonMember;
            isVIP = payStatus == PayStatus.PaymentReceived;
        }
    }

    /**
     * 检测用户的支付状态，并弹出支付对话框
     *
     * @param activity
     * @return 是否已支付
     */
    public boolean checkUserVipStatus(BaseActivity activity) {
        if (getUserType() == Type.DriverType) {
            if (getUserStatus() == UserStatus.UN_CHECKED) {
                activity.showToast("用户资料不完善，无法使用此功能");
                return false;
            } else if (getUserStatus() == UserStatus.COMMITED) {
                activity.showToast("用户资料正在审核，请耐心等待审核通过");
                return false;
            }
            if (!User.getInstance().isVIP()) {
//                activity.gotoPay();
                new ConfirmDialog(activity)
                        .setMessage(activity.getString(R.string.dialog_ask_pay))
                        .setHideCancelBt()
                        .setListener(new ConfirmDialog.ConfirmDialogListener() {
                            @Override
                            public void onClick(boolean isConfirm) {

                            }
                        }).show();
//                ConfirmDialog.showConfirmDialog(this, getString(R.string.dialog_ask_pay), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startActivity(PayActivity.class);
//                    }
//                });
                return false;
            }
        }

        return true;
    }

    /**
     * 更新用户信息 和 角色信息
     */
    public void requestUserInfo() {
        //如果登陆成功  更新用户的基本信息；AppURL.GetUserInfo
        //这里改为 自动登录

        HttpHelper.getInstance().get(AppURL.GetUserInfo, String.valueOf(User.getInstance().getId()), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                if (TextUtils.isEmpty(result)) {
                    Log.i(TAG, "用户信息更新失败：" + msg);
                    return;
                }
                Log.i(TAG, "用户信息更新成功");
                JsonUser jsonUser = ParseJson_Object(result, JsonUser.class);
                User user = User.getInstance();
//                    user.setLogin(true);
//                    showToast(JSON.toJSONString(jsonUser));
                user.setId(jsonUser.getId());
                user.setUserName(jsonUser.getName());
                user.setPhoneNum(jsonUser.getPhoneNumber());
                user.setNickName(jsonUser.getNickname());
                user.setHeadUrl(jsonUser.getPortraitUrl());
                user.userStatus = UserStatus.parse(jsonUser.getStatus());
                user.isVIP = user.userStatus == UserStatus.PAID;
                user.setJsonUser(jsonUser);
                switch (jsonUser.getRole()) {
                    case 1:
                        user.setUserType(Type.DriverType);
                        requestDriverInfo();
                        break;
                    case 2:
                        user.setUserType(Type.EnterpriseType);
                        requestEnterpriseInfo();
                        break;
                    case 3:
                        user.setUserType(Type.CompanyInformationType);
                        requestCompaniesInfo();
                        break;
                }

            }

            @Override
            public void onFailed(String str) {

            }
        });
    }


    //更新司机信息
    private void requestDriverInfo() {
        HttpHelper.getInstance().get(AppURL.GetDriverInfo, User.getInstance().getId(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                Log.i(TAG, "司机信息更新成功：" + msg);
                JsonDriver json = ParseJson_Object(result, JsonDriver.class);
                User user = User.getInstance();
                user.setDriverID(json.getId());
//                user.setUserName(json.getName());
//                user.setAddress(json.getAddress());
                user.setJsonTypeEntity(json);
                requestCarInfo(null);
//                refreshView();
            }

            @Override
            public void onFailed(String str) {
                Log.i(TAG, "司机信息更新失败：" + str);
            }
        });
    }

    /**
     * 更新车辆列表
     */
    public void requestCarInfo(final JsonCallback callback) {
        HttpHelper.getInstance().get(AppURL.GetCarInfo, new String[]{"driverID=" + User.getInstance().getDriverID()}, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                Log.i(TAG, "司机-车辆信息更新成功：" + msg);
                if (TextUtils.isEmpty(result))
                    return;
                List<JsonCar> list = ParseJson_Array(result, JsonCar.class);
//                JsonCar jsonCarList = ParseJson_Object(result, JsonCar.class);
                if (list.isEmpty())
                    return;
                User.getInstance().setJsonCar(list);
                if (callback != null)
                    callback.onSuccess(msg, result);
            }

            @Override
            public void onFailed(String str) {
                Log.i(TAG, "司机-车辆信息更新失败：" + str);
                if (callback != null)
                    callback.onFailed(str);
            }
        });
    }

    //更新企业信息
    private void requestEnterpriseInfo() {

        HttpHelper.getInstance().get(AppURL.GetEnterprisesInfo, User.getInstance().getId(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                ResponseUserInfo json = ParseJson_Object(result, ResponseUserInfo.class);
                User user = User.getInstance();
                user.setEnterpriseID(json.getEnterprise().getId());
//                user.setUserName(json.getEnterprise().getName());
                user.setJsonTypeEntity(json.getEnterprise());

//                refreshView();
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

    //更新物流公司信息
    private void requestCompaniesInfo() {
        HttpHelper.getInstance().get(AppURL.GetCompaniesInfo, User.getInstance().getId(), new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                ResponseUserInfo json = ParseJson_Object(result, ResponseUserInfo.class);
                User user = User.getInstance();

                user.setCompanyID(json.getCompany().getId());
//                user.setUserName(json.getCompany().getName());
                user.setJsonTypeEntity(json.getCompany());

                List<JsonMotorcades> motorcades = json.getMotorcades();
                user.setMotorcades(motorcades);

//                refreshView();
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }


    /**
     * 请求正在运输中的订单
     */
    public void requestMyRunOrder() {

        RequestFindByDriver params = new RequestFindByDriver();
        params.setDriverId((int) User.getInstance().getId());
        HttpHelper.getInstance().post(AppURL.PostFindByDriver, params, new JsonCallback() {
            @Override
            public void onSuccess(String msg, String result) {
                ResponseFindByDriver response = ResponseFindByDriver.ParseEntity(result, ResponseFindByDriver.class);
                runOrder = response.getOrder();
                runCar = response.getCar();
            }

            @Override
            public void onFailed(String str) {
            }
        });
    }
}
