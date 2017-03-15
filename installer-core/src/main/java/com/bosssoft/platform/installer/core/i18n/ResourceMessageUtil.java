package com.bosssoft.platform.installer.core.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.bosssoft.platform.installer.core.util.StringUtil;

public class ResourceMessageUtil {
	public static final Locale DEFAULT_LOCALE = new Locale("INSTALL_DEFAULT");

	private static Map<Locale, Object> lockMap = new HashMap<Locale, Object>();

	public static String getExceptionResourceMessage(String message_id) {
		return getExceptionResourceMessage(message_id, Locale.getDefault());
	}

	public static String getExceptionResourceMessage(String message_id, Locale locale) {
		return getResourceMessage("EXCEPTION", message_id, locale);
	}

	public static String getI18nResourceMessage(String message_id) {
		return getI18nResourceMessage(message_id, Locale.getDefault());
	}

	public static String getI18nResourceMessage(String message_id, Locale locale) {
		return getResourceMessage("I18N", message_id, locale);
	}

	public static String getResourceMessage(String resourceType, String message_id, Locale locale) {
		if (message_id == null) {
			return null;
		}

		Properties prop = autoSearchProperties(ResourceRegister.PropertiesCache.SYS_PROPERTY, ResourceRegister.ResourceCache.SYS_RESOURCE, resourceType, locale, true);
		String message = prop.getProperty(message_id);

		if (message == null) {
			prop = autoSearchProperties(ResourceRegister.PropertiesCache.USER_PROPERTY, ResourceRegister.ResourceCache.USER_RESOURCE, resourceType, locale, true);
			message = prop.getProperty(message_id);

			if (message == null) {
				prop = ResourceRegister.PropertiesCache.SYS_PROPERTY.get(resourceType, DEFAULT_LOCALE);
				message = prop.getProperty(message_id);

				if (message == null)
					prop = ResourceRegister.PropertiesCache.USER_PROPERTY.get(resourceType, DEFAULT_LOCALE);
			}
		} else {
			return message;
		}

		return prop.getProperty(message_id);
	}

	private static synchronized Object getLock(Locale locale) {
		Object lock = lockMap.get(locale);
		if (lock == null) {
			lock = new Object();
			lockMap.put(locale, lock);
		}
		return lock;
	}

	private static Properties autoSearchProperties(ResourceRegister.PropertiesCache propertyCache, ResourceRegister.ResourceCache resourceCache, String resourceType, Locale locale,
			boolean isOnlyOnce) {
		Properties propLocale = propertyCache.get(resourceType, locale);

		if ((propLocale == null) || ((!isOnlyOnce) && (propLocale.isEmpty()))) {
			Object lock = getLock(locale);
			synchronized (lock) {
				propLocale = propertyCache.get(resourceType, locale);
				if ((propLocale == null) || ((!isOnlyOnce) && (propLocale.isEmpty()))) {
					registerLocalePropperties(propertyCache, resourceCache, resourceType, locale);
					propLocale = propertyCache.get(resourceType, locale);
					if (propLocale == null) {
						propLocale = new Properties();

						if (isOnlyOnce) {
							propertyCache.put(resourceType, locale, propLocale);
						}
					}
				}
			}
		}
		return propLocale;
	}

	private static void registerLocalePropperties(ResourceRegister.PropertiesCache propertyCache, ResourceRegister.ResourceCache resourceCache, String resourceType,
			Locale locale) {
		String[] resoureFiles = resourceCache.getResourceFileNames(resourceType, DEFAULT_LOCALE);
		for (String resource : resoureFiles) {
			int index = resource.lastIndexOf('.');
			String localFileName = resource;
			String extention = "";

			if (index != -1) {
				localFileName = resource.substring(0, index);
				extention = resource.substring(index);
			}
			localFileName = StringUtil.concat(new Object[] { localFileName, "_", locale.toString(), extention });
			Properties prop = ResourceRegister.loadResourceProperties(localFileName);
			if (!prop.isEmpty()) {
				resourceCache.registerResource(resourceType, locale, localFileName);
				propertyCache.put(resourceType, locale, prop);
			}
		}
	}
}