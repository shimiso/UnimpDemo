package com.example.unimpdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.example.unimpdemo.util.SharePreferenceUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import androidx.multidex.MultiDexApplication;
import io.dcloud.feature.sdk.DCSDKInitConfig;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.dcloud.feature.sdk.Interface.IDCUniMPPreInitCallback;
import io.dcloud.feature.sdk.MenuActionSheetItem;
import io.dcloud.uniplugin.TestModule;
import io.dcloud.uniplugin.TestText;
import okhttp3.OkHttpClient;

public class MainApp extends MultiDexApplication {
    private static MainApp app;
    public List<Activity> activityList = new LinkedList<Activity>();
    SharePreferenceUtil spUtil = null;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        spUtil = new SharePreferenceUtil(this);
        initOkGo();
        try {
            WXSDKEngine.registerModule("TestModule", TestModule.class);
            WXSDKEngine.registerComponent("myText", TestText.class);
        } catch (WXException e) {
            e.printStackTrace();
        }
        //初始化 uni小程序SDK ----start----------
        MenuActionSheetItem item = new MenuActionSheetItem("关于", "gy");
        MenuActionSheetItem item1 = new MenuActionSheetItem("获取当前页面url", "hqdqym");
        MenuActionSheetItem item2 = new MenuActionSheetItem("跳转到宿主原生测试页面", "gotoTestPage");
        List<MenuActionSheetItem> sheetItems = new ArrayList<>();
        sheetItems.add(item);
        sheetItems.add(item1);
        sheetItems.add(item2);
        Log.i("unimp","onCreate----");
        DCSDKInitConfig config = new DCSDKInitConfig.Builder()
                .setCapsule(true)
                .setMenuDefFontSize("16px")
                .setMenuDefFontColor("#ff00ff")
                .setMenuDefFontWeight("normal")
                .setMenuActionSheetItems(sheetItems)
                .setEnableBackground(false)//开启后台运行
                .build();
        DCUniMPSDK.getInstance().initialize(this, config, new IDCUniMPPreInitCallback() {
            @Override
            public void onInitFinished(boolean b) {
                Log.i("unimp","onInitFinished----"+b);
            }
        });
        //初始化 uni小程序SDK ----end----------
    }
    public static MainApp getApp() {
        return app;
    }
    /**
     * 添加Activity到容器中.
     * @param activity
     * @author 史明松
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }
    /**
     * 重新登陆 （登陆失效后执行）
     * @author 史明松
     */
    public void relogin() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        spUtil.setCurrentUserName("");
//        startActivity(new Intent(app, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * 登陆失效
     */
    public void toInvalidate(Integer id) {
//        startActivity(new Intent(app, InvalidateActivity.class).putExtra(INVALIDATE_MESSAGE, getString(id)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    /**
     * 退出应用 但不清空用户信息(主页连续按2次返回、APP下载完成更新后执行)
     */
    public void exitNoClearUser() {
        for (Activity activity : activityList) {
            activity.finish();
        }
//        stopService(new Intent(this, LoopService.class));
    }

    public String getResString(int resId) {
        return this.getApplicationContext().getString(resId);
    }
    private void initOkGo() {
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        //----------------------------------------------------------------------------------------//

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));

        //超时时间设置，默认60秒
        builder.readTimeout(15000, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(15000, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(15000, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，使用预埋证书，校验服务端证书（自签名证书）
//        HttpsUtils.SSLParams sslParams = null;
//        try {
//            sslParams = HttpsUtils.getSslSocketFactory(getAssets().open("yuechenggroup.com.pem"));
//            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        //方法一：信任所有证书,不安全有风险
//        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
//        //方法二：自定义信任规则，校验服务端证书
//        HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
//        //方法三：使用预埋证书，校验服务端证书（自签名证书）
//        HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
//        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//        HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
//        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
//        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//        builder.hostnameVerifier(new SafeHostnameVerifier());

        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0);                             //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers);                        //全局公共头
//                .addCommonParams(params);                       //全局公共参数
    }
    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }
}
