package com.example.zhouy.news_refactor.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.example.zhouy.news_refactor.R;
import com.example.zhouy.news_refactor.model.ImageLink;

import java.util.List;

/**
 * Created by zhouy on 2018/11/27.
 */

public class imageListAdapter extends RecyclerView.Adapter<imageListAdapter.ViewHolder> implements View.OnClickListener {
    private List<ImageLink> mimageList;
    private Context context;
    private OnRecyclerviewItemClickListener mOnRecyclerviewItemClickListener = null;

    public imageListAdapter(List<ImageLink>imageList,Context context,OnRecyclerviewItemClickListener mOnRecyclerviewItemClickListener){
        mimageList = imageList;
        this.context = context;
        this.mOnRecyclerviewItemClickListener = mOnRecyclerviewItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View imageView;
        ImageView image;
        TextView index;

        public ViewHolder(View view) {
            super(view);
            imageView = view;
            image = (ImageView) view.findViewById(R.id.image);
            index = (TextView) view.findViewById(R.id.imgindex);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        view.setOnClickListener(this);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageLink imageLink = mimageList.get(position);
        holder.index.setText(String.valueOf(imageLink.getIndex()));
        holder.imageView.setTag(position);

        RequestBuilder<Drawable> requestBuilder =
                Glide.with(context)
                        .load(imageLink.getLink());

        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .override(500, 500);
        requestBuilder
//                .thumbnail(0.2f)
                .apply(myOptions)
                .into(holder.image);
//        GlideApp.wi
    }

    @Override
    public int getItemCount() {
        return mimageList.size();
    }

    @Override
    public void onClick(View v) {
        mOnRecyclerviewItemClickListener.onItemClickListener(v, ((int) v.getTag()));
    }
}
