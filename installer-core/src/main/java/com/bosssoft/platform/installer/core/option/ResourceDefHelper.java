package com.bosssoft.platform.installer.core.option;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
/**
 * 加载resource.xml(运行环境配置)
 * @author huangxw
 *
 */
public class ResourceDefHelper {
	private static Map<String,ResourceDef> resourceMap=null;
	
	public static Map<String,ResourceDef> getResourceMap(){
			try {
				loadResource();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return resourceMap;
	}
	
	private static void loadResource() throws Exception {
		Map<String,ResourceDef> map=new HashMap<String, ResourceDef>();
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
			String required=ele.elementText("required");
			if("false".equalsIgnoreCase(required)) def.setRequired(false);
			else if("true".equalsIgnoreCase(required)) def.setRequired(true);
			
			//端口信息解析
			Element portele=ele.element("port");
			if(portele!=null){
				String[] portsStr=portele.attributeValue("default").split(",");
				for (String p : portsStr) {
					def.addPort(Integer.valueOf(p));
				}
				def.setPortConfigFile(portele.attributeValue("configfile"));
				String[] configKeys=portele.attributeValue("key").split(",");
				def.setPortConfigKeys(Arrays.asList(configKeys));
			}
			
		
			Iterator<Element> paramters=ele.element("paramters").elementIterator("paramter");
			while(paramters.hasNext()){
				Element paramter=paramters.next();
				def.setParams(paramter.attributeValue("key"), paramter.attributeValue("value"));
			}
			map.put(def.getName(), def);
		}
		resourceMap=map;
	}
}
