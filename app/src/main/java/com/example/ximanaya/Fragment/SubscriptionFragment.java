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

import com.example.ximanaya.Adapter.AlbumListAdapter;
import com.example.ximanaya.Base.BaseFragment;
import com.example.ximanaya.DetailActivity;
import com.example.ximanaya.Interface.ISubscriptionCallback;
import com.example.ximanaya.Predenter.AlbumDetailPresenter;
import com.example.ximanaya.Predenter.SubscriptionPresenter;
import com.example.ximanaya.R;
import com.example.ximanaya.Utils.Constants;
import com.example.ximanaya.View.ConfirmDialog;
import com.example.ximanaya.View.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.OnAlbumItemClickListener, AlbumListAdapter.OnAlbumLongItemClickListener {

    private SubscriptionPresenter mSubscriptionPresenter;
    private AlbumListAdapter mAlbumListAdapter;
    private Album mCurrentClickAlbum=null;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_subscription,container,false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

                @Override
                protected View getEmptyView() {
                    //创建一个新的EmptyView
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_enpty_view,this,false);
                    TextView textView=emptyView.findViewById(R.id.empty_view_tips_tv);
                    textView.setText(R.string.no_sub_content_tips_text);
                    return emptyView;
                }
            };
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            rootView.addView(mUiLoader);
        }

        return rootView;
    }

    private View createSuccessView() {
        View itemView=LayoutInflater.from(getContext()).inflate(R.layout.item_subscription,null,false);
        TwinklingRefreshLayout twinklingRefreshLayout=itemView.findViewById(R.id.over_scroll_view);
        twinklingRefreshLayout.setEnableRefresh(false);
        twinklingRefreshLayout.setEnableLoadmore(false);
        RecyclerView subListView=itemView.findViewById(R.id.sub_list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        subListView.setLayoutManager(linearLayoutManager);
        subListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom=UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        //设置适配器
        mAlbumListAdapter = new AlbumListAdapter();
        subListView.setAdapter(mAlbumListAdapter);
        mSubscriptionPresenter = SubscriptionPresenter.getSubscriptionPresenter();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscription();
        mAlbumListAdapter.setOnAlbumItemClickListener(this);
        mAlbumListAdapter.setOnAlbumLongItemClickListener(this);
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return itemView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        //给出取消订阅的提示
        if (isSuccess) {
            Toast.makeText(getContext(), R.string.cancel_sub_success, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), R.string.cancel_sub_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        if (albums.size()==0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }else {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }


        //更新数据
        if (mAlbumListAdapter != null) {
//            Collections.reverse(albums);
            mAlbumListAdapter.setData(albums);
        }
    }

    @Override
    public void onSubTooMany() {
        //处理一个就行
        Toast.makeText(getContext(), "订阅个数不能超过"+ Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSubscriptionPresenter.unregisterViewCallback(this);
        mAlbumListAdapter.setOnAlbumItemClickListener(null);
        mAlbumListAdapter.setOnAlbumLongItemClickListener(null);
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //根据位置拿到数据
        //item被点击了,跳转到详情界面
        Intent intent=new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Album album) {
        mCurrentClickAlbum =album;
        //订阅的item被长按
//        Toast.makeText(getContext(), "长按了"+album.getAlbumTitle(), Toast.LENGTH_SHORT).show();
        ConfirmDialog confirmDialog=new ConfirmDialog(getContext());
        confirmDialog.setOnDialogActionClickListener(new ConfirmDialog.OnDialogActionClickListener() {
            @Override
            public void onCancelSubClick() {
                //取消订阅
                if (mCurrentClickAlbum != null&&mSubscriptionPresenter!=null) {
                    mSubscriptionPresenter.deleteSubscription(mCurrentClickAlbum);
                }
            }

            @Override
            public void onGiveUpClick() {
                Toast.makeText(getContext(), "我再想想", Toast.LENGTH_SHORT).show();
            }
        });
        confirmDialog.show();
    }
}
