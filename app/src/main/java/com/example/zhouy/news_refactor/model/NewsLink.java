package com.example.zhouy.news_refactor.model;

import java.io.Serializable;

/**
 * Created by zhouy on 2018/11/27.
 */

public class NewsLink implements Serializable{
    private String title;
    private String link;
    private boolean isUpdate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}
