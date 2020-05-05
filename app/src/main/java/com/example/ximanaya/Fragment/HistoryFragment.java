package com.example.ximanaya.Fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ximanaya.Adapter.TrackListAdapter;
import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.Base.BaseFragment;
import com.example.ximanaya.Interface.IHistoryCallback;
import com.example.ximanaya.PlayerActivity;
import com.example.ximanaya.Predenter.HistoryPresenter;
import com.example.ximanaya.Predenter.PlayPresenter;
import com.example.ximanaya.R;
import com.example.ximanaya.View.ConfirmCheckBoxDialog;
import com.example.ximanaya.View.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class HistoryFragment extends BaseFragment implements IHistoryCallback {

    private UILoader mUiLoader;
    private TrackListAdapter mTrackListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private Track mCurrentClickHistory=null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history,container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEmptyView() {
                    View enptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_enpty_view, this, false);
                    TextView tips=enptyView.findViewById(R.id.empty_view_tips_tv);
                    tips.setText("没有历史记录哦!");
                    return enptyView;
                }
            };
        }else {
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        //MistoryPresenter
        mHistoryPresenter = HistoryPresenter.getHistoryPresenter();
        mHistoryPresenter.registerViewCallback(this);
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        mHistoryPresenter.listHistories();
        rootView.addView(mUiLoader);
        return rootView;
    }

    private View createSuccessView(ViewGroup container) {
        View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history, null, false);
        TwinklingRefreshLayout twinklingRefreshLayout=successView.findViewById(R.id.over_scroll_view);
        twinklingRefreshLayout.setEnableLoadmore(false);
        twinklingRefreshLayout.setEnableRefresh(false);
        RecyclerView historyList=successView.findViewById(R.id.history_list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(container.getContext());
        historyList.setLayoutManager(linearLayoutManager);
        //设置是适配器
        mTrackListAdapter = new TrackListAdapter();
        historyList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        mTrackListAdapter.setItemClickListener(new TrackListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(List<Track> detailData, int position) {
                //设置播放器的数据
                PlayPresenter playPresenter=PlayPresenter.getPlayPresentrer();
                playPresenter.setPlayList(detailData,position);

                Intent intent = new Intent(getContext(), PlayerActivity.class);
                startActivity(intent);
            }
        });
        mTrackListAdapter.setItemLongClickListener(new TrackListAdapter.ItemLongClickListener() {
            @Override
            public void onItemLongClick(Track track) {
                mCurrentClickHistory =track;
                //删除历史
//                Toast.makeText(getContext(), "历史记录长按..."+track.getTrackTitle(), Toast.LENGTH_SHORT).show();
                ConfirmCheckBoxDialog dialog=new ConfirmCheckBoxDialog(getContext());
                dialog.show();
                dialog.setOnDialogActionClickListener(new ConfirmCheckBoxDialog.OnDialogActionClickListener() {
                    @Override
                    public void onCancel() {
                        //没事做
                    }

                    @Override
                    public void onConfirm(boolean isCheck) {
                        if (mHistoryPresenter != null&&mCurrentClickHistory!=null) {
                            if (!isCheck) {
                                mHistoryPresenter.delHistory(mCurrentClickHistory);
                            }else {
                                mHistoryPresenter.cleanHistories();
                            }
                        }
                    }
                });
            }
        });
        historyList.setAdapter(mTrackListAdapter);
        return successView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
        if (mTrackListAdapter != null) {
            //更新数据
            mTrackListAdapter.setData(tracks);
        }
        if (mUiLoader != null) {
            if (tracks==null||tracks.size()==0) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }else {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }
}
