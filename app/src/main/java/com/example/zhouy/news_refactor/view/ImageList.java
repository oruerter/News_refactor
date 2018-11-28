package com.example.zhouy.news_refactor.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.zhouy.news_refactor.MainActivity;
import com.example.zhouy.news_refactor.R;
import com.example.zhouy.news_refactor.adapter.OnRecyclerviewItemClickListener;
import com.example.zhouy.news_refactor.adapter.imageListAdapter;
import com.example.zhouy.news_refactor.model.ImageLink;
import com.example.zhouy.news_refactor.model.NewsLink;
import com.example.zhouy.news_refactor.util.DataUtil;
import com.example.zhouy.news_refactor.util.sendValueToServer;
import com.example.zhouy.news_refactor.view.BaseActivity.BaseActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.shinichi.library.ImagePreview;
import cc.shinichi.library.bean.ImageInfo;
import cc.shinichi.library.glide.ImageLoader;
import cc.shinichi.library.tool.MyToast;
import okhttp3.Call;
import okhttp3.Response;

public class ImageList extends BaseActivity {
    private List<ImageLink> imageList = new ArrayList<>();;
    private RecyclerView imageRecycler;
    private imageListAdapter imageListAdapter;
    private NewsLink newsLink;
    private boolean isEmpty = false;
//    private
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagelist_activity);

        findViewById();
        initView();
        initData();
    }

    @Override
    protected void findViewById() {
        imageRecycler = findViewById(R.id.recycler_view);
    }

    @Override
    protected void initView() {
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        imageRecycler.setLayoutManager(layoutManager);
        imageListAdapter = new imageListAdapter(imageList,ImageList.this,onRecyclerviewItemClickListener);
        imageRecycler.setAdapter(imageListAdapter);
    }

    @Override
    protected void initData() {
        newsLink = (NewsLink) getIntent().getSerializableExtra("link");
        DataUtil.sendOkHttpRequest(newsLink.getLink(), new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("errorOnNewsContent", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] responseByte = response.body().bytes();
                String responseData = new String(responseByte, "GB2312");
                new Thread(new sendValueToServer(responseData,newsLink.getLink()) {
                    @Override
                    public void run() {
                        String html1;
                        html1 = this.html;
                        String htmllink;
                        htmllink = this.htmllink;

                        Bundle bundle = new Bundle();
                        bundle.putString("html", html1);
                        bundle.putString("htmllink",htmllink);
                        Message msg = handler1.obtainMessage();
                        msg.setData(bundle);
                        handler1.sendMessage(msg);
                    }
                }).start();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler1 = new Handler(new Handler.Callback() {
        int j;
        @Override
        public boolean handleMessage(Message msg){
            Bundle bundle = msg.getData();

            if(!TextUtils.isEmpty(bundle.get("html").toString())){
                String html = bundle.get("html").toString();
                Document doc = Jsoup.parse(html);
                Elements pagenum = doc.getElementsByTag("span");

                if(pagenum.size() == 0){
                    isEmpty = true;
                    onDestroy();
//                    Intent intent = new Intent(ImageList.this,EmptyActivity.class);
//                    startActivity(intent);
                }else {
                    isEmpty = false;
                    String numstring = pagenum.get(0).text();
                    String regEx="[^0-9]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(numstring);
                    int num = Integer.parseInt(m.replaceAll("").trim());

                    String htmllink = bundle.get("htmllink").toString();
                    htmllink = htmllink.replace("?","");

                    String htmlhead = htmllink.substring(0,htmllink.length()-5);

                    String htmlfoot = htmllink.substring(htmllink.length()-5,htmllink.length());


                    Log.i("newslink",htmllink+" "+htmlhead+" "+htmlfoot);

                    for(j = 1 ; j <= num ; j ++){
                        String link;
                        if(j == 1)link = htmlhead + htmlfoot;
                        else link = htmlhead+'_'+j+htmlfoot;
                        Log.i("htmllink",link);
                        DataUtil.sendOkHttpRequest(link, new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.i("errorOnNewsContent", e.toString());
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
                                        int index = this.html.charAt(this.html.length()-1);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("html",html1);
                                        Message msg = handler2.obtainMessage();
                                        msg.setData(bundle);
                                        handler2.sendMessage(msg);
                                    }
                                }).start();
                            }
                        });
                    }
                }

            }

            return false;
        }
    });

    Handler handler2 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg){
            String html = null;
            Bundle bundle = msg.getData();
            html = bundle.get("html").toString();
            if(!TextUtils.isEmpty(html)){
                Document doc = Jsoup.parse(html);
                Elements image_doc = doc.getElementsByTag("img");
                String imgsrc = image_doc.get(0).attr("src");
//                Log.i("index", String.valueOf(index) + imgsrc);
                int index;
                Elements index_dec = doc.getElementsByClass("thisclass");
                index = Integer.parseInt(index_dec.get(0).text());
//                Log.i("!!!!!!!!", String.valueOf(ind)+" "+imgsrc);
                imageList.add(new ImageLink(imgsrc,index));
                Collections.sort(imageList,new SortByAge());
                imageListAdapter.notifyDataSetChanged();
            }
            return false;
        }
    });

    private OnRecyclerviewItemClickListener onRecyclerviewItemClickListener = new OnRecyclerviewItemClickListener() {
        @Override
        public void onItemClickListener(View v, int position) {
            Log.i("position", String.valueOf(position));
            //这里的view就是我们点击的view  position就是点击的position
            ImageInfo imageInfo;
            final List<ImageInfo> imageInfoList = new ArrayList<>();
            for (ImageLink image : imageList) {
                imageInfo = new ImageInfo();
                // 原图地址（必填）
                imageInfo.setOriginUrl(image.getLink());
                // 缩略图地址（必填）
                // 如果没有缩略图url，可以将两项设置为一样。（注意：此处作为演示用，加了-1200，你们不要这么做）
                imageInfo.setThumbnailUrl(image.getLink());
                imageInfoList.add(imageInfo);
                imageInfo = null;
            }
            // 仅加载原图
                    ImagePreview
                            .getInstance()
                            .setContext(ImageList.this)
                            .setIndex(position)
                            .setImageInfoList(imageInfoList)
                            .setShowDownButton(true)
                            .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysOrigin)
                            .setFolderName("BigImageViewDownload")
                            .setScaleLevel(1, 3, 8)
                            .setZoomTransitionDuration(300)

                            .setEnableClickClose(true)// 是否启用点击图片关闭。默认启用
                            .setEnableDragClose(true)// 是否启用上拉/下拉关闭。默认不启用

                            .setShowCloseButton(false)// 是否显示关闭页面按钮，在页面左下角。默认不显示
                            .setCloseIconResId(R.drawable.ic_action_close)// 设置关闭按钮图片资源，可不填，默认为：R.drawable.ic_action_close

                            .setShowDownButton(true)// 是否显示下载按钮，在页面右下角。默认显示
                            .setDownIconResId(R.drawable.icon_download_new)// 设置下载按钮图片资源，可不填，默认为：R.drawable.icon_download_new

                            .setShowIndicator(true)// 设置是否显示顶部的指示器（1/9）。默认显示
                            .start();
        }
    };
    class SortByAge implements Comparator {
        public int compare(Object o1, Object o2) {
            ImageLink s1 = (ImageLink) o1;
            ImageLink s2 = (ImageLink) o2;
            if (s1.getIndex() > s2.getIndex())
                return 1;
            return -1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.i("empty?", String.valueOf(isEmpty));
        if(isEmpty){
            MyToast.getInstance()._short(ImageList.this,"还未更新");
        }else{
            ImageLoader.cleanDiskCache(ImageList.this);
            MyToast.getInstance()._short(ImageList.this, "磁盘缓存已成功清除");
        }

//        Intent intent = new Intent(ImageList.this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        startActivity(intent);
    }
}
