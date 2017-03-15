package com.bosssoft.platform.installer.core.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessOutputReader {
	public static String read(final Process proc) {
		final StringBuffer sb = new StringBuffer();
		new Thread() {
			public void run() {
				int ch = 0;
				InputStreamReader bis = null;
				try {
					bis = new InputStreamReader(new BufferedInputStream(proc.getInputStream()), "GBK");
					while ((ch = bis.read()) != -1) {
						sb.append((char) ch);
					}
					bis.close();
				} catch (IOException localIOException) {
				}
			}
		}.start();

		new Thread() {
			public void run() {
				int ch = 0;
				try {
					InputStreamReader bis = new InputStreamReader(new BufferedInputStream(proc.getErrorStream()), "GBK");
					while ((ch = bis.read()) != -1) {
						sb.append((char) ch);
					}
					bis.close();
				} catch (IOException localIOException) {
				}
			}
		}.start();
		try {
			proc.waitFor();
		} catch (InterruptedException localInterruptedException) {
		}
		return new String(sb);
	}
}