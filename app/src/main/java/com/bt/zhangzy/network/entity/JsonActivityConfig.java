package com.bt.zhangzy.network.entity;

import com.zhangzy.base.http.BaseEntity;

import java.util.List;

/**
 * Created by ZhangZy on 2016-7-14.
 */
public class JsonActivityConfig extends BaseEntity {
    String name;
    List<JsonActivityEntity> list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JsonActivityEntity> getList() {
        return list;
    }

    public void setList(List<JsonActivityEntity> list) {
        this.list = list;
    }

    public class JsonActivityEntity extends BaseEntity {
        String label, image, url;
        int userType;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }
    }

}

