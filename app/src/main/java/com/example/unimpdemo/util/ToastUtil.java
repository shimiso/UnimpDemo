package com.example.unimpdemo.util;

import android.content.Context;
import android.widget.Toast;

import com.example.unimpdemo.BuildConfig;


public class ToastUtil {
	private static Toast toast;
	public static void log(Context context, String message) {
		if( BuildConfig.DEBUG){
			normal(context,message);
		}
	}

	public static void success(Context context, String message) {
		normal(context,message);
	}

	public static void success(Context context, String message, int duration) {
		normal(context,message,duration);
	}
	public static void normal(Context context, String message) {
		if (toast==null){
			toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
		}else {
			toast.setText(message);
		}
		toast.show();
	}

	public static void normal(Context context, String message, int duration) {
		if (toast==null){
			toast = Toast.makeText(context,message,duration);
		}else {
			toast.setText(message);
		}
		toast.show();
	}
	public static void info(Context context, String message) {
		normal(context,message);
	}

	public static void info(Context context, String message, int duration) {
		normal(context,message,duration);
	}
	public static void error(Context context, String message) {
		normal(context,message);
	}

	public static void error(Context context, String message, int duration) {
		normal(context,message,duration);
	}
	public static void warning(Context context, String message) {
		normal(context,message);
    }

	public static void warning(Context context, String message, int duration) {
		normal(context,message,duration);
	}
	public static void reset() {//销毁的时候将Toast制空
		toast = null;
	}
}
