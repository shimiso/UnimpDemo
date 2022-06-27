package com.example.unimpdemo.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.unimpdemo.MainApp;
import com.example.unimpdemo.R;
import com.example.unimpdemo.module.update.CheckVersionPresenter;
import com.example.unimpdemo.module.update.Version;
import com.example.unimpdemo.module.update.VersionUpdateDialog;
import com.example.unimpdemo.util.AndroidUtil;
import com.example.unimpdemo.util.SharePreferenceUtil;
import com.example.unimpdemo.util.ToastUtil;
import com.gyf.immersionbar.ImmersionBar;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Activity基类
 */
public class BaseActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    /**
     * 共享存储工具类
     **/
    protected SharePreferenceUtil spUtil;
    /**
     * Application
     **/
    protected MainApp mainApp;
    /**
     * Android工具类
     **/
    protected AndroidUtil androidUtil;
    protected boolean isFullFullscreen = false;
    private CheckVersionPresenter checkVersionPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.spUtil = new SharePreferenceUtil(this);
        this.mainApp = (MainApp) getApplication();
        this.androidUtil = AndroidUtil.init(this, spUtil, mainApp);
        mainApp.addActivity(this);
    }

    /**
     * 初始化窗口
     *
     * @param barTintResource 通知栏颜色
     */
    protected void setBarColor(int barTintResource) {
        ImmersionBar.with(this)
                .reset()  //重置所以沉浸式参数
                .statusBarColor(barTintResource)     //状态栏颜色，不写默认透明色
                .init();
    }

    /**
     * 全屏
     */
    protected void setFullFullscreen() {
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        isFullFullscreen = true;
        setBarColor(R.color.primary);
    }

    /**
     * 检查版本
     * @param isQuiet 是否静默
     */
    protected void checkVersion(boolean isQuiet) {
        checkVersionPresenter = new CheckVersionPresenter(this);
        Context context = this;

        try {
            String topActivity = AndroidUtil.getTopActivity(this);
            //当VersionUpdateDialog不在栈顶的时候并且网络正常（由于赵闯手机在更新的时候点击home键，回来再次弹出更新界面）
            if (!Class.forName(topActivity).getName().equals(VersionUpdateDialog.class.getName())&& androidUtil.hasInternetConnected()) {
                //版本验证
                checkVersionPresenter.checkVersion(androidUtil.getApkVersionCode(), new CheckVersionPresenter.CheckVersionListener() {

                    @Override
                    public void toNext() {
                        if(!isQuiet)
                            ToastUtil.normal(context,context.getString(R.string.version_update_hint));
                    }

                    @Override
                    public void updateNewVersion(Version version) {
                        if (!AndroidUtil.isActivityRunning(context, VersionUpdateDialog.class.getName())) {
                            Intent in = new Intent(context, VersionUpdateDialog.class);
                            in.putExtra("VERSION",version);
                            startActivity(in);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if(!isQuiet)
                            ToastUtil.normal(context,context.getString(R.string.login_server_net_error));
                    }
                });
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setBarColor(R.color.primary);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//         必须调用该方法，防止内存泄漏
//        ImmersionBar.destroy(this,null);
        ToastUtil.reset();//销毁的时候将Toast制空
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
            // 非必加
            // 如果你的app可以横竖屏切换，适配了4.4或者华为emui3.1系统手机，并且navigationBarWithKitkatEnable为true，
            // 请务必在onConfigurationChanged方法里添加如下代码（同时满足这三个条件才需要加上代码哦：1、横竖屏可以切换；2、android4.4或者华为emui3.1系统手机；3、navigationBarWithKitkatEnable为true）
            // 否则请忽略
            ImmersionBar.with(this).init();
    }
}
