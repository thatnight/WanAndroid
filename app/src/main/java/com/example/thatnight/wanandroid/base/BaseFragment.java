package com.example.thatnight.wanandroid.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.thatnight.wanandroid.R;
import com.example.thatnight.wanandroid.utils.ToastUtil;

/**
 * Created by thatnight on 2017.10.27.
 */

public abstract class BaseFragment<V extends BaseContract.IBaseView,
        P extends BasePresenter> extends Fragment implements BaseContract.IBaseView {

    protected Activity mActivity;
    protected View mRootView;
    protected boolean mIsPrepare;
    protected boolean mIsVisible;
    protected Toolbar mToolbar;
    protected TextView mTitle;
    protected ImageButton mIbtnMenu;
    protected ImageButton mIbtnDraw;

    protected P mPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mPresenter = getPresenter();
        initPresenter();
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
            initData(getArguments());
            initView();
            initListener();
            mIsPrepare = true;
            onLazyLoad();
        }
        return mRootView;
    }

    private void initPresenter() {
        if (mPresenter != null) {
            mPresenter.attachView(initModel(), this);
        }
    }

    protected abstract BaseModel initModel();

    protected abstract P getPresenter();


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisibleToUser();
        } else {
            mIsVisible = false;
        }
        Log.d("onlazy", getContext() + "   " + isVisibleToUser);
    }

    private void onVisibleToUser() {
        if (mIsVisible && mIsPrepare) {
            onLazyLoad();
        }
    }

//    protected <T extends View> T $(int resId) {
//        if (mRootView == null) {
//            return null;
//        }
//        return mRootView.findViewById(resId);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    protected void setDraw(boolean isShow) {
        if (isShow) {
            mIbtnDraw = mRootView.findViewById(R.id.tb_draw);
            if (mIbtnDraw != null) {
                mIbtnDraw.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void setTitle(String title) {
        mTitle = mRootView.findViewById(R.id.tb_title);
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView();


    protected abstract void initData(Bundle arguments);

    protected abstract void initListener();

    protected abstract void onLazyLoad();

    @Override
    public void onDestroyView() {
        Log.d("onlazy", "onDestroyView: ");
        super.onDestroyView();
        if (mRootView != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        if (mPresenter != null) {
            mPresenter.detachView();
        }

    }

    public void showToast(String s) {
        ToastUtil.showToast(mActivity, s);
    }
}
