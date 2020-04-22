package com.example.ximanaya.View;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ximanaya.Adapter.PlayListAdapter;
import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public class SobPopWindow extends PopupWindow {
    private RecyclerView mPlayListRv;
    private TextView mPlayListCloseBtn;
    private final View mPopView;
    private PlayListAdapter mPlayListAdapter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mPlayModeContainer;
    private PlayListaActionListrener mPlayListplayModeClickListrener = null;
    private View mPlayOrderContainer;
    private TextView mPlayOrderTv;
    private ImageView mMPlayOrderIv;

    public SobPopWindow() {
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //这里要注意，setOutsideTouchable之前，先要设置setBackgroundDrawable,
        //否则点击外部无法关闭pop
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);

        //载进来view
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null, false);
        //设置内容
        setContentView(mPopView);

        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    private void initView() {
        mPlayListCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        //找到控件
        mPlayListRv = mPopView.findViewById(R.id.play_list_rv);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mPlayListRv.setLayoutManager(linearLayoutManager);
        //设置适配器
        mPlayListAdapter = new PlayListAdapter();
        mPlayListRv.setAdapter(mPlayListAdapter);
        //播放模式相关
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_mode_container);
        mPlayModeTv = mPopView.findViewById(R.id.play_list_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_mode_iv);

        mPlayOrderContainer = mPopView.findViewById(R.id.play_list_order_container);
        mPlayOrderTv = mPopView.findViewById(R.id.play_list_order_tv);
        mMPlayOrderIv = mPopView.findViewById(R.id.play_list_order_iv);

    }

    private void initEvent() {
        //点击关闭以后，窗口消失
        mPlayListCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });

        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                if (mPlayListplayModeClickListrener != null) {
                    mPlayListplayModeClickListrener.onPlayModeClick();
                }
            }
        });

        mPlayOrderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放列表为顺序或逆序
                if (mPlayListplayModeClickListrener != null) {
                    mPlayListplayModeClickListrener.onOrderClick();
                }
            }
        });
    }

    public void setListData(List<Track> data) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(data);
        }
    }

    public void setImageAndString(int image, String string) {
        if (mPlayModeIv != null) {
            mPlayModeIv.setImageResource(image);
        }
        if (mPlayModeTv != null) {
            mPlayModeTv.setText(string);
        }
    }

    //更新列表顺序或逆序
    public void setUpdateOrder(boolean isOrder){
        if (isOrder) {
            mMPlayOrderIv.setImageResource(R.drawable.selector_player_descending2);
            mPlayOrderTv.setText(BaseApplication.getAppContext().getResources().getString(R.string.play_order_false));
        }else {
            mMPlayOrderIv.setImageResource(R.drawable.selector_player_descending);
            mPlayOrderTv.setText(BaseApplication.getAppContext().getResources().getString(R.string.play_order_true));

        }
    }

    public void setCurrentPlayPosition(int position) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlayPosition(position);
            mPlayListRv.scrollToPosition(position);
        }
    }

    public void onPlayListItemClickListener(PlayListItemClickListener listener) {
        mPlayListAdapter.setOnItemClickListener(listener);
    }

    public interface PlayListItemClickListener {
        void onItemClick(int position);
    }

    public void setPlayListplayModeClickListrener(PlayListaActionListrener listrener) {
        mPlayListplayModeClickListrener = listrener;
    }

    public interface PlayListaActionListrener {
        //播放模式改变
        void onPlayModeClick();

        //播放循序或逆序改变
        void onOrderClick();
    }
}
