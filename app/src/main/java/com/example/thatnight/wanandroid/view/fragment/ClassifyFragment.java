package com.example.thatnight.wanandroid.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.expandpopview.callback.OnTwoListCallback;
import com.example.expandpopview.entity.KeyValue;
import com.example.expandpopview.view.ExpandPopView;
import com.example.thatnight.wanandroid.R;
import com.example.thatnight.wanandroid.adapter.NewArticleRvAdapter;
import com.example.thatnight.wanandroid.base.BaseFragment;
import com.example.thatnight.wanandroid.base.BaseModel;
import com.example.thatnight.wanandroid.base.BaseRecyclerViewAdapter;
import com.example.thatnight.wanandroid.constant.Constant;
import com.example.thatnight.wanandroid.entity.Article;
import com.example.thatnight.wanandroid.mvp.contract.ClassifyContract;
import com.example.thatnight.wanandroid.mvp.model.ClassifyModel;
import com.example.thatnight.wanandroid.mvp.presenter.ClassifyPresenter;
import com.example.thatnight.wanandroid.utils.LoginContextUtil;
import com.example.thatnight.wanandroid.utils.ViewUtil;
import com.example.thatnight.wanandroid.view.activity.SearchActivity;
import com.example.thatnight.wanandroid.view.activity.WebViewActivity;
import com.example.thatnight.wanandroid.view.customview.SpaceItemDecoration;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类页面
 * @author thatnight
 * @date 2017.10.27
 */
public class ClassifyFragment extends BaseFragment<ClassifyContract.IView, ClassifyPresenter>
        implements OnRefreshListener,
        OnLoadmoreListener,
        BaseRecyclerViewAdapter.OnClickRecyclerViewListener,
        ClassifyContract.IView, NewArticleRvAdapter.OnArticleItemClickListener {

    private List<Article> mArticles;
    private RecyclerView mRv;
    private RefreshLayout mRefreshLayout;
    private int mPage;
    private View mIbtnCollect;
    private int mSelectPosition;
    private NewArticleRvAdapter mAdapter;
    private List<KeyValue> mParentList;
    private List<KeyValue> mChildList;
    private List<List<KeyValue>> mParentChildList;
    private ExpandPopView mExpandPopView;
    private boolean isLoad;
    private KeyValue mNormalKeyValue;
    private int mParentPosition;

    @Override
    protected void initView() {
        mRv = mRootView.findViewById(R.id.rv_main);
        mRefreshLayout = mRootView.findViewById(R.id.srl_main);
        mExpandPopView = mRootView.findViewById(R.id.epv_classify);
        mExpandPopView.setVisibility(View.VISIBLE);
        mAdapter = new NewArticleRvAdapter();
        mRv.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mRv.setItemAnimator(new DefaultItemAnimator());
        mRv.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.recyclerview_decoration)));
        mRv.setAdapter(mAdapter);
    }

    @Override
    protected void initData(Bundle arguments) {
        EventBus.getDefault().register(this);
        mNormalKeyValue = new KeyValue();
        mArticles = new ArrayList<>();
        mParentChildList = new ArrayList<>();
        mParentList = new ArrayList<>();
        mChildList = new ArrayList<>();
    }

    @Override
    protected void initListener() {
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadmoreListener(this);
        mAdapter.setOnRecyclerViewListener(this);
        mAdapter.setOnIbtnClickListener(this);
        mPage = 0;
    }

    @Override
    protected void onLazyLoad() {
        mPresenter.getParentChildren();

    }

    @Override
    protected BaseModel initModel() {
        return new ClassifyModel();
    }

    @Override
    protected ClassifyPresenter getPresenter() {
        return new ClassifyPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        mPage = 0;
        mPresenter.getArticle(true, mPage, mNormalKeyValue.getValue());
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        mPage += 1;
        mPresenter.getArticle(false, mPage, mNormalKeyValue.getValue());
    }


    @Override
    public void onItemClick(int pos) {
        Article article = mArticles.get(pos);
        Intent intent = WebViewActivity.newIntent(mActivity,
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
        if (LoginContextUtil.getInstance().getUserState().collect(mActivity)) {
            mIbtnCollect = v;
            mSelectPosition = position;
            ViewUtil.setSelected(v);
            mPresenter.collect(v.isSelected(), String.valueOf(mArticles.get(position).getId()));
        }
    }

    @Override
    public void onTypeClick(View v, int position) {

    }

    @Override
    public void onAuthorClick(View v, int position) {
        startActivityAnim(SearchActivity.newIntent(mActivity, mArticles.get(position).getAuthor()));
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
    public void setExpandPopView(List<KeyValue> parentList, List<List<KeyValue>> parentChildrenList) {
        if (!isLoad) {
            if (parentList == null || parentChildrenList == null || parentList.size() <= 0 || parentChildrenList.size() <= 0) {
                return;
            }
            mParentList = parentList;
            mParentChildList = parentChildrenList;
            mExpandPopView.addItemToExpandTab(parentChildrenList.get(0).get(0).getKey(), parentList, parentChildrenList, new OnTwoListCallback() {
                @Override
                public void returnParentKeyValue(int pos, com.example.expandpopview.entity.KeyValue keyValue) {
                    mParentPosition = pos;
                    if (mParentChildList.get(pos) == null) {
//                        mPresenter.getChildren(keyValue.getValue());
                    } else {

                    }
                }

                @Override
                public void returnChildKeyValue(int pos, com.example.expandpopview.entity.KeyValue keyValue) {
                    mNormalKeyValue = keyValue;
                    mPresenter.getArticle(true, mPage, keyValue.getValue());
                }
            });
            mNormalKeyValue = parentChildrenList.get(0).get(0);
            mPresenter.getArticle(true, 0, parentChildrenList.get(0).get(0).getValue());
            isLoad = true;
        }
    }

    @Override
    public void refreshExpandPopView(List<KeyValue> childrenList) {
        mExpandPopView.refreshItemChildrenData(0, childrenList);
        mChildList.clear();
        mChildList.addAll(childrenList);
    }

    @Override
    public void refreshHtml(List<Article> articles) {
        mArticles.clear();
        mArticles.addAll(articles);
        mAdapter.updateData(mArticles);
    }

    @Override
    public void loadMoreHtml(List<Article> articles) {
        mRefreshLayout.finishLoadmore();
        mArticles.addAll(articles);
        mAdapter.appendData(articles);
    }

    @Override
    public void isCollectSuccess(boolean isSuccess, String s) {
        Snackbar.make(mRootView, s, Snackbar.LENGTH_SHORT)
                .setAction("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mIbtnCollect != null) {
                            ViewUtil.setSelected(mIbtnCollect);
                        }
                        mPresenter.collect(mIbtnCollect.isSelected(), String.valueOf(mArticles.get(mSelectPosition).getId()));
                    }
                }).show();
        if (!isSuccess) {
            if (mIbtnCollect != null) {
                ViewUtil.setSelected(mIbtnCollect);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                mPresenter.getArticle(true, 0, mNormalKeyValue.getValue());
//                mRefreshLayout.autoRefresh();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshLayout(String requestCode) {
        if (Constant.REFRESH.equals(requestCode)) {
            mRefreshLayout.autoRefresh();
        } else if (Constant.TOP_CLASSIFY.equals(requestCode)) {
            mRv.smoothScrollToPosition(0);
        }
    }

    @Subscribe
    public void switchToClassify(KeyValue key) {
        if (Constant.SWITCH_TO_CLASSIFY.equals(key.getKey())) {
            int index = -1;
            for (int i = 0; i < mParentChildList.size(); i++) {
                if (mParentChildList.get(i) != null) {
//                    if (mParentChildList.get(i).contains(key.getValue())) {
//                        index = mParentChildList.get(i).indexOf(key.getValue());
//                        mExpandPopView.performClick(0, i, index);
//                    }
                    for (int j = 0; j < mParentChildList.get(i).size(); j++) {
                        if (mParentChildList.get(i).get(j).getKey().toLowerCase().contains(key.getValue().toLowerCase())) {
                            mExpandPopView.performClick(0, i, j);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

}
