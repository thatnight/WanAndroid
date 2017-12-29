package com.example.thatnight.wanandroid.mvp.model;

import android.text.TextUtils;
import android.util.Log;

import com.example.thatnight.wanandroid.base.BaseModel;
import com.example.thatnight.wanandroid.constant.Constant;
import com.example.thatnight.wanandroid.entity.ArticleData;
import com.example.thatnight.wanandroid.entity.Msg;
import com.example.thatnight.wanandroid.mvp.contract.SearchContract;
import com.example.thatnight.wanandroid.utils.GsonUtil;
import com.example.thatnight.wanandroid.utils.OkHttpResultCallback;
import com.example.thatnight.wanandroid.utils.OkHttpUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by ThatNight on 2017.12.16.
 */

public class SearchModel extends BaseModel implements SearchContract.IModel {

    @Override
    public void search( String key, String page, final OnSearchCallback onSearchCallback) {
        Map<String, String> map = new HashMap<>();
        map.put("k", key);
        OkHttpUtil.getInstance().postAsync(Constant.URL_BASE + Constant.URL_SEARCH + page + "/json"
                , new OkHttpResultCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        onSearchCallback.error(e.toString());
                    }

                    @Override
                    public void onResponse(byte[] bytes) {
                        String response = new String(bytes);
//                        Log.d("ssss", "onResponse: " + response);
                        if (TextUtils.isEmpty(response)) {
                            onSearchCallback.error("服务器开小差了");
                        } else {
                            Msg msg = GsonUtil.gsonToBean(response, Msg.class);
                            if (msg != null) {
                                if (0 == msg.getErrorCode()) {
                                    String json = GsonUtil.gsonToJson(msg.getData());
                                    ArticleData articleData = GsonUtil.gsonToBean(json, ArticleData.class);
                                    if (articleData != null) {
                                        onSearchCallback.getResult( articleData.getDatas());
                                    }
                                } else {
                                    onSearchCallback.error(msg.getErrorMsg().toString());
                                }
                            } else {
                                onSearchCallback.error("服务器开小差了,"+msg.getErrorMsg());
                            }
                        }
                    }
                }, map);
    }
}