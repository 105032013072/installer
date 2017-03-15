package com.bosssoft.platform.installer.core.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.bosssoft.platform.installer.core.util.UrlUtil;

public class ResourceRegister {
	public static final String EXCEPTION = "EXCEPTION";
	public static final String I18N = "I18N";
	static Map<String, Locale> language_country_enum = new HashMap<String, Locale>();
	public static final Locale DEFAULT_LOCALE = new Locale("INSTALL_DEFAULT");

	public static void registerResource(String resourceType, String fileName) {
		Locale locale = getResourceLoacle(fileName);
		ResourceCache.USER_RESOURCE.registerResource(resourceType, locale, fileName);
		PropertiesCache.USER_PROPERTY.put(resourceType, locale, loadResourceProperties(fileName));
	}

	public static void unRegisterResource(String resourceType) {
		ResourceCache.USER_RESOURCE.unRegisterResource(resourceType);
		PropertiesCache.USER_PROPERTY.clear(resourceType);
	}

	public static void unRegisterResource(String resourceType, String fileName) {
		Locale locale = getResourceLoacle(fileName);
		ResourceCache.USER_RESOURCE.unRegisterResource(resourceType, locale, fileName);
		Properties property = PropertiesCache.USER_PROPERTY.get(resourceType, locale);
		if (property != null)
			for (Iterator localIterator = loadResourceProperties(fileName).keySet().iterator(); localIterator.hasNext();) {
				Object key = localIterator.next();
				property.remove(key);
			}
	}

	public static void reloadResource(String resourceType) {
		PropertiesCache.USER_PROPERTY.clear(resourceType);
		Map localResourceMap = (Map) ResourceCache.USER_RESOURCE.resource_cache.get(resourceType);
		if (localResourceMap != null) {
			for (Iterator localIterator1 = localResourceMap.entrySet().iterator(); localIterator1.hasNext();) {
				Map.Entry entry = (Map.Entry) localIterator1.next();
				Iterator localIterator2 = ((Set) entry.getValue()).iterator();
				while (localIterator2.hasNext()) {
					String resource = (String) localIterator2.next();
					PropertiesCache.USER_PROPERTY.put(resourceType, (Locale) entry.getKey(), loadResourceProperties(resource));
				}

			}
		}
	}

	public static void reloadResource(String resourceType, String fileName) {
		registerResource(resourceType, fileName);
	}

	public static Locale getResourceLoacle(String resource) {
		if (resource == null) {
			return DEFAULT_LOCALE;
		}
		int index = resource.lastIndexOf('.');
		String localFileName = resource;

		if (index != -1) {
			localFileName = resource.substring(0, index);
		}
		index = localFileName.lastIndexOf('_');

		if (index != -1) {
			String sufix = localFileName.substring(index + 1);
			Locale locale = getLocale(sufix);
			if (locale != null) {
				return locale;
			}

			String prefix = localFileName.substring(0, index);
			index = prefix.lastIndexOf('_');

			if (index != -1) {
				sufix = localFileName.substring(index + 1);
				locale = getLocale(sufix);
				if (locale != null) {
					return locale;
				}

				prefix = prefix.substring(0, index);
				index = prefix.lastIndexOf('_');

				if (index != -1) {
					sufix = localFileName.substring(index + 1);
					locale = getLocale(sufix);
					if (locale != null) {
						return locale;
					}
				}

			}

		}

		return DEFAULT_LOCALE;
	}

	public static Locale getLocale(String locale) {
		return (Locale) language_country_enum.get(locale == null ? "" : locale.toLowerCase());
	}

	public static Properties loadResourceProperties(String resource) {
		Properties prop = new Properties();

		if (resource != null) {
			InputStream stream = null;
			try {
				stream = UrlUtil.getURL(resource, ResourceRegister.class.getClassLoader()).openStream();
				prop.load(stream);
			} catch (Throwable localThrowable) {
				if (stream != null)
					try {
						stream.close();
					} catch (IOException ignore) {
						ignore.printStackTrace();
					}
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException ignore) {
						ignore.printStackTrace();
					}
				}
			}
		}
		return prop;
	}

	static class PropertiesCache {
		private Map<String, Map<Locale, Properties>> cache = new HashMap<String, Map<Locale, Properties>>(2);

		static PropertiesCache SYS_PROPERTY = new PropertiesCache();

		static PropertiesCache USER_PROPERTY = new PropertiesCache();

		static {
			SYS_PROPERTY.put("EXCEPTION", ResourceMessageUtil.DEFAULT_LOCALE, null);
			SYS_PROPERTY.put("I18N", ResourceMessageUtil.DEFAULT_LOCALE, null);

			USER_PROPERTY.put("EXCEPTION", ResourceMessageUtil.DEFAULT_LOCALE, null);
			USER_PROPERTY.put("I18N", ResourceMessageUtil.DEFAULT_LOCALE, null);
		}

		public Map<Locale, Properties> get(String resourceType) {
			return (Map) this.cache.get(resourceType);
		}

		public Properties get(String resourceType, Locale locale) {
			Map localeMap = (Map) this.cache.get(resourceType);
			if (localeMap == null) {
				return null;
			}
			return (Properties) localeMap.get(locale);
		}

		public void put(String resourceType, Locale locale, Properties properties) {
			Map localeMap = (Map) this.cache.get(resourceType);
			if (localeMap == null) {
				localeMap = new HashMap(5);
			}
			Properties prop = (Properties) localeMap.get(locale);
			if (prop == null) {
				prop = new Properties();
			}
			if (properties != null) {
				prop.putAll(properties);
			}
			localeMap.put(locale, prop);
			this.cache.put(resourceType, localeMap);
		}

		void clear(String resourceType) {
			Map map = (Map) this.cache.get(resourceType);
			if (map != null) {
				map.clear();
				this.cache.remove(resourceType);
			}
		}
	}

	static class ResourceCache {
		Map<String, Map<Locale, Set<String>>> resource_cache = new HashMap<String, Map<Locale, Set<String>>>(2);

		static ResourceCache SYS_RESOURCE = new ResourceCache();

		static ResourceCache USER_RESOURCE = new ResourceCache();

		static {
			for (Locale locale : Locale.getAvailableLocales()) {
				ResourceRegister.language_country_enum.put(locale.toString().toLowerCase(), locale);
			}

			SYS_RESOURCE.registerResource("EXCEPTION", ResourceMessageUtil.DEFAULT_LOCALE, null);
			SYS_RESOURCE.registerResource("I18N", ResourceMessageUtil.DEFAULT_LOCALE, null);

			USER_RESOURCE.registerResource("EXCEPTION", ResourceMessageUtil.DEFAULT_LOCALE, null);
			USER_RESOURCE.registerResource("I18N", ResourceMessageUtil.DEFAULT_LOCALE, null);
		}

		String[] getResourceFileNames(String resourceType, Locale locale) {
			Map localResourceMap = (Map) this.resource_cache.get(resourceType);
			if (localResourceMap != null) {
				Set set = (Set) localResourceMap.get(locale);
				if (set != null) {
					return (String[]) set.toArray(new String[0]);
				}
			}
			return new String[0];
		}

		void registerResource(String resourceType, Locale locale, String resource) {
			Map localResourceMap = (Map) this.resource_cache.get(resourceType);
			if (localResourceMap == null) {
				localResourceMap = new HashMap(5);
			}
			Set set = (Set) localResourceMap.get(locale);
			if (set == null) {
				set = new HashSet(10);
			}
			if (resource != null) {
				set.add(resource);
			}
			localResourceMap.put(locale, set);
			this.resource_cache.put(resourceType, localResourceMap);
		}

		void unRegisterResource(String resourceType) {
			Map localResourceMap = (Map) this.resource_cache.get(resourceType);
			if (localResourceMap != null) {
				localResourceMap.clear();
			}
			this.resource_cache.remove(resourceType);
		}

		void unRegisterResource(String resourceType, Locale locale, String resource) {
			if (resource == null) {
				return;
			}
			Map localResourceMap = (Map) this.resource_cache.get(resourceType);
			if (localResourceMap != null) {
				Set set = (Set) localResourceMap.get(locale);
				if ((set != null) && (resource != null))
					set.remove(resource);
			}
		}
	}
}