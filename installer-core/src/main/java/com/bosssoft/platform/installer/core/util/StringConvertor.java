package com.bosssoft.platform.installer.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

public class StringConvertor {
	static transient Logger logger = Logger.getLogger(StringConvertor.class);

	public static final String replace(String src, Properties props) {
		return replace(src, "${", "}", props);
	}

    //将string对象类型转为object
    public final static Object makeObj(final String item)  {
        return new Object() { public String toString() { return item; } };
      }
    
	public static final String[] parsePath(String path) {
		List<String> list = new ArrayList<String>();
		path = path.trim();

		if (path.indexOf(",") >= 0) {
			StringTokenizer st = new StringTokenizer(path, ",");
			while (st.hasMoreTokens())
				list.add(st.nextToken());
		} else if (path.indexOf(";") >= 0) {
			StringTokenizer st = new StringTokenizer(path, ";");
			while (st.hasMoreTokens())
				list.add(st.nextToken());
		} else {
			list.add(path);
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	public static final String replace(String src, String prefix, String suffix, Properties props) {
		int len1 = prefix.length();
		int len2 = suffix.length();

		StringBuffer sb = new StringBuffer();

		int index1 = src.indexOf(prefix);
		while (index1 >= 0) {
			sb.append(src.substring(0, index1));
			src = src.substring(index1 + len1);
			if (src.startsWith(prefix)) {
				sb.append(prefix);
				break;
			}
			int index2 = src.indexOf(suffix);
			if (index2 >= 0) {
				String t = src.substring(0, index2);
				String sp = props.getProperty(t);
				if (sp == null) {
					sp = " ";
				}
				sb.append(sp);
				src = src.substring(index2 + len2);
				index1 = src.indexOf(prefix);
			} else {
				sb.append(prefix);
				break;
			}
		}

		sb.append(src);
		return new String(sb);
	}

	public static final String replacePrefixSuffix(String src, String prefix, String suffix, Properties props) {
		int len1 = prefix.length();
		int len2 = suffix.length();

		StringBuffer sb = new StringBuffer();

		int index1 = src.indexOf(prefix);
		while (index1 >= 0) {
			sb.append(src.substring(0, index1));
			src = src.substring(index1 + len1);
			if (src.startsWith(prefix)) {
				sb.append(prefix);
				break;
			}
			int index2 = src.indexOf(suffix);
			if (index2 >= 0) {
				String t = prefix + suffix;
				String sp = props.getProperty(t);
				if (sp == null) {
					sp = " ";
					logger.warn("Can not find variable value of " + t + ",variable " + t + " will be deleted.Note:Case sensitive.");
				}
				sb.append(sp);
				src = src.substring(index2 + len2);
				index1 = src.indexOf(prefix);
			} else {
				sb.append(prefix);
				break;
			}
		}

		sb.append(src);
		return new String(sb);
	}

	public static final String replace(String src, String prefix, String suffix) {
		Properties propies = System.getProperties();
		return replace(src, prefix, suffix, propies);
	}

	public static final String replace(String src) {
		Properties propies = System.getProperties();
		return replace(src, "${", "}", propies);
	}

	public static void replaceFile(String filePath, String prefix, String suffix, Properties props) throws IOException {
		if (!new File(filePath).exists()) {
			Logger.getLogger(StringConvertor.class).debug("The file [" + filePath + "] is not found");
		}

		CharArrayWriter caw = new CharArrayWriter(8096);
		BufferedWriter w = new BufferedWriter(caw);
		BufferedReader r = new BufferedReader(new FileReader(filePath));

		String line = r.readLine();
		while (line != null) {
			String rl = replace(line, prefix, suffix, props);
			w.write(rl);
			w.newLine();
			line = r.readLine();
		}
		r.close();

		w.flush();
		char[] ca = caw.toCharArray();

		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
		bw.write(ca);
		bw.flush();
		bw.close();
	}

	public static void replaceXmlFile(String filePath, String prefix, String suffix, Properties props) throws IOException {
		if (!new File(filePath).exists()) {
			Logger.getLogger(StringConvertor.class).debug("The file [" + filePath + "] is not found");
		}

		CharArrayWriter caw = null;
		BufferedWriter writer = null;
		BufferedReader reader = null;
		BufferedWriter bw = null;
		try {
			caw = new CharArrayWriter(8096);
			writer = new BufferedWriter(caw);
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));

			String line = reader.readLine();
			while (line != null) {
				String rl = replace(line, prefix, suffix, props);
				writer.write(rl);
				writer.newLine();
				line = reader.readLine();
			}
			writer.flush();
			char[] ca = caw.toCharArray();

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
			bw.write(ca);
			bw.flush();
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error(e);
		} finally {
			if (caw != null) {
				caw.close();
			}
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (bw != null)
				bw.close();
		}
	}

	public static void replaceFilePrefixSuffix(String filePath, String prefix, String suffix, Properties props) throws IOException {
		if (!new File(filePath).exists()) {
			Logger.getLogger(StringConvertor.class).debug("The file [" + filePath + "] is not found");
		}

		CharArrayWriter caw = new CharArrayWriter(8096);
		BufferedWriter w = new BufferedWriter(caw);
		BufferedReader r = new BufferedReader(new FileReader(filePath));

		String line = r.readLine();
		while (line != null) {
			String rl = replacePrefixSuffix(line, prefix, suffix, props);
			w.write(rl);

			w.newLine();
			line = r.readLine();
		}
		r.close();

		w.flush();
		char[] ca = caw.toCharArray();

		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
		bw.write(ca);
		bw.flush();
		bw.close();
	}

	public static void replaceFile(String filePath) throws IOException {
		Properties props = System.getProperties();
		replaceFile(filePath, "${", "}", props);
	}

	public static void replaceFile(String filePath, Properties props) throws IOException {
		replaceFile(filePath, "${", "}", props);
	}

	public static void replaceFilePrefixSuffix(String filePath, Properties props) throws IOException {
		replaceFilePrefixSuffix(filePath, "{", "}", props);
	}

	public static String formatPath(String path) {
		if ((path == null) || (path.length() < 1)) {
			return null;
		}
		int len = path.length();

		if (path.charAt(len - 1) == File.separatorChar) {
			return path;
		}
		return path;
	}

	public static String formatPath2(String path) {
		if ((path == null) || (path.length() < 1)) {
			return null;
		}
		int len = path.length();

		if ((path.charAt(len - 1) == File.separatorChar) || (path.charAt(len - 1) == '/')) {
			return path.substring(0, len - 1);
		}
		return path;
	}

	public static void replaceUnixFile(String filePath, Properties props) throws IOException {
		replaceFile(filePath, "%{", "}", props);
	}

	public static String ToSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ')
				c[i] = '　';
			else if (c[i] < '') {
				c[i] = ((char) (c[i] + 65248));
			}
		}

		return new String(c);
	}

	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '　')
				c[i] = ' ';
			else if ((c[i] > 65280) && (c[i] < 65375)) {
				c[i] = ((char) (c[i] - 65248));
			}
		}

		String returnString = new String(c);

		return returnString;
	}
}