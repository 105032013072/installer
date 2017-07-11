package com.bosssoft.platform.installer.wizard.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.tools.ant.Location;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.option.ResourceDef;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.wizard.gui.component.StepTitleLabel;
import com.bosssoft.platform.installer.wizard.gui.component.XFileChooser;

public class ConfigRunEvnPanel extends AbstractSetupPanel implements ActionListener{

	private StepTitleLabel line = new StepTitleLabel();
	private JTextArea introduction = new JTextArea();
	
	
	//Map<location,resourceName>
	private Map<String,String> checkBoxMap=new HashMap<String, String>();
	
	//Map<resourceName,location>
	private Map<String,String> fileChooserMap=new HashMap<String, String>();
	
	
	public ConfigRunEvnPanel(){
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
		add(this.line);
		
		List<ResourceDef> list=loadResource();//加载需要安装的运行组件
	    int checkBoxy=100;
	    int choosery=100;
		for (ResourceDef resourceDef : list) {
			String name=resourceDef.getName();
			JCheckBox checkBox=new JCheckBox(name);
			checkBox.setBounds(new Rectangle(20, checkBoxy, 120, 16));
			checkBoxy=checkBoxy+29;
			checkBox.setOpaque(false);
			checkBox.setSelected(true);
			checkBoxMap.put(checkBox.getLocation().toString(), name);
			add(checkBox);
			
			XFileChooser fileChooser=new XFileChooser();
			fileChooser.setButtonText(I18nUtil.getString("BUTTON.BROWSE"));
			fileChooser.setEnabled(false);
			fileChooser.setBounds(new Rectangle(133, choosery, 237, 21));
			choosery=choosery+29;
			fileChooserMap.put(name, fileChooser.getLocation().toString());
			add(fileChooser);
			
	    }
		
		
	}


	private List<ResourceDef> loadResource() throws Exception {
		List<ResourceDef> list=new ArrayList<ResourceDef>();
		String filePath=InstallerFileManager.getResourcesDir()+File.separator+"resource"+File.separator+"resource.xml";
		SAXReader reader=new SAXReader();
		Document doc=reader.read(new File(filePath));
		Iterator<Element> iter=doc.getRootElement().elementIterator("server");
		while(iter.hasNext()){
			ResourceDef def=new ResourceDef();
			Element ele=iter.next();
			def.setName(ele.elementText("name"));
			def.setSourcePath(ExpressionParser.parseString(ele.elementText("sourcePath")));
			def.setDestPath(ExpressionParser.parseString(ele.elementText("destPath")));
			def.setHome(ExpressionParser.parseString(ele.elementText("home")));
			
			Iterator<Element> paramters=ele.element("paramters").elementIterator("paramter");
			while(paramters.hasNext()){
				Element paramter=paramters.next();
				def.setParams(paramter.attributeValue("key"), paramter.attributeValue("value"));
			}
			list.add(def);
		}
		return list;
	}


	public void actionPerformed(ActionEvent arg0) {
		
		
	}

	@Override
	public void initialize(String[] paramArrayOfString) {
		
		
	}

	@Override
	public void beforeShow() {
		AbstractControlPanel controlPane = MainFrameController.getControlPanel();
		controlPane.setButtonVisible("finish", false);
		controlPane.setButtonVisible("help", false);
		
	}

	@Override
	public boolean checkInput() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void beforePrevious() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeNext() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterActions() {
		// TODO Auto-generated method stub
		
	}

}
