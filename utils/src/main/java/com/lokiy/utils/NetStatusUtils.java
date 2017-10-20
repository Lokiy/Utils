/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lokiy.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.Locale;

/**
 *
 *
 * @author Lokiy
 */
public class NetStatusUtils {

	private static ConnectivityManager connectivityManager;

	/**
	 * new Type<br>
	 * <b> <li>NONE</li><br>
	 * <li>WIFI</li> <br>
	 * <li>CMWAP</li><br>
	 * <li>CMNET</li><br>
	 * </b>
	 *
	 * @author Luki
	 */
	public enum NetType {
		NONE,
		WIFI,
		CMWAP,
		CMNET
	}

	/**
	 * int
	 * @param context context
	 */
	public static void init(Context context) {
		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * get current net type
	 *
	 * @return <li>NONE ：not network</li><br>
	 *         <li>WIFI ：WIFI</li><br>
	 *         <li>CMWAP：WAP</li><br>
	 *         <li>CMNET：NET</li>
	 */
	public static NetType getNetworkType() {
		check();
		NetType netType = NetType.NONE;
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (extraInfo != null && !extraInfo.equals("")) {
				if (extraInfo.toLowerCase(Locale.CHINA).equals("cmnet")) {
					netType = NetType.CMNET;
				} else {
					netType = NetType.CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NetType.WIFI;
		}
		return netType;
	}

	/**
	 * is network connected
	 * @return is network connected
	 */
	public static boolean isNetworkConnected() {
		check();
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	private static void check() {
		if (connectivityManager == null) {
			throw new IllegalArgumentException("please invoke NetStatusUtils.init");
		}
	}

	/**
	 * 获取网络状态
	 * @param  context context
	 * @return  1 WIFI<br>
	 * 			2 2G<br>
	 * 			3 3G<br>
	 * 			4 4G<br>
	 * 			0 NONE
	 */
	public static String getNetworkType(Context context) {
		String strNetworkType = "0";

		NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				strNetworkType = "1";
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				String _strSubTypeName = networkInfo.getSubtypeName();

				// TD-SCDMA   networkType is 17
				int networkType = networkInfo.getSubtype();
				switch (networkType) {
					case TelephonyManager.NETWORK_TYPE_GPRS:
					case TelephonyManager.NETWORK_TYPE_EDGE:
					case TelephonyManager.NETWORK_TYPE_CDMA:
					case TelephonyManager.NETWORK_TYPE_1xRTT:
					case TelephonyManager.NETWORK_TYPE_IDEN: //createApi<8 : replace by 11
						strNetworkType = "2";
						break;
					case TelephonyManager.NETWORK_TYPE_UMTS:
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
					case TelephonyManager.NETWORK_TYPE_HSDPA:
					case TelephonyManager.NETWORK_TYPE_HSUPA:
					case TelephonyManager.NETWORK_TYPE_HSPA:
					case TelephonyManager.NETWORK_TYPE_EVDO_B: //createApi<9 : replace by 14
					case TelephonyManager.NETWORK_TYPE_EHRPD:  //createApi<11 : replace by 12
					case TelephonyManager.NETWORK_TYPE_HSPAP:  //createApi<13 : replace by 15
						strNetworkType = "3";
						break;
					case TelephonyManager.NETWORK_TYPE_LTE:    //createApi<11 : replace by 13
						strNetworkType = "4";
						break;
					default:
						// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
						if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
							strNetworkType = "3";
						} else {
							strNetworkType = "0";
						}

						break;
				}
			}
		}

		return strNetworkType;
	}

}
