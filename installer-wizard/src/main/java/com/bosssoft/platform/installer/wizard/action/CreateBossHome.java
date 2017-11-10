package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.Copydir;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.option.ModuleDef;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class CreateBossHome implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		List<ModuleDef> optionsCompList=(List<ModuleDef>) context.getValue("MODULE_OPTIONS");
		for (ModuleDef moduleDef : optionsCompList) {
			if(moduleDef.getFilesPath().endsWith("war")) create(context,moduleDef);
		}
		
	}

	private void create(IContext context, ModuleDef moduleDef) {
		/*String homePath=context.getStringValue("BOSSSOFT_HOME")+File.separator+moduleDef.getNameKey()+File.separator+"conf";
		File homedir=new File(homePath);
		if(!homedir.exists())
			homedir.mkdirs();
		String copyFiledir=null;
		String appsvrType = context.getStringValue("APP_SERVER_TYPE");
		if(appsvrType.toLowerCase().indexOf("tomcat")!=-1){
			copyFiledir=context.getStringValue("AS_TOMCAT_HOME")+File.separator+"webapps"+File.separator+moduleDef.getNameKey()+File.separator+"WEB-INF"+File.separator+"classes";
		}else if(appsvrType.toLowerCase().indexOf("jboss")!=-1){
			
		}else if(appsvrType.toLowerCase().indexOf("weblogic")!=-1){
			copyFiledir=context.getStringValue("AS_WL_DOMAIN_HOME")+File.separator+"autodeploy"+File.separator+moduleDef.getNameKey()+File.separator+"WEB-INF"+File.separator+"classes";
		}
		
		File[] files = new File(copyFiledir).listFiles();
		for (File file : files) {
			try {
				if(!file.isDirectory()&&file.getName().endsWith(".properties")){
	                String fileName=file.getName();
					String dest=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
					FileUtils.copy(file, new File(homedir+File.separator+dest), null, null);
				}
				
			} catch (OperationException e) {
				
				this.logger.error(e);
			}
		}*/
          
		
		String homePath=context.getStringValue("BOSSSOFT_HOME")+File.separator+moduleDef.getNameKey();
		File homedir=new File(homePath);
		if(!homedir.exists()) homedir.mkdirs();
		  String sourceDir=InstallerFileManager.getResourcesDir()+File.separator+"outerConfig"+File.separator+moduleDef.getNameKey()+File.separator+"conf";
		  if(new File(sourceDir).exists()){
			  Copydir copydir=new Copydir();
			  copydir.setSrcDir(sourceDir);
			  copydir.setDestDir(homePath+File.separator+"conf");
			  copydir.execute(context, null);
		  }
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
