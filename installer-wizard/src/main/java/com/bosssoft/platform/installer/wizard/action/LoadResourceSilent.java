package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.option.ResourceDef;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;

public class LoadResourceSilent implements IAction{

	public void execute(IContext context, Map params) throws InstallException {
		Map<String,ResourceDef> resourceMap=new HashMap<String, ResourceDef>();
		String filePath=InstallerFileManager.getResourcesDir()+File.separator+"resource"+File.separator+"resource.xml";
		SAXReader reader=new SAXReader();
		Document doc=null;
		try {
			doc = reader.read(new File(filePath));
		} catch (DocumentException e) {
			throw new InstallException("faild to  load resource config "+e);
		}
		Iterator<Element> iter=doc.getRootElement().elementIterator("server");
		while(iter.hasNext()){
			Element ele=iter.next();
			ResourceDef def=createResourceDef(ele,context);
			resourceMap.put(def.getName(), def);
		}
		context.setValue("RESOURCE_MAP", resourceMap);
	}
		
    

	private ResourceDef createResourceDef(Element ele, IContext context) {
		ResourceDef def=new ResourceDef();
		def.setName(ele.elementText("name"));
		def.setHome(ExpressionParser.parseString(ele.elementText("home")));
		def.setIsInstall(true);
		def.setSourcePath(ExpressionParser.parseString(ele.elementText("sourcePath")));
		def.setDestPath(ExpressionParser.parseString(ele.elementText("destPath")));
		if(ele.elementText("installfiles")!=null&&!"".equals(ele.elementText("installfiles"))){//不需要脚本安装
			def.setInstallFiles(ExpressionParser.parseString(ele.elementText("installfiles")));
		}
		
		Iterator<Element> paramters=ele.element("paramters").elementIterator("paramter");
		while(paramters.hasNext()){
			Element paramter=paramters.next();
			String key=paramter.attributeValue("key");
			String value=paramter.attributeValue("value").replace("[home]", def.getHome());
			def.setParams(key, value);
			context.setValue(key, value);
		}
		context.setValue(def.getName()+"_home",def.getHome());
		return def;
	}



	public void rollback(IContext context, Map params) throws InstallException {
		
		
	}

}
