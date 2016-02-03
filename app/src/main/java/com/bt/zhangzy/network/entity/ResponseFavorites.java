package com.bt.zhangzy.network.entity;

import java.util.List;

/**
 * Created by ZhangZy on 2016-1-30.
 */
public class ResponseFavorites extends BaseEntity {
    public ResponseFavorites() {
    }
    List<JsonFavorite> favorites;
    List<JsonCompany> companies;
    List<JsonEnterprise> enterprises;

    public List<JsonFavorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<JsonFavorite> favorites) {
        this.favorites = favorites;
    }

    public List<JsonCompany> getCompanies() {
        return companies;
    }

    public void setCompanies(List<JsonCompany> companies) {
        this.companies = companies;
    }
}
