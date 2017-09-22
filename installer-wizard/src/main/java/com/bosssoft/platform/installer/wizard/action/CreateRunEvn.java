package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.action.MkDir;
import com.bosssoft.platform.installer.core.option.ResourceDef;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class CreateRunEvn implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		//创建BOSSOFT_HOM目录
		File path=new File(context.getValue("BOSSSOFT_HOME").toString());
		if(!path.exists()) {
			MkDir dir=new MkDir();
			dir.setDir(path.getPath());
			dir.execute(context, params);
			logger.debug("create BOSSSOFT_HOME dir");
		}
		
		//安装运行环境
		Map<String,ResourceDef> map=(Map<String, ResourceDef>) context.getValue("RESOURCE_MAP");
	    Collection<ResourceDef> values=map.values();
	    for (ResourceDef resourceDef : values) {
	    	if(resourceDef.getIsInstall()){
	    		try {
					install(resourceDef,context);
				} catch (Exception e) {
					throw new InstallException("faild to create run environment "+e);
				}
	    	}
	    }
					
		
	}

	private void install(ResourceDef resourceDef, IContext context) throws Exception {
		
	   logger.info("create run environment: install "+resourceDef.getName());
	   copyInstall(resourceDef);//直接拷贝或者解压
	   
	   String installFiles=resourceDef.getInstallFiles();
	   if(installFiles!=null&&!"".equals(installFiles)) scriptInstall(installFiles);//使用脚本安装
	   
	   
	}

	private void scriptInstall(String installfiles) throws Exception {
		String[] files=installfiles.split(",");
	       
    	 for (String file : files) {
      		//替换执行文件中的变量
    		 String content=org.apache.commons.io.FileUtils.readFileToString(new File(file));
             content=content.replace("${INSTALL_DIR}", ExpressionParser.parseString("${INSTALL_DIR}"));
           //重新写入
     		BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
     		out.write(content);
     		out.close();
     		
    		 //授权，执行
     		Process process=null;
    		  String c1="chmod a+x"+" "+file;
    		 process=Runtime.getRuntime().exec(c1);
    			process.waitFor();
    			
    			Runtime.getRuntime().exec(file).waitFor();
		} 
		
	}

	private void copyInstall(ResourceDef resourceDef) throws Exception {
		File sourceFile=new File(resourceDef.getSourcePath());
		String destPath=resourceDef.getDestPath();
	   if(resourceDef.getSourcePath().endsWith(".zip")){
		   FileUtils.unzip(sourceFile, new File(destPath), null, null);
		   logger.debug("unzip "+sourceFile+" to "+destPath);
	   }else{
		   FileUtils.copy(sourceFile, new File(destPath), null, null);
		   logger.debug("copy "+sourceFile+" to "+destPath);
	   }
		
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
	}

}
