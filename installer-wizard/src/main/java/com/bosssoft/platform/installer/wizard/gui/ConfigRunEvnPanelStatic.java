package com.bosssoft.platform.installer.wizard.gui;

import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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

public class ConfigRunEvnPanelStatic extends AbstractSetupPanel implements ActionListener{
	Logger logger = Logger.getLogger(getClass());
	private StepTitleLabel line = new StepTitleLabel();
	private JTextArea introduction = new JTextArea();
	private BossHomeChoosePanel bossHomeChoosePanel=new BossHomeChoosePanel();
	private Map<String,ResourceDef> resourceMap=null;
	
	//Map<location,resourceName>
	private Map<String,String> checkBoxMap=new HashMap<String, String>();
	
	//Map<resourceName,location>
	private Map<String,String> fileChooserMap=new HashMap<String, String>();
	
	
	
	public ConfigRunEvnPanelStatic(){
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
		this.introduction.setBounds(new Rectangle(30, 30, 500, 50));
		add(introduction);
		
		this.bossHomeChoosePanel.setBounds(new Rectangle(5, 103, 500, 60));
		add(this.bossHomeChoosePanel);
		
		resourceMap=ResourceDefHelper.getResourceMap();//加载需要安装的运行组件
	    int checkBoxy=168;
	    int choosery=181;
	    int labely=183;
	    Collection<ResourceDef> values =resourceMap.values();
		for (ResourceDef resourceDef : values) {
			String name=resourceDef.getName();
			JCheckBox checkBox=new JCheckBox("安装"+name);
			checkBox.setBounds(new Rectangle(30, checkBoxy, 150, 16));
			checkBoxy=checkBoxy+50;
			checkBox.setOpaque(false);
			checkBox.setSelected(true);
			if(resourceDef.getRequired()) checkBox.setEnabled(false);
			checkBox.addActionListener(this);
			checkBoxMap.put(locationToStr(checkBox.getLocation()), name);
			add(checkBox);
			
			JLabel label=new JLabel("请设置"+name+" home:");
			label.setBounds(new Rectangle(30, labely,150, 25));
			labely=labely+50;
			add(label);
			
			XFileChooser fileChooser=new XFileChooser();
			fileChooser.setButtonText(I18nUtil.getString("BUTTON.BROWSE"));
			fileChooser.setEnabled(false);
			fileChooser.setBounds(new Rectangle(180, choosery, 237, 21));
			choosery=choosery+50;
			fileChooserMap.put(name, locationToStr(fileChooser.getLocation()));
			add(fileChooser);
			
	    }
		
		
	}


	private String locationToStr(Point location) {
		double x=location.getX();
		double y=location.getY();
		
		return x+","+y;
	}
	
	private Point StrToLocation(String str){
		String[] indexs=str.split(",");
		double x=Double.parseDouble(indexs[0]);
		double y=Double.parseDouble(indexs[1]);
		Point point=new Point();
		point.setLocation(x, y);
		return point;
		
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		JCheckBox checkBox=(JCheckBox) source;
		String appName=checkBoxMap.get(locationToStr(checkBox.getLocation()));
		Point ponit=StrToLocation(fileChooserMap.get(appName));
		XFileChooser chooser=(XFileChooser) getComponentAt(ponit);
		chooser.setEnabled(!checkBox.isSelected());
		
	}

	@Override
	public void initialize(String[] paramArrayOfString) {
		this.bossHomeChoosePanel.initialize(paramArrayOfString);
		
	}

	@Override
	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
		this.bossHomeChoosePanel.setContext(getContext());
	
	}

	@Override
	public boolean checkInput() {
		if(filechooserCheck()&&bossHomeChoosePanel.checkInput()) return true;
		else return false;
	}

	private boolean filechooserCheck() {
		Iterator iter=checkBoxMap.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String,String> entry=(Entry) iter.next();
			Point checkBoxLoc=StrToLocation(entry.getKey());
			JCheckBox checkbox=(JCheckBox) getComponentAt(checkBoxLoc);
		    if(!checkbox.isSelected()){
		    	String appName=entry.getValue();
		    	Point chooserPoint=StrToLocation(fileChooserMap.get(appName));
		    	XFileChooser fileChooser=(XFileChooser) getComponentAt(chooserPoint);
		    	//输入是否为空
		    	if(fileChooser.getText()==null||"".equals(fileChooser.getText())){
		    		MainFrameController.showMessageDialog("请配置"+appName+"的安装目录", I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
		    	    return false;
		    	}
		    	//输入是否合法
		    	if(!isValid(appName,fileChooser.getText())) return false;
		    }
		}
		return true;
	}


	private Boolean isValid(String appName, String home) {
		ResourceDef resourceDef=resourceMap.get(appName);
		Map<String,String> params=resourceDef.getParams();
		Collection<String> values=params.values();
		for (String path : values) {
			if(path.contains("[home]")){
				String filePath=path.replace("[home]", home);
				if(!new File(filePath).exists()){
					MainFrameController.showMessageDialog(appName+"安装目录不合法", I18nUtil.getString("DIALOG.TITLE.ERROR"), 0);
		    	    return false;
				}
			}
		}
		return true;
	}


	@Override
	public void beforePrevious() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeNext() {
		logger.info("config run environment");
		
		this.bossHomeChoosePanel.beforeNext();
		
		StringBuffer installStr=new StringBuffer();
		Iterator iter=checkBoxMap.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String,String> entry=(Entry) iter.next();
			String appName=entry.getValue();
			ResourceDef resourceDef=resourceMap.get(appName);
			
			Point checkBoxLoc=StrToLocation(entry.getKey());
			JCheckBox checkbox=(JCheckBox) getComponentAt(checkBoxLoc);
			if(checkbox.isSelected()){
				resourceDef.setIsInstall(true);
				installStr.append(appName).append(",");
			}else{
				Point chooserPoint=StrToLocation(fileChooserMap.get(appName));
		    	XFileChooser fileChooser=(XFileChooser) getComponentAt(chooserPoint);
		    	resourceDef.setHome(fileChooser.getText().trim());
		    	resourceDef.setIsInstall(false);
		    	logger.debug("set "+appName+" home " +fileChooser.getText());
			}
			
			Map<String,String> params=resourceDef.getParams();
			Iterator paramit=params.entrySet().iterator();
			while(paramit.hasNext()){
				Map.Entry<String,String> param=(Entry) paramit.next();
				String value=param.getValue().replace("[home]", resourceDef.getHome());
				param.setValue(value);
				getContext().setValue(param.getKey(), value);
			}
			resourceMap.put(appName, resourceDef);
			getContext().setValue(appName+"_home",resourceDef.getHome());
		}
		
		getContext().setValue("RESOURCE_MAP", resourceMap);
		getContext().setValue("INSATLL_SERVERS", installStr);
		
	}

	@Override
	public void afterActions() {
		// TODO Auto-generated method stub
		
	}

}
