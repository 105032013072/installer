package com.bosssoft.platform.installer.wizard.gui.as;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;

import com.bosssoft.platform.installer.wizard.util.XmlUtil;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.io.xml.XmlFile;
import com.bosssoft.platform.installer.wizard.gui.AbstractASEditorPanel;
import com.bosssoft.platform.installer.wizard.gui.component.XFileChooser;
import com.bosssoft.platform.installer.wizard.gui.validate.ValidatorHelper;

public class WLEditorPanel extends AbstractASEditorPanel implements ActionListener,FocusListener{
	private static final long serialVersionUID = 3982484775483532865L;
	private JLabel lbBeaHome = new JLabel();

	private XFileChooser xfcBeaHome = new XFileChooser();

	private JLabel lbWLHome = new JLabel();

	private XFileChooser xfcWLHome = new XFileChooser();

	private JLabel lbDomainHome = new JLabel();

	private XFileChooser xfcDomainHome = new XFileChooser();

	private JLabel lbTarget = new JLabel();

	private JTextField cbTarget = new JTextField();

	private JTextField txPort = new JTextField();

	private JTextField txLoginName = new JTextField();

	private JPasswordField txPassword = new JPasswordField();
	private String cacheBeaHome = "";

	public WLEditorPanel() {
		initUI();
	}

	private void initUI() {
		setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.anchor = 18;
		this.lbBeaHome.setText(I18nUtil.getString("STEP.APPSVR.WEBLOGIC.LABEL.BEAHOME"));
		add(this.lbBeaHome, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 14, 5, 5);
		constraints.anchor = 18;
		constraints.fill = 1;
		constraints.weightx = 1.0D;
		this.xfcBeaHome.setButtonText(I18nUtil.getString("BUTTON.BROWSE2"));

		this.xfcBeaHome.getTextField().getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				WLEditorPanel.this.refreshWLHome(WLEditorPanel.this.xfcBeaHome.getText());
			}

			public void insertUpdate(DocumentEvent e) {
				WLEditorPanel.this.refreshWLHome(WLEditorPanel.this.xfcBeaHome.getText());
			}

			public void changedUpdate(DocumentEvent e) {
				WLEditorPanel.this.refreshWLHome(WLEditorPanel.this.xfcBeaHome.getText());
			}
		});
		add(this.xfcBeaHome, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.anchor = 18;
		this.lbWLHome.setText(I18nUtil.getString("STEP.APPSVR.WEBLOGIC.LABEL.WEBLOGICHOME"));
		add(this.lbWLHome, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 14, 5, 5);
		constraints.anchor = 18;
		constraints.fill = 2;
		constraints.weightx = 1.0D;
		this.xfcWLHome.setButtonText(I18nUtil.getString("BUTTON.BROWSE2"));
		add(this.xfcWLHome, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.anchor = 18;
		this.lbDomainHome.setText(I18nUtil.getString("STEP.APPSVR.WEBLOGIC.LABEL.DOMAINHOME"));
		add(this.lbDomainHome, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 14, 5, 5);
		constraints.anchor = 18;
		constraints.fill = 2;
		constraints.weightx = 1.0D;
		this.xfcDomainHome.setButtonText(I18nUtil.getString("BUTTON.BROWSE2"));
		xfcDomainHome.addActionListener(this);
		xfcDomainHome.getTextField().addFocusListener(this);;
		add(this.xfcDomainHome, constraints);

		JLabel lbLoginName = new JLabel();
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.anchor = 18;
		lbLoginName.setText(I18nUtil.getString("STEP.APPSVR.WEBLOGIC.LABEL.USERNAME"));
		add(lbLoginName, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 14, 5, 5);
		constraints.anchor = 18;
		constraints.fill = 2;
		constraints.weightx = 1.0D;
	
		add(this.txLoginName, constraints);

		JLabel lbPassword = new JLabel();
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.anchor = 18;
		lbPassword.setText(I18nUtil.getString("STEP.APPSVR.WEBLOGIC.LABEL.PASSWORD"));
		add(lbPassword, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 14, 5, 5);
		constraints.anchor = 18;
		constraints.fill = 2;
		constraints.weightx = 1.0D;
		add(this.txPassword, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.anchor = 18;
		this.lbTarget.setText(I18nUtil.getString("STEP.APPSVR.WEBLOGIC.LABEL.SERVERNAME"));
		add(this.lbTarget, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 14, 5, 5);
		constraints.anchor = 18;
		constraints.fill = 2;
		constraints.weightx = 1.0D;
		this.cbTarget.setText("AdminServer");
		this.cbTarget.addFocusListener(this);
		add(this.cbTarget, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 0, 5, 5);
		constraints.anchor = 18;
		JLabel label = new JLabel();
		label.setText("WebServer PORT:");
		label.setOpaque(false);
		add(label, constraints);

		constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = 6;
		constraints.gridwidth = 0;
		constraints.gridheight = 1;
		constraints.insets = new Insets(0, 14, 5, 5);
		constraints.anchor = 18;
		constraints.fill = 2;
		constraints.weightx = (constraints.weighty = 1.0D);
		/*this.txPort.setText("7001");*/
		add(this.txPort, constraints);
		setOpaque(false);
	}

	private void refreshWLHome(String beaHome) {
		if (StringUtils.equals(this.cacheBeaHome, beaHome)) {
			return;
		}
		this.cacheBeaHome = beaHome;
		if (StringUtils.isEmpty(beaHome)) {
			this.xfcWLHome.setText("");
		} else {
			File registFile = new File(beaHome, "registry.xml");
			if (!registFile.exists())
				return;
			try {
				XmlFile xmlFile = new XmlFile(registFile);
				Element element = (Element) xmlFile.findNode("/bea-product-information/host/product/release/component[@name=\"WebLogic Server\"]");
				boolean isResetInstallDir = false;
				if (element != null) {
					isResetInstallDir = resetWlHome(element);
				}
				if (!isResetInstallDir) {
					element = (Element) xmlFile.findNode("/bea-product-information/host/product/release");
					resetWlHome(element);
				}
			} catch (Exception localException) {
			}
		}
	}

	private boolean resetWlHome(Element element) {
		if (element != null) {
			String wlhome = element.getAttribute("InstallDir");
			if (StringUtils.isNotEmpty(wlhome)) {
				this.xfcWLHome.setText(wlhome);
				return true;
			}
		}
		return false;
	}

	public boolean checkInput() {
		if (ValidatorHelper.isBlankOrNull(this.xfcBeaHome.getText())) {
			showErrorMessage(I18nUtil.getString("APPSVR.WL.MSG.BEAHOMENULL"));
			return false;
		}
		if (ValidatorHelper.isBlankOrNull(this.xfcWLHome.getText())) {
			showErrorMessage(I18nUtil.getString("APPSVR.WL.MSG.WLHOMENULL"));
			return false;
		}

		if (ValidatorHelper.isBlankOrNull(this.txPort.getText())) {
			showErrorMessage(I18nUtil.getString("APPSVR.WL.MSG.PORTNULL"));
			return false;
		}

		String domainHome = this.xfcDomainHome.getText();
		if (ValidatorHelper.isBlankOrNull(domainHome)) {
			showErrorMessage("Domain Home must not be empty!");
			return false;
		}

		return true;
	}

	public String getChooserAppSerHome() {
		return this.xfcDomainHome.getText().trim();
	}

	public Properties getProperties() {
		Properties properties = new Properties();
		properties.setProperty("AS_WL_BEA_HOME", this.xfcBeaHome.getText());
		properties.setProperty("AS_WL_HOME", this.xfcWLHome.getText());
		properties.setProperty("AS_WL_DOMAIN_HOME", this.xfcDomainHome.getText());
		properties.setProperty("APP_SERVER_PORT", this.txPort.getText());
		properties.setProperty("AS_WL_TARGET_SERVER", this.cbTarget.getText());
		properties.setProperty("AS_WL_USERNAME", this.txLoginName.getText());
		properties.setProperty("AS_WL_PASSWORD", this.txPassword.getText());
		properties.put("APP_SERVER_DEPLOY_DIR", this.xfcDomainHome.getText()+File.separator+"autodeploy");
		return properties;
	}

	public void actionPerformed(ActionEvent arg0) {
			
			setPort();
		
	}

   public void focusGained(FocusEvent e) {
		
		
	}

	public void focusLost(FocusEvent e) {
         
			setPort();
	}

	private void setPort() {
		String wlhome=this.xfcDomainHome.getText();
		String serverName=this.cbTarget.getText();
		String serverConf=wlhome + File.separator + "config" + File.separator + "config.xml";
		String port=null;
		if(new File(serverConf).exists()) {
		  try{
			  Document doc = XmlUtil.getDocument(new File(serverConf));
				Elements servers = XmlUtil.findElements(doc, "server");
				Iterator<org.jsoup.nodes.Element> it=servers.iterator();
				while(it.hasNext()){
					org.jsoup.nodes.Element element=it.next();
					String name=element.select("name").text();
					if(serverName.equals(name)){
						String portStr =element.select("listen-port").text();
						if (portStr != null) {
							port=portStr.trim();
							
						}
						port="7001";
					}
				}
				
				
	   }catch(Exception e){
		e.printStackTrace();
	   }
   }else MainFrameController.showMessageDialog("weblogic "+I18nUtil.getString("APPSVR.CONF.ERROR"), I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
		 this.txPort.setText(port);
		this.txPort.setEditable(false);
	}
	
	public String getAppSvrPort(){
		return this.txPort.getText();
	}

	
}