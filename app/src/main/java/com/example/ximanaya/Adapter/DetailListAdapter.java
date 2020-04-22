package com.example.ximanaya.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ximanaya.R;
import com.example.ximanaya.Utils.PlayConunt;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolser> {

    private List<Track> mDetailData = new ArrayList<>();
    //格式化时间
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mtimelongDateFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
        return new InnerHolser(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolser holder, final int position) {
        //找到控件.设置数据
        final View itemView = holder.itemView;
        //顺序id
        TextView mItemDetailId = (TextView) itemView.findViewById(R.id.item_detail_id);
        //标题
        TextView mItemDetailName = (TextView) itemView.findViewById(R.id.item_detail_name);
        //播放次数
        TextView mItemDetailPlayConunt = (TextView) itemView.findViewById(R.id.item_detail_play_conunt);
        //时长
        TextView mItemDetailTimeLong = (TextView) itemView.findViewById(R.id.item_detail_time_long);
        //更新日期
        TextView mItemDetailTime = (TextView) itemView.findViewById(R.id.item_detail_time);

        //设置数据
        Track track = mDetailData.get(position);
        mItemDetailId.setText((position + 1) + "");
        mItemDetailName.setText(track.getTrackTitle());
        mItemDetailPlayConunt.setText(PlayConunt.PlayConunts(track.getPlayCount()));

        int timelong = track.getDuration() * 1000;
        String timelongs = mtimelongDateFormat.format(timelong);
        mItemDetailTimeLong.setText(timelongs);
        String updatetime = mSimpleDateFormat.format(track.getUpdatedAt());
        mItemDetailTime.setText(updatetime);

        //设置item的点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    //参数需要有列表和位置
                    mItemClickListener.onItemClick(mDetailData,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailData == null ? 0 : mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        //清空
        mDetailData.clear();
        //设置数据
        mDetailData.addAll(tracks);
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHolser extends RecyclerView.ViewHolder {
        public InnerHolser(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(List<Track> detailData, int position);
    }
}
