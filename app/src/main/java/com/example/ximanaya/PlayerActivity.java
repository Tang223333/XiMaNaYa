package com.example.ximanaya;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.ximanaya.Adapter.PlayerTrackPagerAdapter;
import com.example.ximanaya.Base.BaseActivity;
import com.example.ximanaya.Interface.IPlayerViewCallBack;
import com.example.ximanaya.Predenter.PlayPresenter;
import com.example.ximanaya.Utils.LogUtils;
import com.example.ximanaya.View.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerViewCallBack, ViewPager.OnPageChangeListener {

    private static final String TAG = "PlayerActivity";
    private ImageView mPlayOrPaussBtn;
    private PlayPresenter mPlayPresentrer;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("HH:mm:ss");
    private TextView mTrackDuration;
    private TextView mCurrentPosition;
    private SeekBar mTrackSeekBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchFrogressBar = false;
    private ImageView mPlayerPre;
    private ImageView mPlayerNext;
    private TextView mTrackTitle;
    private Track mTrack;
    private ViewPager mTrackPagerView;
    private PlayerTrackPagerAdapter mPlayerTrackPagerAdapter;
    private boolean mIsUserSlidePager = false;
    private int mPlayIndex;
    private ImageView mPlayerModeSwitchBtn;
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();

    private XmPlayListControl.PlayMode mCurrentMode = PLAY_MODEL_LIST;

    public final int BG_ANIMATION_DURATION=300;
    public final float BG_ANIMATION_ALPHE1=0.7f;
    public final float BG_ANIMATION_ALPHE2=1.0f;

    //1。默认为：PLAY_MODEL_LIST列表播放
    //2.PLAY_MODEL_LIST_LOOP列表循环
    //3.PLAY_MODEL_RANDOM 随机播放
    //4.PLAY_MODEL_SINGLE_LOOP 单曲循环播放
    static {
        sPlayModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    private ImageView mPlayerList;
    private SobPopWindow mSobPopWindow;
    private ValueAnimator mAnimator;
    private ValueAnimator mAnimation2;

    private boolean mTestOrder=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        //测试播放
        mPlayPresentrer = PlayPresenter.getPlayPresentrer();
        mPlayPresentrer.registerViewCallback(this);
        //在界面初始化以后在获取数据
        if (mPlayPresentrer != null) {
            mPlayPresentrer.getPlayList();
            LogUtils.d(TAG, "onClick: " + mPlayPresentrer.isPlaying());
            if (mPlayPresentrer.isPlaying()) {
                mPlayOrPaussBtn.setImageResource(R.drawable.selector_player_stop);
            } else {
                mPlayOrPaussBtn.setImageResource(R.drawable.selector_player_play);
            }
        }
        initEvent();
        initBgAnimation();
    }

    private void initBgAnimation() {
        mAnimator = ValueAnimator.ofFloat(BG_ANIMATION_ALPHE2,BG_ANIMATION_ALPHE1);
        mAnimator.setDuration(BG_ANIMATION_DURATION);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value=(float)animation.getAnimatedValue();
                updateBgAlpha(value);
            }
        });

        mAnimation2 = ValueAnimator.ofFloat(BG_ANIMATION_ALPHE1,BG_ANIMATION_ALPHE2);
        mAnimation2.setDuration(BG_ANIMATION_DURATION);
        mAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value=(float)animation.getAnimatedValue();
                updateBgAlpha(value);
            }
        });
    }


    //给控件设置相关的事件
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mPlayOrPaussBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果正在播放就暂停，是暂停就播放
                if (mPlayPresentrer.isPlaying()) {
                    mPlayPresentrer.pause();
                } else {
                    mPlayPresentrer.play();
                }
            }
        });

        mTrackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchFrogressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchFrogressBar = false;
                //手离开拖动条是更新进度
                if (mPlayPresentrer != null) {
                    mPlayPresentrer.seekTe(mCurrentProgress);
                }
            }
        });

        mPlayerPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayPresentrer != null) {
                    mPlayPresentrer.playPre();
                }
            }
        });

        mPlayerNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayPresentrer.playNext();
            }
        });

        mTrackPagerView.addOnPageChangeListener(this);

        mTrackPagerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                        break;
                }
                return false;
            }
        });

        mPlayerModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlayMode();
            }
        });

        mPlayerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                //修改背景的透明度渐变
                mAnimator.start();
            }
        });

        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //pop窗体小时以后，恢复
               mAnimation2.start();
            }
        });

        mSobPopWindow.onPlayListItemClickListener(new SobPopWindow.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //播放列表item被点击
                if (mPlayPresentrer != null) {
                    mPlayPresentrer.playByIndex(position);
                }
            }
        });

        mSobPopWindow.setPlayListplayModeClickListrener(new SobPopWindow.PlayListaActionListrener() {
            @Override
            public void onPlayModeClick() {
                //切换播放模式模式
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                //点击了切换顺序或逆序
                if (mPlayPresentrer != null) {
                    mPlayPresentrer.reversePlayList();
                }
            }
        });
    }

    private void switchPlayMode() {
        //处理播放模式的切换
        //更具当前的mode获取下一个mode
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
        //修改播放模式
        if (mPlayPresentrer != null) {
            mPlayPresentrer.swichPlayMode(playMode);
        }
    }

    public void updateBgAlpha(float alpha){
        Window window = getWindow();
        WindowManager.LayoutParams attributes=window.getAttributes();
        attributes.alpha= alpha;
        window.setAttributes(attributes);
    }


    //找到控件
    private void initView() {
        mPlayOrPaussBtn = (ImageView) findViewById(R.id.play_or_pauss_btn);
        mTrackDuration = (TextView) findViewById(R.id.track_duration);
        mCurrentPosition = (TextView) findViewById(R.id.current_position);
        mTrackSeekBar = (SeekBar) findViewById(R.id.track_seek_bar);
        mPlayerPre = (ImageView) findViewById(R.id.player_pre);
        mPlayerNext = (ImageView) findViewById(R.id.player_next);
        mTrackTitle = (TextView) findViewById(R.id.track_title);
        if (mTrack != null) {
            mTrackTitle.setText(mTrack.getTrackTitle());
        }
        mTrackPagerView = (ViewPager) findViewById(R.id.track_pager_view);
        //创建适配器
        mPlayerTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTrackPagerView.setAdapter(mPlayerTrackPagerAdapter);
        mPlayerModeSwitchBtn = (ImageView) findViewById(R.id.player_mode_switch_btn);
        //播放列表
        mPlayerList = (ImageView) findViewById(R.id.player_list);
        mSobPopWindow = new SobPopWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayPresentrer != null) {
            mPlayPresentrer.unregisterViewCallback(this);
            mPlayPresentrer = null;
        }
    }

    @Override
    public void onPlayStart() {
        //开始播放,修改UI层暂停的按钮
        if (mPlayOrPaussBtn != null) {
            mPlayOrPaussBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        if (mPlayOrPaussBtn != null) {
            mPlayOrPaussBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        if (mPlayOrPaussBtn != null) {
            mPlayOrPaussBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayError(int i) {
        if (i == 0) {
            Toast.makeText(this, "当前播放是第一条", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "当前是最后一条", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNextPlay(Track track) {
    }

    @Override
    public void onProPlay(Track track) {

    }

    @Override
    public void onListLoading(List<Track> list) {
//        LogUtils.d(TAG, "onListLoading: list->"+list);
        //把数据放在适配器中
        if (mPlayerTrackPagerAdapter != null) {
            mPlayerTrackPagerAdapter.setData(list);
            mTrackPagerView.setCurrentItem(mPlayIndex);
        }
        //数据会俩以后，也要给节目列表一份
        if (mSobPopWindow != null) {
            mSobPopWindow.setListData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式，并且修改UI
        mCurrentMode = playMode;
        int image;
        int txt=R.string.play_mode_list_play_text;
        switch (mCurrentMode) {
            case PLAY_MODEL_LIST:
                image=R.drawable.selector_player_list;
                txt=R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_RANDOM:
                image=R.drawable.selector_player_random;
                txt=R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                image=R.drawable.selector_player_list_loop;
                txt=R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                image=R.drawable.selector_player_single_loop;
                txt=R.string.play_mode_single_play_text;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mCurrentMode);
        }
        if (mPlayerModeSwitchBtn != null) {
            mPlayerModeSwitchBtn.setImageResource(image);
        }
        if (mSobPopWindow != null) {
            mSobPopWindow.setImageAndString(image,getString(txt));
        }

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {
        mTrackSeekBar.setMax(total);
        //更新播放进度，更新进度条
        String totalDuration;
        String currentPostion;
        if (total > 1000 * 60 * 60) {
            totalDuration = mHourFormat.format(new Date(total));
        } else {
            totalDuration = mMinFormat.format(new Date(total));
        }
        if (currentProgress > 1000 * 60 * 60) {
            currentPostion = mHourFormat.format(new Date(currentProgress));
        } else {
            currentPostion = mMinFormat.format(new Date(currentProgress));
        }
        //更新当前的时间
        if (mTrackDuration != null) {
            mTrackDuration.setText(totalDuration);
        }
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPostion);
        }

        //计算当前的进度
        if (!mIsUserTouchFrogressBar) {
            mTrackSeekBar.setProgress(currentProgress);
        }
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
            this.mTrack = track;
            Log.d(TAG, "onTrackTitleUpdate: " + track.getTrackTitle());
            if (mTrackTitle != null) {
                mTrackTitle.setText(track.getTrackTitle());
            }
        }
        //当前积木改变的时候，我们就获取到当前播放器的位置
        //当前的节目改变以后，要修改页面的图片
        mPlayIndex = playIndex;
        if (mTrackPagerView != null) {
            mTrackPagerView.setCurrentItem(mPlayIndex, true);
        }
        if (mSobPopWindow != null) {
            mSobPopWindow.setCurrentPlayPosition(playIndex);
        }
    }

    public void onUpdateListOrder(boolean isReverse) {
        mSobPopWindow.setUpdateOrder(isReverse);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        LogUtils.d(TAG, "onPageSelected: position -> " + position);
        //当页面选中的时候，就去切换播放的内容
        if (mPlayPresentrer != null && mIsUserSlidePager) {
            mPlayPresentrer.playByIndex(position);
        }
        mIsUserSlidePager = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
