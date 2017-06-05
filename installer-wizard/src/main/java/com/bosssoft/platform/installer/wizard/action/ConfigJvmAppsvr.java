package com.bosssoft.platform.installer.wizard.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bosssoft.platform.installer.core.IContext;
import com.bosssoft.platform.installer.core.InstallException;
import com.bosssoft.platform.installer.core.action.IAction;

public class ConfigJvmAppsvr implements IAction{
	transient Logger logger = Logger.getLogger(getClass());
	
	public void execute(IContext context, Map params) throws InstallException {
		String appsvrType = context.getStringValue("APP_SERVER_TYPE");
		if(appsvrType.toLowerCase().indexOf("tomcat")!=-1){
			configTomcat(context, params);
			
		}else if(appsvrType.toLowerCase().indexOf("jboss")!=-1){
			
		}else if(appsvrType.toLowerCase().indexOf("weblogic")!=-1){
			configweblogic(context, params);
		}
		
	}


	private void configweblogic(IContext context, Map params) {
		if("true".equals(context.getStringValue("IS_WINDOWS"))){
			config4windows(context,params);
		}else{
			config4linux(context,params);
		}
		
	}


	private void config4linux(IContext context, Map params) {
		String file=context.getStringValue("AS_WL_DOMAIN_HOME")+"/bin"+"/setDomainEnv.sh";
		StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(file)));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
               if(s.trim().startsWith("WLS_MEM_ARGS_64BIT")){
            	   result.append(System.lineSeparator()+"WLS_MEM_ARGS_64BIT=\"-Xms512m -Xmx1024m");
            	   result.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME")).append("\"");
               }else if(s.trim().startsWith("WLS_MEM_ARGS_32BIT")){
            	   result.append(System.lineSeparator()+"WLS_MEM_ARGS_32BIT=\"-Xms512m -Xmx1024m");
            	   result.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME")).append("\"");
               }else result.append(System.lineSeparator()+s);
            }
            br.close(); 
            
            BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(file)));
			bw.write (result.toString());
			bw.close();
        }catch(Exception e){
        	this.logger.error(e);
        }
		
	}


	private void config4windows(IContext context, Map params) {
		String file=context.getStringValue("AS_WL_DOMAIN_HOME")+"/bin"+"/setDomainEnv.cmd";
		StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(file)));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
               if(s.trim().startsWith("set WLS_MEM_ARGS_64BIT")){
            	   result.append(System.lineSeparator()+"set WLS_MEM_ARGS_64BIT=-Xms512m -Xmx1024m");
            	   result.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME"));
               }else if(s.trim().startsWith("set WLS_MEM_ARGS_32BIT")){
            	   result.append(System.lineSeparator()+"set WLS_MEM_ARGS_32BIT=-Xms512m -Xmx1024m");
            	   result.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME"));
               }else result.append(System.lineSeparator()+s);
            }
            br.close(); 
            
            BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(file)));
			bw.write (result.toString());
			bw.close();
        }catch(Exception e){
        	this.logger.error(e);
        }
        
	}


	private void configTomcat(IContext context, Map params) {
		String file=null;
		StringBuffer addcontext=new StringBuffer();
		StringBuffer result=new StringBuffer();
		Integer inserIndex=null;
		if("true".equals(context.getStringValue("IS_WINDOWS"))){
			file=context.getStringValue("AS_TOMCAT_HOME")+"/bin/catalina.bat";
			result=new StringBuffer(readSource(file));
			addcontext.append("set ");
			inserIndex=0;
			addcontext.append("JAVA_OPTS=%JAVA_OPTS% -Xms512m -Xmx1024m");
			addcontext.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME"));
		}else{
			file=context.getStringValue("AS_TOMCAT_HOME")+"/bin/catalina.sh";
			result=new StringBuffer(readSource(file));
			inserIndex=result.indexOf("#!/bin/sh")+"#!/bin/sh".length()+1;
			addcontext.append(System.lineSeparator());
			addcontext.append("JAVA_OPTS=\"$JAVA_OPTS -Xms512m -Xmx1024m");
			addcontext.append(" -DBOSSSOFT_HOME=").append(context.getStringValue("BOSSSOFT_HOME")).append("\"").append(System.lineSeparator());
		}
		try{
			
			if(!result.toString().contains(addcontext))
			   result.insert(inserIndex, addcontext);
			
			BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream(file)));
			bw.write (result.toString());
			bw.close();
		}catch(Exception e){
			this.logger.error(e);
		}
	}


	public void rollback(IContext context, Map params) throws InstallException {
		
	}

    private String readSource(String sourceFile) {
		
		StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(sourceFile)));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
        	this.logger.error(e);
        }
        return result.toString();
	}
	
}
