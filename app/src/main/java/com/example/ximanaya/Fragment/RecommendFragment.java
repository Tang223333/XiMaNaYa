package com.example.ximanaya.Fragment;

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
import com.example.ximanaya.Interface.IrecommendVoiewCallback;
import com.example.ximanaya.Predenter.RecommendPresenter;
import com.example.ximanaya.R;
import com.example.ximanaya.Utils.Constants;
import com.example.ximanaya.Utils.LogUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment implements IrecommendVoiewCallback {

    private static final String TAG = "RecommendFragment";
    private View rootView;
    private RecyclerView mReconmmendList;
    private RecommendListAdapter recommendListAdapter;
    private RecommendPresenter mRecommendPresenter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        rootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
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

        //获取到逻辑层的对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口注册
        mRecommendPresenter.registerViewCallBack(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();

        return rootView;
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当我们或去到推荐内容的时候，这个方法就会被调用（成功T）
        //数据获取承购，及可以更新UI
        Log.d(TAG, "onRecommendListLoaded: "+result.size());
        recommendListAdapter.setData(result);
    }

    @Override
    public void onLoaderMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消接口的注册，以免内存泄漏
        if (mRecommendPresenter!=null){
            mRecommendPresenter.unregisterViewCallBack(this);
        }
    }
}
