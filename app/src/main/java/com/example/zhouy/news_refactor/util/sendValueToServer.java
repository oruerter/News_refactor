package com.example.zhouy.news_refactor.util;

/**
 * Created by zhouy on 2018/2/19.
 */

public class sendValueToServer implements Runnable {
    protected String html;
    protected String htmllink;
    @Override
    public void run() {

    }
    public sendValueToServer(String html){
        this.html = html;
    }
    public sendValueToServer(String html , String htmllink){
        this.html = html;
        this.htmllink = htmllink;
    }
}
