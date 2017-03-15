package com.bosssoft.platform.installer.jee.server.internal.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.io.IOUtils;

public class ProcessReader {
	public static String readAll(final Process proc, long timeout) {
		final StringBuffer sb = new StringBuffer();
		Thread infoReader = new Thread() {
			public void run() {
				int ch = 0;
				InputStreamReader bis = null;
				try {
					bis = new InputStreamReader(new BufferedInputStream(proc.getInputStream()));
					while ((ch = bis.read()) != -1) {
						sb.append((char) ch);
					}
					bis.close();
				} catch (IOException localIOException) {
				} finally {
					IOUtils.closeQuietly(bis);
				}
			}
		};
		
		Thread errorReader = new Thread() {
			public void run() {
				int ch = 0;
				InputStreamReader bis = null;
				try {
					bis = new InputStreamReader(new BufferedInputStream(proc.getErrorStream()));
					while ((ch = bis.read()) != -1) {
						sb.append((char) ch);
					}
					bis.close();
				} catch (IOException localIOException) {
				} finally {
					IOUtils.closeQuietly(bis);
				}
			}
		};
		infoReader.setDaemon(true);
		errorReader.setDaemon(true);
		infoReader.start();
		errorReader.start();
		try {
			proc.waitFor();
		} catch (InterruptedException localInterruptedException) {
		}
		return sb.toString();
	}

	public static String readInfo(final Process proc) {
		final StringBuffer sb = new StringBuffer();
		Thread thread = new Thread() {
			public void run() {
				int ch = 0;
				InputStreamReader bis = null;
				try {
					bis = new InputStreamReader(new BufferedInputStream(proc.getInputStream()));
					while ((ch = bis.read()) != -1)
						sb.append((char) ch);
				} catch (IOException localIOException) {
				} finally {
					IOUtils.closeQuietly(bis);
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		try {
			proc.waitFor();
		} catch (InterruptedException localInterruptedException) {
		}
		return sb.toString();
	}

	
	public static String readError(final Process proc) {
		final StringBuffer sb = new StringBuffer();
		Thread thread = new Thread() {
			public void run() {
				int ch = 0;
				InputStreamReader bis = null;
				try {
					bis = new InputStreamReader(new BufferedInputStream(proc.getErrorStream()));
					while ((ch = bis.read()) != -1) {
						sb.append((char) ch);
					}
					bis.close();
				} catch (IOException localIOException) {
				} finally {
					IOUtils.closeQuietly(bis);
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		try {
			proc.waitFor();
		} catch (InterruptedException localInterruptedException) {
		}
		return sb.toString();
	}
}