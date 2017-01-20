package com.magic.load.magicloader.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.magic.load.magicloader.utils.LogUtil;

public class BasePluginActivity extends Activity {
    private static final String TAG = "BasePluginActivity";

    public static final String LOAD_FROM = "Plugin.Load.From"; // 0是自己独立启动，1是从宿主启动


    public static final String EXTRA_DEX_PATH = "extra.dex.path";
    public static final String EXTRA_CLASS = "extra.class";
    public static final String PROXY_VIEW_ACTION = "com.ryg.dynamicloadhost.VIEW";
    public static final String DEX_PATH = "/mnt/sdcard/magicloader-debug.apk";

    protected LoadFrom mLoadFrom = LoadFrom.SELF;
    protected Activity mProxyActivity;

    public void setProxy(Activity activity) {
        LogUtil.d(TAG, "setProxy, activity=" + activity);
        mProxyActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int from = savedInstanceState.getInt(LOAD_FROM);
            if (from == 0) {
                mLoadFrom = LoadFrom.SELF;
            } else {
                mLoadFrom = LoadFrom.OTHER;
            }
        }
        if (mLoadFrom == LoadFrom.SELF) {
            super.onCreate(savedInstanceState);
            mProxyActivity = this;
        }
        LogUtil.d(TAG, "onCreate finished, mLoadFrom=" + mLoadFrom);
    }

    protected void startActivityByProxy(String clsName) {
        if (mProxyActivity == this) {
            Intent intent = new Intent();
            intent.setClassName(this, clsName);
            startActivity(intent);
        } else {
            Intent intent = new Intent(PROXY_VIEW_ACTION);
            intent.putExtra(EXTRA_DEX_PATH, DEX_PATH);
            intent.putExtra(EXTRA_CLASS, clsName);
            mProxyActivity.startActivity(intent);
        }
    }

    @Override
    public void setContentView(View view) {
        if (mProxyActivity == this) {
            super.setContentView(view);
        } else {
            mProxyActivity.setContentView(view);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (mProxyActivity == this) {
            super.setContentView(view, params);
        } else {
            mProxyActivity.setContentView(view, params);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mProxyActivity == this) {
            super.setContentView(layoutResID);
        } else {
            mProxyActivity.setContentView(layoutResID);
        }
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (mProxyActivity == this) {
            super.addContentView(view, params);
        } else {
            mProxyActivity.addContentView(view, params);
        }
    }

    protected enum LoadFrom {
        SELF,
        OTHER
    }
}
