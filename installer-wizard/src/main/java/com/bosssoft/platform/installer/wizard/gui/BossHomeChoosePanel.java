package com.bosssoft.platform.installer.wizard.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.runtime.InstallRuntime;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;
import com.bosssoft.platform.installer.wizard.gui.component.XFileChooser;
import com.bosssoft.platform.installer.wizard.gui.validate.ValidatorHelper;
import com.bosssoft.platform.installer.wizard.util.PropertiesUtil;

public class BossHomeChoosePanel extends AbstractSetupPanel implements ActionListener{
	Logger logger = Logger.getLogger(getClass());
	public static final int install_path_max_length = 64;
	private JLabel lblPath = new JLabel();

	private JTextField tfdDir = new JTextField();
	
	private JLabel ldlabel=new JLabel();

	private JButton btnBrowse = new JButton();

	private JFileChooser dirChooser = new JFileChooser();
	
	private String parameter = null;

	private IContext context = null;

	public BossHomeChoosePanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);
  

		this.lblPath.setText("BOSSSOFT HOME:");
		lblPath.setBounds(new Rectangle(30, 10, 200, 10));
		add(this.lblPath);

		this.ldlabel.setText(I18nUtil.getString("BOSSHOME.LABEL.DIR"));
		this.ldlabel.setBounds(new Rectangle(30, 25, 100, 25));
		add(ldlabel);
		
		this.tfdDir.setBounds(new Rectangle(174, 25, 164, 24));
		add(this.tfdDir);
		
		this.btnBrowse.setBounds(new Rectangle(343, 25, 71, 22));
		this.btnBrowse.setMnemonic('R');
		this.btnBrowse.setMargin(new Insets(2, 2, 2, 2));
		this.btnBrowse.setText(I18nUtil.getString("BUTTON.BROWSE"));
		this.btnBrowse.setOpaque(false);
		this.btnBrowse.setEnabled(true);
		add(btnBrowse);

		this.btnBrowse.addActionListener(this);
		this.dirChooser.setFileSelectionMode(1);

		
	}

	public void afterShow() {
	}

	 //获取路径值，将值存到content中
	public void beforeNext() {
		String dir = this.tfdDir.getText().trim()+File.separator+"BOSSSOFT_HOME";
		if (dir.endsWith(File.separator))
			dir = dir.substring(0, dir.length() - 1);
		getContext().setValue("BOSSSOFT_HOME", dir);
		
	    logger.info("set bosssoft_home: "+dir);
	}

	public void beforePrevious() {
	}

	public void beforeShow() {
	}

	public boolean checkInput() {
		String dir = this.tfdDir.getText().trim();
		if (ValidatorHelper.isBlankOrNull(dir)) {
			showError(I18nUtil.getString("BOSSHOME.NOEMPTY.MSG "));
			return false;
		}
		if (dir.length() > install_path_max_length) {
			showError(I18nUtil.getString("BOSSHOME.PATH.LONG  "));
			return false;
		}

		String pattern = null;
		if (System.getProperty("os.name").toLowerCase().indexOf("window") != -1)
			pattern = "[a-zA-Z]:[/\\\\][\\.\\w\\-_/\\\\]+";
		else {
			pattern = "[\\.\\w\\-_/\\\\]+";
		}

		if (!ValidatorHelper.isPatternValid(dir, pattern)) {
			showError(I18nUtil.getString("BOSSHOME.INVALID_INSTALL_DIR"));
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
				showError(I18nUtil.getString("BOSSHOME.INVALID_INSTALL_DIR"));
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
			showError(I18nUtil.getString("BOSSHOME.INVALID_INSTALL_DIR"));
			return false;
		}

		getContext().setValue("BOSSSOFT_HOME", dir);
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



	public void initialize(String[] parameters) {
		if (System.getProperty("os.name").toLowerCase().indexOf("window") >= 0)
		  //this.tfdDir.setText(("C:\\" + InstallRuntime.INSTANCE.getContext().getStringValue("default.install.dir")).replace('/', '\\'));
			this.tfdDir.setText(System.getProperty("user.home"));
		else
			//this.tfdDir.setText(System.getProperty("user.home") + "/" + InstallRuntime.INSTANCE.getContext().getStringValue("default.install.dir"));
			this.tfdDir.setText(System.getProperty("user.home") + "/" +"BOSSSOFT_HOME");
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

	@Override
	public void afterActions() {
		
		
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public IContext getContext() {
		return context;
	}

	public void setContext(IContext context) {
		this.context = context;
	}

	
}
