package com.example.ximanaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ximanaya.Adapter.DetailListAdapter;
import com.example.ximanaya.Base.BaseActivity;
import com.example.ximanaya.Interface.IAlbumDetaViewCallback;
import com.example.ximanaya.Predenter.AlbumDetailPresenter;
import com.example.ximanaya.Predenter.PlayPresentrer;
import com.example.ximanaya.Utils.ImageBlur;
import com.example.ximanaya.Utils.LogUtils;
import com.example.ximanaya.View.RoundRectImageView;
import com.example.ximanaya.View.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetaViewCallback, UILoader.onRetryClickListener, DetailListAdapter.ItemClickListener {
    private static final String TAG = "DetailActivity";
    private ImageView mIvTargeCover;
    private RoundRectImageView mVivSmallCaver;
    private TextView mTvAlbumTitle;
    private TextView mTvAlbumAuthor;
    private AlbumDetailPresenter albumDetailPresenter;
    private int mCurrenrPage = 1;
    private RecyclerView mAlbumDetailList;
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mAlbumDetailContailer;
    private UILoader mUiLoader;
    private View mDetailListView;
    private long mCurretId = -1;
    private AlbumDetailPresenter mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acrivity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        initView();
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallback(this);
    }

    private void initView() {
        mAlbumDetailContailer = (FrameLayout) findViewById(R.id.album_detail_contailer);
        //
        mUiLoader = new UILoader(this) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSussessView(container);
            }
        };
        mUiLoader.setonRetryClickListener(this);
        if (mUiLoader != null) {
            mAlbumDetailContailer.removeAllViews();
            mAlbumDetailContailer.addView(mUiLoader);
        }

        mIvTargeCover = (ImageView) findViewById(R.id.iv_targe_cover);
        mVivSmallCaver = (RoundRectImageView) findViewById(R.id.viv_small_caver);
        mTvAlbumTitle = (TextView) findViewById(R.id.tv_album_title);
        mTvAlbumAuthor = (TextView) findViewById(R.id.tv_album_author);
    }

    private View createSussessView(ViewGroup container) {
        mDetailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_item, container, false);
        mAlbumDetailList = (RecyclerView) mDetailListView.findViewById(R.id.album_detail_list);

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mAlbumDetailList.setLayoutManager(linearLayoutManager);
        //设置适配器
        mDetailListAdapter = new DetailListAdapter();
        mAlbumDetailList.setAdapter(mDetailListAdapter);
        //设置item的间距（上下）
        mAlbumDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        mDetailListAdapter.setItemClickListener(this);
        return mDetailListView;
    }

    @Override
    public void onDetailListloaded(List<Track> tracks) {
        //判断结果，更具结果显示UI
        if (tracks == null || tracks.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        } else {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
                //更新/设置数据
                mDetailListAdapter.setData(tracks);
            }
        }
    }

    @Override
    public void onAlbumLoaded(final Album album) {

        mCurretId = album.getId();
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAIbumDetail((int) mCurretId, mCurrenrPage);
        }

        //显示loading状态
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }

        Log.d(TAG, "onAlbumLoaded: " + album.getAlbumTitle());
        if (mTvAlbumTitle != null) {
            mTvAlbumTitle.setText(album.getAlbumTitle());
        }
        if (mTvAlbumAuthor != null) {
            mTvAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }


        //高视模糊（毛玻璃效果）

        if (mIvTargeCover != null && null != mIvTargeCover) {
            Glide.with(this).load(album.getCoverUrlLarge()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    mIvTargeCover.setImageResource(R.mipmap.content_empty);
                    return true;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    mIvTargeCover.setImageDrawable(resource);
                    ImageBlur.makeBlur(mIvTargeCover, DetailActivity.this);
                    return true;
                }
            }).into(mIvTargeCover);
        }

        if (mVivSmallCaver != null) {
            Glide.with(this).load(album.getCoverUrlLarge()).into(mVivSmallCaver);
        }
    }

    @Override
    public void onNotworkError(int errorCod, String errorMag) {
        LogUtils.d(TAG, "onNotworkError: errorCod -- >" + errorCod);
        LogUtils.d(TAG, "onNotworkError: errorMsg -- >" + errorMag);
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onRetryClick() {
        if (mAlbumDetailPresenter != null) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
            }
            mAlbumDetailPresenter.getAIbumDetail((int) mCurretId, mCurrenrPage);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置播放器的数据
        PlayPresentrer playPresentrer = PlayPresentrer.getPlayPresentrer();
        playPresentrer.setPlayList(detailData, position);

        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlbumDetailPresenter.getInstance().unregisterViewCallback(this);
    }
}
