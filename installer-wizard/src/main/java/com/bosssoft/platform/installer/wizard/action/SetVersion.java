package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.option.ModuleDef;
import com.bosssoft.platform.installer.wizard.util.XmlUtil;


/**
 * 在boss_home下记录产品的版本信息
 * @author Windows
 *
 */
public class SetVersion implements IAction{

	public void execute(IContext context, Map params) throws InstallException {
		String fileName=params.get("versionFile").toString();
		File versionFile=new File(fileName);
		recordInsatllDir(versionFile,context);
		
	}
	private void recordInsatllDir(File versionFile, IContext context) {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(versionFile);
			Element product= document.getRootElement();
			//创建产品的版本信息文件
			createProductVersion(context,product);
			
			Iterator<Element> it= product.elementIterator();
			while(it.hasNext()){
				Element app=it.next();
				//创建应用的版本信息文件
				createAppVersion(context,app);
				
				//创建应用的升级记录文件
				createAppUpgrade(context,app);
			}
		} catch (Exception e) {
			throw new InstallException("faild to recordInsatllDir",e);
		}
		
	}
	
	private void createAppUpgrade(IContext context, Element app) throws Exception {
		String appName=app.attributeValue("name");
		String version=app.attributeValue("version");
		String filepath=context.getStringValue("BOSSSOFT_HOME")+File.separator
				+appName+File.separator
				+"patchs"+File.separator
				+"upgrade"+File.separator
				+"upgrade-log.xml";
		if(!new File(filepath).exists()) createFile(filepath);
		Document doc=DocumentHelper.createDocument();
		Element upgrades=doc.addElement("upgrades");
		Element upgrad=DocumentHelper.createElement("upgrade");
		upgrad.addElement("upgrade-time").addText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		upgrad.addElement("upgrade-version").addText(version);
	    upgrad.addElement("result").addText("success");
	    upgrad.addElement("logfile").addText(context.getStringValue("INSTALL_LOGFILE_PATH"));
		upgrades.add(upgrad);
	    
		
		//写入文件
        OutputFormat format =OutputFormat.createPrettyPrint(); 
		  format.setEncoding("utf-8");//设置编码格式 
		  format.setNewLineAfterDeclaration(false);
       XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(filepath),format);
	    xmlWriter.write(doc);
	    xmlWriter.close();
	}
	private void createFile(String filepath) {
		File file=new File(filepath);
		File parentFile=file.getParentFile();
		if(!parentFile.exists()){
			parentFile.mkdirs();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
		   throw new InstallException(e);
		}
		
	}
	private void createAppVersion(IContext context, Element app) {
	try{
		String appVFile=context.getStringValue("BOSSSOFT_HOME")+File.separator+app.attributeValue("name")+File.separator+"version.xml";
		if(!new File(appVFile).exists()) new File(appVFile).createNewFile();
		
		BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(appVFile)));
		bw.write (app.asXML());
		bw.close();
	}  catch(Exception e){
		e.printStackTrace();
	}
		
	}
	private void createProductVersion(IContext context, Element element) {
		String productVFile=context.getStringValue("BOSSSOFT_HOME")+File.separator+context.getStringValue("PRODUCT_NAME")+"_info.xml";
		Document document = DocumentHelper.createDocument();
		  Element root = document.addElement("product");
		   List<Attribute> list=element.attributes();
		   for (Attribute attribute : list) {
			 root.addElement(attribute.getQualifiedName()).addText(attribute.getValue());
		 }
		 root.addElement("installTime").addText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		 root.addElement("installDir").addText(context.getStringValue("INSTALL_DIR"));
		 //记录该产品的应用信息
		 Element applications=DocumentHelper.createElement("applications");
		 root.add(applications);
		 
	     List<String> namekeys = getApps(context);
	     for (String name : namekeys) {
	    	 Element application=DocumentHelper.createElement("application");
	    	 application.addElement("appName").addText(name);
	    	 
	    	 application.addElement("deployDir").addText(context.getStringValue("APP_SERVER_DEPLOY_DIR"));
	    	 application.addElement("serverPort").addText(context.getStringValue("APP_SERVER_PORT"));
		     applications.add(application);
	     }
	    try{
				OutputFormat format =OutputFormat.createPrettyPrint(); 
				  format.setEncoding("utf-8");//设置编码格式  
				  format.setNewLineAfterDeclaration(false);
			    XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(productVFile),format);

			     xmlWriter.write(document);
			     xmlWriter.close();
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	private List<String> getApps(IContext context) {
		List<String> apps=new ArrayList<String>();
		List<ModuleDef> optionsCompList=(List<ModuleDef>) context.getValue("MODULE_OPTIONS");
		for (ModuleDef moduleDef : optionsCompList) {
			 if(moduleDef.getFilesPath().endsWith("war")) apps.add(moduleDef.getNameKey());
		}
		return apps;
	}
	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}

	
}
