package com.example.ximanaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.Interface.IAlbumDetaViewCallback;
import com.example.ximanaya.Interface.IPlayerViewCallBack;
import com.example.ximanaya.Predenter.AlbumDetailPresenter;
import com.example.ximanaya.Predenter.PlayPresentrer;
import com.example.ximanaya.Utils.ImageBlur;
import com.example.ximanaya.Utils.LogUtils;
import com.example.ximanaya.View.RoundRectImageView;
import com.example.ximanaya.View.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetaViewCallback, UILoader.onRetryClickListener, DetailListAdapter.ItemClickListener, IPlayerViewCallBack {
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
    private ImageView mDetailPlayControlIv;
    private TextView mDetailPlayControlTv;
    private PlayPresentrer mPlayPresentrer;
    private LinearLayout mDetailPlayControl;
    private List<Track> mCurrentTracks=null;
    private final static int DEFAULT_PLAY_INDEX=0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mTrackTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acrivity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        initView();
        //播放专辑详情的Presenter
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallback(this);
        //播放器的Presenter
        mPlayPresentrer = PlayPresentrer.getPlayPresentrer();
        mPlayPresentrer.registerViewCallback(this);
        updatePlaySate(mPlayPresentrer.isPlaying());
        initListener();
    }

    private void initListener() {
        if (mDetailPlayControl != null) {
            mDetailPlayControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlayPresentrer != null) {
                        //判断播放器是否有播放列表
                        //todo:
                        boolean has = mPlayPresentrer.hasPlayList();
                        if (has){
                            //控制播放器的状态
                            handlePlayControl();
                        }else {
                            handleNoPlayList();
                        }

                    }

                }
            });
        }
    }

    //当播放器内没有播放内容，我们要进行处理
    private void handleNoPlayList() {
        if (mPlayPresentrer != null) {
            mPlayPresentrer.setPlayList(mCurrentTracks,DEFAULT_PLAY_INDEX);
        }
    }

    private void handlePlayControl() {
        if (mPlayPresentrer.isPlaying()) {
            //正在播放，就暂停
            mPlayPresentrer.pause();
        }else {
            mPlayPresentrer.play();
        }
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
        //播放控制图标与文字与容器
        mDetailPlayControl = (LinearLayout) findViewById(R.id.detail_play_control);
        mDetailPlayControlIv = (ImageView) findViewById(R.id.detail_play_control_iv);
        mDetailPlayControlTv = (TextView) findViewById(R.id.detail_play_control_tv);
        mDetailPlayControlTv.setSelected(true);

    }

    public boolean mIsLoaderMore=false;

    private View createSussessView(ViewGroup container) {
        mDetailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_item, container, false);
        mAlbumDetailList = (RecyclerView) mDetailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = (TwinklingRefreshLayout) mDetailListView.findViewById(R.id.refresh_layout);

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
        BezierLayout bezierLayout=new BezierLayout(this);
        mRefreshLayout.setHeaderView(bezierLayout);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                Toast.makeText(DetailActivity.this, "开始下拉刷新", Toast.LENGTH_SHORT).show();
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishRefreshing();
                    }
                },2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //去加载更多类容
                mIsLoaderMore=true;
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                }
            }
        });
        return mDetailListView;
    }

    @Override
    public void onDetailListloaded(List<Track> tracks) {
        if (mIsLoaderMore && mRefreshLayout != null) {
            mIsLoaderMore=false;
            mRefreshLayout.finishLoadmore();
        }
        this.mCurrentTracks=tracks;
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
    public void onLoaderMoreFinished(int size) {
        if (size>0) {
            Toast.makeText(this, "成功加载"+size+"条", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "没有更多节目", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

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
        if (mPlayPresentrer != null) {
            mPlayPresentrer.setPlayList(detailData, position);
        }

        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlbumDetailPresenter.getInstance().unregisterViewCallback(this);
    }

    @Override
    public void onPlayStart() {
        //修改图标为展厅的，文字修改为正在播放
        updatePlaySate(true);
    }

    @Override
    public void onPlayPause() {
        //修改图标为展厅的，文字修改为已暂停
        updatePlaySate(false);
    }

    @Override
    public void onPlayStop() {
        //修改图标为展厅的，文字修改为已暂停
        updatePlaySate(false);
    }

    private void updatePlaySate(boolean playing) {
        if (playing){
            if (mDetailPlayControlTv != null&&mDetailPlayControlIv!=null) {
                mDetailPlayControlIv.setImageResource(R.drawable.selector_play_control_stop);
                if (!TextUtils.isEmpty(mTrackTitle)) {
                    mDetailPlayControlTv.setText(mTrackTitle);
                }
            }
        }else {
            if (mDetailPlayControlTv != null&&mDetailPlayControlIv!=null) {
                mDetailPlayControlIv.setImageResource(R.drawable.selector_play_control_play);
                mDetailPlayControlTv.setText(R.string.click_play_tips_text);
            }
        }
    }

    @Override
    public void onPlayError(int i) {

    }

    @Override
    public void onNextPlay(Track Track) {

    }

    @Override
    public void onProPlay(Track Track) {

    }

    @Override
    public void onListLoading(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFubusged() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            mTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mTrackTitle)&&mDetailPlayControlTv!=null){
                mDetailPlayControlTv.setText(mTrackTitle);
            }
        }
    }

    @Override
    public void onUpdateListOrder(boolean isReverse) {

    }
}
