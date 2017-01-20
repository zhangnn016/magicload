package com.magic.load.magicload;

import android.app.Activity;
import android.os.Bundle;

/**
 * 代理Activity
 * 主要的工作就是显示插件中的Activity，处理其生命周期
 *
 */

public class ProxyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);
    }
}
