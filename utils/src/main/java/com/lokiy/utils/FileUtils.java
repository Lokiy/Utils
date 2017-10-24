package com.lokiy.utils;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileUtils
 * Created by Lokiy on 2017/10/24.
 * Version:1
 */
public class FileUtils {
	public static String getDataFolderPath(Context paramContext) {
		return Environment.getDataDirectory() + "/data/" + paramContext.getPackageName() + "/files";
	}

	public static String getMyFileDir(Context context) {
		return context.getFilesDir().toString();
	}

	public static String getMyCacheDir(Context context) {
		return context.getCacheDir().toString();
	}

	/**
	 * @param fileName file name
	 * @param content content
	 */
	public static void save(Context context, String fileName, String content, int module) {
		try {
			FileOutputStream os = context.openFileOutput(fileName, module);
			os.write(content.getBytes());
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param fileName file name
	 * @return file content
	 */
	public static String read(Context context, String fileName) {

		try {
			FileInputStream fis = context.openFileInput(fileName);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = fis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
			byte[] data = bos.toByteArray();
			fis.close();
			bos.close();
			return new String(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * saveToSDCard
	 *
	 * @param context context
	 * @param file file
	 * @param content content
	 * @throws IOException io
	 */
	public static void saveToSDCard(Context context, File file, String content) throws IOException {

		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content.getBytes());
		fos.close();
	}

	/**
	 * @param file file
	 * @return file content
	 *
	 * @throws IOException io
	 */
	public static String readSDCard(File file) throws IOException {

		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = fis.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		byte[] data = bos.toByteArray();
		fis.close();
		bos.close();

		return new String(data);
	}

	/*获取缓存路径，存储临时文件，可被一键清理和卸载清理*/
	/*
    * 可以看到，当SD卡存在或者SD卡不可被移除的时候，
    * 就调用getExternalCacheDir()方法来获取缓存路径，
    * 否则就调用getCacheDir()方法来获取缓存路径。
    * 前者获取到的就是/sdcard/Android/data/<application package>/cache 这个路径，
    * 而后者获取到的是 /data/data/<application package>/cache 这个路径。*/
	public static File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/*返回缓存路径*/
	public static File getDiskCacheDir(Context context) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath);
	}

	/*判断sd卡可用*/
	public static boolean hasSDCardMounted() {
		String state = Environment.getExternalStorageState();
		if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}