package com.bt.zhangzy.network.entity;

/**
 * Created by ZhangZy on 2016-1-21.
 */
public class JsonDriver extends BaseEntity {

    int id , userId;
    String licensePhotoUrl;//驾驶证照片URL
    String special_qualifications_photo_url;//特殊资质图片URL
    String person_license_photo_url;//本人手持驾驶证照片URL
    String my_wealth;//我的财富
    int star;//星级
    int comments_count;//评论数
    int order_count;//接单数
    int total_mileage;//总里程


    public JsonDriver() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLicensePhotoUrl() {
        return licensePhotoUrl;
    }

    public void setLicensePhotoUrl(String licensePhotoUrl) {
        this.licensePhotoUrl = licensePhotoUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSpecial_qualifications_photo_url() {
        return special_qualifications_photo_url;
    }

    public void setSpecial_qualifications_photo_url(String special_qualifications_photo_url) {
        this.special_qualifications_photo_url = special_qualifications_photo_url;
    }

    public String getPerson_license_photo_url() {
        return person_license_photo_url;
    }

    public void setPerson_license_photo_url(String person_license_photo_url) {
        this.person_license_photo_url = person_license_photo_url;
    }

    public String getMy_wealth() {
        return my_wealth;
    }

    public void setMy_wealth(String my_wealth) {
        this.my_wealth = my_wealth;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
    }

    public int getTotal_mileage() {
        return total_mileage;
    }

    public void setTotal_mileage(int total_mileage) {
        this.total_mileage = total_mileage;
    }
}
