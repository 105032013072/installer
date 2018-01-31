package com.bosssoft.platform.installer.wizard.gui;

import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Location;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.option.ResourceDef;
import com.bosssoft.platform.installer.core.option.ResourceDefHelper;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.core.util.ReflectUtil;
import com.bosssoft.platform.installer.wizard.gui.as.TomcatEditorPanel;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;
import com.bosssoft.platform.installer.wizard.gui.component.XFileChooser;

public class ConfigRunEvnPanelSenior extends AbstractSetupPanel implements MouseListener{
	Logger logger = Logger.getLogger(getClass());
	private StepTitleLabel line = new StepTitleLabel();
	private JTextArea introduction = new JTextArea();
	private JLabel seniorIcon=new JLabel();
	private JLabel seniorLabel=new JLabel();
	
	private ResourcePanel resourcePanel=new ResourcePanel();
	

	public ConfigRunEvnPanelSenior(){
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	private void jbInit() throws Exception {
		setLayout(null);
		setOpaque(false);
		this.line.setText(I18nUtil.getString("STEP.RUNEVN.CONF"));
		this.line.setBounds(new Rectangle(26, 5, 581, 27));
		add(line);
		
		this.introduction.setOpaque(false);
		this.introduction.setText(I18nUtil.getString("CONFIG.RUNEVN.INTRODUCTION"));
		this.introduction.setLineWrap(true);
		this.introduction.setWrapStyleWord(true);
		this.introduction.setEditable(false);
		this.introduction.setBounds(new Rectangle(30, 60, 500, 50));
		add(introduction);
		
		this.seniorLabel.setBounds(new Rectangle(352, 110, 100, 60));
		this.seniorLabel.setText(I18nUtil.getString("CONFIG.RUNEVN.SENIORLABLE"));
		add(this.seniorLabel);
		
		this.seniorIcon.setBounds(new Rectangle(362, 110, 100, 60));
		this.seniorIcon.setVerifyInputWhenFocusTarget(true);
		this.seniorIcon.setHorizontalAlignment(0);
		String iconPath=InstallerFileManager.getImageDir()+"/down.jpg";
		this.seniorIcon.setIcon(new ImageIcon(iconPath));
		this.seniorIcon.setVerticalAlignment(0);
		this.seniorIcon.addMouseListener(this);;
		
		add(this.seniorIcon);
		
		this.resourcePanel.setBounds(new Rectangle(5, 160, 500, 200));
		add(this.resourcePanel);
		
		this.resourcePanel.setVisible(false);
	}

	@Override
	public void initialize(String[] paramArrayOfString) {
		
	}

	@Override
	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
	    this.resourcePanel.setContext(getContext());
	    this.resourcePanel.beforeShow();
	}

	@Override
	public boolean checkInput() {
		if( resourcePanel.checkInput()) return true;
		else return false;
	}

	@Override
	public void beforePrevious() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeNext() {
		logger.info("config run environment");
		//设置bosssoftHome
		String bosssofHome = System.getProperty("BOSSSOFT_HOME");
		if (StringUtils.isBlank(bosssofHome)) {
			bosssofHome=getContext().getStringValue("INSTALL_DIR")+File.separator+"bosssoft_home";
		}
		
		getContext().setValue("BOSSSOFT_HOME", bosssofHome);
		logger.info("set bosssoft_home: "+bosssofHome);
		this.resourcePanel.beforeNext();
	}

	@Override
	public void afterActions() {
		
	}


	public void mouseClicked(MouseEvent ac) {
		if(ac.getSource()==this.seniorIcon){
			String sourceIcon=this.seniorIcon.getIcon().toString();
			if(sourceIcon.endsWith("up.jpg")){
				String iconPath=InstallerFileManager.getImageDir()+"/down.jpg";
				this.seniorIcon.setIcon(new ImageIcon(iconPath));
				this.resourcePanel.setVisible(false);
			}else if(sourceIcon.endsWith("down.jpg")){
				String iconPath=InstallerFileManager.getImageDir()+"/up.jpg";
				this.seniorIcon.setIcon(new ImageIcon(iconPath));
				this.resourcePanel.setVisible(true);
			}
		}
		
	}


	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseReleased(MouseEvent arg0) {
		
		
	}

}
