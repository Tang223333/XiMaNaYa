package com.example.ximanaya.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.R;
import com.example.ximanaya.Utils.LogUtils;

public abstract class UILoader extends FrameLayout {

    private static final String TAG = "UILoader";
    private View loaingView;
    private View mSuccessView;
    private View mNetWorkErrorView;
    private View mEnptyView;
    LayoutInflater layoutInflater=LayoutInflater.from(getContext());
    private onRetryClickListener mOnRetryClickListener=null;

    public enum UIStatus {
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }

    public UIStatus mCurrenStatus=UIStatus.NONE;

    public UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        init();
    }

    public void updateStatus(UIStatus uiStatus){
        mCurrenStatus=uiStatus;
        LogUtils.d(TAG,""+uiStatus);
        //更新UI一定要在主线程上
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                swicUICurrentStatus();
            }
        });
    }

    /**
     * 初始化UI
     */
    private void init() {
        swicUICurrentStatus();
    }

    private void swicUICurrentStatus() {
        //加载中
        if (loaingView == null) {
            loaingView = getLoadingView();
            addView(loaingView);
        }
        //是否可见
        loaingView.setVisibility(mCurrenStatus==UIStatus.LOADING?VISIBLE:GONE);

        //成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        //是否可见
        mSuccessView.setVisibility(mCurrenStatus==UIStatus.SUCCESS?VISIBLE:GONE);

        //网络错误
        if (mNetWorkErrorView == null) {
            mNetWorkErrorView = getNetWorkErrorView();
            addView(mNetWorkErrorView);
        }
        //是否可见
        mNetWorkErrorView.setVisibility(mCurrenStatus==UIStatus.NETWORK_ERROR?VISIBLE:GONE);

        //数据为空
        if (mEnptyView == null) {
            mEnptyView = getEmptyView();
            addView(mEnptyView);
        }
        //是否可见
        mEnptyView.setVisibility(mCurrenStatus==UIStatus.EMPTY?VISIBLE:GONE);

    }

    protected View getNetWorkErrorView() {
        View nerworkErrorView=layoutInflater.inflate(R.layout.fragment_network_error_view,this,false);
        nerworkErrorView.findViewById(R.id.nerwork_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRetryClickListener != null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });
        return nerworkErrorView;
    }

    protected View getEmptyView(){
        return layoutInflater.inflate(R.layout.fragment_enpty_view,this,false);
    }

    protected abstract View getSuccessView(ViewGroup container);

    protected View getLoadingView() {
        return layoutInflater.inflate(R.layout.fragment_loading_view,null,false);
    }

    public void setonRetryClickListener(onRetryClickListener listener){
        this.mOnRetryClickListener=listener;
    }

    public interface onRetryClickListener{
        void onRetryClick();
    }
}
