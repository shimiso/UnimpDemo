package com.example.unimpdemo.base;

import android.os.Bundle;
import com.example.unimpdemo.MainApp;
import com.example.unimpdemo.util.AndroidUtil;
import com.example.unimpdemo.util.SharePreferenceUtil;
import com.example.unimpdemo.util.ToastUtil;
import androidx.fragment.app.Fragment;

/**
 * Fragment基类
 */
public class BaseFragment extends Fragment {
    /** 共享存储工具类 **/
    protected SharePreferenceUtil spUtil;
    /** Application **/
    protected MainApp mainApp;
    /** Android工具类 **/
    protected AndroidUtil androidUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.spUtil = new SharePreferenceUtil(getActivity());
        this.mainApp = (MainApp)getActivity().getApplication();
        this.androidUtil = AndroidUtil.init(getActivity(),spUtil, mainApp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ToastUtil.reset();//销毁的时候将Toast制空
    }

    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }

}
