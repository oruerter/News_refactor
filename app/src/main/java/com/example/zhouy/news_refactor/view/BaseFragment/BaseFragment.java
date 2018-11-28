package com.example.zhouy.news_refactor.view.BaseFragment;

import android.support.v4.app.Fragment;

/**
 * Created by zhouy on 2018/11/27.
 */

public abstract class BaseFragment extends Fragment {
    protected abstract void findViewById();
    protected abstract void initView();
    protected abstract void initData();
}
