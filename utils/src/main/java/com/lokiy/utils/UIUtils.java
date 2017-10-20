/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lokiy.utils;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Locale;
import java.util.Timer;

/**
 * @author Luki
 */
@SuppressWarnings("unused")
public class UIUtils {

	private static InputMethodManager imm;
	private static Timer timer = new Timer(true);
	private static Handler handler = new Handler();

	/**
	 * dp to px
	 * @param context context
	 * @param dp dpValue
	 * @return px value
	 */
	public static int dp2px(Context context, float dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * px to dp
	 * @param context context
	 * @param px spValue
	 * @return dip value
	 */
	public static int px2dp(Context context, int px) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	/**
	 * sp to px
	 * @param context context
	 * @param spValue spValue
	 * @return px value
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * screen width
	 * @param context context
	 * @return    screenWidth
	 */
	public static int screenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * screen height
	 * @param context context
	 * @return    screenHeight
	 */
	public static int screenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * hide inputmethod
	 * @param view view
	 * @return    hideInputMethod
	 */
	public static boolean hideInputMethod(View view) {
		if (view == null) {
			return false;
		}
		if (imm == null) {
			imm = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * show inputmethod
	 * @param view view
	 * @return    showInputMethod
	 */
	public static boolean showInputMethod(final View view) {
		if (view == null) {
			return false;
		}
		if (imm == null) {
			imm = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		view.requestFocusFromTouch();
		view.requestFocus();
		return imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * toggle inputmethod
	 * @param context context
	 */
	public static void toggleInputMethod(Context context) {
		if (imm == null) {
			imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * is inputmethod active
	 * @param context context
	 * @return isInputMethodActive
	 */
	public static boolean isInputMethodActive(Context context) {
		if (imm == null) {
			imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		return imm.isActive();
	}
}
