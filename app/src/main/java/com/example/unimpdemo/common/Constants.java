package com.example.unimpdemo.common;

import android.graphics.Color;

/**
 * 
 * 系统全局配置.
 * @author 史明松
 */
public interface Constants {
	//当前登录用户ID
	String CURRENT_USER_NAME = "current_user_name";
	String CURRENT_IS_BCIS = "is_bcis";
	String INVALID_GRANT = "invalid_grant";
	String INVALID_USERNAME_OR_PASSWORD = "invalid_username_or_password";


	// 乐成养老群组
	String GROUP_TYPE_YCYL = "YCYL";
	// 机构固定群组
	String GROUP_TYPE_ORGS = "ORGS";
	// 员工自建群组
	String GROUP_TYPE_YCGROUP = "YCGROUP";
	//文件传输助手
	String FILE_HELPER = "filehelper";
	//虚拟消息
	String SYSTEM_MSG = "SystemMsg";
	/**
	 * 地图中绘制多边形、圆形的边界颜色
	 * @since 3.3.0
	 */
	int STROKE_COLOR = Color.argb(100, 63, 145, 252);
	/**
	 * 地图中绘制多边形、圆形的填充颜色
	 * @since 3.3.0
	 */
	int FILL_COLOR = Color.argb(40, 118, 212, 243);

	/**
	 * 地图中绘制多边形、圆形的边框宽度
	 * @since 3.3.0
	 */
	float STROKE_WIDTH = 5F;


}