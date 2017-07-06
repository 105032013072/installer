package com.bosssoft.platform.installer.wizard.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.runtime.InstallRuntime;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;
import com.bosssoft.platform.installer.wizard.gui.validate.ValidatorHelper;
import com.bosssoft.platform.installer.wizard.util.PropertiesUtil;

public class ChooseInstallDirPanel extends AbstractSetupPanel implements ActionListener {
	Logger logger = Logger.getLogger(getClass());
	private StepTitleLabel line = new StepTitleLabel();
	public static final int install_path_max_length = 64;
	private JTextArea introduction = new JTextArea();

	private JLabel lblPath = new JLabel();

	private JTextField tfdDir = new JTextField();

	private JButton btnBrowse = new JButton();

	private JFileChooser dirChooser = new JFileChooser();

	public ChooseInstallDirPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setOpaque(false);
		setLayout(new GridBagLayout());
		this.line.setText(I18nUtil.getString("STEP.INSTALLPATH"));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = (constraints.gridy = 0);
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(5, 26, 0, 5);
		constraints.fill = 1;
		add(this.line, constraints);

		this.introduction.setOpaque(false);
		this.introduction.setText(I18nUtil.getString("INSTALLPATH.LABEL"));
		this.introduction.setLineWrap(true);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setEditable(false);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 0;
		constraints.gridheight = 4;
		constraints.insets.left = 37;
		constraints.weightx = (constraints.weighty = 1.0D);
		add(this.introduction, constraints);

		this.lblPath.setText(I18nUtil.getString("INSTALLPATH.LABEL.DIRECTORY"));
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.weightx = (constraints.weighty = 0.0D);
		add(this.lblPath, constraints);

		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets.left = 45;
		add(this.tfdDir, constraints);

		constraints.gridx = 5;
		constraints.gridy = 7;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets.left = 10;
		add(this.btnBrowse, constraints);

		this.btnBrowse.setMargin(new Insets(2, 2, 2, 2));
		this.btnBrowse.setMnemonic('R');
		this.btnBrowse.setText(I18nUtil.getString("BUTTON.BROWSE"));
		this.btnBrowse.setOpaque(false);

		this.btnBrowse.addActionListener(this);
		this.dirChooser.setFileSelectionMode(1);

		JLabel jLabel1 = new JLabel();
		constraints.gridx = 1;
		constraints.gridy = 8;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = (constraints.weighty = 1.0D);
		add(jLabel1, constraints);
		jLabel1.setOpaque(false);
	}

	public void afterShow() {
	}

	 //获取安装路径，将值存到content中
	public void beforeNext() {
		String dir = this.tfdDir.getText().trim();
		if (dir.endsWith(File.separator))
			dir = dir.substring(0, dir.length() - 1);
		getContext().setValue("INSTALL_DIR", dir);
		
		logger.info("set install dir: "+dir);
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
	}

	public boolean checkInput() {
		String dir = this.tfdDir.getText().trim();
		if (ValidatorHelper.isBlankOrNull(dir)) {
			showError(I18nUtil.getString("DISK.NOEMPTY.MSG"));
			return false;
		}
		if (dir.length() > install_path_max_length) {
			showError(I18nUtil.getString("INSTLL.PATH.LONG"));
			return false;
		}

		String pattern = null;
		if (System.getProperty("os.name").toLowerCase().indexOf("window") != -1)
			pattern = "[a-zA-Z]:[/\\\\][\\.\\w\\-_/\\\\]+";
		else {
			pattern = "[\\.\\w\\-_/\\\\]+";
		}

		if (!ValidatorHelper.isPatternValid(dir, pattern)) {
			showError(I18nUtil.getString("DISK.INVALID_INSTALL_DIR"));
			return false;
		}
		try {
			File tempDir = new File(dir);
			int count = 0;
			while (!tempDir.exists()) {
				tempDir = tempDir.getParentFile();
				if (tempDir == null) {
					break;
				}
				count++;
			}

			if (!isValidDirPath(dir)) {
				showError(I18nUtil.getString("DISK.INVALID_INSTALL_DIR"));
				return false;
			}
			try {
				for (int i = 0; i < count; i++) {
					tempDir.delete();
					tempDir = tempDir.getParentFile();
				}
			} catch (Throwable localThrowable) {
			}
		} catch (Exception e) {
			showError(I18nUtil.getString("DISK.INVALID_INSTALL_DIR"));
			return false;
		}

		int iInstalledEOS = hasInstalledEOS(dir);
		if (iInstalledEOS != 0) {
			if ((iInstalledEOS == 2) && (getContext().getStringValue("EDITION").equalsIgnoreCase("pe"))) {
				getContext().setValue("IS_REPEAT_INSTALLED", Boolean.valueOf(true));
				Properties p = PropertiesUtil.readProperties(dir + File.separator + "install.inf");
				getContext().setValue("appServerHome", p.getProperty("appServerHome"));
				getContext().setValue("appServerType", p.getProperty("appServerType"));
			} else {
				showError(I18nUtil.getString("DISK.DIR_ALREADY_INSTALL_EOS"));
				return false;
			}
		}

		String needSpace = (String) getContext().getValue("DISK_NEEDED");
		if ((needSpace == null) || (!ValidatorHelper.isLong(needSpace))) {
			getContext().setValue("INSTALL_DIR", dir);
			return true;
		}
		long freeSpace = 0L;
		try {
			freeSpace = freeSpace(dir);
		} catch (Exception e) {
			showError(I18nUtil.getString("DISK.INVALID_INSTALL_DIR"));
			return false;
		}

		long ns = Long.parseLong(needSpace);
		ns *= 1024L;
		if (ns > freeSpace) {
			showError(I18nUtil.getString("DISK.SPACE.MSG"));
			return false;
		}

		getContext().setValue("INSTALL_DIR", dir);
		return true;
	}

	private boolean isValidDirPath(String dirPath) {
		File f = new File(dirPath);
		if (f.exists()) {
			return f.isDirectory();
		}
		if (dirPath.indexOf(".") != -1) {
			return false;
		}
		return true;
	}

	private int hasInstalledEOS(String path) {
		File file = new File(path);
		if ((file == null) || (file.list() == null)) {
			return 0;
		}
		List fileNames = Arrays.asList(file.list());

		if (fileNames.contains("install.inf")) {
			return 2;
		}
		if ((fileNames.contains("apps_config")) && (fileNames.contains("startServer.cmd"))) {
			return 1;
		}
		return 0;
	}

	private long freeSpace(String tempDir) throws OperationException {
		long freeSpace = 0L;
		File tmpDir = new File(tempDir);

		if (!tmpDir.exists()) {
			String[] ss = tempDir.split("/");
			String tmpPath = "";
			File tmpFileDir = null;
			for (String s : ss) {
				tmpPath = tmpPath + "/" + s;
				if (!new File(tmpPath).exists()) {
					tmpFileDir = new File(tmpPath);
					break;
				}
			}
			tmpDir.mkdirs();
			freeSpace = FileUtils.freeSpace(tmpDir);
			if (tmpFileDir != null)
				FileUtils.delete(tmpFileDir, null, null);
			else
				tmpDir.deleteOnExit();
		} else {
			freeSpace = FileUtils.freeSpace(tmpDir);
		}

		return freeSpace;
	}

	public void initialize(String[] parameters) {
		if (System.getProperty("os.name").toLowerCase().indexOf("window") >= 0)
			this.tfdDir.setText(("C:\\" + InstallRuntime.INSTANCE.getContext().getStringValue("default.install.dir")).replace('/', '\\'));
		else
			this.tfdDir.setText(System.getProperty("user.home") + "/" + InstallRuntime.INSTANCE.getContext().getStringValue("default.install.dir"));
	}

	public void actionPerformed(ActionEvent ae) {
		if (!new File(this.tfdDir.getText()).exists()) {
			this.dirChooser.setCurrentDirectory(new File("c:\\"));
		}
		this.dirChooser.setCurrentDirectory(new File(this.tfdDir.getText()));
		int option = this.dirChooser.showOpenDialog(this);
		if (option == 0)
			this.tfdDir.setText(this.dirChooser.getSelectedFile().getPath());
	}

	//磁盘空间是否足够
	public void afterActions() {
		if (getContext().getStringValue("INSTALL_SPACE_ENOUGH").equalsIgnoreCase("false"))
			MainFrameController.showMessageDialog(I18nUtil.getString("DISK.SPACE.MSG"), I18nUtil.getString("DIALOG.TITLE.INFO"), 0);
	}
}