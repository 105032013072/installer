package com.bosssoft.platform.installer.core.action;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Copy extends AbstractAction {
	protected String file = null;
	protected String destFile = null;
	protected String destDir = null;

	protected boolean forceOverwrite = false;
	protected Vector<Fileset> filesets = new Vector();

	private Hashtable fileCopyMap = new Hashtable();

	public String getDestDir() {
		return this.destDir;
	}

	public void setDestDir(String destDir) {
		this.destDir = destDir;
	}

	public String getDestFile() {
		return this.destFile;
	}

	public void setDestFile(String destFile) {
		this.destFile = destFile;
	}

	public String getFile() {
		return this.file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setOverwrite(boolean overwrite) {
		this.forceOverwrite = overwrite;
	}

	public boolean addFileset(Fileset fs) {
		return this.filesets.add(fs);
	}

	public List<Fileset> getFilesets() {
		return this.filesets;
	}

	public void rollback(IContext context, Map parameters) throws InstallException {
	}

	public void execute(IContext context, Map parameters) throws InstallException {
		if ((this.file == null) && (this.filesets.size() == 0))
			throw new InstallException("Specify at least one source--a file or a resource collection.");
		if ((this.destDir == null) && (this.destFile == null)) {
			throw new InstallException("Only one of tofile and todir may be set.");
		}
		File savedFile = null;
		File savedDestFile = null;
		File savedDestDir = null;

		if (this.file != null) {
			savedFile = new File(this.file);
		}

		if (this.destDir != null) {
			savedDestDir = new File(this.destDir);
		}

		if (this.destFile != null) {
			savedDestFile = new File(this.destFile);
		}

		if ((this.file != null) && (this.destFile != null) && (savedFile.exists()) && ((!savedFile.exists()) || (this.forceOverwrite))) {
			this.fileCopyMap.put(this.file, this.destFile);
		}

		if ((this.file != null) && (this.destDir != null) && (savedFile.exists())) {
			savedDestFile = new File(savedDestDir, savedFile.getName());
			if ((!savedFile.exists()) || (this.forceOverwrite)) {
				this.fileCopyMap.put(this.file, this.destDir + "/" + savedFile.getName());
			}
		}
		doFileOperations();

		if ((this.destDir != null) && (this.filesets.size() > 0))
			copyFileset(savedDestDir);
	}

	private void copyFileset(File todir) {
		String excludes = null;
		String includes = null;
		InstallFileFilter filter = null;

		for (Fileset fs : this.filesets) {
			excludes = fs.getExcludes();
			includes = fs.getIncludes();
			filter = new InstallFileFilter();
			filter.setExcludes(excludes);
			filter.setIncludes(includes);
			try {
				FileUtils.copy(new File(fs.getDir()), todir, filter, FileOperationMessageListener.INSTANCE);
			} catch (OperationException e) {
				throw new InstallException("Failed to copy " + fs.getDir() + " to " + todir.getAbsolutePath(), e);
			}
		}
	}

	protected void doFileOperations() {
		if (this.fileCopyMap.size() > 0) {
			Enumeration e = this.fileCopyMap.keys();
			while (e.hasMoreElements()) {
				String fromFile = (String) e.nextElement();
				String toFile = (String) this.fileCopyMap.get(fromFile);
				try {
					FileUtils.copy(new File(fromFile), new File(toFile), null, FileOperationMessageListener.INSTANCE);
				} catch (OperationException e1) {
					throw new InstallException("Failed to copy " + fromFile + " to " + toFile, e1);
				}
			}
		}
	}
}