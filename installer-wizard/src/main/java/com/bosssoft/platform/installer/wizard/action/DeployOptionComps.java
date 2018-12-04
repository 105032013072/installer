package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;
import com.bosssoft.platform.installer.core.message.FileOperationMessageListener;
import com.bosssoft.platform.installer.core.option.CompDef;
import com.bosssoft.platform.installer.core.option.ComponentsDefHelper;
import com.bosssoft.platform.installer.core.option.ModuleDef;
import com.bosssoft.platform.installer.core.util.ExpressionParser;
import com.bosssoft.platform.installer.io.FileUtils;
import com.bosssoft.platform.installer.io.operation.exception.OperationException;
import com.bosssoft.platform.installer.jee.JEEServerOperationException;
import com.bosssoft.platform.installer.jee.server.DeployType;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatEnv;
import com.bosssoft.platform.installer.jee.server.impl.tomcat.TomcatServerImpl;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicEnv;
import com.bosssoft.platform.installer.jee.server.impl.weblogic.WeblogicServerImpl;
import com.bosssoft.platform.installer.jee.server.internal.TargetModelImpl;

/**
 * 部署相关组件
 * @author Windows
 *
 */
public class DeployOptionComps implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	
	public void execute(IContext context, Map params) throws InstallException {
		//获取配置的部署方式
		DeployType deployType=null;
		Object param= params.get("deployType");
		if(param!=null){
			String deployTypeStr=param.toString().toUpperCase();
			deployType=DeployType.valueOf(deployTypeStr);
		}
		
		if(deployType==null){//默认以文件夹的形式部署
			deployType=DeployType.DIRECTORY;
		}
		
		//读取组件信息
		try{
			List<ModuleDef> optionsCompList=new ArrayList<ModuleDef>();
		    optionsCompList=(List<ModuleDef>) context.getValue("MODULE_OPTIONS");
			

			for (ModuleDef moduleDef : optionsCompList) {
				deployWar(context,moduleDef,deployType);
			}
		}catch(Exception e){
           this.logger.error(e);
		}
		
		
	}

	private void deployWar(IContext context, ModuleDef moduleDef,DeployType deployType) throws IOException {
		String appsvrType = context.getStringValue("APP_SERVER_TYPE");
		if(appsvrType.toLowerCase().indexOf("tomcat")!=-1){
			tomcatDeploy(context, moduleDef,deployType);
			
		}else if(appsvrType.toLowerCase().indexOf("jboss")!=-1){
			
		}else if(appsvrType.toLowerCase().indexOf("weblogic")!=-1){
			weblogicDeploy(context, moduleDef,deployType);
		}
	}

	private void weblogicDeploy(IContext context, ModuleDef moduleDef,DeployType deployType) throws IOException {
		String beaHome=context.getStringValue("AS_WL_BEA_HOME");
		String weblogicHome=context.getStringValue("AS_WL_HOME");
		String domainHome=context.getStringValue("AS_WL_DOMAIN_HOME");
		String host=context.getStringValue("AS_WL_TARGET_SERVER");
		String port=context.getStringValue("AS_WL_WEBSVR_PORT");
		String appName=moduleDef.getNameKey();
		String appWarPath=ExpressionParser.parseString(moduleDef.getFilesPath());
			
		weblogicDeploy(appName,appWarPath,weblogicHome,domainHome,deployType);
			
		
	}

	private void weblogicDeploy(String appName, String appWarPath, String weblogicHome, String domainHome,DeployType deployType) throws IOException {
		File dest=new File(domainHome+"/autodeploy/");
		File destAppDir = new File(dest, appName);
		File appFile=new File(appWarPath);
		try {
			if (appFile.isDirectory()) {
				FileUtils.copy(appFile, destAppDir, null, null);
				logger.debug("deploy "+appName+": "+"copy "+appFile+" to "+destAppDir);
			} else{
				if(DeployType.WAR.toString().equals(deployType.toString())){//以war形式部署，直接拷贝
					FileUtils.copy(appFile, new File(dest,appFile.getName()), null, null);
					logger.debug("deploy "+appName+": "+"copy "+appFile+" to "+destAppDir);
				}else{
					FileUtils.unzip(appFile, destAppDir, null, null);
					logger.debug("deploy "+appName+": "+"unzip "+appFile+" to "+destAppDir);
				}
			}
				
		} catch (OperationException e) {
			String message = e.getMessage();
			if ((StringUtils.isEmpty(message)) && (e.getCause() != null)) {
				message = e.getCause().getMessage();
			}
			throw new IOException(message);
		}
		
	}

	private void tomcatDeploy(IContext context, ModuleDef moduleDef,DeployType deployType) {
		try{
			String tomcatHome=context.getStringValue("AS_TOMCAT_HOME");
			TomcatEnv env = new TomcatEnv(tomcatHome);
			TomcatServerImpl server = new TomcatServerImpl(env,deployType);
			String appName=moduleDef.getNameKey();
			String appSourceFile=ExpressionParser.parseString(moduleDef.getFilesPath());
			server.deploy(appName, new File(appSourceFile), null, null);
			logger.debug("deploy "+appName+": "+"unzip "+appSourceFile+" to "+tomcatHome+File.separator+appName);
		}catch(Exception e){
		    e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void rollback(IContext context, Map params) throws InstallException {
		// TODO Auto-generated method stub
		
	}

}
