package com.example.ximanaya.Adapter;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.R;
import com.example.ximanaya.View.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InterHolder> {

    private List<Track> mData = new ArrayList<>();
    private static final String TAG = "PlayListAdapter";
    private int playingIndex=0;
    private SobPopWindow.PlayListItemClickListener mPlayListItemClickListener;

    @NonNull
    @Override
    public InterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);

        return new InterHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull InterHolder holder, final int position) {
        //设置数据
        Track track = mData.get(position);
        TextView trackTitleTv = holder.itemView.findViewById(R.id.track_title_tv);

        //设置字体颜色
        trackTitleTv.setTextColor(BaseApplication.getAppContext().getResources().getColor(playingIndex==position?R.color.main_color:R.color.play_list_text_color));
        trackTitleTv.setText(track.getTrackTitle());
        //找播放状态图标
        View playingIconView=holder.itemView.findViewById(R.id.play_icon_iv);
        playingIconView.setVisibility(playingIndex==position?View.VISIBLE:View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayListItemClickListener != null) {
                    mPlayListItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setData(List<Track> data) {
        //谁知数据，更新列表
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int position) {
        playingIndex=position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(SobPopWindow.PlayListItemClickListener listener) {
        this.mPlayListItemClickListener =listener;
    }

    public class InterHolder extends RecyclerView.ViewHolder {
        public InterHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
