/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lokiy.utils;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;

public class ReflectUtils {

	/**
	 * @param item item
	 * @param field filed
	 * @return field value
	 *
	 * @throws Exception exception
	 */
	public static Object getFieldValue(Object item, String field) throws Exception {
		Class<?> cls = item.getClass();
		Field f = null;
		try {
			f = cls.getDeclaredField(field);
		} catch (Exception ignored) {
		}
		if (f == null) {
			f = cls.getField(field);
		}
		if (f == null) {
			throw new NullPointerException("can't find field " + field + " in Class " + item.getClass());
		}
		f.setAccessible(true);
		return f.get(item);
	}

	/**
	 * @param item item
	 * @param field field
	 *
	 * @throws Exception exception
	 */
	public static void setFieldValue(Object item, String field, Object value) throws Exception {
		Class<?> cls = item.getClass();
		Field f = null;
		try {
			f = cls.getDeclaredField(field);
		} catch (Exception ignored) {
		}
		if (f == null) {
			f = cls.getField(field);
		}
		if (f == null) {
			throw new NullPointerException("can't find field " + field + " in Class " + item.getClass());
		}
		f.setAccessible(true);
		f.set(item, value);
	}

	/**
	 * @param item item
	 * @param method method
	 * @param parameterTypes parameterTypes
	 * @param args args
	 * @return method value
	 *
	 * @throws Exception exception
	 */
	public static Object getMethodValue(Object item, String method, Class<?>[] parameterTypes, Object... args) throws Exception {
		Class<?> cls = item.getClass();
		Method m = null;
		try {
			m = cls.getDeclaredMethod(method, parameterTypes);
		} catch (Exception ignored) {
		}
		if (m == null) {
			m = cls.getMethod(method, parameterTypes);
		}
		if (m == null) {
			throw new NullPointerException("can't find method " + method + " in Class " + item.getClass());
		}
		m.setAccessible(true);
		return m.invoke(item, args);
	}

	/**
	 * @param className className
	 * @return forName
	 *
	 * @throws Exception exception
	 */
	public static Object getClassInstance(String className) throws Exception {
		Class<?> cls = Class.forName(className);
		return cls.newInstance();
	}

	/**
	 * is normal GenericType(include Long,long,String,Double,double...)
	 *
	 * @param type type
	 * @return is normal generic type
	 */
	public static boolean isNormalGenericType(Type type) {
		return type == Long.class || type == long.class || type == String.class || type == Double.class || type == double.class ||
				type == Integer.class || type == int.class || type == Float.class || type == float.class || type == Short.class ||
				type == short.class || type == Byte[].class || type == byte[].class || type == boolean.class || type == Boolean.class;
	}

	/**
	 *
	 * @param field field
	 * @param <T> t
	 * @return class
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getFieldClass(Field field) {
		Class<?> type2 = field.getType();
		Type type = field.getGenericType();
		Class<T> clazz = null;
		if ((type2 == List.class || type2 == ArrayList.class) && type instanceof ParameterizedType) {
			clazz = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
		} else if (type instanceof Class) {
			clazz = (Class<T>) type;
		}
		return clazz;
	}

	/**
	 * has Parameterless constructor
	 *
	 * @param clazz class
	 * @return has parameterless ,return true.otherwise false.
	 */
	public static boolean hasParameterlessConstructor(Class<?> clazz) {
		try {
			clazz.newInstance();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 *
	 * @param clazz class
	 * @param name field name
	 * @return field
	 */
	public static Field getField(Class<?> clazz, String name) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (Exception e) {
			if (clazz.getSuperclass() != null) {
				field = getField(clazz.getSuperclass(), name);
			}
		}
		return field;
	}

	/**
	 * @param cls class
	 * @param method method
	 * @param parameterTypes parameterTypes
	 * @return method value
	 *
	 * @throws Exception exception
	 */
	public static Method getMethod(Class<?> cls, String method, Class<?>... parameterTypes) throws Exception {
		Method m = null;
		try {
			m = cls.getDeclaredMethod(method, parameterTypes);
		} catch (Exception ignored) {
		}
		if (m == null) {
			m = cls.getMethod(method, parameterTypes);
		}
		if (m == null) {
			throw new NullPointerException("can't find method " + method + " in Class " + cls);
		}
		m.setAccessible(true);
		return m;
	}

	/**
	 *  load class
	 * @param context context
	 * @param className className
	 * @param filePath filePath
	 * @param fileName fileName
	 * @param clazz clazz
	 * @param defaultClazz defaultClazz
	 * @param <T> t
	 * @return class
	 */
	@SuppressWarnings({
			"unchecked",
			"unused"
	})
	public static <T> Class<? extends T> loadClass(Context context, String className, String filePath, String fileName, Class<T> clazz, Class<? extends T> defaultClazz) {
		try {
			final File dexInternalStoragePath = new File(filePath, fileName);
			final File optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE);

			String from = dexInternalStoragePath.getAbsolutePath();
			String to = optimizedDexOutputPath.getAbsolutePath();

			ClassLoader cl = new DexClassLoader(from, to, null, context.getClassLoader());
			return (Class<? extends T>) cl.loadClass(className);

		} catch (Exception e) {
			return defaultClazz;
		}
	}

}
