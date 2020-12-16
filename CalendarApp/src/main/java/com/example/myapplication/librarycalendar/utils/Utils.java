package com.example.myapplication.librarycalendar.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

/**
 * @author 朱城委
 *
 * @date 2017年2月13日 上午9:41:06
 */
public class Utils {
	private static Toast toast;
	
	/**
	 * 避免Toast重复显示
	 * 
	 * @param resId
	 *            要显示的字符串Id
	 */
	public static void toast(Context context, int resId) {
		String message = context.getResources().getString(resId);
		toast(context, message, Toast.LENGTH_SHORT);
	}

	/**
	 * 避免Toast重复显示
	 * 
	 * @param message
	 *            要显示的字符串
	 */
	public static void toast(Context context, String message) {
		toast(context, message, Toast.LENGTH_SHORT);
	}
	
	public static void toast(Context context, int resId, int duration) {
		String message = context.getResources().getString(resId);
		toast(context, message, duration);
	}
	
	public static void toast(Context context, String message, int duration) {
		if (toast == null) {
			toast = Toast.makeText(context.getApplicationContext(), message, duration);
		} else {
			toast.setText(message);
		}
		toast.show();
	}
	
	/**
	 * 应用是否有某项权限。
	 * @param context
	 * @param permName 权限名，比如：android.permission.CAMERA
	 * @return 如果有此项权限，返回true；没有，返回false
	 */
	public static boolean hasPermission(Context context, String permName) {
		boolean result = true;
	
		if (getTargetSdkVersion(context) >= 23) {
			int hasPermission = ContextCompat.checkSelfPermission(context, permName);
			result = hasPermission == PackageManager.PERMISSION_GRANTED;
		} else {
			int hasPermission = PermissionChecker.checkSelfPermission(context, permName);
			result = hasPermission == PermissionChecker.PERMISSION_GRANTED;
		}
	
		return result;
	}
	
	/**
	 * 获取应用targetSdkVersion
	 * @param context
	 * @return 如果获取失败，返回{@link Build.VERSION#SDK_INT}
	 */
	public static int getTargetSdkVersion(Context context) {
		int targetSdkVersion = Build.VERSION.SDK_INT;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			targetSdkVersion = info.applicationInfo.targetSdkVersion;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return targetSdkVersion;
	}
}
