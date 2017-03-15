package com.bosssoft.platform.installer.core.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import org.dom4j.Document;

public class PathUtil {
	public static String getPathFromClass(Class cls) throws IOException {
		String path = null;
		if (cls == null) {
			throw new NullPointerException();
		}
		URL url = getClassLocationURL(cls);
		if (url != null) {
			path = url.getPath();
			if ("jar".equalsIgnoreCase(url.getProtocol())) {
				try {
					path = new URL(path).getPath();
				} catch (MalformedURLException localMalformedURLException) {
				}
				int location = path.indexOf("!/");
				if (location != -1) {
					path = path.substring(0, location);
				}
			}
			File file = new File(path);
			path = URLDecoder.decode(file.getCanonicalPath(), "UTF-8");
		}
		return path;
	}

	public static String getFullPathRelateClass(String relatedPath, Class cls) throws IOException {
		String path = null;
		if (relatedPath == null) {
			throw new NullPointerException();
		}
		String clsPath = getPathFromClass(cls);
		File clsFile = new File(clsPath);
		String tempPath = clsFile.getParent() + File.separator + relatedPath;
		File file = new File(tempPath);
		path = URLDecoder.decode(file.getCanonicalPath(), "UTF-8");
		return path;
	}

	private static URL getClassLocationURL(Class cls) {
		if (cls == null)
			throw new IllegalArgumentException("null input: cls");
		URL result = null;
		String clsAsResource = cls.getName().replace('.', '/').concat(".class");
		ProtectionDomain pd = cls.getProtectionDomain();

		if (pd != null) {
			CodeSource cs = pd.getCodeSource();

			if (cs != null) {
				result = cs.getLocation();
			}
			if (result != null) {
				if ("file".equals(result.getProtocol())) {
					try {
						if ((result.toExternalForm().endsWith(".jar")) || (result.toExternalForm().endsWith(".zip")))
							result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(clsAsResource));
						else if (new File(result.getFile()).isDirectory())
							result = new URL(result, clsAsResource);
					} catch (MalformedURLException localMalformedURLException) {
					}
				}
			}
		}
		if (result == null) {
			ClassLoader clsLoader = cls.getClassLoader();
			result = clsLoader != null ? clsLoader.getResource(clsAsResource) : ClassLoader.getSystemResource(clsAsResource);
		}
		return result;
	}

	public static void main(String[] args) {
		try {
			System.out.println(getPathFromClass(Document.class));
			System.out.println(getFullPathRelateClass("../resouce/", Document.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}