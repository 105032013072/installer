package com.bosssoft.platform.installer.wizard.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;
import com.bosssoft.platform.installer.wizard.gui.validate.ValidatorHelper;
import com.bosssoft.platform.installer.wizard.util.PropertiesUtil;

public class ChooseIPPanel extends AbstractSetupPanel {
	Logger logger = Logger.getLogger(getClass());
	private BorderLayout borderLayout1 = new BorderLayout();

	private StepTitleLabel line = new StepTitleLabel();

	private JPanel setupPane = new JPanel();

	private JTextArea introduction = new JTextArea();
	private JLabel lblIP = new JLabel();
	private JComboBox cbxIP = new JComboBox();

	private JLabel lblPort = new JLabel();
	private JTextField tfPort = new JTextField();

	private final String NOT_USED_PORT = "6299";

	public ChooseIPPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	//读取支持的IP列表（空方法）
	void jbInit() throws Exception {
		setLayout(this.borderLayout1);
		setOpaque(false);

		this.setupPane.setLayout(null);
		this.line.setText(I18nUtil.getString("STEP.ADMINIP"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));

		this.introduction.setOpaque(false);
		this.introduction.setText(I18nUtil.getString("CHOOSEIP.LABEL"));
		this.introduction.setLineWrap(true);
		this.introduction.setEditable(false);
		this.introduction.setFocusable(false);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setBounds(new Rectangle(37, 43, 370, 65));
		this.cbxIP.setBounds(new Rectangle(165, 100, 170, 20));
		loadSupportedIP();
		this.lblIP.setText(I18nUtil.getString("CHOOSEIP.LABEL.IP"));
		this.lblIP.setBounds(new Rectangle(37, 100, 120, 16));

		this.lblPort.setText(I18nUtil.getString("CHOOSEIP.LABEL.PORT"));
		this.lblPort.setBounds(new Rectangle(37, 126, 120, 16));

		this.tfPort.setBounds(new Rectangle(165, 126, 170, 20));
		this.tfPort.setText("6200");

		this.setupPane.setOpaque(false);
		this.setupPane.add(this.line, null);
		this.setupPane.add(this.introduction, null);
		this.setupPane.add(this.cbxIP, null);
		this.setupPane.add(this.lblIP, null);
		this.setupPane.add(this.tfPort, null);
		this.setupPane.add(this.lblPort, null);
		add(this.setupPane, "Center");
	}

	private void loadSupportedIP() {
	}

	public void afterShow() {
	}

	//IP和端口加入到context中
	public void beforeNext() {
		String asIP = (String) this.cbxIP.getSelectedItem();
		if ((asIP == null) || (asIP.equals("")))
			asIP = "127.0.0.1";
		getContext().setValue("USER_IP", asIP);
		getContext().setValue("USER_PORT", this.tfPort.getText());
	}

	public void beforePrevious() {
	}

	//设置下拉列表的可选IP
	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);

		if (this.cbxIP.getItemCount() > 0) {
			return;
		}
		String[] IP = getAllLocalHostIPAddress(true);
		if (this.context.getStringValue("EDITION").equalsIgnoreCase("DE")) {
			this.cbxIP.addItem("127.0.0.1");
			if (IP != null)
				for (String as : IP)
					this.cbxIP.addItem(as);
		} else {
			if (IP != null) {
				for (String as : IP) {
					this.cbxIP.addItem(as);
				}
			}
			this.cbxIP.addItem("127.0.0.1");
		}
	}

	public boolean checkInput() {
		String port = this.tfPort.getText();
		if ((StringUtils.isEmpty(port)) || (StringUtils.isBlank(port))) {
			showError(I18nUtil.getString("CHOOSEIP.PORT.EMPTY"));
			return false;
		}
		if (!ValidatorHelper.isInteger(port)) {
			showError(I18nUtil.getString("CHOOSEIP.PORT.INVALID"));
			return false;
		}
		int _port = Integer.valueOf(port).intValue();

		if ((_port <= 0) || (_port > 65535)) {
			showError(I18nUtil.getString("CHOOSEIP.PORT.INVALID"));
			return false;
		}
		if ("6299".equals(port)) {
			showError(I18nUtil.getString("CHOOSEIP.PORT.EXIST.USED"));
			return false;
		}

		if (checkAdminPort(port)) {
			String msg = I18nUtil.getString("CHOOSEIP.PORT.USED");
			msg = MessageFormat.format(msg, new Object[] { port });
			showError(msg);
			return false;
		}

		String asIP = (String) this.cbxIP.getSelectedItem();
		if (("127.0.0.1".equals(asIP))
				&& (MainFrameController.showConfirmDialog(I18nUtil.getString("DBCONFIG.MSG.USERIPNULL"), I18nUtil.getString("DIALOG.TITLE.WARNING"), 0, 2) != 0)) {
			return false;
		}

		return true;
	}

	public boolean checkAdminPort(String port) {
		Boolean isInstalled = (Boolean) getContext().getValue("IS_REPEAT_INSTALLED");
		if ((isInstalled != null) && (isInstalled.booleanValue())) {
			String installDir = getContext().getValue("INSTALL_DIR").toString();
			File apps_config_dir = new File(installDir + File.separator + "apps_config");
			for (File app_dir : apps_config_dir.listFiles()) {
				String conf_path = app_dir.getAbsolutePath() + File.separator + "startup.conf";
				Properties p = PropertiesUtil.readProperties(conf_path);
				String adminPort = p.getProperty("AdminPort");
				if (port.equals(adminPort)) {
					return true;
				}
			}
		}
		return false;
	}

	public void initialize(String[] parameters) {
	}

	public void afterActions() {
	}

	private String[] getAllLocalHostIPAddress(boolean allIpFlag) {
		List<String> ips = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
	
			while(netInterfaces.hasMoreElements()){
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> netAddresses = ni.getInetAddresses();
				while(netAddresses.hasMoreElements()){
					InetAddress ip =  netAddresses.nextElement();
					if ((ip != null) && (!ip.isLoopbackAddress()) && (ip.getHostAddress().indexOf(":") == -1)) {
						String address = ip.getHostAddress();
						if (allIpFlag)
							ips.add(address);
						else
							return new String[] { address };
					}
				}
			}
		} catch (Exception localException) {
		}
		return (String[]) ips.toArray(new String[ips.size()]);
	}
}