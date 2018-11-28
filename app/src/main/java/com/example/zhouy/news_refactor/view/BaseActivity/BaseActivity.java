package com.example.zhouy.news_refactor.view.BaseActivity;

import android.app.Activity;

/**
 * Created by zhouy on 2018/11/27.
 */

public abstract class BaseActivity extends Activity {
    protected abstract void findViewById();
    protected abstract void initView();
    protected abstract void initData();
}
