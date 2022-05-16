package com.example.unimpdemo.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.unimpdemo.MainApp;
import com.example.unimpdemo.R;
import com.example.unimpdemo.widget.ConfirmDialog;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import static android.content.Context.WIFI_SERVICE;

/**
 * Android的工具类
 * <p>
 * Created by shims on 2016/1/25 0025.
 */
public class AndroidUtil {
    public final static int TIPS_ERROR = 0;
    public final static int TIPS_SUCCESS = 1;
    public Context context;
    public SharePreferenceUtil spUtil;
    public MainApp mainApp;
    public static AndroidUtil androidUtils;

    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static final String KEY_APP_KEY = "JPUSH_APPKEY";

    private AndroidUtil(Context context, SharePreferenceUtil spUtil, MainApp mainApp) {
        this.context = context;
        this.spUtil = spUtil;
        this.mainApp = mainApp;
    }

    private AndroidUtil(Context context) {
        this.context = context;
    }

    public static AndroidUtil init(Context context, SharePreferenceUtil spUtil, MainApp mainApp) {
        androidUtils = new AndroidUtil(context, spUtil, mainApp);
        return androidUtils;
    }

    public static AndroidUtil init(Context context) {
        androidUtils = new AndroidUtil(context);
        return androidUtils;
    }

    private static long mLastClickTime = 0;
    private static final int SPACE_TIME = 500;
    //双击进入
    public static boolean isFastDoubleClick() {
        long time = SystemClock.elapsedRealtime();
        if (time - mLastClickTime <= SPACE_TIME) {
            return true;
        } else {
            mLastClickTime = time;
            return false;
        }
    }

    private static long lastClickTime=0;
    public static boolean isManyClick() {//防止短时间内多次点击
        long now = System.currentTimeMillis();
        if(now - lastClickTime >500) {
            lastClickTime = now;
            return true;
        }
        return false;
    }
    public boolean editTextIsEmptyFocus(TextView editText) {
        if (StringUtils.isNullOrEmpty(editText.getText().toString())) {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            return true;
        }
        return false;
    }
    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }
    /**
     * 获取栈顶Activity
     */
    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            return (runningTaskInfos.get(0).topActivity.getClassName());
        } else
            return null;
    }


    public boolean editTextIsEmpty(TextView editText) {
        if (StringUtils.isNullOrEmpty(editText.getText().toString())) {
            return true;
        }
        return false;
    }

    /**
     * 检测并设置可用的存储路径
     *
     * @return
     */
    public boolean checkStoragePathAndSetBaseApp() {
        String storagePath = null;
        List<Long> memorySize = new ArrayList<Long>();
        Map<Long, String> storageMap = new HashMap<Long, String>();

        // 如果可以检测到SD卡返回SD卡路径，否则就反射得到最大可以用的机身存储
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) == true) {
            storagePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            StorageManager sm = (StorageManager) context
                    .getSystemService(Context.STORAGE_SERVICE);
            String[] paths;
            try {
                paths = (String[]) sm.getClass()
                        .getMethod("getVolumePaths", (Class) null).invoke(sm, (Object) null);
                for (String path : paths) {
                    StatFs stat = new StatFs(path);
                    long blockSize = stat.getBlockSize();
                    long availableBlocks = stat.getAvailableBlocks();
                    long storageSize = availableBlocks * blockSize;
                    if (storageSize > 0) {
                        memorySize.add(storageSize);
                        storageMap.put(storageSize, path);
                    }
                }
                if (memorySize != null)
                    storagePath = storageMap.get(Collections.max(memorySize));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (storagePath == null) {
            storagePath = context.getFilesDir().getAbsolutePath();

        }

        if (storagePath != null) {
            spUtil.setStoragePath(storagePath);
            return true;
        } else {
            ConfirmDialog dialog = ConfirmDialog.createDialog(context);
            dialog.setDialogTitle(context.getString(R.string.prompt));
            dialog.setCancelable(false);
            dialog.setDialogMessage(context.getString(R.string.check_sdcard));
            dialog.setOnButton1ClickListener(context.getString(R.string.stting), null,
                    (view, dialog1) -> {
                        dialog1.cancel();
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        context.startActivity(intent);
                    });
            dialog.setOnButton2ClickListener(context.getString(R.string.exit), null,
                    (view, dialog12) -> {
                        dialog12.cancel();
                        mainApp.relogin();
                    });
            dialog.show();
            return false;
        }
    }

    /**
     * 检测是否存在网络
     *
     * @return
     */
    public boolean hasInternetConnected() {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo network = manager.getActiveNetworkInfo();
            if (network != null && network.isConnectedOrConnecting()) {
                return true;
            }
        }
        ToastUtil.normal(context, getString(context,R.string.check_connection));
        return false;
    }
    /**
     * 验证网络 如果没有网直接打开网络设置页面
     *
     * @return
     */
    public boolean validateInternet() {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            openWirelessSet();
            return false;
        } else {
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        openWirelessSet();
        return false;
    }

    /**
     * 打开网络设置
     */
    public void openWirelessSet() {
        ConfirmDialog dialog = ConfirmDialog.createDialog(context);
        dialog.setDialogTitle(R.string.prompt);
        dialog.setDialogMessage(R.string.check_connection);
        dialog.setCancelable(false);
        dialog.setOnButton1ClickListener(R.string.settings, null,
                new ConfirmDialog.OnButton1ClickListener() {
                    @Override
                    public void onClick(View view, DialogInterface dialog) {
                        dialog.cancel();
                        Intent intent = null;
                        try {
                            intent = new Intent(Settings.ACTION_SETTINGS);
                            context.startActivity(intent);
                        } catch (Exception e) {
                        }

                    }
                });
        dialog.setOnButton2ClickListener(R.string.exit, null,
                new ConfirmDialog.OnButton2ClickListener() {
                    @Override
                    public void onClick(View view, DialogInterface dialog) {
                        dialog.cancel();
                        mainApp.relogin();
                    }
                });
        dialog.show();
    }

    /**
     * 功能: 获取应用的PackageName
     *
     * @return
     */
    public String getPackageName() {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.packageName;
        } catch (Exception e) {

        }
        return "";
    }
    public String getApkVersionName() {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 功能: 获取应用的版本Code
     *
     * @return
     */
    public int getApkVersionCode() {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {

        }
        return 0;
    }

    /**
     * dp转Px
     *
     * @param dp
     * @return
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * 获取屏幕的高度
     *
     * @param c
     * @return
     */
    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    /**
     * 获取屏幕宽度
     *
     * @param c
     * @return
     */
    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    /**
     * 是否是android5.0版本
     *
     * @return
     */
    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 取得Imei
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public String getImei() {
        String imei = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            Log.e(AndroidUtil.class.getSimpleName(), e.getMessage());
        }
        return imei;
    }

    public  String getAndroidId() {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }

    /**
     * 取得AppKey
     *
     * @return
     */
    public String getAppKey() {
        Bundle metaData = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY);
                if ((null == appKey) || appKey.length() != 24) {
                    appKey = null;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return appKey;
    }

    /**
     * 判断某个activity 时候后台运行
     *
     * @param mContext
     * @param activityClassName 要判断的activity 的class
     * @return
     * @author 史明松
     */
    public static boolean isActivityRunning(Context mContext, String activityClassName) {
        if (mContext == null || TextUtils.isEmpty(activityClassName))
            return false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if (info != null && info.size() > 0) {
            ComponentName component = info.get(0).topActivity;
            if (activityClassName.equals(component.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证SD卡是够可用
     *
     * @return
     */
    public boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    /**
     * 显示通知
     *
     * @param id      notificationId
     * @param title   标题
     * @param content 内容
     * @param intent  PendingIntent
     */
    public static int promptType = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
    public static void showNotification(Context context, long id, String title, String content, PendingIntent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(intent);
        builder.setVibrate(new long[]{0l});
        builder.setDefaults(promptType);//默认有震动以及声音
        @SuppressWarnings("deprecation") Notification notification = builder.getNotification();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) id, notification);
    }

    /**
     * 获取radioGroup选中索引
     *
     * @param group
     * @return
     */
    public String getRadioChild(RadioGroup group) {
        String level = "";
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton childAt = (RadioButton) group.getChildAt(i);
            if (childAt.isChecked()) {
                level = String.valueOf(i);
                break;
            } else {
                level = "";
            }
        }
        return level;
    }

    /**
     * 得到string.xml中的字符串
     *
     * @param resId
     * @return
     */
    public static String getString(Context context,int resId) {
        return context.getResources().getString(resId);
    }

    /**
     * 得到string.xml中的字符串
     * @param resId
     * @return
     */
    public  String getString(int resId) {
        return context.getResources().getString(resId);
    }

    /**
     * Android6.0以下系统判断相机权限是否被开启
     * 返回true 表示可以使用  返回false表示不可以使用
     */
    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    /**
     * 判断软键盘是否显示方法
     * @param activity
     * @return
     */

    public static boolean isSoftShowing(Activity activity) {
        //获取当屏幕内容的高度
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        //DecorView即为activity的顶级view
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight*2/3 > rect.bottom+getSoftButtonsBarHeight(activity);
    }

    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }
    private static Vibrator vibrator;

    /**
     * 简单震动
     * @param context     调用震动的Context
     * @param millisecond 震动的时间，毫秒
     */
    @SuppressWarnings("static-access")
    public static void vSimple(Context context, int millisecond) {
        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(millisecond);
    }

    /**
     * 复杂的震动
     * @param context 调用震动的Context
     * @param pattern 震动形式
     * @param repeate 震动的次数，-1不重复，非-1为从pattern的指定下标开始重复
     */
    @SuppressWarnings("static-access")
    public static void vComplicated(Context context, long[] pattern, int repeate) {
        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, repeate);
    }

    /**
     * 停止震动
     */
    public static void stop() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    /**
     * 权限设置提醒对话框
     * @param content
     */
    public static void showPermissionsDialog(Context context,String content,Intent intent) {
        ConfirmDialog dialog = ConfirmDialog.createDialog(context);
        dialog.setCancelable(false);
        dialog.setDialogTitle(context.getString(R.string.prompt));
        dialog.setDialogMessage(content);

        dialog.setOnButton1ClickListener(context.getString(R.string.cancel), null, new ConfirmDialog.OnButton1ClickListener() {
            @Override
            public void onClick(View view, DialogInterface dialog) {
                dialog.cancel();
            }
        });
        dialog.setOnButton2ClickListener(context.getString(R.string.stting), null, new ConfirmDialog.OnButton2ClickListener() {
            @Override
            public void onClick(View view, DialogInterface dialog) {
                dialog.cancel();
                //跳转设置页面
                if(intent != null) {
                    context.startActivity(intent);
                }
            }
        });
        dialog.show();
    }

    /**
     * 没有连接网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 1;

    public static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    /**
     * 检查是否安装了某应用
     *
     * @param packageName 包名
     * @return
     */
    public static boolean isAvilible(String packageName, Context mContext) {
        final PackageManager packageManager = mContext.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
    /**
     * 获取当前Wifi的SSID
     * @return
     */
    public String getWifiSSID(){
        WifiManager wifiManager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = StringUtils.doEmpty(wifiInfo.getSSID()).toLowerCase().replaceAll("\"","");
        return  ssid;
    }
    /**
     * 获取当前Wifi的BSSID
     * @return
     */
    public String getWifiBSSID(){
        WifiManager wifiManager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = StringUtils.doEmpty(wifiInfo.getBSSID());
        return  ssid;
    }

    /**
     * 判断当前应用是否是后台运行
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packname = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0之后getRunningTasks废弃
            //当前程序是否前台运行
            //详见ActivityManager.START_TASK_TO_FRONT(hide),前台任务
            int START_TASK_TO_FRONT = 2;
            Field field = null;
            try {
                field = ActivityManager.RunningAppProcessInfo.class.getField("processState");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfoList) {
                if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    //如果当前是运行在前台的UI
                    try {
                        Integer processState = field.getInt(runningAppProcessInfo);
                        if (START_TASK_TO_FRONT == processState) {
                            packname = runningAppProcessInfo.processName;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            //5.0之前可直接使用getRunningTasks
            //<uses-permission android:name="android.permission.GET_TASKS"/>
            List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
            packname = runningTaskInfoList.get(0).topActivity.getPackageName();
        }
        return !context.getPackageName().equals(packname);
    }



    /**
     * 获取application中指定的meta-data。本例中，调用方法时key就是UMENG_CHANNEL
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public  String getAppMetaData(String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    //检测GPS是否打开
    public void checkOpenGPS() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ConfirmDialog dialog = ConfirmDialog.createDialog(context);
            dialog.setDialogTitle(context.getString(R.string.prompt));
            dialog.setCancelable(false);
            dialog.setDialogMessage(context.getString(R.string.open_position));//请开启位置服务，获取精准定位
            dialog.setOnButton1ClickListener(context.getString(R.string.cancel), null,
                    (view, dialog1) -> dialog1.cancel());
            dialog.setOnButton2ClickListener(context.getString(R.string.stting), null,
                    (view, dialog12) -> {
                        dialog12.cancel();
                        // 转到手机设置界面，用户设置GPS
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); //
                    });
            dialog.show();
        }
    }

    public static int dp2px(Context context, float dpVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
    }

    public static Point getScreenSize(@NonNull Context context) {
        Point screenSize = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            screenSize.set(context.getResources().getDisplayMetrics().widthPixels,
                    context.getResources().getDisplayMetrics().heightPixels);
            return screenSize;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(screenSize);
        } else {
            wm.getDefaultDisplay().getSize(screenSize);
        }
        return screenSize;
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 判断URL是否有效
     * @param URLName
     * @return
     */
    public  static boolean checkIfUrlExists(final String URLName) {
        boolean  URL_exists=true;
        try {
            //设置此类是否应该自动执行 HTTP 重定向（响应代码为 3xx 的请求）。
            HttpURLConnection.setFollowRedirects(false);
            //到 URL 所引用的远程对象的连接
            HttpURLConnection con = (HttpURLConnection) new URL(URLName)
                    .openConnection();
            /* 设置 URL 请求的方法， GET POST HEAD OPTIONS PUT DELETE TRACE 以上方法之一是合法的，具体取决于协议的限制。*/
            con.setRequestMethod("HEAD");
            //从 HTTP 响应消息获取状态码
            //   LogUtil.e("ryan","head "+con.getResponseCode());
            if(con.getResponseCode() == HttpURLConnection.HTTP_OK)
                URL_exists=true;
            else
                URL_exists=false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return URL_exists;
    }
}

