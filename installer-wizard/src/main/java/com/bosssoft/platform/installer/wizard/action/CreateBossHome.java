package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.Copydir;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.option.ModuleDef;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class CreateBossHome implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		List<ModuleDef> optionsCompList=(List<ModuleDef>) context.getValue("MODULE_OPTIONS");
		for (ModuleDef moduleDef : optionsCompList) {
			if(moduleDef.getFilesPath().endsWith("war")){
                String[] templetparams=params.get("initParams").toString().split(",");
				try {
					create(context,moduleDef,templetparams);
				} catch (Exception e) {
					logger.error(e);
					throw new InstallException(e);
				}
			} 
		}
		
	}

	private void create(IContext context, ModuleDef moduleDef,String[] templetparams) throws Exception {
		String templetFolder=InstallerFileManager.getResourcesDir()+File.separator+"outerConfig"+File.separator+moduleDef.getNameKey()+File.separator+"conf";
		File[] templetFils=new File(templetFolder).listFiles();
		for (File tempFile : templetFils) {
			
			String helper[]=FilenameUtils.getBaseName(tempFile.getName()).split("_");
			String sourceFilePath=context.getStringValue("BOSSSOFT_HOME")+File.separator+moduleDef.getNameKey()
			                                                         +File.separator+"conf"
			                                                         +File.separator+helper[0]+"."+helper[1];
			File sourceFile=new File(sourceFilePath);
			
			if(!sourceFile.exists()){
				FileUtils.mkdir(sourceFile, false);
				sourceFile.createNewFile();
			} 
			
			VelocityEngine ve = new VelocityEngine();
			
		
			Properties p = new Properties();
	        // 初始化默认加载路径为：D:/template
	        p.setProperty(ve.FILE_RESOURCE_LOADER_PATH, tempFile.getParent());
	        p.setProperty(ve.ENCODING_DEFAULT, "UTF-8");
	        p.setProperty(ve.INPUT_ENCODING, "UTF-8");
	        p.setProperty(ve.OUTPUT_ENCODING, "UTF-8");
	        // 初始化Velocity引擎，init对引擎VelocityEngine配置了一组默认的参数 
	        ve.init(p);
			
			Template t = ve.getTemplate(tempFile.getName()); 
			VelocityContext vc = new VelocityContext();
			
			for (String v : templetparams) {
				vc.put(v, context.getStringValue(v));
			}
			StringWriter writer = new StringWriter();
			t.merge(vc, writer); 
			
			BufferedWriter bw =null;
			try {
				bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(sourceFile)));
				bw.write (writer.toString());
				
			} catch (IOException e) {
				this.logger.error(e);
			}finally{
				bw.close();
			}
		}
		
		
		
		
		
		
		
          
		
		/*String homePath=context.getStringValue("BOSSSOFT_HOME")+File.separator+moduleDef.getNameKey();
		File homedir=new File(homePath);
		if(!homedir.exists()) homedir.mkdirs();
		  String sourceDir=InstallerFileManager.getResourcesDir()+File.separator+"outerConfig"+File.separator+moduleDef.getNameKey()+File.separator+"conf";
		  if(new File(sourceDir).exists()){
			  Copydir copydir=new Copydir();
			  copydir.setSrcDir(sourceDir);
			  copydir.setDestDir(homePath+File.separator+"conf");
			  copydir.execute(context, null);
		  }*/
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
