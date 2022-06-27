package com.example.unimpdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.unimpdemo.base.BaseActivity;
import com.example.unimpdemo.module.update.Version;
import com.example.unimpdemo.module.update.VersionUpdateDialog;
import com.example.unimpdemo.util.DownloadUtil;
import org.json.JSONObject;
import java.io.File;
import java.util.HashMap;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.dcloud.feature.sdk.Interface.IUniMP;
import io.dcloud.feature.unimp.config.IUniMPReleaseCallBack;
import io.dcloud.feature.unimp.config.UniMPOpenConfiguration;
import io.dcloud.feature.unimp.config.UniMPReleaseConfiguration;

public class UniappTestActivity extends BaseActivity {
    Context mContext;
    Handler mHandler;
    /** unimp小程序实例缓存**/
    HashMap<String, IUniMP> mUniMPCaches = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mHandler = new Handler();
        setContentView(R.layout.uniapp_test);
        ButterKnife.bind(this);
        Button button1 = findViewById(R.id.button1);

        //用来测试sdkDemo 胶囊×的点击事件，是否生效；lxl增加的
        DCUniMPSDK.getInstance().setCapsuleCloseButtonClickCallBack(appid -> {
            Toast.makeText(mContext, "点击×号了", Toast.LENGTH_SHORT).show();
            if(mUniMPCaches.containsKey(appid)) {
                IUniMP mIUniMP = mUniMPCaches.get(appid);
                if (mIUniMP != null && mIUniMP.isRuning()){
                    mIUniMP.closeUniMP();
                    mUniMPCaches.remove(appid) ;
                }
            }
        });

        DCUniMPSDK.getInstance().setUniMPOnCloseCallBack(appid -> {
            Toast.makeText(mContext, appid+"被关闭了", Toast.LENGTH_SHORT).show();
            //小程序被关闭需要对实例缓存删除操作
            if(mUniMPCaches.containsKey(appid)) {
                mUniMPCaches.remove(appid);
            }
        });

        // 启动小程序 有splash
        button1.setOnClickListener(view -> {
            try {
                UniMPOpenConfiguration uniMPOpenConfiguration = new UniMPOpenConfiguration();
                uniMPOpenConfiguration.splashClass = MySplashView.class;
                IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(mContext,"__UNI__4138A06", uniMPOpenConfiguration);
                mUniMPCaches.put(uniMP.getAppid(), uniMP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        // 启动小程序 无splash
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(view -> {
            try {
                IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(mContext,"__UNI__4D3C304");
                mUniMPCaches.put(uniMP.getAppid(), uniMP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 启动小程序直达二级页面
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(view -> {
            try {
                UniMPOpenConfiguration uniMPOpenConfiguration = new UniMPOpenConfiguration();
                uniMPOpenConfiguration.redirectPath = "pages/tabBar/extUI/extUI?aaa=123&bbb=456";
                IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(mContext,"__UNI__4138A06", uniMPOpenConfiguration);
                mUniMPCaches.put(uniMP.getAppid(), uniMP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 延迟5秒钟关闭开启的小程序
        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(view -> {
            try {
                UniMPOpenConfiguration uniMPOpenConfiguration = new UniMPOpenConfiguration();
                uniMPOpenConfiguration.redirectPath = "pages/component/view/view";

                final IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(mContext,"__UNI__4138A06", uniMPOpenConfiguration);
                mUniMPCaches.put(uniMP.getAppid(), uniMP);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("unimp", "延迟5秒结束 开始关闭当前小程序");
                        uniMP.closeUniMP();
                    }
                }, 5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 获取已运行过的应用版本信息
        Button button5 = findViewById(R.id.button5);
        button5.setOnClickListener(view -> {
            JSONObject info = DCUniMPSDK.getInstance().getAppVersionInfo("__UNI__C8B25A0");
            if(info != null) {
                Log.e("unimp", "info==="+info.toString());
                Toast.makeText(mContext,info.toString(),Toast.LENGTH_LONG).show();
            }
        });

        // 原生扩展Module示例
        Button button6 = findViewById(R.id.button6);
        button6.setOnClickListener(view -> {
            try {
//                    IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(mContext,"__UNI__B61D13B");
                IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(mContext,"__UNI__C8B25A0");
                mUniMPCaches.put(uniMP.getAppid(), uniMP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 小程序与宿主互通示例
        Button button7 = findViewById(R.id.button7);
        button7.setOnClickListener(view -> {
            try {
                UniMPOpenConfiguration uniMPOpenConfiguration = new UniMPOpenConfiguration();
                uniMPOpenConfiguration.redirectPath = "pages/sample/send-event";

                IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(mContext,"__UNI__B61D13B", uniMPOpenConfiguration);
                mUniMPCaches.put(uniMP.getAppid(), uniMP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 小程序调用宿主ativity弹窗
        Button button8 = findViewById(R.id.button8);
        button8.setOnClickListener(v -> {
            try {
                UniMPOpenConfiguration uniMPOpenConfiguration = new UniMPOpenConfiguration();
                uniMPOpenConfiguration.redirectPath = "pages/sample/ext-module";
                IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(mContext,"__UNI__B61D13B", uniMPOpenConfiguration);
                mUniMPCaches.put(uniMP.getAppid(), uniMP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        DCUniMPSDK.getInstance().setDefMenuButtonClickCallBack((appid, id) -> {
            switch (id) {
                case "gy":{
                    Log.e("unimp", "点击了关于" + appid);
                    //宿主主动触发事件
                    JSONObject data = new JSONObject();
                    try {
                        IUniMP uniMP = mUniMPCaches.get(appid);
                        if(uniMP != null) {
                            data.put("sj", "点击了关于");
                            uniMP.sendUniMPEvent("gy", data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "hqdqym" :{
                    IUniMP uniMP = mUniMPCaches.get(appid);
                    if(uniMP != null) {
                        Log.e("unimp", "当前页面url=" + uniMP.getCurrentPageUrl());
                    } else {
                        Log.e("unimp", "未找到相关小程序实例");
                    }
                    break;
                }
                case "gotoTestPage" :
                    Intent intent = new Intent();
                    intent.setClassName(mContext, "com.example.unimpdemo.TestPageActivity");
                    DCUniMPSDK.getInstance().startActivityForUniMPTask(appid, intent);
                    break;
            }
        });


        Button btn_encrypt_wgt_install = findViewById(R.id.btn_encrypt_wgt_install);
        btn_encrypt_wgt_install.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(UniappTestActivity.this, new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1002);
            // 远程下载并且安装
            updateWgt();
        });

        DCUniMPSDK.getInstance().setOnUniMPEventCallBack((appid, event, data, callback) -> {
            Log.i("cs", "onUniMPEventReceive    event="+event);
            //回传数据给小程序
            callback.invoke( "收到消息");
        });

        checkPermission();
    }

    /**
     * 模拟更新wgt
     */
    private void updateWgt() {
        //
        final String wgtUrl = "https://vkceyugu.cdn.bspapp.com/VKCEYUGU-9a144bde-8bdc-4f18-a7e6-1edc44d22d10/09bc883c-04b1-4ca5-994e-a3bc412d0825.wgt";
        final String wgtName = "__UNI__90861A7.wgt";

        String downFilePath = getExternalCacheDir().getPath();

        Handler uiHandler = new Handler();


        DownloadUtil.get().download(UniappTestActivity.this, wgtUrl, downFilePath, wgtName, new DownloadUtil.OnDownloadListener() {

            @Override
            public void onDownloadSuccess(File file) {

                Log.e("unimp","onDownloadSuccess --- file === " + file.getPath());
                Log.e("unimp","onDownloadSuccess --- file length === " + file.length());

                UniMPReleaseConfiguration uniMPReleaseConfiguration = new UniMPReleaseConfiguration();
                uniMPReleaseConfiguration.wgtPath = file.getPath();
                uniMPReleaseConfiguration.password = "789456123";

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DCUniMPSDK.getInstance().releaseWgtToRunPath("__UNI__90861A7", uniMPReleaseConfiguration, new IUniMPReleaseCallBack() {
                            @Override
                            public void onCallBack(int code, Object pArgs) {
                                Log.e("unimp","code ---  " + code + "  pArgs --" + pArgs);
                                if(code ==1) {
                                    //释放wgt完成
                                    try {
                                        DCUniMPSDK.getInstance().openUniMP(UniappTestActivity.this, "__UNI__90861A7");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else{
                                    //释放wgt失败
                                }
                            }
                        });
                    }
                });


            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log.e("unimp", "downFilePath  ===  onDownloadFailed");
            }
        });
    }
    
    /**
     * 检查并申请权限
     */
    public void checkPermission() {
        int targetSdkVersion = 0;
        String[] PermissionString={Manifest.permission.WRITE_EXTERNAL_STORAGE};
        try {
            final PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;//获取应用的Target版本
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Build.VERSION.SDK_INT是获取当前手机版本 Build.VERSION_CODES.M为6.0系统
            //如果系统>=6.0
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                //第 1 步: 检查是否有相应的权限
                boolean isAllGranted = checkPermissionAllGranted(PermissionString);
                if (isAllGranted) {
                    Log.e("err","所有权限已经授权！");
                    return;
                }
                // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
                ActivityCompat.requestPermissions(this, PermissionString, 1);
            }
        }
    }
    @OnClick(R.id.button9)
    public void button9(View view) {
        /*  版本更新测试*/
        Intent in = new Intent(mContext, VersionUpdateDialog.class);
        Version bean = new Version();
        bean.setVersionName(androidUtil.getApkVersionName());
        bean.setUpdateInfo("1.更新一些小bug");
        bean.setApkUrl("http://knowapp.b0.upaiyun.com/hz/knowbox_student_2631.apk");
        in.putExtra("VERSION", bean);
        startActivity(in);
    }
    @OnClick(R.id.button10)
    public void button10(View view) {
        try {
            IUniMP uniMP = DCUniMPSDK.getInstance().openUniMP(mContext,"__UNI__4AA0CFF");
            mUniMPCaches.put(uniMP.getAppid(), uniMP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                //Log.e("err","权限"+permission+"没有授权");
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUniMPCaches.clear();
    }
}