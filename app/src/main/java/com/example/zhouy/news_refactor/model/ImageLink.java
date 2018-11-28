package com.example.zhouy.news_refactor.model;

import java.io.Serializable;

/**
 * Created by zhouy on 2018/11/27.
 */

public class ImageLink implements Serializable{
    String link;
    int index;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ImageLink(String link, int index) {
        this.link = link;
        this.index = index;
    }
}
