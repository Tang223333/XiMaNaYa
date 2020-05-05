package com.example.ximanaya;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.ximanaya.Adapter.MainContentAdapter;
import com.example.ximanaya.Adapter.indicatAdapter;
import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.Interface.IPlayerViewCallBack;
import com.example.ximanaya.Predenter.PlayPresenter;
import com.example.ximanaya.Predenter.RecommendPresenter;
import com.example.ximanaya.View.RoundRectImageView;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

public class MainActivity extends FragmentActivity implements IPlayerViewCallBack {

    private static final String TAG = "MainActivity";
    private MagicIndicator indicat;
    private ViewPager indicat_pager;
    private com.example.ximanaya.Adapter.indicatAdapter indicatAdapter;
    private RoundRectImageView mMainTrackCover;
    private TextView mMainHeadTitle;
    private TextView mMainSubTitle;
    private ImageView mMainPlayControl;
    private PlayPresenter mPlayPresentrer;
    private LinearLayout mMainPlayControlItem;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();

        initPresenter();

    }

    private void initPresenter() {
        mPlayPresentrer = PlayPresenter.getPlayPresentrer();
        mPlayPresentrer.registerViewCallback(this);
    }

    private void initEvent() {
        indicatAdapter.setOnIndicatorTapClickListener(new indicatAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(Integer index) {
                if (indicat_pager != null) {//
                    indicat_pager.setCurrentItem(index,false);
                }
            }
        });

        mMainPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //控制播放
                if (mPlayPresentrer != null) {
                    boolean hasPlayList = mPlayPresentrer.hasPlayList();
                    if (!hasPlayList) {
                        //没有设置过播放列表，就播放默认的第一个推荐专辑
                        playFirstRecommend();
                    } else {
                        if (mPlayPresentrer.isPlaying()) {
                            mPlayPresentrer.pause();
                        } else {
                            mPlayPresentrer.play();
                        }
                    }

                }
            }
        });

        mMainPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到播放器界面
                boolean hasPlayList = mPlayPresentrer.hasPlayList();
                if (!hasPlayList) {
                    playFirstRecommend();
                }
                if (mPlayPresentrer != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                Log.d(TAG, "onClick: " + mPlayPresentrer.isPlaying());
                                if (mPlayPresentrer.hasPlayList()) {
                                    break;
                                }
                            }
                            BaseApplication.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(MainActivity.this, PlayerActivity.class));
                                }
                            });
                        }
                    }).start();

                }
            }
        });

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    //播放第一个推荐内容
    private void playFirstRecommend() {
        RecommendPresenter instance = RecommendPresenter.getInstance();
        List<Album> currentRecommend = instance.getCurrentRecommend();
        if (currentRecommend != null && currentRecommend.size() > 0) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayPresentrer.playByAibumId(albumId);
        }
    }

    private void initView() {
        indicat = (MagicIndicator) findViewById(R.id.indicat);
        indicat_pager = (ViewPager) findViewById(R.id.indicat_pager);
        indicat.setBackgroundColor(this.getColor(R.color.main_color));
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        indicatAdapter = new indicatAdapter(this);
        commonNavigator.setAdapter(indicatAdapter);

        //创建viewpager适配器
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(fragmentManager);
        indicat_pager.setAdapter(mainContentAdapter);

        //设置内容
        indicat.setNavigator(commonNavigator);
        //ViewPager与指示器绑定到一起
        ViewPagerHelper.bind(indicat, indicat_pager);
        //找到相关控件
        mMainTrackCover = (RoundRectImageView) findViewById(R.id.main_track_cover);
        mMainHeadTitle = (TextView) findViewById(R.id.main_head_title);
        mMainSubTitle = (TextView) findViewById(R.id.main_sub_title);
        mMainPlayControl = (ImageView) findViewById(R.id.main_play_control);
        mMainHeadTitle.setSelected(true);
        mMainPlayControlItem = (LinearLayout) findViewById(R.id.main_play_control_item);
        //搜索
        mView = findViewById(R.id.search_btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayPresentrer != null) {
            mPlayPresentrer.unregisterViewCallback(this);
        }
    }

    @Override
    public void onPlayStart() {
        updatePlaControl(true);
    }

    private void updatePlaControl(boolean isplaying) {
        if (mMainPlayControl != null) {
            mMainPlayControl.setImageResource(isplaying ? R.drawable.selector_player_stop : R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayPause() {
        updatePlaControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlaControl(false);
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
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            if (mMainHeadTitle != null) {
                mMainHeadTitle.setText(trackTitle);
            }
            if (mMainSubTitle != null) {
                mMainSubTitle.setText(nickname);
            }
            if (coverUrlMiddle != null) {
                Glide.with(this).load(coverUrlMiddle).into(mMainTrackCover);
            }
        }
    }

    @Override
    public void onUpdateListOrder(boolean isReverse) {

    }
}
