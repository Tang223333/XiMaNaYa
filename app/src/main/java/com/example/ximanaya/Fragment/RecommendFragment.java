package com.example.ximanaya.Fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ximanaya.Adapter.RecommendListAdapter;
import com.example.ximanaya.Base.BaseFragment;
import com.example.ximanaya.DetailActivity;
import com.example.ximanaya.Interface.IrecommendVoiewCallback;
import com.example.ximanaya.Predenter.AlbumDetailPresenter;
import com.example.ximanaya.Predenter.RecommendPresenter;
import com.example.ximanaya.R;
import com.example.ximanaya.Utils.LogUtils;
import com.example.ximanaya.View.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IrecommendVoiewCallback, UILoader.onRetryClickListener, RecommendListAdapter.OnRecommendItemClickListener {

    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView mReconmmendList;
    private RecommendListAdapter recommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader uiLoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        uiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup continer) {
                return createSwiccessView(layoutInflater,continer);
            }
        };

        //获取到逻辑层的对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口注册
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();

        if (uiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) uiLoader.getParent()).removeView(uiLoader);
        }

        uiLoader.setonRetryClickListener(this);
        //返回记得该
        return uiLoader;
    }

    private View createSwiccessView(LayoutInflater layoutInflater, ViewGroup continer) {
        LogUtils.d(TAG, "createSwiccessView: "+continer);
        rootView = layoutInflater.inflate(R.layout.fragment_recommend, continer, false);
        //RecycleView的使用
        //1.找到控件
        mReconmmendList=rootView.findViewById(R.id.reconmmend_list);
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mReconmmendList.setLayoutManager(linearLayoutManager);
        mReconmmendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom=UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        //3.设置适配器
        recommendListAdapter = new RecommendListAdapter();
        mReconmmendList.setAdapter(recommendListAdapter);
        recommendListAdapter.setOnRecommendItemClickListener(this);
        return rootView;
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当我们或去到推荐内容的时候，这个方法就会被调用（成功T）
        //数据获取承购，及可以更新UI
        Log.d(TAG, "onRecommendListLoaded: "+result.size());
        recommendListAdapter.setData(result);
        uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        LogUtils.d(TAG,"onNetworkError");
        uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        LogUtils.d(TAG,"onEmpty");
        uiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        LogUtils.d(TAG,"onLoading");
        uiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消接口的注册，以免内存泄漏
        if (mRecommendPresenter!=null){
            mRecommendPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //表示网络不佳的时候用户点击了重试
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //根据位置拿到数据
        //item被点击了,跳转到详情界面
        Intent intent=new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
