package com.bosssoft.platform.installer.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;

public final class ArrayUtil {
	public static String[] NULL_STRINGS = new String[0];

	public static Object[] NULL_OBJECTS = new Object[0];

	public static Class[] NULL_CLASSES = new Class[0];

	public static void copyArray(Object[] sourceObjects, Object[] targetObjects) throws IllegalArgumentException {
		if ((ArrayUtils.isEmpty(sourceObjects)) || (ArrayUtils.isEmpty(targetObjects))) {
			return;
		}

		if (sourceObjects.length != targetObjects.length) {
			throw new IllegalArgumentException("The length of the two arrays must be equal");
		}

		System.arraycopy(sourceObjects, 0, targetObjects, 0, targetObjects.length);
	}

	public static String[] getStringArrayValues(Object value) {
		if ((value instanceof Collection)) {
			String[] stringArray = new String[((Collection) value).size()];
			Iterator iterator = ((Collection) value).iterator();
			int index = 0;
			while (iterator.hasNext()) {
				Object t_Object = iterator.next();
				stringArray[index] = ObjectUtils.toString(t_Object, null);
				index++;
			}

			return stringArray;
		}

		if ((value instanceof Object[])) {
			Object[] objects = (Object[]) value;
			return getStringArrayValues(objects);
		}

		return NULL_STRINGS;
	}

	public static String[] getStringArrayValues(Object[] value) {
		if (ArrayUtils.isEmpty(value)) {
			return NULL_STRINGS;
		}

		String[] stringArray = new String[value.length];

		for (int i = 0; i < value.length; i++) {
			Object object = value[i];
			stringArray[i] = ObjectUtils.toString(object, null);
		}

		return stringArray;
	}

	public static Object[] getArrayValues(Object value) {
		if ((value instanceof Object[])) {
			return (Object[]) value;
		}

		if ((value instanceof Collection)) {
			return ((Collection) value).toArray();
		}

		return null;
	}

	public static boolean hasType(Object[] objects, Class clazz) {
		return hasType(objects, clazz, false);
	}

	public static boolean hasType(Object[] objects, Class clazz, boolean allowSuperType) {
		if (clazz == null) {
			return false;
		}

		if (ArrayUtils.isEmpty(objects)) {
			return false;
		}

		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];

			if (object != null) {
				if (allowSuperType) {
					if (clazz.isAssignableFrom(object.getClass())) {
						return true;
					}

				} else if (clazz == object.getClass()) {
					return true;
				}
			}
		}

		return false;
	}

	public static Object[] sort(Object[] array) {
		if (ArrayUtils.isEmpty(array)) {
			return array;
		}
		Arrays.sort(array);
		return array;
	}

	public static boolean isArray(Object r_Object) {
		if (r_Object == null) {
			return false;
		}

		Class t_Class = r_Object.getClass();
		return t_Class.isArray();
	}
}