package com.example.unimpdemo.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.unimpdemo.UniappTestActivity;
import com.example.unimpdemo.MainApp;
import com.example.unimpdemo.R;
import com.example.unimpdemo.base.BaseService;
import com.example.unimpdemo.module.update.VersionUpdateDialog;
import com.example.unimpdemo.util.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import androidx.core.content.FileProvider;

/**
 * 后台静默下载apk
 */
public class ApkDownloadService extends BaseService {
    private static final int NOTIFY_ID = 01201;
    /**
     * 下载进度条
     **/
    private int progress;
    private NotificationManager mNotificationManager;

    /**
     * 是否取消下载 true表示取消下载
     **/
    private boolean canceled;
    /**
     * 安装包url
     **/
    private String apkUrl = "";
    /** 安装包md5 **/
//	private String md5 = "";
    /**
     * 下载包安装文件名
     **/
    private String destFileName = "";
    /**
     * 下载包安装文路径
     **/
    private String destFileDir = "";
    /**
     * 回调结果
     **/
    private VersionUpdateDialog.ICallbackResult callback;
    /**
     * 下载的Binder
     **/
    private DownloadBinder binder;
    private MainApp app;
    /**
     * 下载服务是否挂掉 true表示已经挂了，默认false
     **/
    private boolean serviceIsDestroy = false;
    private Context context;
    /**
     * 下载完成后的apk文件
     **/
    private File apkfile;
    private Context mContext = this;
    /**
     * 下载失败重试的次数
     **/
    private int reDownCount = 0;

    @Override
    public IBinder onBind(Intent intent) {
        apkUrl = intent.getStringExtra("apkUrl");
        destFileDir = intent.getStringExtra("destFileDir");
        destFileName = intent.getStringExtra("destFileName");
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        binder = new DownloadBinder();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        stopForeground(true);// 这个不确定是否有作用
        app = (MainApp) getApplication();
    }

    public class DownloadBinder extends Binder {
        public void start() {
            progress = 0;
            setUpNotification();
            // 下载
            canceled = false;
            downloadApk();
        }

        public void cancel() {
            canceled = true;
        }

        public int getProgress() {
            return progress;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public boolean serviceIsDestroy() {
            return serviceIsDestroy;
        }

        public void cancelNotification() {
            // 这里是用户界面手动取消，所以会经过activity的onDestroy();方法
            // 取消通知
            mNotificationManager.cancel(NOTIFY_ID);
        }

        public void addCallback(VersionUpdateDialog.ICallbackResult callback) {
            ApkDownloadService.this.callback = callback;
        }

        public void retryDownload() {
            downloadApk();
        }


    }

    // 通知栏
    Notification mNotification;

    /**
     * 创建通知
     */
    private void setUpNotification() {
        mNotification = new Notification(R.mipmap.icon, context.getString(R.string.start_downloading), System.currentTimeMillis());
        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.version_apkdown_notifi);
        contentView.setTextViewText(R.id.tv_progress, context.getString(R.string.new_version_downloading));
        // 指定个性化视图
        mNotification.contentView = contentView;

//        Intent intent = new Intent(this, WelcomeActivity.class);
        Intent intent = new Intent(this, UniappTestActivity.class);
        // 下面两句是 在按home后，点击通知栏，返回之前activity 状态;
        // 有下面两句的话，假如service还在后台下载， 在点击程序图片重新进入程序时，直接到下载界面，相当于把程序MAIN 入口改了 - -
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 指定内容意图
        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

    /**
     * 下载完成.
     *
     * @author 史明松
     * @update 2014年6月26日 下午1:36:18
     */
    private void downloadComplete() {
        // 点击后消失
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri apkUri;
        // Android 7.0 以上不支持 file://协议 需要通过 FileProvider 访问 sd卡 下面的文件，所以 Uri 需要通过 FileProvider 构造，协议为 content://
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // content:// 协议
            apkUri = FileProvider.getUriForFile(context, "com.yuecheng.workportal.FileProvider", apkfile);
            //Granting Temporary Permissions to a URI
        } else {
            // file:// 协议
            apkUri = Uri.fromFile(apkfile);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        // 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(NOTIFY_ID, mNotification);

        serviceIsDestroy = true;
        // 下载完了，cancelled也要设置
        canceled = true;
        stopSelf();// 停掉服务自身
    }

    /**
     * 下载apk.
     *
     * @author 史明松
     * @update 2014年6月20日 下午7:21:38
     */
    private void downloadApk() {
        File file =new File(destFileDir,destFileName);
        if(file.exists()){//如果本地已存在该文件，删除掉，重新下载
            file.delete();
        }
        OkGo.<File>get(apkUrl)//
                .tag(this)//
                .execute(new FileCallback(destFileDir, destFileName) {

                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                        apkfile = response.body();
                        downloadComplete();
                        ToastUtil.success(context, context.getString(R.string.download_completed), Toast.LENGTH_LONG);
                        installApk();
                    }

                    @Override
                    public void onError(Response<File> response) {
                        if(apkfile != null && apkfile.exists()){
                            apkfile.delete();
                        }
                        /** 如果次数耗尽就终止下载服务，清除通知栏任务 **/
                        if (reDownCount == 2) {
                            // 下载完毕 取消通知
                            mNotificationManager.cancel(NOTIFY_ID);
                            ToastUtil.error(context, context.getString(R.string.server_net_error));
                            stopSelf();// 停掉服务自身
                            callback.OnBackResult(null, VersionUpdateDialog.ICallbackResult.BACK_RESULT_FAILED);
                            return;
                        }
                        /** 如果下载失败程序就不断递归重试直到下载完成 **/
                        reDownCount = reDownCount + 1;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                        }
                        downloadApk();
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        int progre = (int) (((float) progress.currentSize / progress.totalSize) * 100);

                        String message = "";
                        if (progre < 100) {
                            message = context.getString(R.string.downloaded) + progre + "%";
                        } else {
                            message = context.getString(R.string.download_completed);
                        }
                        callback.OnBackResult(progre, message);
                        mNotification.contentView.setTextViewText(R.id.tv_progress, message);
                        mNotification.contentView.setProgressBar(R.id.progressbar, 100, progre, false);
                        mNotificationManager.notify(NOTIFY_ID, mNotification);
                    }
                });
    }

    /**
     * 安装apk .
     *
     * @author 史明松
     * @update 2014年6月20日 下午7:20:40
     */
    private void installApk() {
        /** 如果文件为空或者不存在就重新开启下载 **/
        if (apkfile == null || !apkfile.exists()) {
            downloadApk();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri;
        // Android 7.0 以上不支持 file://协议 需要通过 FileProvider 访问 sd卡 下面的文件，所以 Uri 需要通过 FileProvider 构造，协议为 content://
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // content:// 协议
            apkUri = FileProvider.getUriForFile(context, "com.yuecheng.workportal.FileProvider", apkfile);
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            // file:// 协议
            apkUri = Uri.fromFile(apkfile);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);

        callback.OnBackResult(null, VersionUpdateDialog.ICallbackResult.BACK_RESULT_FINISH);
    }

    public String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
