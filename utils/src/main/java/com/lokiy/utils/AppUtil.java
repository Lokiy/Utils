package com.lokiy.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppUtil {
	private static TelephonyManager tm;

	/**
	 * 获取当前运行的进程名
	 */
	public static String getProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}

		return null;
	}

	/**
	 * 获取当前运行的所有进程名
	 */
	public static List<String> getProcessName(Context context, String packageName) {
		List<String> list = new ArrayList<String>();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
			if (appProcess.processName.startsWith(packageName)) {
				// System.out.println("p:"+appProcess.processName);
				list.add(appProcess.processName);
			}
		}
		return list;
	}

	/**
	 * 获取当前运行界面的包名
	 */
	public static String getTopPackageName(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
		return cn.getPackageName();
	}

	/**
	 * 实现文本复制功能
	 *
	 */
	@SuppressWarnings("deprecation")
	public static void copy(Context context, String content) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	/**
	 * 实现粘贴功能
	 *
	 */
	@SuppressWarnings("deprecation")
	public static String paste(Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getText().toString().trim();
	}

	/**
	 * 获取当前应用的versionCode
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 获取当前应用的versionName
	 *
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 获取Manifest里面配置的渠道版本
	 * <p>
	 * 2014-11-14
	 * </p>
	 *
	 */
	public static String getMetaData(Context context, String name) {
		String metaData = "";
		try {
			metaData = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metaData;
	}

	// 判断当前设备是否是模拟器。如果返回TRUE，则当前是模拟器，不是返回FALSE
	public static boolean isEmulator(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			return imei != null && imei.equals("000000000000000") || (Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"));
		} catch (Exception ignored) {

		}
		return false;
	}

	public static String getMac(Context context) {
		SharedPreferences config = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		String macSerial = config.getString("_mac", null);
		if (macSerial != null) {
			return macSerial;
		}
		String str = "";

		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str; ) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		if (macSerial != null) {
			config.edit().putString("_mac", macSerial).apply();
		}
		if (macSerial == null) {
			macSerial = "xxxxxx";
		}
		return macSerial;
	}

	public static String getUUID(Context context) {
		try {
			final String tmDevice, tmSerial, androidId;
			tmDevice = "" + getDeviceId(context);
			tmSerial = "" + getSimSerialNumber(context);
			androidId = "" + getAndroidId(context);

			UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
			return deviceUuid.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getDeviceId(Context context) {
		try {
			if (tm == null) {
				tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
			}
			return tm.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getSimSerialNumber(Context context) {
		try {
			if (tm == null) {
				tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
			}
			return tm.getSimSerialNumber();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getAndroidId(Context context) {
		return android.provider.Settings.Secure.getString(context.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	}
}
