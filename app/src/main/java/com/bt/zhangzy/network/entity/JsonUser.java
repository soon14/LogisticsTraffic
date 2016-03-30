package com.bt.zhangzy.network.entity;

import com.bt.zhangzy.logisticstraffic.data.Type;
import com.zhangzy.base.http.BaseEntity;

import java.util.Date;

/**
 * Created by ZhangZy on 2016-1-15.
 */
public class JsonUser extends BaseEntity {

    int id, roleId;
    int status, role;
    String name, nickname ,password, recommendCode, phoneNumber;
    //个人正面照片URL  个人身份证URL 个人头像URL
    String personPhotoUrl, idCardPhotoUrl, portraitUrl;
    Date registerDate;
    int recommendUserId;

    public JsonUser() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public int getRecommendUserId() {
        return recommendUserId;
    }

    public void setRecommendUserId(int recommendUserId) {
        this.recommendUserId = recommendUserId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPersonPhotoUrl() {
        return personPhotoUrl;
    }

    public void setPersonPhotoUrl(String personPhotoUrl) {
        this.personPhotoUrl = personPhotoUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * ## 用户状态
     * <p/>
     * |名称|属性值|
     * |未审核|-1|
     * |已审核|0|
     * |冻结|1|
     * |删除|2|
     * |已付费|3|
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 用户角色
     * 名称	属性值
     * 司机	1
     * 企业	2
     * 物流公司/信息部	3
     * 司机 + 企业	4
     * 司机 + 物流公司/信息部	5
     *
     * @return
     */
    public int getRole() {
        return role;
    }

    public Type getType() {
        return role == 1 ? Type.DriverType : role == 2 ? Type.EnterpriseType : role == 3 ? Type.CompanyInformationType : Type.EmptyType;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdCardPhotoUrl() {
        return idCardPhotoUrl;
    }

    public void setIdCardPhotoUrl(String idCardPhotoUrl) {
        this.idCardPhotoUrl = idCardPhotoUrl;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    public String getRecommendCode() {
        return recommendCode;
    }

    public void setRecommendCode(String recommendCode) {
        this.recommendCode = recommendCode;
    }
}
