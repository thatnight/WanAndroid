package com.example.thatnight.wanandroid.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thatnight.wanandroid.R;
import com.example.thatnight.wanandroid.base.BaseRecyclerViewAdapter;
import com.example.thatnight.wanandroid.entity.CollectArticle;
import com.example.thatnight.wanandroid.entity.Article;
import com.example.thatnight.wanandroid.utils.CleanStringHtmlUtil;

;

/**
 * Created by thatnight on 2017.10.27.
 */

public class ArticleRvAdapter extends BaseRecyclerViewAdapter {

    private IOnIbtnClickListener mOnIbtnClickListener;
    private SparseBooleanArray mSelectArray = new SparseBooleanArray();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, null);
        return new NewsHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((NewsHolder) holder).bindView(mDataList.get(position));
    }

    class NewsHolder extends BaseRvHolder {

        TextView mTitle;
        TextView mAuthor;
        TextView mTime;
        TextView mType;
        ImageButton mIbLike;
        RelativeLayout mItem;

        public NewsHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.item_tv_title);
            mAuthor = itemView.findViewById(R.id.item_tv_author);
            mTime = itemView.findViewById(R.id.item_tv_time);
            mType = itemView.findViewById(R.id.item_tv_type);
            mIbLike = itemView.findViewById(R.id.item_ib_like);
            mItem = itemView.findViewById(R.id.rl_item_news);

        }


        @Override
        protected void bindView(Object o) {
            if (o instanceof Article) {
                Article article = (Article) o;
                String nonHtmlTitle = CleanStringHtmlUtil.delHTMLTag(article.getTitle());
                mTitle.setText(nonHtmlTitle);

                mAuthor.setText(article.getAuthor());
                mTime.setText(article.getNiceDate());
                mType.setText(article.getChapterName());
                mIbLike.setTag(getLayoutPosition());
                if (article.isCollect()) {
                    mIbLike.setSelected(true);
                    mSelectArray.put(getLayoutPosition(), true);
                } else {
                    mIbLike.setSelected(false);
                    mSelectArray.put(getLayoutPosition(), false);
                }
                mIbLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isSelected()) {
                            mSelectArray.put(getLayoutPosition(), false);
                        } else {
                            mSelectArray.put(getLayoutPosition(), true);
                        }
                        if (mOnIbtnClickListener != null) {
                            mOnIbtnClickListener.onIbtnClick(v, getLayoutPosition());
                        }
                    }
                });
            } else if (o instanceof CollectArticle) {
                CollectArticle article = (CollectArticle) o;
                mTitle.setText(article.getTitle());
                mAuthor.setText(article.getAuthor());
                mTime.setText(article.getNiceDate());
                mType.setText(article.getChapterName());
                mIbLike.setTag(getLayoutPosition());
                mIbLike.setSelected(true);
                mIbLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnIbtnClickListener != null) {
                            mOnIbtnClickListener.onIbtnClick(v, getLayoutPosition());
                        }
                    }
                });
            }
        }

    }

    public void setOnIbtnClickListener(IOnIbtnClickListener onIbtnClickListener) {
        mOnIbtnClickListener = onIbtnClickListener;
    }

    public interface IOnIbtnClickListener {
        void onIbtnClick(View v, int position);
    }

}