package com.yunos.tv.app.widget.utils;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@SuppressWarnings("rawtypes")
public class ReflectUtils {
	protected final static boolean DEBUG = false;

	// 从com.android.internal.R等资源文件中读取常量
	public static boolean getInternalBoolean(String inerClass, String fieldName) {
		boolean bool = false;
		try {
			Class c = Class.forName(inerClass);
			Object obj = c.newInstance();
			Field field = c.getField(fieldName);
			makeAccessible(field);
			bool = field.getBoolean(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (DEBUG) {
			Log.i("ReflectUtils", "getInternalBoolean bool = " + bool);
		}
		return bool;
	}

	public static int getInternalInt(String inerClass, String fieldName) {
		int id = 0;
		try {
			Class c = Class.forName(inerClass);
			Object obj = c.newInstance();
			Field field = c.getField(fieldName);
			makeAccessible(field);
			id = field.getInt(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (DEBUG) {
			Log.i("ReflectUtils", "getInternalInt id = " + id);
		}
		return id;
	}

	public static int[] getInternalIntArray(String inerClass, String fieldName) {
		int[] id = new int[0];
		try {
			Class c = Class.forName(inerClass);
			Object obj = c.newInstance();
			Field field = c.getField(fieldName);
			makeAccessible(field);
			id = (int[]) field.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (DEBUG) {
			Log.i("ReflectUtils", "getInternalIntArray id = " + id + " id.length = " + id.length);
		}
		return id;
	}

	// 从com.android.internal.R中读取常量 end

	/**
	 * 执行某对象的方法
	 * 
	 * @param owner
	 *            所属对象
	 * @param methodName
	 *            方法名
	 * @param args
	 *            参数数组
	 * @return 方法的返回值
	 * @throws Exception
	 */
	public static Object invokeMethod(final Object object, final String methodName, Class<?>[] parameterTypes, final Object[] parameters) {
		Method method = getDeclaredMethod(object, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
		}

		method.setAccessible(true);

		try {
			return method.invoke(object, parameters);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	public static Object invokeMethod(final Object object, final String methodName, final Object[] parameters) {
		Class[] parameterTypes = new Class[parameters.length];
		for (int i = 0, j = parameters.length; i < j; i++) {
			parameterTypes[i] = parameters[i].getClass();
		}

		Method method = getDeclaredMethod(object, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
		}

		method.setAccessible(true);

		try {
			return method.invoke(object, parameters);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod.
	 *
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	public static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {
		return getDeclaredMethod(object.getClass(), methodName, parameterTypes);
	}

	private static Method getDeclaredMethod(Class<?> object, String methodName, Class<?>[] parameterTypes) {
		for (Class<?> superClass = object; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {// NOSONAR
				// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 不建议使用，目前不能调用父类的方法
	 *
	 * @param owner
	 * @param methodName
	 * @param parameterTypes
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public static Object invokeSuperMethod(Object owner, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {
		Method method = getDeclaredMethod(owner.getClass().getSuperclass(), methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + owner + "]");
		}

		method.setAccessible(true);

		try {
			return method.invoke(owner, parameters);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 得到某个对象的隐藏属性
	 * 
	 * @param owner
	 *            所属对象
	 * @param fieldName
	 *            属性名
	 * @return 隐藏属性的值
	 * @throws Exception
	 */
	public static Object getProperty(Object object, String fieldName) throws Exception {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 强行设置Field可访问.
	 */
	private static void makeAccessible(final Field field) {
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField.
	 *
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	private static Field getDeclaredField(final Object object, final String fieldName) {
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {// NOSONAR
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	private static Field getDeclaredField(Class<?> object, final String fieldName) {
		for (Class<?> superClass = object; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {// NOSONAR
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	public static void setProperty(Object object, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException {
		Field field = getDeclaredField(object, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
		}

		makeAccessible(field);

		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到某个类的静态属性
	 * 
	 * @param className
	 *            类名
	 * @param fieldName
	 *            属性名
	 * @return 隐藏属性的值
	 * @throws Exception
	 */
	public static Object getStaticProperty(String className, String fieldName) {
		try {
			Class<?> ownerClass = Class.forName(className);
			Field field = getDeclaredField(ownerClass, fieldName);
			if (field == null) {
				throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + className + "]");
			}

			makeAccessible(field);

			Object result = null;
			try {
				result = field.get(ownerClass);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object invokeStaticMethod(String className, String methodName, Object[] args) throws Exception {
		Class[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}
		return invokeStaticMethod(className, methodName, argsClass, args);
	}

	public static Object invokeStaticMethod(String className, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
		try {
			Class<?> ownerClass = Class.forName(className);
			Method method = getDeclaredMethod(ownerClass, methodName, parameterTypes);
			if (method == null) {
				throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + className + "]");
			}

			method.setAccessible(true);
			return method.invoke(null, parameters);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 带参数的构造方法使用该方法实例化对象
	 *
	 * @param className
	 *            类名
	 * @param args
	 *            构造方法需要的参数
	 * @return 类的实例对象
	 * @throws Exception
	 */
	public static Object newInstance(String className, Object[] args) throws Exception {
		Class<?> newoneClass = Class.forName(className);
		Class[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}

		Constructor<?> cons = newoneClass.getConstructor(argsClass);
		cons.setAccessible(true);
		return cons.newInstance(args);
	}

	/**
	 * 将反射时的checked exception转换为unchecked exception.
	 */
	public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
		return convertReflectionExceptionToUnchecked(null, e);
	}

	public static RuntimeException convertReflectionExceptionToUnchecked(String desc, Exception e) {
		desc = (desc == null) ? "Unexpected Checked Exception." : desc;
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException || e instanceof NoSuchMethodException) {
			return new IllegalArgumentException(desc, e);
		} else if (e instanceof InvocationTargetException) {
			return new RuntimeException(desc, ((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException(desc, e);
	}
}
