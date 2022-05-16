package com.example.unimpdemo.module.update;

import android.app.Activity;

import com.example.unimpdemo.MainApp;
import com.example.unimpdemo.R;
import com.example.unimpdemo.callback.StringCustomCallback;
import com.example.unimpdemo.common.UrlConstant;
import com.example.unimpdemo.util.SharePreferenceUtil;
import com.example.unimpdemo.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 类的说明：版本更新
 * 作者：shims
 * 创建时间：2016/2/26 0026 11:11
 */
public class CheckVersionPresenter {

    private Activity activity;

    public CheckVersionPresenter(Activity activity) {
        this.activity = activity;
    }

    public void checkVersion(final int versionCode, final CheckVersionListener checkVersionListener) {
        Map<String, Object> params = new HashMap<>();
        params.put("terminalType", "1");
        JSONObject jsonObject = new JSONObject(params);
        OkGo.<String>post(UrlConstant.GET_NEW_VERSION)//
                .tag(this)//
                .upJson(jsonObject)
                .execute(new StringCustomCallback(){
                    @Override
                    public void onSuccess(Response<String> result) {
                        try {
                            JSONObject json = new JSONObject(result.body());
                            JSONObject data = json.getJSONObject("result");
                            boolean  success = json.getBoolean("success");
                            if (success) {
                                Gson gson = new Gson();
                                Version version = gson.fromJson(data.toString(), new TypeToken<Version>() {
                                }.getType());
                                //如果当前版本不是最新就打开更新
                                if (version.getVersionCode() > versionCode) {
                                    checkVersionListener.updateNewVersion(version);
                                } else {//如果已经是最新就跳转到向导页或自动登录进主页
                                    checkVersionListener.toNext();
                                }
                            } else {
                                checkVersionListener.toNext();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            checkVersionListener.toNext();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        checkVersionListener.onError(activity.getString(R.string.server_net_error));
                    }
                });
    }

    /**
     * 校验版本监听.
     *
     * @author 史明松
     */
    public interface CheckVersionListener {
        /**
         * 跳转到引导或登录界面.
         * @author 史明松
         * @update 2014年6月23日 下午12:52:49
         */
        void toNext();

        /**
         * 立刻更新.
         * @author 史明松
         * @update 2014年6月20日 下午5:34:23
         */
        void updateNewVersion(Version version);

        void onError(String errorMessage);

    }
}
