package com.example.ximanaya;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ximanaya.Adapter.AlbumListAdapter;
import com.example.ximanaya.Adapter.SearchRecommendAdapter;
import com.example.ximanaya.Base.BaseActivity;
import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.Interface.ISearchCallback;
import com.example.ximanaya.Predenter.AlbumDetailPresenter;
import com.example.ximanaya.Predenter.SearchPresenter;
import com.example.ximanaya.Utils.Constants;
import com.example.ximanaya.Utils.LogUtils;
import com.example.ximanaya.View.FlowTextLayout;
import com.example.ximanaya.View.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback {

    private static final String TAG = "SearchActivity";
    private FrameLayout mSearchContainer;
    private ImageView mSearchBack;
    private EditText mSearchInput;
    private TextView mSearchBtn;
    private SearchPresenter mSearchPresenter;
    private UILoader mUILoader;
    private RecyclerView mResultListView;
    private AlbumListAdapter mAlbumListAdapter;
    private FlowTextLayout mFlowTextLayout;
    private InputMethodManager mInputMethodManager;
    private ImageView mSearchInputDetele;
    private static final int TIME_SHOW_IMM=500;
    private List<String> mHotWords;
    private RecyclerView mSearchRecommendList;
    private SearchRecommendAdapter mSearchRecommendAdapter;
    private TwinklingRefreshLayout mSearchResultRefreshLayout;
    private boolean mNeedSuggestWords=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresentrer();
    }

    private void initPresentrer() {
        mInputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        mSearchPresenter = SearchPresenter.getSearchPresenter();
        //注册ui与新的接口
        mSearchPresenter.registerViewCallback(this);
        //去拿热词
        mSearchPresenter.getHotWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            //干掉ui更新的接口
            mSearchPresenter.unregisterViewCallback(this);
            mSearchPresenter = null;
        }
    }

    private void initEvent() {
        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                mNeedSuggestWords=false;
                Log.d(TAG, "onTextChanged: "+mNeedSuggestWords);
                switch2Search(text);
            }
        });

        mUILoader.setonRetryClickListener(new UILoader.onRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    if (mUILoader != null) {
                        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                    }
                }
            }
        });

        mSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去调用搜索逻辑
                String keyWord = mSearchInput.getText().toString().trim();
                switch2Search(keyWord);
            }
        });

        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mSearchInputDetele.setVisibility(View.GONE);
                    mSearchPresenter.getHotWord();
                }else {
                    mSearchInputDetele.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onTextChanged: "+mNeedSuggestWords);
                    if (mNeedSuggestWords) {
                        //触发联想查询
                        getSuggestWord(s.toString());
                    }else {
                        mNeedSuggestWords=true;
                    }
                 }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (mSearchRecommendAdapter != null) {
            mSearchRecommendAdapter.setItemClickListener(new SearchRecommendAdapter.ItemClickListener() {
                @Override
                public void onItemClick(String keyWord) {
                    mNeedSuggestWords=false;
                    Log.d(TAG, "onTextChanged: "+mNeedSuggestWords);
                    //推荐热词的点击
                    switch2Search(keyWord);
                    //不需要相关的联想
                }
            });
        }

        mSearchInputDetele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInput.setText("");
            }
        });

        mSearchResultRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                Log.d(TAG, "onLoadMore: load more...");
                //加载更多
                if (mSearchPresenter != null) {
                    mSearchPresenter.loadMore();
                }
            }
        });

        mAlbumListAdapter.setOnRecommendItemClickListener(new AlbumListAdapter.OnRecommendItemClickListener() {
            @Override
            public void onItemClick(int position, Album album) {
                AlbumDetailPresenter.getInstance().setTargetAlbum(album);
                //根据位置拿到数据
                //item被点击了,跳转到详情界面
                Intent intent=new Intent(SearchActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void switch2Search(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            //给个提示//
            Toast.makeText(this, "搜索关键字不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mSearchInput.setText(keyWord);
        mSearchInput.setSelection(keyWord.length());
        if (mSearchPresenter != null) {
            mSearchPresenter.doSearch(keyWord);
            if (mUILoader != null) {
                mUILoader.updateStatus(UILoader.UIStatus.LOADING);
            }

        }
    }

    /**
     * 获取联想的关键词
     * @param keyWord
     */
    private void getSuggestWord(String keyWord) {
        LogUtils.d(TAG, "getSuggestWord: keyWord -->  "+keyWord);
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendMord(keyWord);
        }
    }

    private void initView() {
        mSearchContainer = (FrameLayout) findViewById(R.id.search_container);
        mSearchBack = (ImageView) findViewById(R.id.search_back);
        mSearchInput = (EditText) findViewById(R.id.search_input);
        mSearchInputDetele = (ImageView)findViewById(R.id.search_input_delete);
        mSearchInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSearchInput.requestFocus();
                mInputMethodManager.showSoftInput(mSearchInput,InputMethodManager.SHOW_IMPLICIT);
            }
        },TIME_SHOW_IMM);
        mSearchBtn = (TextView) findViewById(R.id.search_btn);
        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };
            if (mUILoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
            mSearchContainer.addView(mUILoader);
        }
    }

    /**
     * 创建数据请求成功的view
     * @return
     */
    private View createSuccessView() {
        View resultView= LayoutInflater.from(this).inflate(R.layout.search_result_layout, null, false);
        //刷新控件
        mSearchResultRefreshLayout = resultView.findViewById(R.id.search_result_refresh_layout);
        mSearchResultRefreshLayout.setEnableRefresh(false);
       //显示热词的
        mFlowTextLayout = resultView.findViewById(R.id.recommend_font_word_view);
        mResultListView = resultView.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        mResultListView.setLayoutManager(linearLayoutManager);
        //设置适配器
        mAlbumListAdapter = new AlbumListAdapter();
        mResultListView.setAdapter(mAlbumListAdapter);
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom=UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        //搜索推荐
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(linearLayoutManager1);
        //设置适配器
        mSearchRecommendAdapter = new SearchRecommendAdapter();
        mSearchRecommendList.setAdapter(mSearchRecommendAdapter);
        return resultView;
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        handleSearch(result);
        //隐藏键盘
        mInputMethodManager.hideSoftInputFromWindow(mSearchInput.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void handleSearch(List<Album> result) {
        //隐藏热词
        hideSuccessView();
        mSearchResultRefreshLayout.setVisibility(View.VISIBLE);
        if (result != null) {
            if (result.size()==0) {
                //数据为空
                if (mUILoader != null) {
                    mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
                }

            }else {
                //如果数据部位空，就设置数据
                mAlbumListAdapter.setData(result);
                if (mUILoader != null) {
                    mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
                }
            }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        hideSuccessView();
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        Log.d(TAG, "onHotWordLoaded: hotWordList size --> " + hotWordList.size());
        mHotWords = new ArrayList<>();
        mHotWords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchWord = hotWord.getSearchword();
            mHotWords.add(searchWord);
        }
        Collections.sort(mHotWords, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length()-o2.length();
            }
        });
        Log.d(TAG, "onHotWordLoaded: "+ mHotWords.size());
        //更新ui
        mFlowTextLayout.setTextContents(mHotWords);
    }

    @Override
    public void onLoadmoreResult(List<Album> result, boolean isOkay) {
        //处理加载更多的结果
        if (mSearchResultRefreshLayout != null) {
            mSearchResultRefreshLayout.finishLoadmore();
        }
        //判断是否成功
        if (isOkay) {
            handleSearch(result);
        }else {
            Toast.makeText(this, "没有更多内容", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {
        //联想相关的关键字
        LogUtils.d(TAG, "onRecommendWordLoaded: keyWordList --> "+keyWordList.size());
        if (mSearchRecommendAdapter != null) {
            mSearchRecommendAdapter.setData(keyWordList);
        }
        //todo:控制UI的状态可影藏显示
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //控制显示和影藏
        hideSuccessView();
        mSearchRecommendList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    private void hideSuccessView(){
        mFlowTextLayout.setVisibility(View.GONE);
        mSearchResultRefreshLayout.setVisibility(View.GONE);
        mSearchRecommendList.setVisibility(View.GONE);
    }
}
