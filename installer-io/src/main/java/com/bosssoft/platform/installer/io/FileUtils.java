package com.bosssoft.platform.installer.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.bosssoft.platform.installer.io.listener.IFileOperationListener;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.io.operation.impl.AbstractFileOperation;
import com.bosssoft.platform.installer.io.operation.impl.AbstractPermissionOperation;
import com.bosssoft.platform.installer.io.operation.impl.CopyOperation;
import com.bosssoft.platform.installer.io.operation.impl.DeleteOperation;
import com.bosssoft.platform.installer.io.operation.impl.DiskCheckOperation;
import com.bosssoft.platform.installer.io.operation.impl.MoveFileOperation;
import com.bosssoft.platform.installer.io.operation.impl.UnzipOperation;
import com.bosssoft.platform.installer.io.operation.impl.XOSPermissionOperation;
import com.bosssoft.platform.installer.io.operation.impl.ZipOperation;
import com.bosssoft.platform.installer.io.util.OS;

public class FileUtils {
	public static void copy(File src, File dest, FileFilter filter, IFileOperationListener listener) throws OperationException {
		copy(src, dest, true, filter, listener);
	}

	public static void copy(File src, File dest, boolean preserveFileDate, FileFilter filter, IFileOperationListener listener) throws OperationException {
		AbstractFileOperation operation = new CopyOperation(src, dest);
		excute(operation, filter, listener, preserveFileDate);
	}

	public static void delete(File deleteFile, FileFilter filter, IFileOperationListener listener) throws OperationException {
		DeleteOperation operation = new DeleteOperation(deleteFile);
		operation.setFileFilter(filter);
		operation.addListeter(listener);
		operation.excute();
	}

	public static void move(File src, File destFile, IFileOperationListener listener) throws OperationException {
		AbstractFileOperation operation = new MoveFileOperation(src, destFile);
		excute(operation, null, listener, true);
	}

	public static void zip(File src, File dest, FileFilter filter, IFileOperationListener listener) throws OperationException {
		zip(src, dest, true, filter, listener);
	}

	public static void zip(File src, File dest, boolean preserveFileDate, FileFilter filter, IFileOperationListener listener) throws OperationException {
		AbstractFileOperation operation = new ZipOperation(src, dest);
		excute(operation, filter, listener, preserveFileDate);
	}

	public static void unzip(File zipFile, File dest, FileFilter filter, IFileOperationListener listener) throws OperationException {
		unzip(zipFile, dest, true, filter, listener);
	}

	public static void unzip(File zipFile, File dest, boolean preserveFileDate, FileFilter filter, IFileOperationListener listener) throws OperationException {
		AbstractFileOperation operation = new UnzipOperation(zipFile, dest);
		excute(operation, filter, listener, preserveFileDate);
	}

	public static String chmod(File file, int permission, String action) throws OperationException {
		if (OS.isFamily("unix")) {
			AbstractPermissionOperation operation = new XOSPermissionOperation(file, permission, action);
			operation.excute();
			return operation.getMessage();
		}

		return null;
	}

	public static long freeSpace(File file) throws OperationException {
		DiskCheckOperation operation = new DiskCheckOperation(file);
		return operation.getFreeSpace();
	}
	
	public static boolean mkdir(File _file, boolean _isDir) {
		File dir = _file;
		if(!_isDir){
			dir = _file.getParentFile();
		}
		return dir.mkdirs();
	}

	private static void excute(AbstractFileOperation operation, FileFilter filter, IFileOperationListener listener, boolean preserveFileDate) throws OperationException {
		operation.setFilter(filter);
		operation.addListeter(listener);
		operation.setPreserveFileDate(preserveFileDate);
		operation.excute();
	}
	
	
	
    public static List<File> listFiles(File dir, FileFilter filter) {
        List<File> files = new ArrayList<File>();
        if (!dir.exists() || dir.isFile()) {
            return files;
        } else {
            listFiles(files, dir, filter);
            return files;
        }
    }

    private static void listFiles(List filesList, File dir, FileFilter filter) {
        File files[] = dir.listFiles(filter);
        List temp = Arrays.asList(files);
        Collections.sort(temp);
        filesList.addAll(temp);
        File subDirs[] = dir.listFiles(directoryFileFilter());
        for (int i = 0; i < subDirs.length; i++)
            listFiles(filesList, subDirs[i], filter);

    }
    
    private  static FileFilter directoryFileFilter() {
        return new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        };
    }
}