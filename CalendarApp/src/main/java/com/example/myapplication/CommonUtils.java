package com.example.myapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

public class CommonUtils {
	private static final String TAG = "CommonUtils";

	private static Toast toast = null;

	public static final int MIN_PAD_WIDTH = 0;
	public static final int MIN_PAD_HEIGHT = 0;
	public static final int MIN_PAD_SIZE = 0;

	public static boolean isPad(Context applicationContext) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		float density = dm.density; 	// 屏幕密度（0.75 / 1.0 / 1.5）
		double diagonalPixels = Math.sqrt(Math.pow(screenWidth, 2) + Math.pow(screenHeight, 2));
		double screenSize = diagonalPixels / (160 * density);
		return screenSize > MIN_PAD_SIZE;
	}

	/**
	 * 把时间转换成指定格式，比如“yyyy-MM-dd HH:mm:ss”
	 * 
	 * @param milis
	 *            时间
	 * @param pattern
	 *            时间格式，比如“yyyy-MM-dd HH:mm:ss”
	 * @return
	 */
	public static String dateTime(long milis, String pattern) {
		return new SimpleDateFormat(pattern, Locale.US).format(new Date(milis));
	}

	/**
	 * 获取当前时间
	 * 
	 * @param format
	 *            获取时间的格式，比如"yyyy-MM-dd HH:mm:ss"
	 * @return 返回当前时间
	 */
	public static String getCurrentTime(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
		Date curDate = new Date(System.currentTimeMillis());
		String date = formatter.format(curDate);

		return date;
	}
	
	/**
	 * 将时间转换为时间戳
	 * @param time 要转换的时间
	 * @param format {@code time}的时间格式，如“yyyyMMddHHmmss”
	 * @return
	 */
    public static long dateToStamp(String time, String format) {
        long result = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        Date date;
        
		try {
			date = simpleDateFormat.parse(time);
			result = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtil.e(TAG, "转换时间戳失败");
			return result;
		}
		
        return result;
    }

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
	 * 字符串转换为JSON
	 * 
	 * @param string
	 * @return 如果转换失败，则返回没有name/value的JSON对象。
	 */
	public static JSONObject stringToJson(String string) {
		JSONObject json = new JSONObject();
		try {
			json = new JSONObject(string);
		} catch (JSONException e) {
			LogUtil.e(TAG, "String 转换  JSON失败\n" + e.getMessage());
			return json;
		}
		return json;
	}

	/**
	 * 获取应用版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			LogUtil.e(TAG, "获取应用版本号失败");
			return "获取应用版本号失败";
		}
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

	/**
	 * 检查设备是否连接网络
	 * 
	 * @param context
	 * @return 如果链接到网络，返回true，否则返回false
	 */
	public static Boolean isNetworkConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}

		return true;
	}

	/**
	 * 获取连接网络类型(3G/4G/wifi,不包含运营商信息)
	 * 
	 * @param context
	 * @return 返回结果中，不包含运营商，返回连接网络类型(3G/4G/wifi)，如果网络未连接，返回"";
	 */
	public static String getNetworkTypeNoProvider(Context context) {
		String strNetworkType = "";

		NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				strNetworkType = "wifi";
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				String _strSubTypeName = networkInfo.getSubtypeName();
				LogUtil.d(TAG, "Network getSubtypeName : " + _strSubTypeName);

				// TD-SCDMA networkType is 17
				int networkType = networkInfo.getSubtype();
				switch (networkType) {
				case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2G
				case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2G
				case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2G
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by 11
					strNetworkType = "2G";
					break;

				case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3G
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by 14
				case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by 12
				case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by 15
					strNetworkType = "3G";
					break;

				case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by 13
					strNetworkType = "4G";
					break;

				default:
					if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") ||
							_strSubTypeName.equalsIgnoreCase("WCDMA") ||
							_strSubTypeName.equalsIgnoreCase("CDMA2000")) {
						strNetworkType = "3G";
					}
					else {
						strNetworkType = _strSubTypeName;
					}
					break;
				}
				LogUtil.d(TAG, "Network getSubtype : " + Integer.valueOf(networkType).toString());
			}
		}

		LogUtil.d(TAG, "Network Type : " + strNetworkType);
		return strNetworkType;
	}
	
	/**
	 * 获取连接网络类型(3G/4G/wifi,包含运营商信息)
	 * 
	 * @param context
	 * @return 返回连接网络类型(运营商3G/4G/wifi)，如果网络未连接，返回"";
	 */
	public static String getNetworkType(Context context) {
		String networkType = "";
		networkType = getNetworkTypeNoProvider(context);
		
		// 如果使用的数据流量，则添加运营商信息
		if(networkType.contains("G")) {
			networkType = getProvider(context) + networkType;
		}
		
		return networkType;
	}

	/**
	 * 获取运营商
	 * 
	 * @return 中国移动/中国联通/中国电信/未知
	 */
	public static String getProvider(Context context) {
		String provider = "未知";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String IMSI = telephonyManager.getSubscriberId();
			LogUtil.d(TAG, "getProvider.IMSI:" + IMSI);
			if (IMSI == null) {
				if (TelephonyManager.SIM_STATE_READY == telephonyManager.getSimState()) {
					String operator = telephonyManager.getSimOperator();
					LogUtil.d(TAG, "getProvider.operator:" + operator);
					if (operator != null) {
						if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
							provider = "中国移动";
						} else if (operator.equals("46001")) {
							provider = "中国联通";
						} else if (operator.equals("46003")) {
							provider = "中国电信";
						}
					}
				}
			} else {
				if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
					provider = "中国移动";
				} else if (IMSI.startsWith("46001")) {
					provider = "中国联通";
				} else if (IMSI.startsWith("46003")) {
					provider = "中国电信";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return provider;
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
	 * 隐藏软键盘
	 * @param activity
	 */
	public static void hideSoftInput(Activity activity) {
		if (activity.getCurrentFocus().getWindowToken() != null) {
			InputMethodManager manager = (InputMethodManager) activity.getApplicationContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/**
	 * 判断字符串是否为网址
	 * 
	 * @param
	 * @return 如果是返回true,否则返回false
	 */
	public static boolean isUrl(String str) {
		String regex = "^((https|http|ftp|rtsp|mms)?://)"
				+ "?(([0-9a-zA-Z_!~*'().&=+$%-]+: )?[0-9a-zA-Z_!~*'().&=+$%-]+@)?" 	// ftp的user@
				+ "(([0-9]{1,3}\\.){3}[0-9]{1,3}" 									// IP形式的URL- 199.194.52.184
				+ "|" 							// 允许IP和DOMAIN（域名）
				+ "([0-9a-zA-Z_!~*'()-]+\\.)*" 	// 域名- www.
				+ "([0-9a-zA-Z][0-9a-zA-Z-]{0,61})?[0-9a-zA-Z]\\."	// 二级域名
				+ "[a-zA-Z]{2,6})"		// first level domain- .com or .museum
				+ "(:[0-9]{1,4})?"		// 端口- :80
				+ "((/?)|" + "(/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)$";
		return match(regex, str);
	}

	/**
	 * @param regex
	 *            正则表达式字符串
	 * @param str
	 *            要匹配的字符串
	 * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	 */
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}
