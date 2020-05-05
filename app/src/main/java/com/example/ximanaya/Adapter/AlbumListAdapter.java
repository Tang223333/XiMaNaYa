package com.example.ximanaya.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ximanaya.R;
import com.example.ximanaya.Utils.PlayConunt;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private static final String TAG = "AlbumListAdapter";
    private OnAlbumItemClickListener mRecommendItemClickListener=null;

    private List<Album> mData=new ArrayList<>();
    private OnAlbumLongItemClickListener mOnAlbumLongItemClickListener=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //找到View
        View ItemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHolder(ItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //封装数据
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecommendItemClickListener!=null){
                    int clickPosition=(int) v.getTag();
                    mRecommendItemClickListener.onItemClick(clickPosition,mData.get(clickPosition));
                }
                Log.d(TAG, "onClick: itemView  -->"+v.getTag());
            }
        });
        holder.setData(mData.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnAlbumLongItemClickListener != null) {
                    int clickPosition=(int) v.getTag();
                    mOnAlbumLongItemClickListener.onItemLongClick(mData.get(clickPosition));
                }
                //true 表示消费掉该事件
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        //返回个数
        return mData==null?0:mData.size();
    }

    public void setData(List<Album> albumList) {
        if (mData!=null){
            mData.clear();
            mData.addAll(albumList);
        }
        notifyDataSetChanged();
    }

    public int getDataSise() {
        return mData.size();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到控件,设置数据
            //专辑封面
            ImageView mAlbumCover = (ImageView) itemView.findViewById(R.id.album_cover);
            //Title
            TextView mAlbumTitelTv = (TextView) itemView.findViewById(R.id.album_titel_tv);
            //描述
            TextView mAlbumDescriptionTv = (TextView) itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView mAlbumPlayConunt = (TextView) itemView.findViewById(R.id.album_play_conunt);
            //专辑内容数量
            TextView mAlbumContentSize = (TextView) itemView.findViewById(R.id.album_content_size);

            mAlbumTitelTv.setText(album.getAlbumTitle());
            mAlbumDescriptionTv.setText(album.getAlbumIntro());
            mAlbumPlayConunt.setText(""+ PlayConunt.PlayConunts(album.getPlayCount()));
            mAlbumContentSize.setText(""+album.getIncludeTrackCount());

            Glide.with(itemView.getContext()).load(album.getCoverUrlLarge()).error(R.mipmap.content_empty).into(mAlbumCover);
        }
    }

    public void setOnAlbumItemClickListener(OnAlbumItemClickListener listener){
        this.mRecommendItemClickListener=listener;
    }

    public interface OnAlbumItemClickListener {
        void onItemClick(int position, Album album);
    }

    public void setOnAlbumLongItemClickListener(OnAlbumLongItemClickListener onAlbumLongItemClickListener){
        mOnAlbumLongItemClickListener =onAlbumLongItemClickListener;
    }

    public interface OnAlbumLongItemClickListener{
        void onItemLongClick(Album album);
    }
}
