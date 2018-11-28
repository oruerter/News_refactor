package com.example.zhouy.news_refactor.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhouy.news_refactor.R;
import com.example.zhouy.news_refactor.model.NewsLink;

import java.util.List;

/**
 * Created by zhouy on 2018/11/27.
 */

public class newsListAdapter extends RecyclerView.Adapter<newsListAdapter.ViewHolder> implements View.OnClickListener {
    private OnRecyclerviewItemClickListener mOnRecyclerviewItemClickListener = null;
    private List<NewsLink> mnewsLink;

    public newsListAdapter(List<NewsLink> newsLink,OnRecyclerviewItemClickListener mOnRecyclerviewItemClickListener){
        this.mnewsLink = newsLink;
        this.mOnRecyclerviewItemClickListener = mOnRecyclerviewItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View newView;
        TextView newTitle;
        ImageView newStat;

        public ViewHolder(View view) {
            super(view);
            newView = view;
            newTitle = (TextView) view.findViewById(R.id.newsTitle);
            newStat = (ImageView) view.findViewById(R.id.newStat);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        view.setOnClickListener(this);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsLink newsLink = mnewsLink.get(position);
        holder.newTitle.setText(newsLink.getTitle());
        holder.newView.setTag(position);
        if(newsLink.isUpdate()){
            holder.newStat.setImageResource(R.drawable.ic_track_changes_blue_300_36dp);
        }else {
            holder.newStat.setImageResource(R.drawable.ic_track_changes_pink_a200_36dp);
        }
    }

    @Override
    public int getItemCount() {
        return mnewsLink.size();
    }

    @Override
    public void onClick(View v) {
        mOnRecyclerviewItemClickListener.onItemClickListener(v, ((int) v.getTag()));
    }


}
