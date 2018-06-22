package com.example.thatnight.wanandroid.mvp.presenter;

import com.example.thatnight.wanandroid.base.BasePresenter;
import com.example.thatnight.wanandroid.entity.Comment;
import com.example.thatnight.wanandroid.mvp.contract.CommentContract;
import com.example.thatnight.wanandroid.mvp.model.CommentModel;
import com.example.thatnight.wanandroid.view.fragment.CommentFragment;

import java.util.List;

/**
 * Created by ThatNight on 2018.1.7.
 */

public class CommentPresenter extends BasePresenter<CommentModel, CommentFragment>
        implements CommentContract.IPresenter {

    @Override
    public void getComment(boolean isRefresh, int page) {
        if(model==null){
            // TODO: 2018.1.10 解决Activity退出而引用空指针
            return; //防止activity退出, 而空指针
        }
        model.getComment(isRefresh, page, new CommentContract.IModel.OnCommentListener() {

            @Override
            public void success(boolean isRefresh, List<Comment> comments) {
                if (view != null) {
                    view.showComment(isRefresh, comments);
                }
            }

            @Override
            public void error(String error) {
                if (view != null) {
                    view.error(error);
                }
            }
        });
    }

    @Override
    public void addComment(Comment comment) {
        model.addComment(comment, new CommentContract.IModel.OnAddCommentListener() {
            @Override
            public void isSuccess(boolean isSuccess, String error) {
                if (view != null) {
                    if (isSuccess) {
                        view.success();
                    } else {
                        view.error(error);
                    }
                }
            }

        });
    }


}
