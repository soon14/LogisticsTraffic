package com.bt.zhangzy.network.entity;

/**
 * Created by ZhangZy on 2016-1-28.
 */
public class ResponseCompany extends BaseEntity {
    public ResponseCompany() {
    }

    int id, userId;
    int status;
    String name, address, area;
    String photoUrl, businessLicenseUrl, taxRegistrationCertificateUrl;
    String validationMessage;
    String longitude, latitude;
    String myWealth, star;
    String commentsCount, orderCount, totalMileage, oftenRoute, numberOfTeams;
    String callCount, viewCount;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getBusinessLicenseUrl() {
        return businessLicenseUrl;
    }

    public void setBusinessLicenseUrl(String businessLicenseUrl) {
        this.businessLicenseUrl = businessLicenseUrl;
    }

    public String getTaxRegistrationCertificateUrl() {
        return taxRegistrationCertificateUrl;
    }

    public void setTaxRegistrationCertificateUrl(String taxRegistrationCertificateUrl) {
        this.taxRegistrationCertificateUrl = taxRegistrationCertificateUrl;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getMyWealth() {
        return myWealth;
    }

    public void setMyWealth(String myWealth) {
        this.myWealth = myWealth;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(String commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        this.orderCount = orderCount;
    }

    public String getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(String totalMileage) {
        this.totalMileage = totalMileage;
    }

    public String getOftenRoute() {
        return oftenRoute;
    }

    public void setOftenRoute(String oftenRoute) {
        this.oftenRoute = oftenRoute;
    }

    public String getNumberOfTeams() {
        return numberOfTeams;
    }

    public void setNumberOfTeams(String numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    public String getCallCount() {
        return callCount;
    }

    public void setCallCount(String callCount) {
        this.callCount = callCount;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }
}
