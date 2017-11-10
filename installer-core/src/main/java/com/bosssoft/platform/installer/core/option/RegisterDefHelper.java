package com.bosssoft.platform.installer.core.option;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;

/**
 * 加载registe.xml
 * @author huangxw
 *
 */
public class RegisterDefHelper {
	
   private static String configPath=InstallerFileManager.getResourcesDir()+File.separator+"resource"+File.separator+"register.xml";

   private static List<RegisterDef> registerList=null; 
    
   public static List<RegisterDef> getRegisterList(IContext context){
	   if(registerList==null){
		   try{
			   loadRegisterList(context);
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   
	   }
	   return registerList;
   }

    private static void loadRegisterList(IContext context) throws Exception {
    	registerList=new ArrayList<RegisterDef>();
    	SAXReader reader=new SAXReader();
	   Document document=reader.read(new File(configPath));
	   Element servers=document.getRootElement();
	   Iterator<Element> it=servers.elementIterator();
	   while(it.hasNext()){
		   RegisterDef def=new RegisterDef();
		   Element ser=it.next();
		   String name=ser.elementText("name");
		   if(!"tomcat".equalsIgnoreCase(context.getStringValue("APP_SERVER_NAME"))&& "tomcat".equalsIgnoreCase(name)) continue;
		   def.setName(name);
		   def.setRegisterCmd(ser.elementText("register"));
		   def.setRemoveCmd(ser.elementText("remove"));
		   def.setStartCmd(ser.elementText("start"));
		   def.setStopCmd(ser.elementText("stop"));
		   def.setWorkDir(ExpressionParser.parseString(ser.elementText("wordDir")));
		   registerList.add(def);
	   }
	   
   }
}
