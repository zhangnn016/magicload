package com.magic.load.magicload;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * 代理Activity
 * 主要的工作就是显示插件中的Activity，处理其生命周期
 *
 */

public class ProxyActivity extends Activity {
    private static final String TAG = "ProxyActivity";

    public static final String EXTRA_DEX_PATH = "ProxyActivity.Extra.Dex.Path";

    public static final String LOAD_FROM = "Plugin.Load.From"; // 0是自己独立启动，1是从宿主启动

    private AssetManager mAssetManager;
    private Resources mResources;
    private Resources.Theme mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String dexPath = getIntent().getStringExtra(EXTRA_DEX_PATH);
        loadResources(dexPath);

        launchPluginActivity(dexPath);
    }

    private void launchPluginActivity(String dexPath) {
        if (TextUtils.isEmpty(dexPath)) {
            throw new RuntimeException("DexPath is empty!");
        }
        try {
            PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null && packageInfo.activities != null && packageInfo.activities.length > 0) {
                String activityName = packageInfo.activities[0].name;
                launchPluginActivityByClassName(dexPath, activityName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchPluginActivityByClassName(String dexPath, String clsName) {
        if (TextUtils.isEmpty(clsName) || TextUtils.isEmpty(dexPath)) {
            throw new RuntimeException("ClsName or DexPath is empty!");
        }
        File dexOutputDir = getDir("plugin", Context.MODE_PRIVATE);
        String dexOutputPath = dexOutputDir.getAbsolutePath();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, dexOutputPath, null, classLoader);
        try {
            Class<?> localClass = dexClassLoader.loadClass(clsName);
            Constructor<?> localConstructor = localClass.getConstructor(new Class[] {});
            Object localInstance = localConstructor.newInstance(new Object[]{});

            Method setProxy = localClass.getMethod("setProxy", new Class[] {Activity.class});
            setProxy.setAccessible(true);
            setProxy.invoke(localInstance, new Object[] {this});

            Method onCreate = localClass.getDeclaredMethod("onCreate", new Class[] {Bundle.class});
            onCreate.setAccessible(true);
            Bundle bundle = new Bundle();
            // 可以给bundle设置一下参数传递给插件
            bundle.putInt(LOAD_FROM, 1);
            onCreate.invoke(localInstance, new Object[] {bundle});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadResources(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            mAssetManager = assetManager;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (mAssetManager != null) {
            Resources superRes = super.getResources();
            mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
            mTheme = mResources.newTheme();
            mTheme.setTo(super.getTheme());
        }
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }
}
