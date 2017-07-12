package com.bosssoft.platform.installer.wizard.action;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.option.ResourceDef;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;

public class CreateRunEvn implements IAction{

	transient Logger logger = Logger.getLogger(getClass());
	public void execute(IContext context, Map params) throws InstallException {
		Map<String,ResourceDef> map=(Map<String, ResourceDef>) context.getValue("RESOURCE_MAP");
	    Collection<ResourceDef> values=map.values();
	    for (ResourceDef resourceDef : values) {
	    	if(resourceDef.getIsInstall()){
	    		try {
					install(resourceDef);
				} catch (Exception e) {
					throw new InstallException("faild to create run environment "+e);
				}
	    	}
	    }
					
		
	}

	private void install(ResourceDef resourceDef) throws Exception {
		File sourceFile=new File(resourceDef.getSourcePath());
		String destPath=resourceDef.getDestPath();
	   if(resourceDef.getSourcePath().endsWith(".zip")){
		   FileUtils.unzip(sourceFile, new File(destPath), null, null);
	   }else{
		   FileUtils.copy(sourceFile, new File(destPath), null, null);
	   }
	}

	public void rollback(IContext context, Map params) throws InstallException {
		
	}

}
