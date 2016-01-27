package com.bt.zhangzy.network.entity;

/**
 * Created by ZhangZy on 2016-1-25.
 */
public class JsonUserDriver extends BaseEntity {
    public JsonUserDriver() {
    }

    int id, motorcadeId, driverId;
    String name, nickname,phoneNumber;
    String personPhotoUrl, idCardPhotoUrl, portraitUrl;
    int role;
    long registerDate;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMotorcadeId() {
        return motorcadeId;
    }

    public void setMotorcadeId(int motorcadeId) {
        this.motorcadeId = motorcadeId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPersonPhotoUrl() {
        return personPhotoUrl;
    }

    public void setPersonPhotoUrl(String personPhotoUrl) {
        this.personPhotoUrl = personPhotoUrl;
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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }
}
