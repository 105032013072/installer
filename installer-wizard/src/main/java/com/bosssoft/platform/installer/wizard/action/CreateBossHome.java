package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
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
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.core.option.ModuleDef;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.core.util.InstallerFileManager;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class CreateBossHome implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		String[] templetparams = params.get("initParams").toString().split(",");
		// 应用的外部化配置
		try{
			List<ModuleDef> optionsCompList = (List<ModuleDef>) context.getValue("MODULE_OPTIONS");
			for (ModuleDef moduleDef : optionsCompList) {
				if (moduleDef.getFilesPath().endsWith("war")) {

					String templetFolder = InstallerFileManager.getAppOuterconfigDir() + File.separator
							+ moduleDef.getNameKey() + File.separator + "conf";
					if(!new File(templetFolder).exists()) continue;
					File[] templetFils = new File(templetFolder).listFiles();
					for (File file : templetFils) {
						create(context, file, templetparams, moduleDef.getNameKey(),InstallerFileManager.getAppOuterconfigDir());

					}
				}
 
			}
			
			//应用所依赖的第三方的外部化配置
			if(new File(InstallerFileManager.getThirdOuterconfigDir()).exists()){
				String[] names=new File(InstallerFileManager.getThirdOuterconfigDir()).list();
				for (String name : names) {
					String dir=InstallerFileManager.getThirdOuterconfigDir()+File.separator+name;
					List<File> files=FileUtils.listFiles(new File(dir), new FileFilter() {
						
						public boolean accept(File file) {
							if(file.isDirectory()) return false;
							else return true;
						}
					});
					
					for (File file : files) {
						create(context, file, templetparams, name,InstallerFileManager.getThirdOuterconfigDir());
					}
					
				}
			}
			
			
		}catch(Exception e){
			logger.error(e);
			throw new InstallException(e);
		} 
		
		

	}

	private void create(IContext context,File tempFile,String[] templetparams,String appName,String parentPath) throws Exception {
		
		    if(!tempFile.getName().endsWith(".vm")){
		    	//直接拷贝
		    	String targetPath=tempFile.getPath().replace(new File(parentPath).getPath(), context.getStringValue("BOSSSOFT_HOME"));
		    	FileUtils.copy(tempFile, new File(targetPath), null, FileOperationMessageListener.INSTANCE);
		 
		    }else {
		    	String helper[]=FilenameUtils.getBaseName(tempFile.getName()).split("_");
		    	String sourceFilePath=tempFile.getParent().replace(new File(parentPath).getPath(), context.getStringValue("BOSSSOFT_HOME"))
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
		
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
