package com.example.thatnight.wanandroid.base;

import android.app.Application;

import com.example.thatnight.wanandroid.utils.OkHttpUtil;
import com.tencent.bugly.Bugly;
import com.tencent.smtt.sdk.QbSdk;

import cn.bmob.v3.Bmob;
import skin.support.SkinCompatManager;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;

/**
 * Created by thatnight on 2017.10.25.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SkinCompatManager.withoutActivity(App.this)
                        .addInflater(new SkinMaterialViewInflater())
                        .addInflater(new SkinConstraintViewInflater())
                        .loadSkin();

                QbSdk.initX5Environment(App.this, new QbSdk.PreInitCallback() {
                    @Override
                    public void onCoreInitFinished() {

                    }

                    @Override
                    public void onViewInitFinished(boolean b) {

                    }
                });

                //Bugly
                Bugly.init(getApplicationContext(), "9bc290a7b0", false);

                OkHttpUtil.init(getApplicationContext());
                Bmob.initialize(App.this, "54bd3008726f332bb21334f096b4b0c3");
            }
        }).start();

    }
}
