package com.bt.zhangzy.network.entity;

/**
 * Created by ZhangZy on 2016-1-21.
 */
public class JsonCompany extends BaseEntity {
    int id, userId;
    String name, address;
    String photoUrl, photoUrl2, photoUrl3;//门头照片URL
    String taxRegistrationCertificateUrl;//税务登记证URL
    String businessLicenseUrl;//营业执照URL
    String organizationCode;//组织机构代码
    String taxCode;//税号
    int status;
    String oftenRoute;//常发线路
    String validationMessage;//验证返回信息
    double longitude, latitude;//经度信息 纬度信息
    String area;//所属区域
    String myWealth;//我的财富
    double star;//星级
    int commentsCount;//评论数
    int orderCount;//接单数
    int totalMileage;//总里程
    int numberOfTeams;//车队人数
    int callCount;
    int viewCount;
    String oftenSendType;//常发货物类型
    String scaleOfOperation;//经营规模


    public JsonCompany() {
    }

    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getOftenSendType() {
        return oftenSendType;
    }

    public void setOftenSendType(String oftenSendType) {
        this.oftenSendType = oftenSendType;
    }

    public String getScaleOfOperation() {
        return scaleOfOperation;
    }

    public void setScaleOfOperation(String scaleOfOperation) {
        this.scaleOfOperation = scaleOfOperation;
    }

    public String getOftenRoute() {
        return oftenRoute;
    }

    public void setOftenRoute(String oftenRoute) {
        this.oftenRoute = oftenRoute;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getMyWealth() {
        return myWealth;
    }

    public void setMyWealth(String myWealth) {
        this.myWealth = myWealth;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(int totalMileage) {
        this.totalMileage = totalMileage;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public void setNumberOfTeams(int numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl2() {
        return photoUrl2;
    }

    public void setPhotoUrl2(String photoUrl2) {
        this.photoUrl2 = photoUrl2;
    }

    public String getPhotoUrl3() {
        return photoUrl3;
    }

    public void setPhotoUrl3(String photoUrl3) {
        this.photoUrl3 = photoUrl3;
    }

    public String getTaxRegistrationCertificateUrl() {
        return taxRegistrationCertificateUrl;
    }

    public void setTaxRegistrationCertificateUrl(String taxRegistrationCertificateUrl) {
        this.taxRegistrationCertificateUrl = taxRegistrationCertificateUrl;
    }

    public String getBusinessLicenseUrl() {
        return businessLicenseUrl;
    }

    public void setBusinessLicenseUrl(String businessLicenseUrl) {
        this.businessLicenseUrl = businessLicenseUrl;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }
}
