package com.example.zhouy.news_refactor.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhouy.news_refactor.util.DataUtil;
import com.example.zhouy.news_refactor.R;
import com.example.zhouy.news_refactor.adapter.OnRecyclerviewItemClickListener;
import com.example.zhouy.news_refactor.adapter.newsListAdapter;
import com.example.zhouy.news_refactor.model.NewsLink;
import com.example.zhouy.news_refactor.util.sendValueToServer;
import com.example.zhouy.news_refactor.view.BaseFragment.BaseFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.shinichi.library.tool.MyToast;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhouy on 2018/11/27.
 */

public class NewList extends BaseFragment {
    public static final String NEWS_BASE_URL = "http://www.hqck.net/";
    private List<NewsLink> newsList = new ArrayList<NewsLink>();
    private SwipeRefreshLayout newsRefresh;

    private RecyclerView newsRecycler;
    private newsListAdapter newsAdapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        view = inflater.inflate(R.layout.newslist_fragment, container, false);
        findViewById();
        initView();
        initData();
        return view;
    }

    private OnRecyclerviewItemClickListener onRecyclerviewItemClickListener = new OnRecyclerviewItemClickListener() {
        @Override
        public void onItemClickListener(View v, int position) {
            //这里的view就是我们点击的view  position就是点击的position
//            if(newsList.get(position).isUpdate()){
            Intent intent = new Intent(getActivity(),ImageList.class);
            intent.putExtra("link",newsList.get(position));
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
//            }else{
//                MyToast.getInstance()._short(getActivity(), "磁盘缓存已成功清除");
//            }

        }
    };

    @Override
    protected void findViewById() {
        newsRefresh = view.findViewById(R.id.swipe_refresh);
        newsRecycler = view.findViewById(R.id.recycler_view);
    }

    @Override
    protected void initView() {
        newsRefresh.setColorSchemeResources(R.color.colorAccent);
        newsRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNews();
            }
        });
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        newsRecycler.setLayoutManager(layoutManager);
        newsAdapter = new newsListAdapter(newsList, onRecyclerviewItemClickListener);
        newsRecycler.setAdapter(newsAdapter);
    }

    @Override
    protected void initData() {
        DataUtil.sendOkHttpRequest(NEWS_BASE_URL, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("error", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] responseByte = response.body().bytes();
                String responseData = new String(responseByte, "GB2312");
                new Thread(new sendValueToServer(responseData) {
                    @Override
                    public void run() {
                        String html1;
                        html1 = this.html;
                        Bundle bundle = new Bundle();
                        bundle.putString("html", html1);
                        Message msg = handler.obtainMessage();
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }

    private void refreshNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsList.clear();
                        initData();
                        newsAdapter.notifyDataSetChanged();
                        newsRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String html = bundle.get("html").toString();

            Document doc = Jsoup.parse(html);
            Elements html_list = doc.getElementsByClass("item-baozhi");

            for (int i = 0; i < html_list.size(); i++) {
                NewsLink newslink = new NewsLink();
                String pagelink = NEWS_BASE_URL + html_list.get(i).attr("href");//?
                String pagecolor = html_list.get(i).getElementsByTag("span").attr("class");
                String pagetitle = html_list.get(i).getElementsByTag("span").text();
//                pagetitle.replace
                pagetitle = pagetitle.replace("电子版在线阅读","");
                Log.i("hello", pagelink + "  " + pagetitle);

                newslink.setLink(pagelink);
                newslink.setTitle(pagetitle);
                if (pagecolor.contains("Red")) {
                    newslink.setUpdate(false);
                } else if (pagecolor.contains("Gray")) {
                    newslink.setUpdate(true);
                }
                newsList.add(newslink);
                newsAdapter.notifyDataSetChanged();
            }
            return false;
        }
    });
}

