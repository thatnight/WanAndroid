package com.example.thatnight.wanandroid.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.thatnight.wanandroid.R;
import com.example.thatnight.wanandroid.adapter.ArticleRvAdapter;
import com.example.thatnight.wanandroid.adapter.SearchAdapter;
import com.example.thatnight.wanandroid.base.BaseActivity;
import com.example.thatnight.wanandroid.base.BaseModel;
import com.example.thatnight.wanandroid.base.BaseRecyclerViewAdapter;
import com.example.thatnight.wanandroid.base.SwipeBackActivity;
import com.example.thatnight.wanandroid.entity.Article;
import com.example.thatnight.wanandroid.mvp.contract.SearchContract;
import com.example.thatnight.wanandroid.mvp.model.SearchModel;
import com.example.thatnight.wanandroid.mvp.presenter.SearchPresenter;
import com.example.thatnight.wanandroid.utils.GsonUtil;
import com.example.thatnight.wanandroid.utils.HelperCallback;
import com.example.thatnight.wanandroid.utils.SharePreferenceUtil;
import com.example.thatnight.wanandroid.utils.ViewUtil;
import com.example.thatnight.wanandroid.view.customview.SpaceItemDecoration;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends SwipeBackActivity<SearchContract.IView, SearchPresenter>
        implements SearchContract.IView, View.OnClickListener, OnRefreshListener, OnLoadmoreListener, BaseRecyclerViewAdapter.OnClickRecyclerViewListener, ArticleRvAdapter.IOnIbtnClickListener {

    private List<Article> mArticles;
    private ArticleRvAdapter mAdapter;
    private SearchAdapter mSearchAdatper;
    private RecyclerView mRv;
    private EditText mSearchView;
    private ImageButton mIbtnClear;
    private RefreshLayout mRefreshLayout;
    private int mPage = 0;
    private View mIbtnCollect;
    private int mSelectPosition;
    private List<String> mSearchHistory;
    private ItemTouchHelper.Callback mItemCallback;
    private boolean isEditting = false;

    @Override
    protected Boolean isSetStatusBar() {
        return false;
    }

    @Override
    protected BaseModel initModel() {
        return new SearchModel();
    }

    @Override
    protected SearchPresenter getPresenter() {
        return new SearchPresenter();
    }

    @Override
    protected void initData() {
        mArticles = new ArrayList<>();
        String history = (String) SharePreferenceUtil.get(getApplicationContext(), "search_list", "");
        if (!TextUtils.isEmpty(history)) {
            mSearchHistory = GsonUtil.gsonToList(history, String.class);
        }
        mSearchAdatper = new SearchAdapter();
        mSearchAdatper.updateData(mSearchHistory);
    }

    @Override
    protected void initView() {
        mShowBack = true;
        mRefreshLayout = $(R.id.srl_search);
        mRv = $(R.id.rv_search);
        mIbtnClear = $(R.id.tb_search_clear);
        mAdapter = new ArticleRvAdapter();

        mRv.setItemAnimator(new DefaultItemAnimator());
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.recyclerview_decoration)));
        mRv.setAdapter(mSearchAdatper);
        mItemCallback = new HelperCallback(mSearchAdatper);
        ItemTouchHelper touchHelper = new ItemTouchHelper(mItemCallback);
        touchHelper.attachToRecyclerView(mRv);

        mSearchView = $(R.id.tb_searchview);
    }

    @Override
    protected void initListener() {
        mSearchAdatper.setOnRecyclerViewListener(new BaseRecyclerViewAdapter.OnClickRecyclerViewListener() {
            @Override
            public void onItemClick(int pos) {
                mSearchView.setText(mSearchHistory.get(pos));
                mSearchView.setSelection(mSearchHistory.get(pos).length());
            }

            @Override
            public void onItemLongClick(int pos) {

            }
        });
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadmoreListener(this);
        mAdapter.setOnRecyclerViewListener(this);
        mAdapter.setOnIbtnClickListener(this);
        mIbtnClear.setOnClickListener(this);
        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (v != null && v.getText() != null) {
                        mPresenter.search(true, v.getText().toString(), String.valueOf(mPage));
                        ViewUtil.inputSoftWare(false, v);
                        if (mSearchHistory == null) {
                            mSearchHistory = new ArrayList<>();
                        }
                        mSearchHistory.add(v.getText().toString());
                        mSearchAdatper.updateData(mSearchHistory);
                        return true;
                    }
                }
                return false;
            }
        });

        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mRv.setAdapter(mSearchAdatper);
                    isEditting = false;
                } else {
                    mPresenter.search(true, s.toString(), String.valueOf(mPage));
                }
            }
        });
    }

    @Override
    public void isLoading(boolean isLoading) {
        if (isLoading) {
            mRefreshLayout.autoRefresh();
        } else {
            mRefreshLayout.finishRefresh();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public void showArticles(boolean isRefresh, List<Article> articles) {
        if (isRefresh) {
            mRefreshLayout.finishRefresh(true);
            mArticles.clear();
        } else {
            mRefreshLayout.finishLoadmore(true);
        }
        mArticles.addAll(articles);
        mAdapter.updateData(mArticles);
        if (!isEditting) {
            mRv.setAdapter(mAdapter);
            isEditting = true;
        }
    }

    @Override
    public void error(String s) {
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadmore(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tb_search_clear:
                if (!TextUtils.isEmpty(mSearchView.getText())) {
                    mSearchView.setText("");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (mSearchView != null && !TextUtils.isEmpty(mSearchView.getText().toString())) {
            mPage = 0;
            mPresenter.search(true, mSearchView.getText().toString(), String.valueOf(mPage));
        } else {
            mRefreshLayout.finishRefresh(false);
        }
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if (mSearchView != null && !TextUtils.isEmpty(mSearchView.getText().toString())) {
            mPage += 1;
            mPresenter.search(false, mSearchView.getText().toString(), String.valueOf(mPage));
        } else {
            mRefreshLayout.finishLoadmore(false);
        }
    }

    @Override
    public void onItemClick(int pos) {
        Article article = mArticles.get(pos);
        Intent intent = WebViewActivity.newIntent(this,
                article.getId(),
                article.getOriginId(),
                article.getTitle(),
                article.getLink(),
                article.isCollect());
        startActivityForresultAnim(intent, 1);
    }

    @Override
    public void onItemLongClick(int pos) {

    }

    @Override
    public void onIbtnClick(View v, int position) {
        mIbtnCollect = v;
        mSelectPosition = position;
        ViewUtil.setSelected(v);
//        mPresenter.collect(v.isSelected(), String.valueOf(mArticles.get(position).getId()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                if (mSearchView != null && TextUtils.isEmpty(mSearchView.getText().toString())) {
                    mPresenter.search(true, mSearchView.getText().toString(), String.valueOf(mPage));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchHistory != null) {
            if (mSearchAdatper != null) {
                mSearchHistory = mSearchAdatper.getData();
            }
            SharePreferenceUtil.put(getApplicationContext(), "search_list", GsonUtil.gsonToJson(mSearchHistory));
        }
    }
}