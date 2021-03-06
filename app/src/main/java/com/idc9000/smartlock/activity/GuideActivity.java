package com.idc9000.smartlock.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.utils.ActivityUtil;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Created by 赵先生 on 2017/11/10.
 * 引导页
 */

public class GuideActivity extends BaseActivity {
    private static final String TAG = "GuideActivity";
    public Context context = this;
    private BGABanner mBackgroundBanner;
    private BGABanner mForegroundBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();
        processLogic();
    }

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_guide);
        mBackgroundBanner = (BGABanner) findViewById(R.id.banner_guide_background);
        mForegroundBanner = (BGABanner) findViewById(R.id.banner_guide_foreground);

        //判断是否第一次安装
        if (SPUtils.getInstance().getBoolean("isInstall")) {
            ActivityUtil.start(context, SplashActivity.class, true);
        }
    }

    private void setListener() {
        /*
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        mForegroundBanner.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                SPUtils.getInstance().put("isInstall", true);
                startActivity(new Intent(context, SplashActivity.class));
                finish();
            }
        });
    }

    private void processLogic() {
        // 设置数据源
//        mBackgroundBanner.setData(R.mipmap.uoko_guide_background_1, R.mipmap.uoko_guide_background_2, R.mipmap.uoko_guide_background_3);
        mForegroundBanner.setData(R.mipmap.guide1, R.mipmap.guide2, R.mipmap.guide3, R.mipmap.guide4);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        mBackgroundBanner.setBackgroundResource(android.R.color.white);
    }
}
