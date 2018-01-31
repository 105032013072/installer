package com.bosssoft.platform.installer.wizard.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.MainFrameController;
import com.bosssoft.platform.installer.core.gui.AbstractControlPanel;
import com.bosssoft.platform.installer.core.gui.AbstractSetupPanel;
import com.bosssoft.platform.installer.core.option.ResourceDef;
import com.bosssoft.platform.installer.core.option.ResourceDefHelper;
import com.bosssoft.platform.installer.core.util.I18nUtil;
import com.bosssoft.platform.installer.wizard.gui.component.XFileChooser;
import com.bosssoft.platform.installer.wizard.util.ServerUtil;

public class ResourcePanel extends AbstractSetupPanel implements ActionListener,MouseListener{
	Logger logger = Logger.getLogger(getClass());
   private Map<String,ResourceDef> resourceMap=null;
	
	//Map<location,resourceName>
	private Map<String,String> checkBoxMap=new HashMap<String, String>();
	
	//Map<resourceName,location>
	private Map<String,String> fileChooserMap=new HashMap<String, String>();
	
	//Map<resourceName,location>
	private Map<String,String> fileChoosePathMap=new HashMap<String, String>();//fileChooser中路径文本空的位置
	
	public ResourcePanel(){
		try {
			//jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	private void jbInit() {
		setLayout(null);
		setOpaque(false);
		resourceMap=ResourceDefHelper.getResourceMap();//加载需要安装的运行组件
	
	    /*int checkBoxy=168;
	    int choosery=181;
	    int labely=183;*/
		//int checkBoxy=0;
		int checkBoxy=2;
	    int choosery=3;
	    int labely=10;
	    Collection<ResourceDef> values =resourceMap.values();
		for (ResourceDef resourceDef : values) {
			String name=resourceDef.getName();
			JCheckBox checkBox=new JCheckBox("安装"+name);
			//checkBox.setBounds(new Rectangle(30, checkBoxy, 145, 16));
			checkBox.setBounds(new Rectangle(30, checkBoxy, 120, 16));
			checkBoxy=checkBoxy+50;
			checkBox.setOpaque(false);
			checkBox.setSelected(true);
			if(resourceDef.getRequired()) checkBox.setEnabled(false);
			checkBox.addActionListener(this);
			checkBoxMap.put(locationToStr(checkBox.getLocation()), name);
			add(checkBox);
			
			/*JLabel label=new JLabel("请设置"+name+" home:");
			label.setBounds(new Rectangle(30, labely,150, 25));
			labely=labely+50;
			add(label);*/
			
			XFileChooser fileChooser=new XFileChooser();
			fileChooser.setButtonText(I18nUtil.getString("BUTTON.BROWSE"));
			fileChooser.setEnabled(false);
			fileChooser.getTfdPath().setEnabled(false);
			//fileChooser.setBounds(new Rectangle(180, choosery, 237, 21));
			fileChooser.setBounds(new Rectangle(155, choosery, 272, 21));
		    fileChooser.addMouseListenerToPath(this);;
		    fileChooser.getTextField().setName(name);
			choosery=choosery+50;
			fileChooserMap.put(name, locationToStr(fileChooser.getLocation()));
			add(fileChooser); 
			
			fileChoosePathMap.put(name, locationToStr(fileChooser.getTfdPath().getLocation()));
			
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
		chooser.getTfdPath().setEnabled(!checkBox.isSelected());
		
		//显示默认的安装路径
		if(checkBox.isSelected()){
			String homePath=resourceMap.get(appName).getHome();
			chooser.setText(homePath);
		}else{
			chooser.setText("请选择"+appName+"的目录");
		}
		
	}

	@Override
	public void initialize(String[] paramArrayOfString) {
		
	}

	@Override
	public void beforeShow() {
		jbInit();
		
		
	   //显示默认的安装路径
		for (Map.Entry<String,String> entry : fileChooserMap.entrySet()) {
			Point chooserPoint=StrToLocation(entry.getValue());
	    	XFileChooser fileChooser=(XFileChooser) getComponentAt(chooserPoint);
	        String homePath=resourceMap.get(entry.getKey()).getHome();
	    	fileChooser.setText(homePath);
		}
		
	}

	@Override
	public boolean checkInput() {
		if(filechooserCheck() && checkevn()) return true;
		else return false;
	}

	//环境检查
	private boolean checkevn() {
		Iterator iter=checkBoxMap.entrySet().iterator();
		while(iter.hasNext()){
			List<Integer> port=new ArrayList<Integer>();
			
			Map.Entry<String,String> entry=(Entry) iter.next();
			String appName=entry.getValue();
			ResourceDef def=resourceMap.get(appName);
			if(def.getPorts().size()==0) continue;
			
			Point checkBoxLoc=StrToLocation(entry.getKey());
			JCheckBox checkbox=(JCheckBox) getComponentAt(checkBoxLoc);
		    if(!checkbox.isSelected()){//使用自己配置的
		    	Point chooserPoint=StrToLocation(fileChooserMap.get(appName));
		    	XFileChooser fileChooser=(XFileChooser) getComponentAt(chooserPoint);
		        String homePath=fileChooser.getText().trim();
		        String configPath=def.getPortConfigFile();
		        if(configPath!=null && !"".equals(configPath)){
		        	configPath=configPath.replace("[home]", fileChooser.getText().trim());
		        	port=setport(configPath,def.getPortConfigKeys());//配置文件中读取端口号
		        }
		    }else{
		    	port=def.getPorts();
		    }
		    
		    //检测是否有进程占用端口
		    Set<Integer> pids=new HashSet<Integer>();
		    if("true".equals(getContext().getStringValue("IS_WINDOWS"))){
		    	pids=ServerUtil.searchProcess4Win(port);
		    }else{
		    	pids=ServerUtil.searchProcess4Linux(port);
		    }
		   
		    if(pids.size()!=0){
		    	int dialog_result=MainFrameController.showConfirmDialog(appName+I18nUtil.getString("CONFIG.RUNEVN.PORTCONFLICT"), I18nUtil.getString("DIALOG.TITLE.WARNING"), 0, 2);
		    	if(dialog_result==0){
		    		if("true".equals(getContext().getStringValue("IS_WINDOWS"))){
		    			ServerUtil.killWithPid4Win(pids);
				    }else{
				    	ServerUtil.killWithPid4Linux(pids);
				    }
		    	}
		    	return false;
		    }
		    
		}
		return true;
	}



	private List<Integer> setport(String configPath, List<String> portConfigKeys) {
		List<Integer> ports=new ArrayList<Integer>();
		   Properties props = new Properties();
	       try {
	        InputStream in = new BufferedInputStream (new FileInputStream(configPath));
	        props.load(in);
	        for (String key : portConfigKeys) {
	            String value = props.getProperty (key);
	            ports.add(Integer.valueOf(value));
			}
	      
	       } catch (Exception e) {
	        e.printStackTrace();
	       }
		return ports;
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
				String pathes[]=path.split(";");
				path=pathes[pathes.length-1];
				String filePath=path.replace("[home]", home).trim();
				/*if(filePath.indexOf(" ")>-1) filePath=filePath.substring(0, filePath.indexOf(" "));
				if(filePath.startsWith("%")){
					 filePath=filePath.substring(filePath.indexOf(";")+1, filePath.length());
				}*/
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
		
	}


	public void mouseClicked(MouseEvent arg0) {
		
	}


	public void mouseEntered(MouseEvent ae) {
		Object source = ae.getSource();
		JTextField fileChoosertext=(JTextField) source;
		String appName=fileChoosertext.getName();
		if(fileChoosertext.isEnabled()){
			 fileChoosertext.setText("");
		}
			
	}


	public void mouseExited(MouseEvent ae) {
		
		Object source = ae.getSource();
		JTextField fileChoosertext=(JTextField) source;
		String appName=fileChoosertext.getName();
		if(fileChoosertext.isEnabled()){
			fileChoosertext.setText("请选择"+appName+"目录");
		}
		
	}


	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
