package com.example.ximanaya.Adapter;

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

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.ximanaya.Utils.PlayConunt.PlayConunts;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {

    private List<Album> mData=new ArrayList<>();

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //找到View
        View ItemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHolder(ItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //封装数据
        holder.itemView.setTag(position);
        holder.setData(mData.get(position));
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

            Glide.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(mAlbumCover);
        }
    }
}
